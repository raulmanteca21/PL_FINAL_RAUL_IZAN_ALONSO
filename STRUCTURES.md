# Especificacion De Estructuras Propias

Este documento define las estructuras propias del primer modulo.

## Objetivo Del Modulo

Implementar estructuras genericas que sustituyan las estructuras prohibidas en la logica principal del juego.

Estructuras de la primera fase:

- `MyLinkedList<T>`
- `MyQueue<T>`
- `MyStack<T>`
- `MyMatrix<T>`
- `MyGraph<T>`

## Restricciones

No usar internamente:

- `ArrayList`
- `LinkedList`
- `HashMap`
- `HashSet`
- `Queue`
- `Stack`
- `Deque`

Se pueden usar arrays nativos cuando tenga sentido, especialmente en `MyMatrix<T>`.

## 1. MyLinkedList<T>

Uso en el juego:

- Inventario.
- Lista de enemigos.
- Lista de objetos.
- Lista de NPCs.
- Vecinos de nodos del grafo.
- Log del juego.

Operaciones minimas:

```text
void add(T value)
void addFirst(T value)
T get(int index)
T set(int index, T value)
boolean remove(T value)
T removeAt(int index)
boolean contains(T value)
int indexOf(T value)
int size()
boolean isEmpty()
void clear()
```

Decisiones:

- Lista simplemente enlazada o doblemente enlazada.
- Recomendacion: simplemente enlazada con referencia a `head` y `tail`.
- Mantener `tail` permite `add` en O(1).

Costes esperados:

```text
add: O(1)
addFirst: O(1)
get: O(n)
set: O(n)
remove: O(n)
removeAt: O(n)
contains: O(n)
size: O(1)
isEmpty: O(1)
clear: O(1)
```

Casos borde:

- Lista vacia.
- Eliminar primer elemento.
- Eliminar ultimo elemento.
- Indice negativo.
- Indice fuera de rango.
- Elementos `null`: decidir si se permiten. Recomendacion: no permitir `null` para simplificar.

## 2. MyQueue<T>

Uso en el juego:

- BFS en matriz.
- BFS en grafo.
- Posible orden de turnos.

Operaciones minimas:

```text
void enqueue(T value)
T dequeue()
T peek()
int size()
boolean isEmpty()
void clear()
```

Decisiones:

- Implementar con nodos propios y referencias `front` y `rear`.
- No depender de `java.util.Queue`.

Costes esperados:

```text
enqueue: O(1)
dequeue: O(1)
peek: O(1)
size: O(1)
isEmpty: O(1)
clear: O(1)
```

Casos borde:

- Dequeue en cola vacia.
- Peek en cola vacia.
- Encolar despues de vaciar.

## 3. MyStack<T>

Uso en el juego:

- Historial de rutas.
- Reconstruccion de camino.
- Posible ampliacion de deshacer.

Operaciones minimas:

```text
void push(T value)
T pop()
T peek()
int size()
boolean isEmpty()
void clear()
```

Costes esperados:

```text
push: O(1)
pop: O(1)
peek: O(1)
size: O(1)
isEmpty: O(1)
clear: O(1)
```

Casos borde:

- Pop en pila vacia.
- Peek en pila vacia.

## 4. MyMatrix<T>

Uso en el juego:

- Matriz de celdas de cada sala.
- Todas las salas empiezan como 7x7.
- Debe permitir otros tamanos en el futuro.

Operaciones minimas:

```text
MyMatrix(int rows, int columns)
T get(int row, int column)
void set(int row, int column, T value)
int getRows()
int getColumns()
boolean isInside(int row, int column)
void fill(T value)
```

Decisiones:

- Puede usar array nativo `Object[][]` internamente.
- Validar filas y columnas positivas.

Costes esperados:

```text
get: O(1)
set: O(1)
isInside: O(1)
fill: O(rows * columns)
```

Casos borde:

- Dimensiones 0 o negativas.
- Acceso fuera de rango.
- Matriz rectangular, no solo cuadrada.

## 5. MyGraph<T>

Uso en el juego:

- Mapa de habitaciones.
- BFS para camino minimo.
- Validacion de conexiones.

Operaciones minimas:

```text
void addNode(T value)
void addUndirectedEdge(T from, T to)
boolean containsNode(T value)
boolean areConnected(T from, T to)
MyLinkedList<T> getNeighbors(T value)
int size()
MyLinkedList<T> shortestPath(T start, T goal)
int shortestDistance(T start, T goal)
```

Decisiones:

- Grafo no dirigido.
- Representacion recomendada: lista propia de nodos; cada nodo tiene lista propia de vecinos.
- No usar `HashMap`.
- Como el numero de salas es pequeno, buscar nodos linealmente es aceptable y justificable.

Costes esperados:

```text
addNode: O(n)
addUndirectedEdge: O(n + degree)
containsNode: O(n)
areConnected: O(n + degree)
getNeighbors: O(n)
shortestPath BFS: O(V + E) mas coste de busqueda lineal si no hay acceso directo
shortestDistance BFS: O(V + E) mas coste de busqueda lineal si no hay acceso directo
```

Casos borde:

- Nodo duplicado.
- Arista duplicada.
- Nodo inexistente.
- Camino inexistente.
- `start` igual a `goal`.

## Excepciones Recomendadas

Para estructuras puede usarse:

- `IndexOutOfBoundsException` para indices invalidos.
- `IllegalArgumentException` para argumentos invalidos.
- `IllegalStateException` para `pop`, `peek` o `dequeue` en estructuras vacias.

Las excepciones personalizadas del juego se reservan para logica de dominio.

## Orden De Implementacion

1. `MyLinkedList<T>`.
2. `MyQueue<T>`.
3. `MyStack<T>`.
4. `MyMatrix<T>`.
5. `MyGraph<T>`.
6. BFS de grafo.

## Criterio De Finalizacion

El modulo de estructuras esta terminado cuando:

- Todas las clases compilan.
- Hay tests JUnit para cada estructura.
- No se usan estructuras prohibidas internamente.
- BFS del grafo funciona.
- Los costes estan documentados.
- Se puede representar el grafo del casino con `MyGraph<Integer>` o `MyGraph<String>`.
