package casinoescape.game;

import casinoescape.items.Armor;
import casinoescape.items.Consumable;
import casinoescape.items.Effect;
import casinoescape.items.EffectType;
import casinoescape.items.Weapon;
import casinoescape.model.CasinoMap;
import casinoescape.model.Cell;
import casinoescape.model.CellType;
import casinoescape.model.Door;
import casinoescape.model.Enemy;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.structures.MyGraph;
import casinoescape.structures.MyLinkedList;

public class CasinoMapBuilder {
    public static final int ROOM_SIZE = 7;
    public static final int INITIAL_ROOM_ID = 1;
    public static final String TREASURY_KEY_NAME = "Llave de Tesoreria";
    public static final Position WELCOME_NPC_POSITION = new Position(3, 5);
    public static final Position BAR_SHOP_POSITION = new Position(3, 3);
    public static final Position BAR_SPECIAL_NPC_POSITION = new Position(2, 3);
    public static final Position FRIEND_POSITION = new Position(3, 2);
    public static final Position DANGEROUS_COMPANION_POSITION = new Position(3, 4);
    public static final Position RUSSIAN_ROULETTE_POSITION = new Position(3, 4);
    public static final Position EXIT_POSITION = new Position(0, 3);
    public static final String BROKEN_BOTTLE_ID = "BROKEN_BOTTLE";
    public static final String TOBACCO_PACK_ID = "TOBACCO_PACK";
    public static final String GOLD_SUIT_ID = "GOLD_SUIT";
    public static final String GYPSY_CANE_ID = "GYPSY_CANE";
    public static final String SHARP_CARDS_ID = "SHARP_CARDS";
    public static final String SHIELD_SUIT_ID = "SHIELD_SUIT";
    public static final String PRIVATE_ROOM_HEAL_ID = "REVITALIZING_SHOT";
    public static final String SLOT_MACHINE_ENEMY_ID = "SLOT_MACHINE_BROKEN";
    public static final String BLACKJACK_DEALER_ENEMY_ID = "BLACKJACK_DEALER";
    public static final String DRUNK_ENEMY_ID = "AGGRESSIVE_DRUNK";
    public static final String RUSSIAN_MAFIA_ENEMY_ID = "RUSSIAN_MAFIA";
    public static final String VIP_THUG_ENEMY_ID = "VIP_THUG";

    public CasinoMap buildBaseMap() {
        MyLinkedList<Room> rooms = createRooms();
        MyGraph<Integer> graph = createGraph();
        placeBaseCells(rooms);
        return new CasinoMap(graph, rooms, INITIAL_ROOM_ID, new Position(3, 3));
    }

    public void placeBaseDynamicContent(CasinoMap map) {
        placeBaseDynamicContent(
                map.getRoom(1),
                map.getRoom(2),
                map.getRoom(3),
                map.getRoom(4),
                map.getRoom(5),
                map.getRoom(6),
                map.getRoom(7));
    }

    private MyLinkedList<Room> createRooms() {
        MyLinkedList<Room> rooms = new MyLinkedList<>();
        rooms.add(new Room(1, "Hall / Entrada", ROOM_SIZE, ROOM_SIZE));
        rooms.add(new Room(2, "Tragaperras", ROOM_SIZE, ROOM_SIZE));
        rooms.add(new Room(3, "Tesoreria / Caja Fuerte", ROOM_SIZE, ROOM_SIZE));
        rooms.add(new Room(4, "Blackjack", ROOM_SIZE, ROOM_SIZE));
        rooms.add(new Room(5, "Bar", ROOM_SIZE, ROOM_SIZE));
        rooms.add(new Room(6, "Zona Privada", ROOM_SIZE, ROOM_SIZE));
        rooms.add(new Room(7, "Sala VIP", ROOM_SIZE, ROOM_SIZE));
        rooms.add(new Room(8, "Ruleta / Final", ROOM_SIZE, ROOM_SIZE));
        return rooms;
    }

    private MyGraph<Integer> createGraph() {
        MyGraph<Integer> graph = new MyGraph<>();
        for (int roomId = 1; roomId <= 8; roomId++) {
            graph.addNode(roomId);
        }
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(1, 4);
        graph.addUndirectedEdge(2, 3);
        graph.addUndirectedEdge(2, 5);
        graph.addUndirectedEdge(4, 5);
        graph.addUndirectedEdge(4, 6);
        graph.addUndirectedEdge(5, 6);
        graph.addUndirectedEdge(5, 7);
        graph.addUndirectedEdge(7, 8);
        return graph;
    }

    private void placeBaseCells(MyLinkedList<Room> rooms) {
        Room room1 = rooms.get(0);
        Room room2 = rooms.get(1);
        Room room3 = rooms.get(2);
        Room room4 = rooms.get(3);
        Room room5 = rooms.get(4);
        Room room6 = rooms.get(5);
        Room room7 = rooms.get(6);
        Room room8 = rooms.get(7);

        addObstacles(room1, new Position(1, 1), new Position(1, 5), new Position(5, 1), new Position(5, 5));
        addObstacles(room2, new Position(1, 1), new Position(1, 3), new Position(1, 5), new Position(3, 3), new Position(5, 1), new Position(5, 5));
        addObstacles(room3, new Position(1, 1), new Position(1, 5), new Position(3, 3), new Position(5, 1), new Position(5, 5));
        addObstacles(room4, new Position(1, 2), new Position(1, 4), new Position(3, 3), new Position(5, 2), new Position(5, 4));
        addObstacles(room5, new Position(1, 1), new Position(1, 5), new Position(3, 1), new Position(3, 5), new Position(5, 3));
        addObstacles(room6, new Position(1, 1), new Position(1, 5), new Position(3, 3), new Position(5, 1), new Position(5, 5));
        addObstacles(room7, new Position(1, 2), new Position(1, 4), new Position(3, 1), new Position(3, 5), new Position(5, 3));
        addObstacles(room8, new Position(1, 1), new Position(1, 5), new Position(3, 3), new Position(5, 1), new Position(5, 5));

        room1.setCellType(new Position(3, 3), CellType.PLAYER);
        room1.setCell(WELCOME_NPC_POSITION, new Cell(CellType.NPC, "Recepcionista del casino"));
        addDoor(room1, new Position(0, 3), 2, false);
        addDoor(room1, new Position(3, 0), 4, false);

        addDoor(room2, new Position(6, 3), 1, false);
        addDoor(room2, new Position(0, 3), 3, true);
        addDoor(room2, new Position(3, 6), 5, false);

        addDoor(room3, new Position(6, 3), 2, false);

        addDoor(room4, new Position(0, 3), 1, false);
        addDoor(room4, new Position(3, 6), 5, false);
        addDoor(room4, new Position(6, 3), 6, false);

        addDoor(room5, new Position(3, 0), 2, false);
        addDoor(room5, new Position(0, 3), 4, false);
        addDoor(room5, new Position(6, 3), 6, false);
        addDoor(room5, new Position(3, 6), 7, false);
        room5.setCell(BAR_SHOP_POSITION, new Cell(CellType.SHOP, "Bar interactivo"));
        room5.setCell(BAR_SPECIAL_NPC_POSITION, new Cell(CellType.NPC, "Cliente sospechoso"));

        addDoor(room6, new Position(0, 3), 4, false);
        addDoor(room6, new Position(3, 6), 5, false);
        room6.setCell(FRIEND_POSITION, new Cell(CellType.NPC, "Amigo borracho"));
        room6.setCell(DANGEROUS_COMPANION_POSITION, new Cell(CellType.TRAP, "Acompanante peligrosa"));

        addDoor(room7, new Position(3, 0), 5, false);
        addDoor(room7, new Position(3, 6), 8, false);

        addDoor(room8, new Position(3, 0), 7, false);
        room8.setCell(RUSSIAN_ROULETTE_POSITION, new Cell(CellType.MINIGAME, "Ruleta rusa"));
        room8.setCell(EXIT_POSITION, new Cell(CellType.EXIT, "Salida exterior"));

        placeBaseDynamicContent(room1, room2, room3, room4, room5, room6, room7);
    }

    private void placeBaseDynamicContent(Room room1, Room room2, Room room3, Room room4, Room room5, Room room6, Room room7) {
        room1.addItem(new Weapon(BROKEN_BOTTLE_ID, "Botella rota", 3), new Position(3, 4));
        room2.addEnemy(new Enemy(SLOT_MACHINE_ENEMY_ID, "Maquina Tragaperras Averiada", 35, 7, 2, new Position(3, 4), 4, ""));
        room2.addItem(new Consumable(TOBACCO_PACK_ID, "Cajetilla de tabaco",
                new Effect(EffectType.MOVEMENT_BONUS, 1, 4)), new Position(5, 3));
        room3.addItem(new Armor(GOLD_SUIT_ID, "Traje de oro", 6, 2), new Position(3, 2));
        room3.addItem(new Weapon(GYPSY_CANE_ID, "Baston gitano", 9), new Position(3, 4));
        room4.addEnemy(new Enemy(BLACKJACK_DEALER_ENEMY_ID, "Crupier de Blackjack", 45, 9, 4, new Position(3, 2), 6, "Traje con escudo"));
        room4.addItem(new Weapon(SHARP_CARDS_ID, "Baraja afilada", 5), new Position(3, 4));
        room5.addEnemy(new Enemy(DRUNK_ENEMY_ID, "Borracho Agresivo", 30, 8, 2, new Position(4, 3), 3, ""));
        room6.addItem(new Consumable(PRIVATE_ROOM_HEAL_ID, "Chupito revitalizante",
                new Effect(EffectType.HEAL, 30, 0)), new Position(4, 2));
        room7.addEnemy(new Enemy(RUSSIAN_MAFIA_ENEMY_ID, "Mafioso Ruso", 65, 12, 5, new Position(3, 3), 8, ""));
        room7.addEnemy(new Enemy(VIP_THUG_ENEMY_ID, "Maton VIP", 28, 8, 2, new Position(2, 3), 3, ""));
    }

    private void addDoor(Room room, Position position, int destinationRoomId, boolean locked) {
        Door door = locked
                ? new Door(destinationRoomId, true, TREASURY_KEY_NAME)
                : new Door(destinationRoomId);
        room.setCell(position, new Cell(door, locked ? "Puerta bloqueada" : "Puerta"));
    }

    private void addObstacles(Room room, Position first, Position second, Position third, Position fourth) {
        room.setCellType(first, CellType.OBSTACLE);
        room.setCellType(second, CellType.OBSTACLE);
        room.setCellType(third, CellType.OBSTACLE);
        room.setCellType(fourth, CellType.OBSTACLE);
    }

    private void addObstacles(Room room, Position first, Position second, Position third, Position fourth, Position fifth) {
        addObstacles(room, first, second, third, fourth);
        room.setCellType(fifth, CellType.OBSTACLE);
    }

    private void addObstacles(Room room, Position first, Position second, Position third, Position fourth, Position fifth, Position sixth) {
        addObstacles(room, first, second, third, fourth, fifth);
        room.setCellType(sixth, CellType.OBSTACLE);
    }
}
