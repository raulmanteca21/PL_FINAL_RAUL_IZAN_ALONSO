package casinoescape.structures;

public class MyQueue<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    public void enqueue(T value) {
        requireValue(value);
        Node<T> newNode = new Node<>(value);
        if (isEmpty()) {
            front = newNode;
        } else {
            rear.next = newNode;
        }
        rear = newNode;
        size++;
    }

    public T dequeue() {
        ensureNotEmpty();
        T value = front.value;
        front = front.next;
        size--;
        if (size == 0) {
            rear = null;
        }
        return value;
    }

    public T peek() {
        ensureNotEmpty();
        return front.value;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
    }

    private void requireValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Null values are not supported");
        }
    }

    private static class Node<T> {
        private final T value;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }
    }
}
