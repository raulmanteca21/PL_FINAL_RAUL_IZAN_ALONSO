# Plan Y Estado De Pruebas

## Pruebas Automatizadas

Hay tests JUnit para:

- Estructuras propias: lista, cola, pila, matriz y grafo.
- Modelo: jugador, sala, celda, puerta, enemigo y estado.
- Movimiento: BFS, caminos y movimiento especial.
- Combate: calculo de dano y resultados.
- Inventario, objetos y tienda.
- Juego: mapa, puertas, interactivos, recogida, combate y soporte UI.
- Persistencia JSON: configuracion inicial, guardado, carga, inventario, turnos, enemigos y objetos recogidos.

## Pruebas Manuales JavaFX

Pendientes de ejecutar en entorno con JavaFX/Maven disponible:

- Mover jugador por celdas vacias resaltadas.
- Recoger Botella rota desde sala 1.
- Atacar enemigos adyacentes en salas 2, 4, 5 y 7.
- Comprar Llave de Tesoreria en sala 5 y abrir sala 3.
- Usar Pastilla de dudosa procedencia y movimiento en linea.
- Rescatar amigo en sala 6.
- Probar ruleta rusa en sala 8.
- Guardar y cargar partida desde la interfaz.
- Ver victoria al salir por sala 8 con amigo rescatado.

## Limitaciones Conocidas

- En este entorno no se puede ejecutar `mvn test` porque no existe `mvn` ni `mvnw`.
- Ultima verificacion local en este entorno: compilacion parcial con `javac` de paquetes no visuales y sin Gson completada correctamente.
- La IA enemiga usa BFS simple para acercarse, pero no cambia de sala.
- La interfaz mantiene dialogos simples; se prioriza funcionalidad academica sobre estetica.
