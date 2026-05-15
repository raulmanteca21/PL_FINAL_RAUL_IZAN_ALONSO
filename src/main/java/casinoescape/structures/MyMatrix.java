package casinoescape.structures;

public class MyMatrix<T> {
    private final Object[][] values;
    private final int rows;
    private final int columns;

    public MyMatrix(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive");
        }
        this.rows = rows;
        this.columns = columns;
        this.values = new Object[rows][columns];
    }

    @SuppressWarnings("unchecked")
    public T get(int row, int column) {
        checkPosition(row, column);
        return (T) values[row][column];
    }

    public void set(int row, int column, T value) {
        checkPosition(row, column);
        values[row][column] = value;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public boolean isInside(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    public void fill(T value) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                values[row][column] = value;
            }
        }
    }

    private void checkPosition(int row, int column) {
        if (!isInside(row, column)) {
            throw new IndexOutOfBoundsException("Position out of matrix: " + row + ", " + column);
        }
    }
}
