package casinoescape.logging;

import casinoescape.structures.MyLinkedList;

public class GameLog {
    private final MyLinkedList<LogEntry> entries = new MyLinkedList<>();

    public void add(String message) {
        entries.add(new LogEntry(message));
    }

    public void add(int turn, String message) {
        entries.add(new LogEntry(turn, message));
    }

    public LogEntry getEntry(int index) {
        return entries.get(index);
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void clear() {
        entries.clear();
    }
}
