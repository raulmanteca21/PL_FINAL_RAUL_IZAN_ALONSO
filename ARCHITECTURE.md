# Arquitectura Tecnica

Este documento define la arquitectura base del proyecto `CasinoEscape`.

## Decisiones Iniciales

- IDE: IntelliJ IDEA.
- Control de versiones: Git + GitHub.
- Gestor recomendado: Maven.
- Java recomendado: Java 17.
- Testing: JUnit 5.
- Interfaz: JavaFX.
- Paquete base recomendado: `casinoescape`.

## Objetivo Arquitectonico

Construir un juego por turnos modular, donde la logica sea independiente de JavaFX y pueda probarse con JUnit.

La interfaz debe ser una capa externa. El modelo del juego debe poder ejecutarse y probarse sin abrir ventanas.

## Estructura De Carpetas Recomendada

```text
src/main/java/casinoescape/
    Main.java
    structures/
    model/
    game/
    movement/
    combat/
    items/
    persistence/
    ui/
    exceptions/
    logging/

src/test/java/casinoescape/
    structures/
    model/
    movement/
    combat/
    items/
    persistence/

config/
    game_config.json

saves/
    savegame.json

docs/
    uml/
    sketches/
    memoria/
```

## Paquetes

### `casinoescape.structures`

Estructuras propias genericas.

Clases previstas:

- `MyLinkedList<T>`
- `MyQueue<T>`
- `MyStack<T>`
- `MyMatrix<T>`
- `MyGraph<T>`
- Clases auxiliares privadas o de paquete como nodos internos.

No debe depender de:

- `model`
- `game`
- `ui`

### `casinoescape.model`

Entidades basicas del dominio.

Clases previstas:

- `Position`
- `Cell`
- `CellType`
- `Room`
- `Player`
- `Enemy`
- `Npc`
- `Door`
- `Trap`
- `GameState`

Puede depender de:

- `structures`
- `items`
- `exceptions`

No debe depender de:

- `ui`

### `casinoescape.game`

Coordinacion de partida.

Clases previstas:

- `Game`
- `TurnManager`
- `GameInitializer`
- `VictoryDefeatChecker`

Puede depender de:

- `model`
- `movement`
- `combat`
- `items`
- `logging`
- `exceptions`

No debe depender de:

- `ui`

### `casinoescape.movement`

Movimiento y caminos.

Clases previstas:

- `MovementService`
- `ReachableCellsCalculator`
- `PathFinder`
- `EnemyMovementService`

Debe usar:

- `MyQueue<T>` para BFS.

### `casinoescape.combat`

Combate.

Clases previstas:

- `CombatService`
- `DamageCalculator`

Debe implementar la formula oficial del enunciado.

### `casinoescape.items`

Objetos, inventario y tienda.

Clases previstas:

- `Item`
- `Weapon`
- `Armor`
- `Consumable`
- `KeyItem`
- `Inventory`
- `Shop`
- `ShopItem`
- `Effect`

El inventario debe usar lista propia.

### `casinoescape.persistence`

Carga y guardado JSON.

Clases previstas:

- `GameConfigLoader`
- `GameSaveWriter`
- `GameSaveLoader`
- `JsonValidator`

Debe gestionar excepciones de E/S y configuracion invalida.

### `casinoescape.ui`

JavaFX.

Clases previstas:

- `CasinoEscapeApp`
- `GameController`
- `RoomGridView`
- `PlayerPanelView`
- `InventoryPanelView`
- `ActionPanelView`
- `LogPanelView`

Regla:

- No incluir reglas de juego en este paquete.

### `casinoescape.exceptions`

Excepciones personalizadas.

Clases previstas:

- `InvalidMoveException`
- `InvalidActionException`
- `LockedDoorException`
- `NotEnoughChipsException`
- `InvalidConfigurationException`
- `PersistenceException`

### `casinoescape.logging`

Log del juego.

Clases previstas:

- `GameLog`
- `LogEntry`

Debe usar estructura propia.

## Dependencias Permitidas Entre Capas

```text
ui -> game -> model -> structures
ui -> game -> movement -> structures
ui -> game -> combat
ui -> game -> items -> structures
ui -> persistence -> model
```

Dependencias prohibidas:

```text
structures -> model
structures -> ui
model -> ui
game -> ui
combat -> ui
movement -> ui
items -> ui
```

## Flujo De Una Accion

Ejemplo: mover jugador.

```text
JavaFX detecta click en celda
GameController llama a Game.movePlayer(destination)
Game valida turno
MovementService valida destino
MovementService usa BFS si hace falta
Game actualiza modelo
GameLog registra evento
GameController refresca vistas
```

## Flujo De Combate

```text
Usuario elige atacar
Game valida accion disponible
CombatService valida objetivo adyacente
DamageCalculator calcula dano
CombatService actualiza vida
Game entrega drops si enemigo muere
GameLog registra evento
UI refresca
```

## Flujo De Cambio De Sala

```text
Jugador interactua con puerta
Game valida que la celda es puerta
Game valida conexion en grafo
Game valida llave si esta bloqueada
Game cambia sala actual
Game coloca jugador en posicion de entrada
Game termina turno
GameLog registra evento
```

## Flujo De Guardado

```text
UI solicita guardar
Game expone estado actual
GameSaveWriter serializa a JSON
Si hay error, lanza PersistenceException
GameLog registra guardado o error
```

## Reglas De Diseno

- Preferir clases pequenas y cohesionadas.
- No crear herencias complejas si una interfaz o composicion simple basta.
- No anadir compatibilidad futura innecesaria.
- Mantener los datos del tablero en el modelo, no en la vista.
- Las reglas deben probarse sin JavaFX.
- Los nombres deben ser claros y consistentes.
