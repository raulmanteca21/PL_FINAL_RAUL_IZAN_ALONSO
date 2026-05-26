package casinoescape.model;

import casinoescape.items.Item;
import casinoescape.structures.MyLinkedList;
import casinoescape.structures.MyMatrix;

public class Room {
    private final int id;
    private final String name;
    private final MyMatrix<Cell> cells;
    private final MyLinkedList<Enemy> enemies = new MyLinkedList<>();
    private final MyLinkedList<RoomItem> items = new MyLinkedList<>();

    public Room(int id, String name, int rows, int columns) {
        if (id <= 0) {
            throw new IllegalArgumentException("Room id must be positive");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Room name is required");
        }
        this.id = id;
        this.name = name;
        this.cells = new MyMatrix<>(rows, columns);
        fillWithEmptyCells();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return cells.getRows();
    }

    public int getColumns() {
        return cells.getColumns();
    }

    public Cell getCell(Position position) {
        requirePosition(position);
        return cells.get(position.getRow(), position.getColumn());
    }

    public void setCell(Position position, Cell cell) {
        requirePosition(position);
        if (cell == null) {
            throw new IllegalArgumentException("Cell is required");
        }
        cells.set(position.getRow(), position.getColumn(), cell);
    }

    public void setCellType(Position position, CellType type) {
        getCell(position).setType(type);
    }

    public boolean isInside(Position position) {
        return position != null && cells.isInside(position.getRow(), position.getColumn());
    }

    public boolean isWalkable(Position position) {
        return isInside(position) && getCell(position).isWalkable();
    }

    public void addEnemy(Enemy enemy) {
        if (enemy == null) {
            throw new IllegalArgumentException("Enemy is required");
        }
        requirePosition(enemy.getPosition());
        requireEmptyCell(enemy.getPosition());
        enemies.add(enemy);
        setCell(enemy.getPosition(), new Cell(CellType.ENEMY, enemy.getName()));
    }

    public Enemy findEnemyAt(Position position) {
        if (!isInside(position)) {
            return null;
        }
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.isAlive() && enemy.getPosition().equals(position)) {
                return enemy;
            }
        }
        return null;
    }

    public Enemy findEnemyById(String enemyId) {
        if (enemyId == null || enemyId.isBlank()) {
            return null;
        }
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.getId().equals(enemyId)) {
                return enemy;
            }
        }
        return null;
    }

    public boolean removeEnemy(Enemy enemy) {
        if (enemy == null) {
            return false;
        }
        boolean removed = enemies.remove(enemy);
        if (removed && isInside(enemy.getPosition())) {
            setCellType(enemy.getPosition(), CellType.EMPTY);
        }
        return removed;
    }

    public void moveEnemy(Enemy enemy, Position destination) {
        if (enemy == null) {
            throw new IllegalArgumentException("Enemy is required");
        }
        requirePosition(destination);
        requireEmptyCell(destination);
        Position origin = enemy.getPosition();
        if (isInside(origin) && getCell(origin).getType() == CellType.ENEMY) {
            setCellType(origin, CellType.EMPTY);
        }
        enemy.setPosition(destination);
        setCell(destination, new Cell(CellType.ENEMY, enemy.getName()));
    }

    public int enemyCount() {
        return enemies.size();
    }

    public Enemy getEnemy(int index) {
        return enemies.get(index);
    }

    public void addItem(Item item, Position position) {
        if (item == null) {
            throw new IllegalArgumentException("Item is required");
        }
        requirePosition(position);
        requireEmptyCell(position);
        items.add(new RoomItem(item, position));
        setCell(position, new Cell(CellType.ITEM, item.getName()));
    }

    public Item findItemAt(Position position) {
        RoomItem roomItem = findRoomItemAt(position);
        return roomItem == null ? null : roomItem.getItem();
    }

    public Position findItemPosition(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return null;
        }
        for (int i = 0; i < items.size(); i++) {
            RoomItem roomItem = items.get(i);
            if (roomItem.getItem().getId().equals(itemId)) {
                return roomItem.getPosition();
            }
        }
        return null;
    }

    public Item removeItemAt(Position position) {
        RoomItem roomItem = findRoomItemAt(position);
        if (roomItem == null) {
            return null;
        }
        items.remove(roomItem);
        setCellType(position, CellType.EMPTY);
        return roomItem.getItem();
    }

    public boolean removeItemById(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return false;
        }
        for (int i = 0; i < items.size(); i++) {
            RoomItem roomItem = items.get(i);
            if (roomItem.getItem().getId().equals(itemId)) {
                items.removeAt(i);
                setCellType(roomItem.getPosition(), CellType.EMPTY);
                return true;
            }
        }
        return false;
    }

    public int itemCount() {
        return items.size();
    }

    public RoomItem getRoomItem(int index) {
        return items.get(index);
    }

    private RoomItem findRoomItemAt(Position position) {
        if (!isInside(position)) {
            return null;
        }
        for (int i = 0; i < items.size(); i++) {
            RoomItem roomItem = items.get(i);
            if (roomItem.getPosition().equals(position)) {
                return roomItem;
            }
        }
        return null;
    }

    private void fillWithEmptyCells() {
        for (int row = 0; row < cells.getRows(); row++) {
            for (int column = 0; column < cells.getColumns(); column++) {
                cells.set(row, column, Cell.empty());
            }
        }
    }

    private void requirePosition(Position position) {
        if (!isInside(position)) {
            throw new IndexOutOfBoundsException("Position is outside room");
        }
    }

    private void requireEmptyCell(Position position) {
        if (getCell(position).getType() != CellType.EMPTY) {
            throw new IllegalStateException("Room position is already occupied");
        }
    }
}
