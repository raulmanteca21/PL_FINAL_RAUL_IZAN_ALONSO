package casinoescape.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyQueueTest {
    @Test
    void newQueueIsEmpty() {
        MyQueue<String> queue = new MyQueue<>();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void dequeueRespectsFifoOrder() {
        MyQueue<String> queue = new MyQueue<>();

        queue.enqueue("one");
        queue.enqueue("two");

        assertEquals("one", queue.dequeue());
        assertEquals("two", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekDoesNotRemoveElement() {
        MyQueue<String> queue = new MyQueue<>();
        queue.enqueue("one");

        assertEquals("one", queue.peek());
        assertEquals(1, queue.size());
    }

    @Test
    void queueCanBeUsedAfterBeingEmptied() {
        MyQueue<String> queue = new MyQueue<>();
        queue.enqueue("one");
        queue.dequeue();

        queue.enqueue("two");

        assertEquals("two", queue.dequeue());
    }

    @Test
    void clearEmptiesQueue() {
        MyQueue<String> queue = new MyQueue<>();
        queue.enqueue("one");

        queue.clear();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void emptyQueueOperationsThrowException() {
        MyQueue<String> queue = new MyQueue<>();

        assertThrows(IllegalStateException.class, queue::dequeue);
        assertThrows(IllegalStateException.class, queue::peek);
    }

    @Test
    void nullValuesAreRejected() {
        MyQueue<String> queue = new MyQueue<>();

        assertThrows(IllegalArgumentException.class, () -> queue.enqueue(null));
    }
}
