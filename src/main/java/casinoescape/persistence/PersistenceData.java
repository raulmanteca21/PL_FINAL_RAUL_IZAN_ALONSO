package casinoescape.persistence;

class PersistenceData {
    static class GameConfigData {
        int version;
        int initialTurns;
        int initialRoomId;
        PositionData initialPlayerPosition;
        RoomConfigData[] rooms;
        ConnectionData[] connections;
        ShopItemConfigData[] shop;
        VictoryData victory;
    }

    static class RoomConfigData {
        int id;
        String name;
        int rows;
        int columns;
        CellConfigData[] cells;
        RoomItemConfigData[] items;
        EnemyConfigData[] enemies;
    }

    static class RoomItemConfigData extends ItemData {
        int row;
        int column;
    }

    static class EnemyConfigData {
        String id;
        String name;
        int maxHealth;
        int attack;
        int defense;
        int row;
        int column;
        int chipReward;
        String dropName;
    }

    static class ShopItemConfigData extends ItemData {
        String shopItemId;
        int price;
    }

    static class CellConfigData {
        int row;
        int column;
        String type;
        String label;
        int destinationRoomId;
        boolean locked;
        String requiredKeyName;
    }

    static class ConnectionData {
        int from;
        int to;
    }

    static class VictoryData {
        int exitRoomId;
        boolean requiresFriendRescued;
    }

    static class SaveGameData {
        int version;
        String state;
        PlayerData player;
        TurnData turn;
        InventoryData inventory;
        InteractivesData interactives;
        EnemySaveData[] enemies;
        String[] collectedObjectIds;
        DoorSaveData[] doors;
        boolean treasuryKeyBought;
        LogEntryData[] log;
    }

    static class EnemySaveData {
        String id;
        int roomId;
        int row;
        int column;
        int currentHealth;
        boolean alive;
        boolean rewardClaimed;
    }

    static class DoorSaveData {
        int fromRoomId;
        int toRoomId;
        boolean locked;
    }

    static class PlayerData {
        int currentRoomId;
        int row;
        int column;
        int maxHealth;
        int currentHealth;
        int attack;
        int defense;
        int movementPoints;
        int chips;
        boolean friendRescued;
    }

    static class TurnData {
        int turnsRemaining;
        boolean movementUsed;
        boolean actionUsed;
        boolean enemyPhaseProcessedLastTurn;
        String phase;
    }

    static class InventoryData {
        ItemData[] items;
        String equippedWeaponId;
        String equippedArmorId;
        EffectData[] activeEffects;
    }

    static class ItemData {
        String id;
        String name;
        String type;
        int attackBonus;
        int defenseBonus;
        EffectData effect;
    }

    static class EffectData {
        String type;
        int amount;
        int remainingTurns;
    }

    static class InteractivesData {
        boolean welcomeNpcInteracted;
        boolean barSpecialNpcInteracted;
        boolean friendNpcInteracted;
    }

    static class LogEntryData {
        int turn;
        String message;
    }

    static class PositionData {
        int row;
        int column;
    }
}
