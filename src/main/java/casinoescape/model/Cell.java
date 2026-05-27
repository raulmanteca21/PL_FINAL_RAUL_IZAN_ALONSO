package casinoescape.model;

public class Cell {
    private CellType type;
    private String label;
    private Door door;

    public Cell(CellType type) {
        this(type, "");
    }

    public Cell(CellType type, String label) {
        setType(type);
        this.label = label == null ? "" : label;
    }

    public Cell(Door door, String label) {
        if (door == null) {
            throw new IllegalArgumentException("Door is required");
        }
        this.type = CellType.DOOR;
        this.label = label == null ? "" : label;
        this.door = door;
    }

    public static Cell empty() {
        return new Cell(CellType.EMPTY);
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        if (type == null) {
            throw new IllegalArgumentException("Cell type is required");
        }
        this.type = type;
        this.label = "";
        if (type != CellType.DOOR) {
            this.door = null;
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label == null ? "" : label;
    }

    public Door getDoor() {
        return door;
    }

    public boolean isWalkable() {
        return type == CellType.EMPTY
                || type == CellType.TRAP;
    }

    public boolean isInteractive() {
        return type == CellType.ITEM
                || type == CellType.DOOR
                || type == CellType.NPC
                || type == CellType.SHOP
                || type == CellType.EXIT
                || type == CellType.MINIGAME;
    }
}
