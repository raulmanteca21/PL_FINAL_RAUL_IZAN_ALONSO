# Plan De Desarrollo Modular

Este plan define como desarrollar el juego por fases. Todo agente o subagente debe leer primero `JUEGO.md` y despues este archivo.

Objetivo del plan: construir una base funcional, academica y ampliable, evitando implementar mecanicas avanzadas antes de que el nucleo este terminado.

## 1. Filosofia De Desarrollo

El proyecto se desarrollara por modulos independientes pero integrados progresivamente.

Principios:

- Primero logica, despues interfaz.
- Primero estructuras propias, despues sistemas de juego.
- Primero juego minimo funcional, despues ampliaciones.
- Cada fase debe poder probarse.
- Cada modulo debe tener responsabilidad clara.
- Evitar acoplar JavaFX con la logica de dominio.
- No usar estructuras prohibidas para resolver partes evaluadas.

La interfaz JavaFX debe consumir el modelo del juego, no contener reglas de juego.

## 2. Modulos Tecnicos

### 2.1 Modulo De Estructuras Propias

Responsabilidad:

- Proporcionar las estructuras de datos usadas por el resto del juego.

Componentes previstos:

- `MyList<T>` o lista enlazada propia.
- `MyQueue<T>` o cola propia.
- `MyStack<T>` si se usa historial, deshacer o apoyo a algoritmos.
- `MyCircularList<T>` si se usa para turnos o ciclos.
- `MyMatrix<T>` para salas.
- `Graph<T>` propio para mapa de habitaciones.
- `GraphNode<T>` y `GraphEdge<T>` si procede.

Uso previsto:

- Inventario: lista propia.
- Enemigos de una sala: lista propia.
- Objetos de una sala: lista propia o matriz de celdas.
- Cola BFS: cola propia.
- Grafo de habitaciones: grafo propio.
- Matriz de sala: matriz propia.
- Log: lista propia.
- Turnos: cola o lista circular propia.

Pruebas obligatorias:

- Insertar, eliminar, buscar y recorrer lista.
- Encolar y desencolar.
- Apilar y desapilar si se implementa pila.
- Acceso y modificacion de matriz.
- Anadir nodos y aristas al grafo.
- BFS sobre grafo.

Documentacion obligatoria:

- Que estructura se usa.
- Por que se usa.
- Coste de operaciones principales.

### 2.2 Modulo De Dominio Del Juego

Responsabilidad:

- Representar entidades y reglas basicas sin interfaz.

Clases previstas:

- `Game`.
- `GameState`.
- `Player`.
- `Enemy`.
- `CharacterStats` o equivalente.
- `Room`.
- `Cell`.
- `Position`.
- `Door`.
- `Item`.
- `Weapon`.
- `Armor`.
- `Consumable`.
- `KeyItem`.
- `Inventory`.
- `Npc`.
- `Shop`.
- `Trap`.
- `GameLog`.

Reglas:

- Las entidades conocen sus datos, no la interfaz.
- La sala conoce su matriz y sus elementos.
- El jugador conoce su inventario, estadisticas, fichas y posicion.
- El juego conoce sala actual, mapa, turnos y estado global.

Pruebas:

- Crear jugador.
- Crear sala.
- Colocar objetos.
- Validar posiciones.
- Equipar objetos.
- Consumir objetos.

### 2.3 Modulo De Mapa Y Habitaciones

Responsabilidad:

- Gestionar el grafo del casino y las matrices de las salas.

Debe implementar:

- Grafo definitivo de 8 salas.
- Carga de conexiones.
- Habitaciones 7x7.
- Puertas como celdas.
- Obstaculos como celdas bloqueadas.
- Validacion de transiciones.
- Puerta bloqueada de sala 2 a sala 3.
- Salida exterior en sala 8.

Reglas importantes:

- Cambiar de sala solo desde una celda puerta valida.
- La puerta a sala 3 requiere Llave de Tesoreria.
- La salida exterior requiere amigo rescatado.

Pruebas:

- Grafo contiene 8 salas.
- Conexiones coinciden con `JUEGO.md`.
- Sala 3 solo conecta con sala 2.
- Sala 8 conecta con sala 7 y salida exterior.
- Puerta bloqueada impide paso sin llave.
- Puerta bloqueada permite paso con llave.

### 2.4 Modulo De Movimiento

Responsabilidad:

- Resolver movimiento del jugador y enemigos dentro de matrices.

Debe implementar:

- Movimiento ortogonal.
- Prohibicion de diagonales.
- Coste 1 por paso.
- Calculo de casillas alcanzables con BFS.
- Validacion de obstaculos.
- Validacion de limites de sala.
- Movimiento especial en linea recta por Pastilla de dudosa procedencia.

Algoritmos:

- BFS sobre matriz para casillas alcanzables.
- BFS o heuristica simple para que enemigos se acerquen al jugador.

Pruebas:

- No mover fuera de matriz.
- No atravesar obstaculos.
- No mover en diagonal.
- Calcular casillas alcanzables con velocidad concreta.
- Movimiento especial no atraviesa bloqueo.

### 2.5 Modulo De Turnos

Responsabilidad:

- Controlar orden de juego.

Debe implementar:

- Turno del jugador.
- Movimiento maximo 1 vez por turno.
- Accion maxima 1 vez por turno.
- Turno de enemigos despues del jugador.
- Fin automatico del turno al cambiar de sala.
- Reduccion de contadores temporales.
- Comprobacion de victoria y derrota.

Estructura recomendada:

- Cola propia o lista circular propia para orden de entidades.

Pruebas:

- Jugador no puede moverse dos veces en el mismo turno.
- Jugador no puede hacer dos acciones en el mismo turno.
- Enemigos actuan tras jugador.
- Cambio de sala finaliza turno.
- Contadores temporales bajan correctamente.

### 2.6 Modulo De Combate

Responsabilidad:

- Resolver ataques, defensa, muerte y drops.

Debe implementar:

- Formula del enunciado.
- Ataques adyacentes.
- Defensa automatica.
- Vida nunca negativa.
- Muerte de enemigos.
- Drops de fichas.
- Drop especial del Crupier de Blackjack.

Formula:

```text
vidaDefensor = vidaDefensor - maximo(0, ataque * (aleatorio * 2) - defensa)
```

Pruebas:

- Dano no negativo.
- Vida no baja de 0.
- Enemigo muere y desaparece.
- Jugador gana fichas al matar enemigo.
- Crupier suelta Traje con escudo.

### 2.7 Modulo De Inventario Y Objetos

Responsabilidad:

- Gestionar objetos recogidos, equipados y consumidos.

Debe implementar:

- Inventario con lista propia.
- Recoger objeto.
- Usar consumible.
- Equipar arma.
- Equipar armadura.
- Llave de Tesoreria.
- Efectos temporales.
- Fichas como recurso del jugador.

Objetos base:

- Botella rota.
- Cajetilla de tabaco.
- Llave de Tesoreria.
- Traje de oro.
- Baston gitano.
- Baraja afilada.
- Traje con escudo.
- Vodka Redbull.
- Coctel curativo.
- Chaleco de portero.
- Pastilla de dudosa procedencia.
- Objeto util de sala 6.

Pruebas:

- Recoger objeto anade a inventario.
- Consumir objeto lo elimina.
- Equipar arma modifica ataque.
- Equipar armadura modifica defensa.
- Llave permite abrir puerta a sala 3.
- Efectos temporales duran lo indicado.

### 2.8 Modulo De Economia Y Tienda

Responsabilidad:

- Gestionar fichas y compras en el bar.

Debe implementar:

- Fichas ganadas al matar enemigos.
- Tienda interactiva en sala 5.
- Compra de Llave de Tesoreria.
- Compra de consumibles.
- Validacion de fichas suficientes.
- Registro de compras en log.

Pruebas:

- Comprar resta fichas.
- No comprar sin fichas suficientes.
- Objeto comprado aparece en inventario.
- Llave comprada abre acceso a sala 3.

### 2.9 Modulo De NPCs, Trampas E Interactivos

Responsabilidad:

- Gestionar interacciones no combatientes.

Debe implementar:

- NPC bienvenida sala 1.
- NPC especial del bar que entrega Pastilla de dudosa procedencia una vez.
- Amigo en sala 6.
- Acompanante peligrosa con drenaje de vida.
- Ruleta rusa opcional en sala 8.
- Salida exterior.

Pruebas:

- NPC bienvenida muestra mensaje.
- NPC especial entrega objeto una vez.
- Rescatar amigo activa estado.
- Acompanante drena vida en rango 1.
- Ruleta rusa puede dar recompensa o matar.
- Salida no permite ganar sin amigo.
- Salida permite ganar con amigo.

### 2.10 Modulo De Caminos Minimos

Responsabilidad:

- Cumplir requisito de informar distancia y camino.

Debe implementar:

- BFS en grafo de habitaciones hacia salida.
- Distancia minima en numero de salas.
- Seleccion de puerta recomendada.
- BFS en matriz hacia la puerta recomendada.
- Posible accion de revelar camino.

Pruebas:

- Desde sala 1 calcula ruta minima hacia salida.
- Desde sala 5 calcula ruta hacia sala 7 y 8.
- Desde sala 8 distancia a salida es 0 o directa.
- Puertas bloqueadas se tienen en cuenta si la version lo permite.

### 2.11 Modulo De Persistencia JSON

Responsabilidad:

- Cargar configuracion inicial y guardar/cargar partida.

Debe implementar:

- Lector de configuracion inicial.
- Escritor de estado de partida.
- Lector de estado de partida.
- Validacion de JSON.
- Manejo de errores de E/S.

Archivos previstos:

- `config/game_config.json`.
- `saves/savegame.json`.

Pruebas:

- Cargar mapa inicial.
- Guardar estado.
- Cargar estado y recuperar posicion.
- Recuperar inventario.
- Recuperar enemigos muertos/vivos.
- Recuperar amigo rescatado.
- Gestionar archivo inexistente o invalido.

### 2.12 Modulo De Interfaz JavaFX

Responsabilidad:

- Mostrar el juego y capturar acciones del usuario.

Debe implementar:

- GridPane para matriz.
- Panel de jugador.
- Panel de inventario.
- Panel de acciones.
- Registro de eventos.
- Resaltado de casillas alcanzables.
- Botones contextuales.
- Dialogos de tienda y ruleta rusa.
- Pantallas de victoria y derrota.

Regla:

- La interfaz llama a servicios o metodos del modelo.
- La interfaz no calcula reglas de combate, movimiento o victoria.

Pruebas manuales:

- Ver sala actual.
- Click en casilla alcanzable mueve jugador.
- Botones de accion funcionan.
- Inventario se actualiza.
- Log se actualiza.
- Guardar/cargar desde interfaz si se implementa.

### 2.13 Modulo De Documentacion, UML Y Diario IA

Responsabilidad:

- Preparar entregables academicos.

Debe incluir:

- Requisitos funcionales.
- Requisitos no funcionales.
- Casos de uso.
- Contratos e interfaces.
- Invariantes.
- Diagrama de casos de uso.
- Diagrama de clases.
- Diagrama de secuencia minimo.
- Diagrama de estados.
- Diagrama de actividad.
- Bocetos de interfaz.
- Critica del proyecto.
- Diario de IA.
- Registro de prompts, agentes, resultados y modificaciones.

## 3. Fases De Implementacion

### Fase 0: Preparacion Del Proyecto

Objetivo:

- Crear estructura inicial del proyecto Java.

Tareas:

- Decidir gestor: Maven o Gradle.
- Configurar JavaFX.
- Configurar JUnit.
- Crear paquetes base.
- Crear carpetas `config`, `saves`, `docs` si procede.
- Crear README inicial.

Resultado esperado:

- Proyecto compila sin logica todavia.
- Test vacio o basico ejecuta correctamente.

### Fase 1: Estructuras De Datos Propias

Objetivo:

- Implementar las estructuras fundamentales antes de la logica del juego.

Tareas:

- Lista enlazada propia.
- Cola propia.
- Matriz propia.
- Grafo propio.
- Pruebas unitarias de estructuras.
- Documentar costes.

Resultado esperado:

- Estructuras probadas y listas para usar.

Criterio de cierre:

- No avanzar a sistemas de juego hasta que grafo, matriz, lista y cola funcionen.

### Fase 2: Modelo De Dominio Minimo

Objetivo:

- Crear las clases centrales sin interfaz.

Tareas:

- Crear `Position`, `Cell`, `Room`, `Player`, `Enemy`, `Item`, `Inventory`, `Game`.
- Crear enums o constantes para tipos de celda, estado de juego y tipo de objeto.
- Crear log basico.
- Crear excepciones principales.

Resultado esperado:

- Se puede crear una partida en memoria con jugador, sala y objetos.

### Fase 3: Mapa Del Casino

Objetivo:

- Implementar el grafo definitivo y las salas 7x7.

Tareas:

- Crear las 8 salas.
- Crear conexiones definitivas.
- Colocar puertas como casillas.
- Colocar obstaculos basicos.
- Colocar start y salida.
- Validar puerta bloqueada a sala 3.

Resultado esperado:

- El jugador puede cambiar entre salas validas en logica, sin JavaFX.

### Fase 4: Movimiento Y BFS

Objetivo:

- Implementar movimiento correcto dentro de salas.

Tareas:

- Calcular casillas alcanzables.
- Mover jugador a destino valido.
- Rechazar movimientos invalidos.
- Preparar informacion para resaltar casillas en interfaz.
- Implementar movimiento simple de enemigos.

Resultado esperado:

- Movimiento por matriz probado y compatible con obstaculos.

### Fase 5: Turnos

Objetivo:

- Controlar flujo de juego.

Tareas:

- Implementar inicio de turno.
- Registrar si jugador ya movio.
- Registrar si jugador ya actuo.
- Ejecutar enemigos al final.
- Reducir turnos restantes.
- Comprobar victoria/derrota.

Resultado esperado:

- Partida por turnos funcional desde consola o tests.

### Fase 6: Objetos, Inventario Y Equipamiento

Objetivo:

- Permitir recoger, usar y equipar objetos.

Tareas:

- Implementar inventario con lista propia.
- Implementar armas.
- Implementar armaduras.
- Implementar consumibles.
- Implementar efectos temporales.
- Implementar Llave de Tesoreria.

Resultado esperado:

- El jugador puede recoger, equipar y usar objetos.

### Fase 7: Combate Y Drops

Objetivo:

- Resolver enfrentamientos.

Tareas:

- Implementar formula de dano.
- Ataque del jugador.
- Ataque de enemigos.
- Muerte de enemigos.
- Drops de fichas.
- Drop del Traje con escudo.

Resultado esperado:

- Salas con enemigos son jugables.

### Fase 8: Economia Y Bar

Objetivo:

- Implementar fichas y tienda.

Tareas:

- Fichas al matar enemigos.
- Bar interactivo.
- Comprar Llave de Tesoreria.
- Comprar consumibles.
- Validar fichas suficientes.

Resultado esperado:

- El jugador puede comprar la llave para acceder a sala 3.

### Fase 9: Interactivos Especiales

Objetivo:

- Implementar elementos narrativos y especiales.

Tareas:

- NPC bienvenida.
- NPC especial del bar.
- Amigo rescatable.
- Acompanante peligrosa.
- Ruleta rusa.
- Salida exterior.

Resultado esperado:

- El bucle principal del juego ya permite ganar o perder.

### Fase 10: Caminos Minimos

Objetivo:

- Cumplir requisito de informacion de ruta.

Tareas:

- BFS sobre grafo hacia salida.
- Calcular distancia minima en salas.
- Calcular puerta recomendada.
- BFS hacia puerta dentro de sala actual.
- Exponer informacion a interfaz.

Resultado esperado:

- El juego informa distancia minima hacia objetivo/salida.

### Fase 11: Persistencia JSON

Objetivo:

- Cargar configuracion y guardar/cargar partida.

Tareas:

- Crear JSON de configuracion inicial.
- Implementar carga de configuracion.
- Implementar guardado de partida.
- Implementar carga de partida.
- Gestionar errores de E/S.

Resultado esperado:

- Una partida puede guardarse y recuperarse.

### Fase 12: JavaFX

Objetivo:

- Crear interfaz jugable.

Tareas:

- GridPane de sala.
- Panel de estadisticas.
- Panel de inventario.
- Panel de acciones.
- Panel de log.
- Resaltado de movimiento.
- Interacciones por click o botones.
- Dialogo de tienda.
- Dialogo de ruleta rusa.
- Pantallas de victoria y derrota.

Resultado esperado:

- Juego funcional con interfaz grafica.

### Fase 13: Pruebas, Balance Y Correcciones

Objetivo:

- Asegurar funcionamiento robusto.

Tareas:

- Tests de estructuras.
- Tests de logica de juego.
- Tests de movimiento.
- Tests de combate.
- Tests de JSON.
- Partidas manuales completas.
- Ajuste de vida, dano, fichas y precios.
- Revision de excepciones.

Resultado esperado:

- Base estable y demostrable.

### Fase 14: Documentacion Final

Objetivo:

- Preparar entregables.

Tareas:

- Memoria.
- UML.
- Bocetos.
- JSON de ejemplo.
- Diario IA.
- Critica del proyecto.
- Video demostrativo.
- ZIP y repositorio.

Resultado esperado:

- Entrega completa para ambas asignaturas.

## 4. Distribucion Recomendada Para Grupo De 3

Esta distribucion es orientativa y puede ajustarse.

### Persona A: Estructuras Y Algoritmos

Responsabilidades:

- Lista propia.
- Cola propia.
- Matriz propia.
- Grafo propio.
- BFS de movimiento.
- BFS de caminos minimos.
- Tests de estructuras.
- Justificacion de costes.

### Persona B: Logica De Juego

Responsabilidades:

- Dominio del juego.
- Jugador, enemigos, objetos e inventario.
- Turnos.
- Combate.
- Economia.
- Tienda.
- Interactivos especiales.
- Tests de logica.

### Persona C: Interfaz, Persistencia Y Documentacion

Responsabilidades:

- JavaFX.
- JSON de configuracion.
- Guardado/carga.
- Logs visibles.
- Bocetos.
- UML.
- Memoria.
- Diario IA.

Nota:

- Todos deben entender todo el sistema.
- La division no significa aislar conocimiento.
- Antes del video, los tres deben poder explicar estructuras, grafo, movimiento, JSON e interfaz.

## 5. Orden De Prioridad

Prioridad 1:

- Estructuras propias.
- Grafo.
- Matriz.
- Movimiento.
- Turnos.
- Combate.
- Inventario.
- Victoria/derrota.

Prioridad 2:

- Tienda.
- Fichas.
- Sala 3 bloqueada.
- Amigo como llave narrativa.
- Ruleta rusa.
- Caminos minimos.

Prioridad 3:

- JSON.
- JavaFX.
- Logs.
- Excepciones.
- Pruebas.

Prioridad 4:

- Pulido visual.
- Balance.
- Ampliaciones.

Nota: aunque JSON y JavaFX sean prioridad 3 en orden de construccion, son obligatorios para entrega. No deben dejarse para el ultimo dia.

## 6. Criterios Para Subagentes

Todo subagente debe seguir estas reglas:

- Leer `JUEGO.md` antes de disenar o implementar.
- Leer `PLAN.md` antes de repartir tareas.
- No cambiar tematica, grafo, salas ni mecanicas cerradas sin pedir confirmacion.
- No introducir estructuras prohibidas para resolver requisitos evaluados.
- No meter ampliaciones antes de cerrar la base.
- Escribir tests para logica no visual cuando implemente funcionalidad.
- Mantener separacion entre modelo e interfaz.
- Si detecta una decision no documentada, debe devolver una pregunta o marcarla como pendiente.

## 7. Definicion De Base Funcional Terminada

La base se considera terminada cuando:

- El jugador puede iniciar en sala 1.
- Puede moverse por matrices 7x7.
- Puede cambiar de sala usando puertas.
- El grafo definitivo esta implementado.
- La puerta a sala 3 esta bloqueada hasta comprar llave.
- Hay enemigos y combate.
- Los enemigos sueltan fichas.
- El bar permite comprar objetos.
- El jugador puede rescatar al amigo en sala 6.
- El jugador puede llegar a sala 8.
- La salida solo da victoria con el amigo rescatado.
- La ruleta rusa puede jugarse opcionalmente y puede matar.
- Se puede guardar y cargar partida.
- Se puede cargar configuracion inicial desde JSON.
- Hay interfaz JavaFX basica.
- Hay log visible.
- Hay pruebas relevantes.

## 8. Ampliaciones Futuras

Solo despues de cerrar la base:

- Salida secreta.
- Minijuegos complejos de casino.
- Amigo como entidad fisica que acompana al jugador.
- Habitaciones con tamanos distintos.
- Mas enemigos con IA especifica.
- Eventos aleatorios.
- Sistema de reputacion o deuda.
- Arbol de habilidades.
- Tienda avanzada.
