package casinoescape.movement;

import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.structures.MyLinkedList;
import casinoescape.structures.MyMatrix;
import casinoescape.structures.MyQueue;

public class ReachableCellsCalculator {
    private static final Direction[] DIRECTIONS = {
            Direction.UP,
            Direction.DOWN,
            Direction.LEFT,
            Direction.RIGHT
    };

    public MyLinkedList<Position> calculate(Room room, Position start, int movementPoints) {
        validateInput(room, start, movementPoints);

        MyLinkedList<Position> reachable = new MyLinkedList<>();
        MyMatrix<Boolean> visited = new MyMatrix<>(room.getRows(), room.getColumns());
        MyQueue<SearchNode> pending = new MyQueue<>();

        visited.set(start.getRow(), start.getColumn(), Boolean.TRUE);
        pending.enqueue(new SearchNode(start, 0));

        while (!pending.isEmpty()) {
            SearchNode current = pending.dequeue();
            if (current.distance == movementPoints) {
                continue;
            }

            for (int i = 0; i < DIRECTIONS.length; i++) {
                Direction direction = DIRECTIONS[i];
                int nextRow = current.position.getRow() + direction.getRowDelta();
                int nextColumn = current.position.getColumn() + direction.getColumnDelta();
                if (!isInside(room, nextRow, nextColumn)) {
                    continue;
                }
                Position next = new Position(nextRow, nextColumn);
                if (canVisit(room, visited, next)) {
                    visited.set(next.getRow(), next.getColumn(), Boolean.TRUE);
                    reachable.add(next);
                    pending.enqueue(new SearchNode(next, current.distance + 1));
                }
            }
        }

        return reachable;
    }

    public boolean isReachable(Room room, Position start, Position destination, int movementPoints) {
        if (destination == null) {
            return false;
        }
        MyLinkedList<Position> reachable = calculate(room, start, movementPoints);
        return reachable.contains(destination);
    }

    private boolean canVisit(Room room, MyMatrix<Boolean> visited, Position position) {
        return room.isInside(position)
                && !Boolean.TRUE.equals(visited.get(position.getRow(), position.getColumn()))
                && room.isWalkable(position);
    }

    private boolean isInside(Room room, int row, int column) {
        return row >= 0 && row < room.getRows() && column >= 0 && column < room.getColumns();
    }

    private void validateInput(Room room, Position start, int movementPoints) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required");
        }
        if (start == null) {
            throw new IllegalArgumentException("Start position is required");
        }
        if (movementPoints < 0) {
            throw new IllegalArgumentException("Movement points cannot be negative");
        }
        if (!room.isInside(start)) {
            throw new IllegalArgumentException("Start position is outside room");
        }
    }

    private static class SearchNode {
        private final Position position;
        private final int distance;

        private SearchNode(Position position, int distance) {
            this.position = position;
            this.distance = distance;
        }
    }

}
