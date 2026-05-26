package casinoescape.movement;

import casinoescape.model.Enemy;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.structures.MyMatrix;
import casinoescape.structures.MyQueue;

public class EnemyMovementService {
    private static final Direction[] DIRECTIONS = {
            Direction.UP,
            Direction.DOWN,
            Direction.LEFT,
            Direction.RIGHT
    };

    public Position findNextStepTowards(Room room, Enemy enemy, Position target) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required");
        }
        if (enemy == null) {
            throw new IllegalArgumentException("Enemy is required");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target is required");
        }
        if (!room.isInside(enemy.getPosition()) || !room.isInside(target)) {
            throw new IllegalArgumentException("Positions must be inside room");
        }

        MyMatrix<Boolean> visited = new MyMatrix<>(room.getRows(), room.getColumns());
        MyQueue<SearchNode> pending = new MyQueue<>();
        visited.set(enemy.getPosition().getRow(), enemy.getPosition().getColumn(), Boolean.TRUE);

        enqueueInitialSteps(room, enemy.getPosition(), visited, pending);
        while (!pending.isEmpty()) {
            SearchNode current = pending.dequeue();
            if (isAdjacent(current.position, target)) {
                return current.firstStep;
            }
            enqueueNextSteps(room, current, visited, pending);
        }
        return null;
    }

    private void enqueueInitialSteps(Room room, Position start, MyMatrix<Boolean> visited, MyQueue<SearchNode> pending) {
        for (int i = 0; i < DIRECTIONS.length; i++) {
            Position next = nextPosition(room, start, DIRECTIONS[i]);
            if (canVisit(room, visited, next)) {
                visited.set(next.getRow(), next.getColumn(), Boolean.TRUE);
                pending.enqueue(new SearchNode(next, next));
            }
        }
    }

    private void enqueueNextSteps(Room room, SearchNode current, MyMatrix<Boolean> visited, MyQueue<SearchNode> pending) {
        for (int i = 0; i < DIRECTIONS.length; i++) {
            Position next = nextPosition(room, current.position, DIRECTIONS[i]);
            if (canVisit(room, visited, next)) {
                visited.set(next.getRow(), next.getColumn(), Boolean.TRUE);
                pending.enqueue(new SearchNode(next, current.firstStep));
            }
        }
    }

    private Position nextPosition(Room room, Position position, Direction direction) {
        int row = position.getRow() + direction.getRowDelta();
        int column = position.getColumn() + direction.getColumnDelta();
        if (row < 0 || row >= room.getRows() || column < 0 || column >= room.getColumns()) {
            return null;
        }
        return new Position(row, column);
    }

    private boolean canVisit(Room room, MyMatrix<Boolean> visited, Position position) {
        return position != null
                && room.isWalkable(position)
                && !Boolean.TRUE.equals(visited.get(position.getRow(), position.getColumn()));
    }

    private boolean isAdjacent(Position first, Position second) {
        int rowDistance = absolute(first.getRow() - second.getRow());
        int columnDistance = absolute(first.getColumn() - second.getColumn());
        return rowDistance + columnDistance == 1;
    }

    private int absolute(int value) {
        return value < 0 ? -value : value;
    }

    private static class SearchNode {
        private final Position position;
        private final Position firstStep;

        private SearchNode(Position position, Position firstStep) {
            this.position = position;
            this.firstStep = firstStep;
        }
    }
}
