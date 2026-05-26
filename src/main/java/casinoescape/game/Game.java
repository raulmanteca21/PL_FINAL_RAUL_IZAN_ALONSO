package casinoescape.game;

import casinoescape.combat.CombatResult;
import casinoescape.combat.CombatService;
import casinoescape.exceptions.InvalidActionException;
import casinoescape.exceptions.InvalidMoveException;
import casinoescape.exceptions.LockedDoorException;
import casinoescape.items.Armor;
import casinoescape.items.Inventory;
import casinoescape.items.Item;
import casinoescape.items.Consumable;
import casinoescape.items.Effect;
import casinoescape.items.EffectType;
import casinoescape.items.Shop;
import casinoescape.logging.GameLog;
import casinoescape.model.CasinoMap;
import casinoescape.model.CellType;
import casinoescape.model.Enemy;
import casinoescape.model.GameState;
import casinoescape.model.Npc;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.model.Trap;
import casinoescape.movement.MovementService;
import casinoescape.movement.EnemyMovementService;
import casinoescape.movement.PathFinder;
import casinoescape.movement.Direction;
import casinoescape.movement.ShortestPathInfo;
import casinoescape.structures.MyLinkedList;

public class Game {
    public static final String WELCOME_MESSAGE = "Bienvenido al Casino Fortuna. Si buscas a tu amigo, pregunta en el bar... si sobrevives.";
    public static final String FRIEND_RESCUED_MESSAGE = "Has encontrado a tu amigo. Ahora puedes intentar salir del casino.";
    public static final String EXIT_WITHOUT_FRIEND_MESSAGE = "No puedes abandonar el casino sin tu amigo.";
    public static final String VICTORY_MESSAGE = "Has escapado del casino con tu amigo. Victoria.";
    public static final String ROULETTE_DECLINED_MESSAGE = "Has rechazado jugar a la ruleta rusa.";
    public static final int DANGEROUS_COMPANION_DAMAGE_PERCENT = 10;
    public static final int ROULETTE_REWARD_CHIPS = 5;

    private final CasinoMap map;
    private final Player player;
    private final Inventory inventory;
    private final TurnManager turnManager;
    private final MovementService movementService;
    private final EnemyMovementService enemyMovementService;
    private final PathFinder pathFinder;
    private final CombatService combatService;
    private final Shop barShop;
    private final GameLog log;
    private final Npc welcomeNpc;
    private final Npc barSpecialNpc;
    private final Npc friendNpc;
    private final Trap dangerousCompanion;

    public static Game createNewGame(int turnsRemaining) {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();
        Player player = new Player(100, 10, 5, 3, map.getInitialRoomId(), map.getInitialPlayerPosition());
        return new Game(
                map,
                player,
                new Inventory(),
                new TurnManager(turnsRemaining),
                new MovementService(),
                new PathFinder(),
                Shop.createDefaultBarShop(),
                new GameLog(),
                createWelcomeNpc(),
                createBarSpecialNpc(),
                createFriendNpc(),
                createDangerousCompanion());
    }

    public static Npc createWelcomeNpcForRestore(boolean alreadyInteracted) {
        Npc npc = createWelcomeNpc();
        if (alreadyInteracted) {
            npc.markInteracted();
        }
        return npc;
    }

    public static Npc createBarSpecialNpcForRestore(boolean alreadyInteracted) {
        Npc npc = createBarSpecialNpc();
        if (alreadyInteracted) {
            npc.markInteracted();
        }
        return npc;
    }

    public static Npc createFriendNpcForRestore(boolean alreadyInteracted) {
        Npc npc = createFriendNpc();
        if (alreadyInteracted) {
            npc.markInteracted();
        }
        return npc;
    }

    public static Trap createDangerousCompanionForRestore() {
        return createDangerousCompanion();
    }

    public Game(CasinoMap map, Player player, Inventory inventory, TurnManager turnManager,
            MovementService movementService, Shop barShop, GameLog log) {
        this(map, player, inventory, turnManager, movementService, new PathFinder(), barShop, log,
                createWelcomeNpc(), createBarSpecialNpc(), createFriendNpc(), createDangerousCompanion());
    }

    public Game(CasinoMap map, Player player, Inventory inventory, TurnManager turnManager,
            MovementService movementService, PathFinder pathFinder, Shop barShop, GameLog log,
            Npc welcomeNpc, Npc barSpecialNpc, Npc friendNpc, Trap dangerousCompanion) {
        if (map == null) {
            throw new IllegalArgumentException("Map is required");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player is required");
        }
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory is required");
        }
        if (turnManager == null) {
            throw new IllegalArgumentException("Turn manager is required");
        }
        if (movementService == null) {
            throw new IllegalArgumentException("Movement service is required");
        }
        if (pathFinder == null) {
            throw new IllegalArgumentException("Path finder is required");
        }
        if (barShop == null) {
            throw new IllegalArgumentException("Bar shop is required");
        }
        if (log == null) {
            throw new IllegalArgumentException("Game log is required");
        }
        if (welcomeNpc == null || barSpecialNpc == null || friendNpc == null) {
            throw new IllegalArgumentException("Game npcs are required");
        }
        if (dangerousCompanion == null) {
            throw new IllegalArgumentException("Dangerous companion is required");
        }
        this.map = map;
        this.player = player;
        this.inventory = inventory;
        this.turnManager = turnManager;
        this.movementService = movementService;
        this.enemyMovementService = new EnemyMovementService();
        this.pathFinder = pathFinder;
        this.combatService = new CombatService();
        this.barShop = barShop;
        this.log = log;
        this.welcomeNpc = welcomeNpc;
        this.barSpecialNpc = barSpecialNpc;
        this.friendNpc = friendNpc;
        this.dangerousCompanion = dangerousCompanion;
    }

    public void movePlayer(Position destination) {
        Room currentRoom = getCurrentRoom();
        if (!turnManager.canMove()) {
            throw new InvalidMoveException("Movement is not available in this turn");
        }
        if (!movementService.canMove(currentRoom, player, destination)) {
            throw new InvalidMoveException("Destination is not reachable");
        }

        movementService.movePlayer(currentRoom, player, destination);
        turnManager.registerMovement();
        log.add("Movimiento del jugador a " + destination.getRow() + "," + destination.getColumn());
    }

    public void movePlayerInLine(Direction direction) {
        if (!turnManager.canMove()) {
            throw new InvalidMoveException("Movement is not available in this turn");
        }
        if (!inventory.hasActiveEffect(EffectType.LINE_MOVEMENT)) {
            throw new InvalidMoveException("No hay efecto activo de movimiento en linea");
        }
        movementService.movePlayerInLine(getCurrentRoom(), player, direction);
        turnManager.registerMovement();
        log.add("Movimiento especial en linea hacia " + direction.name());
    }

    public Item buyFromBar(String shopItemId) {
        if (!turnManager.canAct()) {
            throw new IllegalStateException("Action is not available in this turn");
        }

        Item item = barShop.buy(shopItemId, player, inventory, log);
        turnManager.registerAction();
        return item;
    }

    public Item buyFromAdjacentBar(String shopItemId) {
        requireActionAvailable();
        requirePlayerInRoom(5);
        requireAdjacentTo(CasinoMapBuilder.BAR_SHOP_POSITION);

        Item item = barShop.buy(shopItemId, player, inventory, log);
        turnManager.registerAction();
        return item;
    }

    public void useItem(String itemId) {
        requireActionAvailable();
        Item item = inventory.findById(itemId);
        inventory.useConsumable(itemId, player);
        turnManager.registerAction();
        log.add("Objeto usado: " + item.getName());
    }

    public void equipWeapon(String itemId) {
        requireActionAvailable();
        Item item = inventory.findById(itemId);
        inventory.equipWeapon(itemId, player);
        turnManager.registerAction();
        log.add("Arma equipada: " + item.getName());
    }

    public void equipArmor(String itemId) {
        requireActionAvailable();
        Item item = inventory.findById(itemId);
        inventory.equipArmor(itemId, player);
        turnManager.registerAction();
        log.add("Armadura equipada: " + item.getName());
    }

    public Item pickUpAdjacentItem() {
        Position target = findCurrentOrAdjacentItemPosition();
        if (target == null) {
            throw new InvalidActionException("No hay objeto adyacente para recoger");
        }
        return pickUpItemAt(target);
    }

    public Item pickUpItemAt(Position position) {
        requireActionAvailable();
        requireCurrentOrAdjacentTo(position);

        Item item = getCurrentRoom().removeItemAt(position);
        if (item == null) {
            throw new InvalidActionException("No hay objeto en la posicion indicada");
        }
        inventory.addItem(item);
        turnManager.registerAction();
        log.add("Objeto recogido: " + item.getName());
        return item;
    }

    public String interactSimpleAt(Position target) {
        if (target == null) {
            throw new InvalidActionException("Interaction target is required");
        }
        CellType type = getCurrentRoom().getCell(target).getType();
        if (type == CellType.DOOR) {
            useDoorAt(target);
            return "Puerta usada";
        }
        if (type == CellType.ITEM) {
            return "Has recogido: " + pickUpItemAt(target).getName();
        }
        if (type == CellType.EXIT) {
            return interactExit();
        }
        if (type == CellType.TRAP) {
            return "La acompanante peligrosa se aplica como efecto ambiental al finalizar turno.";
        }
        if (type == CellType.NPC) {
            return interactAdjacentNpc();
        }
        throw new InvalidActionException("La interaccion requiere una accion especifica");
    }

    public String interactAdjacentNpc() {
        int roomId = player.getCurrentRoomId();
        if (roomId == 1) {
            return interactWelcomeNpc();
        }
        if (roomId == 5) {
            Item item = interactBarSpecialNpc();
            return item == null ? "El NPC ya entrego su objeto." : "Has recibido: " + item.getName();
        }
        if (roomId == 6) {
            return rescueFriend();
        }
        throw new InvalidActionException("NPC sin interaccion especial documentada");
    }

    public CombatResult attackAdjacentEnemy() {
        Position target = findAdjacentEnemyPosition();
        if (target == null) {
            throw new InvalidActionException("No hay enemigo adyacente para atacar");
        }
        return attackEnemyAt(target, Math.random());
    }

    public CombatResult attackEnemyAt(Position position, double randomValue) {
        requireActionAvailable();
        requireAdjacentTo(position);
        validateRandomValue(randomValue);

        Room room = getCurrentRoom();
        Enemy enemy = room.findEnemyAt(position);
        if (enemy == null) {
            throw new InvalidActionException("No hay enemigo en la posicion indicada");
        }

        CombatResult result = combatService.playerAttacksEnemy(player, enemy, randomValue);
        log.add("Ataque a " + enemy.getName() + ": " + result.getDamageDealt() + " de dano");
        if (result.isDefenderDied()) {
            room.removeEnemy(enemy);
            log.add("Enemigo derrotado: " + enemy.getName());
            if (result.getChipsAwarded() > 0) {
                log.add("Fichas ganadas: " + result.getChipsAwarded());
            }
            grantEnemyDrop(result.getDroppedItemName());
        }
        turnManager.registerAction();
        return result;
    }

    public boolean canUseDoorTo(int destinationRoomId) {
        return map.canTransition(player.getCurrentRoomId(), destinationRoomId, inventory.hasTreasuryKey());
    }

    public ShortestPathInfo getShortestPathInfo() {
        return pathFinder.calculatePathToExit(map, player, inventory.hasTreasuryKey());
    }

    public MyLinkedList<Position> getReachableCells() {
        return movementService.calculateReachableCells(getCurrentRoom(), player);
    }

    public Position findCurrentOrAdjacentInteractive() {
        Position current = player.getPosition();
        if (getCurrentRoom().getCell(current).isInteractive()) {
            return current;
        }
        Position door = findCurrentOrAdjacentCellOfType(CellType.DOOR);
        if (door != null) {
            return door;
        }
        Position npc = findCurrentOrAdjacentCellOfType(CellType.NPC);
        if (npc != null) {
            return npc;
        }
        Position item = findCurrentOrAdjacentCellOfType(CellType.ITEM);
        if (item != null) {
            return item;
        }
        Position shop = findCurrentOrAdjacentCellOfType(CellType.SHOP);
        if (shop != null) {
            return shop;
        }
        Position exit = findCurrentOrAdjacentCellOfType(CellType.EXIT);
        if (exit != null) {
            return exit;
        }
        Position minigame = findCurrentOrAdjacentCellOfType(CellType.MINIGAME);
        if (minigame != null) {
            return minigame;
        }
        return findCurrentOrAdjacentCellOfType(CellType.TRAP);
    }

    public Position findCurrentOrAdjacentCellOfType(CellType type) {
        Room room = getCurrentRoom();
        Position current = player.getPosition();
        if (room.getCell(current).getType() == type) {
            return current;
        }
        Position up = createPositionIfInside(room, current.getRow() - 1, current.getColumn());
        if (up != null && room.getCell(up).getType() == type) {
            return up;
        }
        Position down = createPositionIfInside(room, current.getRow() + 1, current.getColumn());
        if (down != null && room.getCell(down).getType() == type) {
            return down;
        }
        Position left = createPositionIfInside(room, current.getRow(), current.getColumn() - 1);
        if (left != null && room.getCell(left).getType() == type) {
            return left;
        }
        Position right = createPositionIfInside(room, current.getRow(), current.getColumn() + 1);
        if (right != null && room.getCell(right).getType() == type) {
            return right;
        }
        return null;
    }

    public String interactWelcomeNpc() {
        requireActionAvailable();
        requirePlayerInRoom(welcomeNpc.getRoomId());
        requireAdjacentTo(welcomeNpc.getPosition());

        welcomeNpc.markInteracted();
        turnManager.registerAction();
        log.add("NPC bienvenida: " + WELCOME_MESSAGE);
        return WELCOME_MESSAGE;
    }

    public Item interactBarSpecialNpc() {
        requireActionAvailable();
        requirePlayerInRoom(barSpecialNpc.getRoomId());
        requireAdjacentTo(barSpecialNpc.getPosition());

        if (barSpecialNpc.hasAlreadyInteracted()) {
            turnManager.registerAction();
            log.add("NPC especial del bar ya habia entregado la pastilla");
            return null;
        }

        Item suspiciousPill = new Consumable(
                "SUSPICIOUS_PILL",
                "Pastilla de dudosa procedencia",
                new Effect(EffectType.LINE_MOVEMENT, 0, 7));
        inventory.addItem(suspiciousPill);
        barSpecialNpc.markInteracted();
        turnManager.registerAction();
        log.add("NPC especial del bar entrega Pastilla de dudosa procedencia");
        return suspiciousPill;
    }

    public String rescueFriend() {
        requireActionAvailable();
        requirePlayerInRoom(friendNpc.getRoomId());
        requireAdjacentTo(friendNpc.getPosition());

        if (!player.isFriendRescued()) {
            player.rescueFriend();
            friendNpc.markInteracted();
            map.getRoom(friendNpc.getRoomId()).setCellType(friendNpc.getPosition(), CellType.EMPTY);
            log.add("Amigo rescatado");
        }
        turnManager.registerAction();
        return FRIEND_RESCUED_MESSAGE;
    }

    public int applyDangerousCompanionEffectIfInRange() {
        if (player.getCurrentRoomId() != dangerousCompanion.getRoomId()
                || !isOrthogonallyAdjacent(player.getPosition(), dangerousCompanion.getPosition())) {
            return 0;
        }

        int damage = calculateDangerousCompanionDamage();
        player.setCurrentHealth(player.getCurrentHealth() - damage);
        log.add("Acompanante peligrosa drena " + damage + " de vida");
        turnManager.checkDefeatByHealth(player);
        if (getState() == GameState.DEFEAT) {
            log.add("Derrota por drenaje de vida");
        }
        return damage;
    }

    public RouletteResult playRussianRoulette(boolean accepts, double randomValue) {
        requireActionAvailable();
        requirePlayerInRoom(CasinoMap.EXIT_ROOM_ID);
        requireAdjacentTo(CasinoMapBuilder.RUSSIAN_ROULETTE_POSITION);
        validateRandomValue(randomValue);

        if (!accepts) {
            turnManager.registerAction();
            log.add(ROULETTE_DECLINED_MESSAGE);
            return new RouletteResult(false, false, false, 0, 0, ROULETTE_DECLINED_MESSAGE);
        }

        if (randomValue < 0.5) {
            player.addChips(ROULETTE_REWARD_CHIPS);
            turnManager.registerAction();
            String message = "Resultado favorable de ruleta rusa: ganas " + ROULETTE_REWARD_CHIPS + " fichas";
            log.add(message);
            return new RouletteResult(true, true, false, ROULETTE_REWARD_CHIPS, 0, message);
        }

        int damage = player.getCurrentHealth();
        player.setCurrentHealth(0);
        turnManager.registerAction();
        turnManager.markDefeat();
        String message = "Resultado desfavorable de ruleta rusa: derrota inmediata";
        log.add(message);
        return new RouletteResult(true, false, true, 0, damage, message);
    }

    public RouletteResult playRussianRoulette(boolean accepts) {
        return playRussianRoulette(accepts, accepts ? Math.random() : 0.0);
    }

    public String interactExit() {
        requireActionAvailable();
        requirePlayerInRoom(CasinoMap.EXIT_ROOM_ID);
        requireAdjacentTo(CasinoMapBuilder.EXIT_POSITION);

        if (!player.isFriendRescued()) {
            turnManager.registerAction();
            log.add(EXIT_WITHOUT_FRIEND_MESSAGE);
            return EXIT_WITHOUT_FRIEND_MESSAGE;
        }

        turnManager.registerAction();
        turnManager.markVictory();
        log.add(VICTORY_MESSAGE);
        return VICTORY_MESSAGE;
    }

    public void endTurn() {
        if (getState() == GameState.IN_PROGRESS) {
            processEnemyPhase();
        }
        if (getState() == GameState.IN_PROGRESS) {
            applyDangerousCompanionEffectIfInRange();
        }
        if (getState() == GameState.IN_PROGRESS) {
            inventory.decreaseTemporaryEffects(player);
        }
    }

    public void useDoorTo(int destinationRoomId) {
        if (!turnManager.canAct()) {
            throw new IllegalStateException("Action is not available in this turn");
        }
        int originRoomId = player.getCurrentRoomId();
        if (!canUseDoorTo(destinationRoomId)) {
            log.add("Intento de abrir puerta bloqueada o invalida desde sala " + originRoomId + " a sala " + destinationRoomId);
            throw new LockedDoorException("Door cannot be used");
        }

        Room originRoom = getCurrentRoom();
        originRoom.setCellType(player.getPosition(), CellType.EMPTY);
        Position entryPosition = findEntryPosition(destinationRoomId, originRoomId);
        player.setCurrentRoomId(destinationRoomId);
        player.setPosition(entryPosition);
        map.getRoom(destinationRoomId).setCellType(entryPosition, CellType.PLAYER);

        turnManager.registerAction();
        log.add("Cambio de sala " + originRoomId + " -> " + destinationRoomId);
        endTurn();
    }

    public void useDoorAt(Position doorPosition) {
        if (doorPosition == null) {
            throw new IllegalArgumentException("Door position is required");
        }
        Room room = getCurrentRoom();
        if (!room.isInside(doorPosition) || room.getCell(doorPosition).getDoor() == null) {
            throw new InvalidActionException("There is no door at the selected position");
        }
        if (!isOrthogonallyAdjacent(player.getPosition(), doorPosition)) {
            throw new InvalidActionException("Player is not adjacent to the door");
        }
        useDoorTo(room.getCell(doorPosition).getDoor().getDestinationRoomId());
    }

    public CasinoMap getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public Shop getBarShop() {
        return barShop;
    }

    public GameLog getLog() {
        return log;
    }

    public Npc getWelcomeNpc() {
        return welcomeNpc;
    }

    public Npc getBarSpecialNpc() {
        return barSpecialNpc;
    }

    public Npc getFriendNpc() {
        return friendNpc;
    }

    public Trap getDangerousCompanion() {
        return dangerousCompanion;
    }

    public Room getCurrentRoom() {
        return map.getRoom(player.getCurrentRoomId());
    }

    public GameState getState() {
        return turnManager.getGameState();
    }

    private Position findEntryPosition(int destinationRoomId, int originRoomId) {
        Room destinationRoom = map.getRoom(destinationRoomId);
        for (int row = 0; row < destinationRoom.getRows(); row++) {
            for (int column = 0; column < destinationRoom.getColumns(); column++) {
                Position doorPosition = new Position(row, column);
                if (isDoorToOrigin(destinationRoom, doorPosition, originRoomId)) {
                    Position entryPosition = findWalkableAdjacentPosition(destinationRoom, doorPosition);
                    if (entryPosition != null) {
                        return entryPosition;
                    }
                }
            }
        }
        throw new IllegalStateException("No entry position found for room " + destinationRoomId);
    }

    private boolean isDoorToOrigin(Room room, Position position, int originRoomId) {
        return room.getCell(position).getDoor() != null
                && room.getCell(position).getDoor().getDestinationRoomId() == originRoomId;
    }

    private Position findWalkableAdjacentPosition(Room room, Position position) {
        Position up = createPositionIfInside(room, position.getRow() - 1, position.getColumn());
        if (up != null && room.isWalkable(up)) {
            return up;
        }
        Position down = createPositionIfInside(room, position.getRow() + 1, position.getColumn());
        if (down != null && room.isWalkable(down)) {
            return down;
        }
        Position left = createPositionIfInside(room, position.getRow(), position.getColumn() - 1);
        if (left != null && room.isWalkable(left)) {
            return left;
        }
        Position right = createPositionIfInside(room, position.getRow(), position.getColumn() + 1);
        if (right != null && room.isWalkable(right)) {
            return right;
        }
        return null;
    }

    private Position createPositionIfInside(Room room, int row, int column) {
        if (row < 0 || row >= room.getRows() || column < 0 || column >= room.getColumns()) {
            return null;
        }
        return new Position(row, column);
    }

    private void requireActionAvailable() {
        if (!turnManager.canAct()) {
            throw new InvalidActionException("Action is not available in this turn");
        }
    }

    private void requirePlayerInRoom(int roomId) {
        if (player.getCurrentRoomId() != roomId) {
            throw new InvalidActionException("Player is not in the required room");
        }
    }

    private void requireAdjacentTo(Position position) {
        if (!isOrthogonallyAdjacent(player.getPosition(), position)) {
            throw new InvalidActionException("Player is not adjacent to the target");
        }
    }

    private void requireCurrentOrAdjacentTo(Position position) {
        if (position == null || (!player.getPosition().equals(position) && !isOrthogonallyAdjacent(player.getPosition(), position))) {
            throw new InvalidActionException("Player is not adjacent to the target");
        }
    }

    private Position findCurrentOrAdjacentItemPosition() {
        return findCurrentOrAdjacentCellOfType(CellType.ITEM);
    }

    private Position findAdjacentEnemyPosition() {
        Position current = player.getPosition();
        Position up = createPositionIfInside(getCurrentRoom(), current.getRow() - 1, current.getColumn());
        if (up != null && getCurrentRoom().findEnemyAt(up) != null) {
            return up;
        }
        Position down = createPositionIfInside(getCurrentRoom(), current.getRow() + 1, current.getColumn());
        if (down != null && getCurrentRoom().findEnemyAt(down) != null) {
            return down;
        }
        Position left = createPositionIfInside(getCurrentRoom(), current.getRow(), current.getColumn() - 1);
        if (left != null && getCurrentRoom().findEnemyAt(left) != null) {
            return left;
        }
        Position right = createPositionIfInside(getCurrentRoom(), current.getRow(), current.getColumn() + 1);
        if (right != null && getCurrentRoom().findEnemyAt(right) != null) {
            return right;
        }
        return null;
    }

    private void grantEnemyDrop(String droppedItemName) {
        if (droppedItemName == null || droppedItemName.isBlank()) {
            return;
        }
        if ("Traje con escudo".equals(droppedItemName)) {
            Item drop = new Armor(CasinoMapBuilder.SHIELD_SUIT_ID, "Traje con escudo", 4);
            inventory.addItem(drop);
            log.add("Objeto obtenido por drop: " + drop.getName());
        }
    }

    private void processEnemyPhase() {
        if (!turnManager.startEnemyPhase(player)) {
            return;
        }
        Room room = getCurrentRoom();
        for (int i = 0; i < room.enemyCount(); i++) {
            Enemy enemy = room.getEnemy(i);
            if (enemy.isAlive()) {
                processEnemyAction(room, enemy);
                if (getState() == GameState.DEFEAT || !player.isAlive()) {
                    break;
                }
            }
        }
        turnManager.finishEnemyPhase(player);
        if (getState() == GameState.DEFEAT && !player.isAlive()) {
            log.add("Derrota por combate enemigo");
        }
    }

    private void processEnemyAction(Room room, Enemy enemy) {
        if (combatService.areAdjacent(enemy.getPosition(), player.getPosition())) {
            CombatResult result = combatService.enemyAttacksPlayer(enemy, player, Math.random());
            log.add(enemy.getName() + " ataca al jugador: " + result.getDamageDealt() + " de dano");
            turnManager.checkDefeatByHealth(player);
            return;
        }

        Position destination = enemyMovementService.findNextStepTowards(room, enemy, player.getPosition());
        if (destination != null) {
            room.moveEnemy(enemy, destination);
            log.add(enemy.getName() + " se mueve a " + destination.getRow() + "," + destination.getColumn());
        } else {
            log.add(enemy.getName() + " no puede acercarse al jugador");
        }
    }

    private boolean isOrthogonallyAdjacent(Position first, Position second) {
        int rowDistance = absolute(first.getRow() - second.getRow());
        int columnDistance = absolute(first.getColumn() - second.getColumn());
        return rowDistance + columnDistance == 1;
    }

    private int calculateDangerousCompanionDamage() {
        int damage = player.getMaxHealth() * dangerousCompanion.getDamagePercent() / 100;
        return damage <= 0 ? 1 : damage;
    }

    private int absolute(int value) {
        return value < 0 ? -value : value;
    }

    private void validateRandomValue(double randomValue) {
        if (randomValue < 0.0 || randomValue > 1.0) {
            throw new IllegalArgumentException("Random value must be between 0 and 1");
        }
    }

    private static Npc createWelcomeNpc() {
        return new Npc(Npc.WELCOME_NPC_ID, "Recepcionista del casino", 1,
                CasinoMapBuilder.WELCOME_NPC_POSITION, WELCOME_MESSAGE);
    }

    private static Npc createBarSpecialNpc() {
        return new Npc(Npc.BAR_SPECIAL_NPC_ID, "Cliente sospechoso", 5,
                CasinoMapBuilder.BAR_SPECIAL_NPC_POSITION, "Toma esto, pero no preguntes que lleva.");
    }

    private static Npc createFriendNpc() {
        return new Npc(Npc.FRIEND_NPC_ID, "Amigo borracho", 6,
                CasinoMapBuilder.FRIEND_POSITION, FRIEND_RESCUED_MESSAGE);
    }

    private static Trap createDangerousCompanion() {
        return new Trap(Trap.DANGEROUS_COMPANION_ID, "Acompanante peligrosa", 6,
                CasinoMapBuilder.DANGEROUS_COMPANION_POSITION, DANGEROUS_COMPANION_DAMAGE_PERCENT);
    }
}
