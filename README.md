# Casino Escape

Proyecto academico de Metodologia de la Programacion y Estructuras de Datos.

## Resumen

Casino Escape es un juego por turnos en Java/JavaFX. El jugador explora un casino representado por salas 7x7, rescata a su amigo en la sala 6 y gana al salir por la sala 8 con el amigo rescatado.

## Requisitos Principales Implementados En Codigo

- Mapa general con grafo propio `MyGraph`.
- Salas con matriz propia `MyMatrix`.
- Inventario, log, objetos y enemigos con estructuras propias.
- Movimiento ortogonal con BFS sobre matriz.
- Puerta bloqueada a la Tesoreria mediante Llave de Tesoreria.
- Combate con formula del enunciado.
- Enemigos con turno propio tras el jugador.
- Tienda del bar y economia con fichas.
- NPCs, amigo rescatable, trampa, ruleta rusa y salida exterior.
- Guardado/carga JSON.
- Interfaz JavaFX basica.
- Tests JUnit para logica no visual.

La verificacion completa queda pendiente de ejecutar en un entorno con Maven/JavaFX disponible.

## Restricciones Academicas

La logica principal evita `ArrayList`, `HashMap`, `LinkedList`, `Queue`, `Stack`, `Deque` y equivalentes de `java.util` para las estructuras evaluadas. Se usan estructuras propias en `casinoescape.structures`.

## Ejecucion

El proyecto esta preparado para Maven, Java 17 y JavaFX. Si Maven esta disponible:

```bash
mvn test
mvn javafx:run
```

En este entorno no hay wrapper Maven ni `mvn` disponible en PATH, por lo que las pruebas deben ejecutarse desde IntelliJ o desde una instalacion local de Maven.
