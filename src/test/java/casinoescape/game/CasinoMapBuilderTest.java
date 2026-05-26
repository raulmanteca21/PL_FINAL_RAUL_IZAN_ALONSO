package casinoescape.game;

import casinoescape.model.CasinoMap;
import casinoescape.model.CellType;
import casinoescape.model.Door;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.structures.MyLinkedList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CasinoMapBuilderTest {
    @Test
    void mapContainsEightRoomsWithExpectedNamesAndDimensions() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertEquals(8, map.getRoomCount());
        assertRoom(map.getRoom(1), 1, "Hall / Entrada");
        assertRoom(map.getRoom(2), 2, "Tragaperras");
        assertRoom(map.getRoom(3), 3, "Tesoreria / Caja Fuerte");
        assertRoom(map.getRoom(4), 4, "Blackjack");
        assertRoom(map.getRoom(5), 5, "Bar");
        assertRoom(map.getRoom(6), 6, "Zona Privada");
        assertRoom(map.getRoom(7), 7, "Sala VIP");
        assertRoom(map.getRoom(8), 8, "Ruleta / Final");
    }

    @Test
    void graphContainsDefinitiveUndirectedConnections() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertConnectedBothWays(map, 1, 2);
        assertConnectedBothWays(map, 1, 4);
        assertConnectedBothWays(map, 2, 3);
        assertConnectedBothWays(map, 2, 5);
        assertConnectedBothWays(map, 4, 5);
        assertConnectedBothWays(map, 4, 6);
        assertConnectedBothWays(map, 5, 6);
        assertConnectedBothWays(map, 5, 7);
        assertConnectedBothWays(map, 7, 8);
    }

    @Test
    void roomThreeOnlyConnectsWithRoomTwo() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        MyLinkedList<Integer> neighbors = map.getConnectedRooms(3);

        assertEquals(1, neighbors.size());
        assertEquals(2, neighbors.get(0));
    }

    @Test
    void roomEightConnectsWithRoomSevenAndHasExitCell() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertTrue(map.areRoomsConnected(8, 7));
        assertTrue(map.roomHasCellType(8, CellType.EXIT));
    }

    @Test
    void roomFiveHasInteractiveShopCell() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertTrue(map.roomHasCellType(5, CellType.SHOP));
        assertEquals(CellType.SHOP, map.getRoom(5).getCell(new Position(3, 3)).getType());
    }

    @Test
    void moduleNineInteractiveCellsArePlacedInExpectedRooms() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertEquals(CellType.NPC, map.getRoom(1).getCell(CasinoMapBuilder.WELCOME_NPC_POSITION).getType());
        assertEquals(CellType.NPC, map.getRoom(5).getCell(CasinoMapBuilder.BAR_SPECIAL_NPC_POSITION).getType());
        assertEquals(CellType.NPC, map.getRoom(6).getCell(CasinoMapBuilder.FRIEND_POSITION).getType());
        assertEquals(CellType.TRAP, map.getRoom(6).getCell(CasinoMapBuilder.DANGEROUS_COMPANION_POSITION).getType());
        assertEquals(CellType.MINIGAME, map.getRoom(8).getCell(CasinoMapBuilder.RUSSIAN_ROULETTE_POSITION).getType());
        assertEquals(CellType.EXIT, map.getRoom(8).getCell(CasinoMapBuilder.EXIT_POSITION).getType());
    }

    @Test
    void initialPlayerPositionIsDefinedInRoomOne() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertEquals(1, map.getInitialRoomId());
        assertEquals(new Position(3, 3), map.getInitialPlayerPosition());
        assertEquals(CellType.PLAYER, map.getRoom(1).getCell(map.getInitialPlayerPosition()).getType());
    }

    @Test
    void everyRoomHasAtLeastOneDoorAndObstacles() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        for (int roomId = 1; roomId <= 8; roomId++) {
            assertTrue(map.roomHasCellType(roomId, CellType.DOOR));
            assertTrue(map.roomHasCellType(roomId, CellType.OBSTACLE));
        }
    }

    @Test
    void everyGraphConnectionHasDoorCells() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertDoorsBothWays(map, 1, 2);
        assertDoorsBothWays(map, 1, 4);
        assertDoorsBothWays(map, 2, 3);
        assertDoorsBothWays(map, 2, 5);
        assertDoorsBothWays(map, 4, 5);
        assertDoorsBothWays(map, 4, 6);
        assertDoorsBothWays(map, 5, 6);
        assertDoorsBothWays(map, 5, 7);
        assertDoorsBothWays(map, 7, 8);
    }

    @Test
    void treasuryDoorIsLockedAndRequiresTreasuryKey() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        Door door = map.getDoorTo(2, 3);

        assertTrue(door.isLocked());
        assertEquals(CasinoMapBuilder.TREASURY_KEY_NAME, door.getRequiredKeyName());
        assertFalse(map.canTransition(2, 3, false));
        assertTrue(map.canTransition(2, 3, true));
    }

    @Test
    void normalDoorsDoNotRequireTreasuryKey() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertTrue(map.canTransition(1, 2, false));
        assertTrue(map.canTransition(7, 8, false));
    }

    @Test
    void disconnectedRoomsCannotTransition() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertFalse(map.canTransition(1, 8, true));
    }

    private void assertRoom(Room room, int id, String name) {
        assertEquals(id, room.getId());
        assertEquals(name, room.getName());
        assertEquals(CasinoMapBuilder.ROOM_SIZE, room.getRows());
        assertEquals(CasinoMapBuilder.ROOM_SIZE, room.getColumns());
    }

    private void assertConnectedBothWays(CasinoMap map, int first, int second) {
        assertTrue(map.areRoomsConnected(first, second));
        assertTrue(map.areRoomsConnected(second, first));
    }

    private void assertDoorsBothWays(CasinoMap map, int first, int second) {
        assertTrue(map.hasDoorTo(first, second));
        assertTrue(map.hasDoorTo(second, first));
    }
}
