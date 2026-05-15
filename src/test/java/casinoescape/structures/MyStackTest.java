package casinoescape.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyStackTest {
    @Test
    void newStackIsEmpty() {
        MyStack<String> stack = new MyStack<>();

        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void popRespectsLifoOrder() {
        MyStack<String> stack = new MyStack<>();

        stack.push("one");
        stack.push("two");

        assertEquals("two", stack.pop());
        assertEquals("one", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void peekDoesNotRemoveElement() {
        MyStack<String> stack = new MyStack<>();
        stack.push("one");

        assertEquals("one", stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    void clearEmptiesStack() {
        MyStack<String> stack = new MyStack<>();
        stack.push("one");

        stack.clear();

        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void emptyStackOperationsThrowException() {
        MyStack<String> stack = new MyStack<>();

        assertThrows(IllegalStateException.class, stack::pop);
        assertThrows(IllegalStateException.class, stack::peek);
    }

    @Test
    void nullValuesAreRejected() {
        MyStack<String> stack = new MyStack<>();

        assertThrows(IllegalArgumentException.class, () -> stack.push(null));
    }
}
