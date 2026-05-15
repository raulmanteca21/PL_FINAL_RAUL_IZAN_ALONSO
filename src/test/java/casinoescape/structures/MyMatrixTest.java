package casinoescape.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyMatrixTest {
    @Test
    void matrixStoresDimensions() {
        MyMatrix<String> matrix = new MyMatrix<>(2, 3);

        assertEquals(2, matrix.getRows());
        assertEquals(3, matrix.getColumns());
    }

    @Test
    void setAndGetWork() {
        MyMatrix<String> matrix = new MyMatrix<>(2, 2);

        matrix.set(1, 1, "cell");

        assertEquals("cell", matrix.get(1, 1));
    }

    @Test
    void matrixCellsStartAsNull() {
        MyMatrix<String> matrix = new MyMatrix<>(2, 2);

        assertNull(matrix.get(0, 0));
    }

    @Test
    void isInsideDetectsValidAndInvalidPositions() {
        MyMatrix<String> matrix = new MyMatrix<>(2, 3);

        assertTrue(matrix.isInside(0, 0));
        assertTrue(matrix.isInside(1, 2));
        assertFalse(matrix.isInside(-1, 0));
        assertFalse(matrix.isInside(2, 0));
        assertFalse(matrix.isInside(0, 3));
    }

    @Test
    void fillSetsAllCells() {
        MyMatrix<String> matrix = new MyMatrix<>(2, 2);

        matrix.fill("empty");

        assertEquals("empty", matrix.get(0, 0));
        assertEquals("empty", matrix.get(0, 1));
        assertEquals("empty", matrix.get(1, 0));
        assertEquals("empty", matrix.get(1, 1));
    }

    @Test
    void invalidDimensionsThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new MyMatrix<String>(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new MyMatrix<String>(1, 0));
    }

    @Test
    void accessOutsideMatrixThrowsException() {
        MyMatrix<String> matrix = new MyMatrix<>(2, 2);

        assertThrows(IndexOutOfBoundsException.class, () -> matrix.get(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.set(2, 0, "x"));
    }
}
