# Plan De Pruebas

Este documento define las pruebas previstas para el proyecto.

## Herramienta

- JUnit 5 para pruebas unitarias.
- Pruebas manuales para JavaFX.

## Principios

- Toda logica no visual debe poder probarse sin JavaFX.
- Cada estructura propia debe tener tests.
- Cada regla importante del juego debe tener tests.
- Los errores esperados deben comprobarse mediante excepciones.

## 1. Tests De Estructuras

### MyLinkedListTest

Casos minimos:

- Lista nueva esta vacia.
- `add` incrementa tamano.
- `addFirst` inserta al inicio.
- `get` devuelve elementos correctos.
- `set` reemplaza valor.
- `remove` elimina por valor.
- `removeAt` elimina por indice.
- `contains` detecta valores existentes.
- `clear` vacia lista.
- Indice negativo lanza excepcion.
- Indice fuera de rango lanza excepcion.

### MyQueueTest

Casos minimos:

- Cola nueva esta vacia.
- `enqueue` incrementa tamano.
- `dequeue` respeta FIFO.
- `peek` no elimina.
- `clear` vacia cola.
- `dequeue` en cola vacia lanza excepcion.
- `peek` en cola vacia lanza excepcion.

### MyStackTest

Casos minimos:

- Pila nueva esta vacia.
- `push` incrementa tamano.
- `pop` respeta LIFO.
- `peek` no elimina.
- `clear` vacia pila.
- `pop` en pila vacia lanza excepcion.
- `peek` en pila vacia lanza excepcion.

### MyMatrixTest

Casos minimos:

- Matriz guarda filas y columnas.
- `set` y `get` funcionan.
- `isInside` detecta posiciones validas.
- `isInside` rechaza posiciones fuera.
- `fill` rellena todas las celdas.
- Dimensiones invalidas lanzan excepcion.
- Acceso fuera de rango lanza excepcion.

### MyGraphTest

Casos minimos:

- Grafo nuevo esta vacio.
- `addNode` anade nodo.
- No se duplican nodos.
- `addUndirectedEdge` conecta en ambos sentidos.
- `areConnected` detecta conexion.
- `getNeighbors` devuelve vecinos.
- `shortestDistance` calcula distancia.
- `shortestPath` devuelve camino correcto.
- Camino inexistente se gestiona correctamente.
- Nodo inexistente lanza excepcion o devuelve resultado documentado.

## 2. Tests De Movimiento

Casos futuros:

- Jugador no sale de la matriz.
- Jugador no atraviesa obstaculos.
- Jugador no se mueve en diagonal.
- BFS calcula casillas alcanzables con velocidad 1.
- BFS calcula casillas alcanzables con velocidad mayor.
- Obstaculos reducen casillas alcanzables.
- Movimiento especial en linea recta se detiene ante obstaculo.

## 3. Tests De Mapa

Casos futuros:

- El grafo tiene 8 salas.
- Las conexiones coinciden con `JUEGO.md`.
- Sala 2 conecta con sala 3.
- Sala 3 solo conecta con sala 2.
- Sala 8 conecta con sala 7 y salida exterior.
- Puerta a sala 3 esta bloqueada inicialmente.
- Puerta a sala 3 se abre con Llave de Tesoreria.

## 4. Tests De Combate

Casos futuros:

- Dano nunca es negativo.
- Vida nunca baja de 0.
- Ataque requiere adyacencia.
- Enemigo muerto desaparece.
- Enemigo muerto da fichas.
- Crupier suelta Traje con escudo.

## 5. Tests De Inventario Y Tienda

Casos futuros:

- Recoger objeto lo anade al inventario.
- Usar consumible lo elimina.
- Equipar arma modifica ataque.
- Equipar armadura modifica defensa.
- Comprar objeto resta fichas.
- Comprar sin fichas suficientes lanza excepcion.
- Llave comprada permite abrir sala 3.

## 6. Tests De Condiciones De Victoria Y Derrota

Casos futuros:

- No se puede ganar sin amigo.
- Se gana saliendo por sala 8 con amigo.
- Se pierde si vida llega a 0.
- Se pierde si turnos llegan a 0.
- Ruleta rusa puede matar al jugador.

## 7. Tests De Persistencia JSON

Casos futuros:

- Cargar configuracion inicial valida.
- Rechazar configuracion invalida.
- Guardar partida.
- Cargar partida guardada.
- Restaurar sala actual.
- Restaurar posicion del jugador.
- Restaurar inventario.
- Restaurar enemigos muertos.
- Restaurar amigo rescatado.
- Gestionar archivo inexistente.

## 8. Pruebas Manuales JavaFX

Casos futuros:

- La sala se muestra como GridPane.
- El jugador aparece en la posicion correcta.
- Las puertas se distinguen.
- Las casillas alcanzables se resaltan.
- El panel de jugador se actualiza.
- El inventario se ve constantemente.
- El log muestra eventos.
- La tienda abre dialogo.
- La ruleta rusa pide confirmacion.
- Victoria y derrota muestran pantalla final.

## Criterio De Aceptacion Del Primer Modulo

El primer modulo se acepta cuando:

- Todos los tests de estructuras pasan.
- No hay uso de estructuras prohibidas en `structures`.
- El grafo puede representar las 8 salas.
- BFS devuelve un camino minimo en un grafo de ejemplo.
