package casinoescape.game;

import casinoescape.items.Inventory;
import casinoescape.items.Item;
import casinoescape.items.Shop;
import casinoescape.logging.GameLog;
import casinoescape.model.CasinoMap;
import casinoescape.model.CellType;
import casinoescape.model.GameState;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.movement.MovementService;

public class Game {
    private final CasinoMap map;
    private final Player player;
    private final Inventory inventory;
    private final TurnManager turnManager;
    private final MovementService movementService;
    private final Shop barShop;
    private final GameLog log;

    public static Game createNewGame(int turnsRemaining) {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();
        Player player = new Player(100, 10, 5, 3, map.getInitialRoomId(), map.getInitialPlayerPosition());
        return new Game(
                map,
                player,
                new Inventory(),
                new TurnManager(turnsRemaining),
                new MovementService(),
                Shop.createDefaultBarShop(),
                new GameLog());
    }

    public Game(CasinoMap map, Player player, Inventory inventory, TurnManager turnManager,
            MovementService movementService, Shop barShop, GameLog log) {
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
        if (barShop == null) {
            throw new IllegalArgumentException("Bar shop is required");
        }
        if (log == null) {
            throw new IllegalArgumentException("Game log is required");
        }
        this.map = map;
        this.player = player;
        this.inventory = inventory;
        this.turnManager = turnManager;
        this.movementService = movementService;
        this.barShop = barShop;
        this.log = log;
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
}
