package casinoescape.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyLinkedListTest {
    @Test
    void newListIsEmpty() {
        MyLinkedList<String> list = new MyLinkedList<>();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addIncrementsSizeAndPreservesOrder() {
        MyLinkedList<String> list = new MyLinkedList<>();

        list.add("one");
        list.add("two");

        assertEquals(2, list.size());
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
    }

    @Test
    void addFirstInsertsAtBeginning() {
        MyLinkedList<String> list = new MyLinkedList<>();

        list.add("two");
        list.addFirst("one");

        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
    }

    @Test
    void setReplacesValueAndReturnsOldValue() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("old");

        String oldValue = list.set(0, "new");

        assertEquals("old", oldValue);
        assertEquals("new", list.get(0));
    }

    @Test
    void removeByValueDeletesElement() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("one");
        list.add("two");
        list.add("three");

        assertTrue(list.remove("two"));

        assertEquals(2, list.size());
        assertEquals("one", list.get(0));
        assertEquals("three", list.get(1));
    }

    @Test
    void removeFirstAndLastKeepsListConsistent() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("one");
        list.add("two");
        list.add("three");

        assertTrue(list.remove("one"));
        assertTrue(list.remove("three"));

        list.add("four");
        assertEquals(2, list.size());
        assertEquals("two", list.get(0));
        assertEquals("four", list.get(1));
    }

    @Test
    void removeAtDeletesByIndex() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("one");
        list.add("two");

        String removed = list.removeAt(1);

        assertEquals("two", removed);
        assertEquals(1, list.size());
        assertEquals("one", list.get(0));
    }

    @Test
    void containsAndIndexOfFindValues() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("one");
        list.add("two");

        assertTrue(list.contains("two"));
        assertEquals(1, list.indexOf("two"));
        assertFalse(list.contains("three"));
        assertEquals(-1, list.indexOf("three"));
    }

    @Test
    void clearEmptiesList() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("one");

        list.clear();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void invalidIndexThrowsException() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("one");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
    }

    @Test
    void nullValuesAreRejected() {
        MyLinkedList<String> list = new MyLinkedList<>();

        assertThrows(IllegalArgumentException.class, () -> list.add(null));
        assertThrows(IllegalArgumentException.class, () -> list.addFirst(null));
    }
}
