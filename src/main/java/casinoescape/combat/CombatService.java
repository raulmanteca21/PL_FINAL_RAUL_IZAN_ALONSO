package casinoescape.combat;

import casinoescape.model.Enemy;
import casinoescape.model.Player;
import casinoescape.model.Position;

public class CombatService {
    private final DamageCalculator damageCalculator;

    public CombatService() {
        this(new DamageCalculator());
    }

    public CombatService(DamageCalculator damageCalculator) {
        if (damageCalculator == null) {
            throw new IllegalArgumentException("Damage calculator is required");
        }
        this.damageCalculator = damageCalculator;
    }

    public CombatResult playerAttacksEnemy(Player player, Enemy enemy, double randomValue) {
        requirePlayer(player);
        requireEnemy(enemy);
        ensureAdjacent(player.getPosition(), enemy.getPosition());
        ensureEnemyAlive(enemy);

        int damage = damageCalculator.calculateDamage(player.getAttack(), enemy.getDefense(), randomValue);
        enemy.setCurrentHealth(enemy.getCurrentHealth() - damage);

        int chipsAwarded = 0;
        String droppedItemName = "";
        boolean enemyDied = !enemy.isAlive();
        if (enemyDied && !enemy.isRewardClaimed()) {
            chipsAwarded = enemy.getChipReward();
            droppedItemName = enemy.getDropName();
            player.addChips(chipsAwarded);
            enemy.markRewardClaimed();
        }

        return new CombatResult(damage, enemyDied, chipsAwarded, droppedItemName);
    }

    public CombatResult enemyAttacksPlayer(Enemy enemy, Player player, double randomValue) {
        requireEnemy(enemy);
        requirePlayer(player);
        ensureAdjacent(enemy.getPosition(), player.getPosition());
        ensureEnemyAlive(enemy);

        int damage = damageCalculator.calculateDamage(enemy.getAttack(), player.getDefense(), randomValue);
        player.setCurrentHealth(player.getCurrentHealth() - damage);
        return new CombatResult(damage, !player.isAlive(), 0, "");
    }

    public boolean areAdjacent(Position first, Position second) {
        if (first == null || second == null) {
            return false;
        }
        int rowDistance = absolute(first.getRow() - second.getRow());
        int columnDistance = absolute(first.getColumn() - second.getColumn());
        return rowDistance + columnDistance == 1;
    }

    private void ensureAdjacent(Position first, Position second) {
        if (!areAdjacent(first, second)) {
            throw new IllegalArgumentException("Target is not orthogonally adjacent");
        }
    }

    private int absolute(int value) {
        return value < 0 ? -value : value;
    }

    private void requirePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player is required");
        }
    }

    private void requireEnemy(Enemy enemy) {
        if (enemy == null) {
            throw new IllegalArgumentException("Enemy is required");
        }
    }

    private void ensureEnemyAlive(Enemy enemy) {
        if (!enemy.isAlive()) {
            throw new IllegalStateException("Enemy is already dead");
        }
    }
}
