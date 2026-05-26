package casinoescape.model;

public class Enemy {
    private final String id;
    private final String name;
    private final int maxHealth;
    private int currentHealth;
    private final int attack;
    private final int defense;
    private Position position;
    private final int chipReward;
    private final String dropName;
    private boolean rewardClaimed;

    public Enemy(String id, String name, int maxHealth, int attack, int defense, Position position, int chipReward, String dropName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Enemy id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Enemy name is required");
        }
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }
        if (attack < 0 || defense < 0 || chipReward < 0) {
            throw new IllegalArgumentException("Enemy stats are invalid");
        }
        if (position == null) {
            throw new IllegalArgumentException("Enemy position is required");
        }
        this.id = id;
        this.name = name;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.attack = attack;
        this.defense = defense;
        this.position = position;
        this.chipReward = chipReward;
        this.dropName = dropName == null ? "" : dropName;
    }

    public Enemy(String name, int maxHealth, int attack, int defense, Position position, int chipReward, String dropName) {
        this(name, name, maxHealth, attack, defense, position, chipReward, dropName);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public int getDefense() {
        return defense;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Enemy position is required");
        }
        this.position = position;
    }

    public int getChipReward() {
        return chipReward;
    }

    public String getDropName() {
        return dropName;
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public boolean isRewardClaimed() {
        return rewardClaimed;
    }

    public void markRewardClaimed() {
        rewardClaimed = true;
    }
}
