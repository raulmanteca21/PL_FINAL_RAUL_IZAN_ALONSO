package casinoescape.model;

public class Cell {
    private CellType type;
    private String label;

    public Cell(CellType type) {
        this(type, "");
    }

    public Cell(CellType type, String label) {
        setType(type);
        this.label = label == null ? "" : label;
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
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label == null ? "" : label;
    }

    public boolean isWalkable() {
        return type == CellType.EMPTY
                || type == CellType.ITEM
                || type == CellType.DOOR
                || type == CellType.TRAP
                || type == CellType.SHOP
                || type == CellType.EXIT
                || type == CellType.MINIGAME;
    }

    public boolean isInteractive() {
        return type == CellType.ITEM
                || type == CellType.DOOR
                || type == CellType.NPC
                || type == CellType.TRAP
                || type == CellType.SHOP
                || type == CellType.EXIT
                || type == CellType.MINIGAME;
    }
}
