package casinoescape.game;

public class RouletteResult {
    private final boolean played;
    private final boolean favorable;
    private final boolean lethal;
    private final int chipsAwarded;
    private final int damageTaken;
    private final String message;

    public RouletteResult(boolean played, boolean favorable, boolean lethal, int chipsAwarded, int damageTaken, String message) {
        if (chipsAwarded < 0) {
            throw new IllegalArgumentException("Awarded chips cannot be negative");
        }
        if (damageTaken < 0) {
            throw new IllegalArgumentException("Damage taken cannot be negative");
        }
        this.played = played;
        this.favorable = favorable;
        this.lethal = lethal;
        this.chipsAwarded = chipsAwarded;
        this.damageTaken = damageTaken;
        this.message = message == null ? "" : message;
    }

    public boolean wasPlayed() {
        return played;
    }

    public boolean isFavorable() {
        return favorable;
    }

    public boolean isLethal() {
        return lethal;
    }

    public int getChipsAwarded() {
        return chipsAwarded;
    }

    public int getDamageTaken() {
        return damageTaken;
    }

    public String getMessage() {
        return message;
    }
}
