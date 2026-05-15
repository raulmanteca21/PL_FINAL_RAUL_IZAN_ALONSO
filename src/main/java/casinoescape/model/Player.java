package casinoescape.model;

public class Player {
    private final int maxHealth;
    private int currentHealth;
    private int attack;
    private int defense;
    private int movementPoints;
    private int chips;
    private int currentRoomId;
    private Position position;
    private boolean friendRescued;

    public Player(int maxHealth, int attack, int defense, int movementPoints, int currentRoomId, Position position) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }
        if (attack < 0 || defense < 0 || movementPoints <= 0) {
            throw new IllegalArgumentException("Player stats are invalid");
        }
        if (currentRoomId <= 0) {
            throw new IllegalArgumentException("Current room id must be positive");
        }
        if (position == null) {
            throw new IllegalArgumentException("Player position is required");
        }
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.attack = attack;
        this.defense = defense;
        this.movementPoints = movementPoints;
        this.currentRoomId = currentRoomId;
        this.position = position;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        if (currentHealth < 0) {
            this.currentHealth = 0;
        } else if (currentHealth > maxHealth) {
            this.currentHealth = maxHealth;
        } else {
            this.currentHealth = currentHealth;
        }
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        if (attack < 0) {
            throw new IllegalArgumentException("Attack cannot be negative");
        }
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        if (defense < 0) {
            throw new IllegalArgumentException("Defense cannot be negative");
        }
        this.defense = defense;
    }

    public int getMovementPoints() {
        return movementPoints;
    }

    public void setMovementPoints(int movementPoints) {
        if (movementPoints <= 0) {
            throw new IllegalArgumentException("Movement points must be positive");
        }
        this.movementPoints = movementPoints;
    }

    public int getChips() {
        return chips;
    }

    public void addChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add a negative chip amount");
        }
        chips += amount;
    }

    public void spendChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot spend a negative chip amount");
        }
        if (amount > chips) {
            throw new IllegalArgumentException("Not enough chips");
        }
        chips -= amount;
    }

    public int getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(int currentRoomId) {
        if (currentRoomId <= 0) {
            throw new IllegalArgumentException("Current room id must be positive");
        }
        this.currentRoomId = currentRoomId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Player position is required");
        }
        this.position = position;
    }

    public boolean isFriendRescued() {
        return friendRescued;
    }

    public void rescueFriend() {
        friendRescued = true;
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }
}
