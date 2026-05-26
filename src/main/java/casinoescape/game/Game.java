package casinoescape.game;

import casinoescape.items.Inventory;
import casinoescape.items.Item;
import casinoescape.items.Consumable;
import casinoescape.items.Effect;
import casinoescape.items.EffectType;
import casinoescape.items.Shop;
import casinoescape.logging.GameLog;
import casinoescape.model.CasinoMap;
import casinoescape.model.CellType;
import casinoescape.model.GameState;
import casinoescape.model.Npc;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.model.Trap;
import casinoescape.movement.MovementService;
import casinoescape.movement.PathFinder;
import casinoescape.movement.ShortestPathInfo;

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
    private final PathFinder pathFinder;
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
        this.pathFinder = pathFinder;
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
            throw new IllegalStateException("Movement is not available in this turn");
        }
        if (!movementService.canMove(currentRoom, player, destination)) {
            throw new IllegalArgumentException("Destination is not reachable");
        }

        movementService.movePlayer(currentRoom, player, destination);
        turnManager.registerMovement();
        log.add("Movimiento del jugador a " + destination.getRow() + "," + destination.getColumn());
    }

    public Item buyFromBar(String shopItemId) {
        if (!turnManager.canAct()) {
            throw new IllegalStateException("Action is not available in this turn");
        }

        Item item = barShop.buy(shopItemId, player, inventory, log);
        turnManager.registerAction();
        return item;
    }

    public boolean canUseDoorTo(int destinationRoomId) {
        return map.canTransition(player.getCurrentRoomId(), destinationRoomId, inventory.hasTreasuryKey());
    }

    public ShortestPathInfo getShortestPathInfo() {
        return pathFinder.calculatePathToExit(map, player, inventory.hasTreasuryKey());
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
        applyDangerousCompanionEffectIfInRange();
        if (getState() == GameState.IN_PROGRESS) {
            inventory.decreaseTemporaryEffects(player);
            turnManager.endTurn(player);
        }
    }

    public void useDoorTo(int destinationRoomId) {
        if (!turnManager.canAct()) {
            throw new IllegalStateException("Action is not available in this turn");
        }
        int originRoomId = player.getCurrentRoomId();
        if (!canUseDoorTo(destinationRoomId)) {
            log.add("Intento de abrir puerta bloqueada o invalida desde sala " + originRoomId + " a sala " + destinationRoomId);
            throw new IllegalStateException("Door cannot be used");
        }

        Room originRoom = getCurrentRoom();
        originRoom.setCellType(player.getPosition(), CellType.EMPTY);
        Position entryPosition = findEntryPosition(destinationRoomId, originRoomId);
        player.setCurrentRoomId(destinationRoomId);
        player.setPosition(entryPosition);
        map.getRoom(destinationRoomId).setCellType(entryPosition, CellType.PLAYER);

        turnManager.registerAction();
        log.add("Cambio de sala " + originRoomId + " -> " + destinationRoomId);
        turnManager.finishTurnAfterRoomChange(player);
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
            throw new IllegalStateException("Action is not available in this turn");
        }
    }

    private void requirePlayerInRoom(int roomId) {
        if (player.getCurrentRoomId() != roomId) {
            throw new IllegalStateException("Player is not in the required room");
        }
    }

    private void requireAdjacentTo(Position position) {
        if (!isOrthogonallyAdjacent(player.getPosition(), position)) {
            throw new IllegalStateException("Player is not adjacent to the target");
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
