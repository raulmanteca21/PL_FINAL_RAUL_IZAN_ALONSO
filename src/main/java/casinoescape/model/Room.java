package casinoescape.model;

import casinoescape.structures.MyMatrix;

public class Room {
    private final int id;
    private final String name;
    private final MyMatrix<Cell> cells;

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
}
