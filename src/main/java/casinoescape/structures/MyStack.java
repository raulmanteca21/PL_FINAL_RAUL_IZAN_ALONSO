package casinoescape.structures;

public class MyStack<T> {
    private Node<T> top;
    private int size;

    public void push(T value) {
        requireValue(value);
        Node<T> newNode = new Node<>(value);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public T pop() {
        ensureNotEmpty();
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    public T peek() {
        ensureNotEmpty();
        return top.value;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    private void ensureNotEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException("Structure is empty");
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
