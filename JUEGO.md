# Documento Maestro Del Juego

Este documento recoge las decisiones cerradas del juego. Cualquier persona, agente o subagente que trabaje en el proyecto debe leer este archivo antes de proponer cambios tecnicos o implementar funcionalidad.

Regla principal: no inventar mecanicas, salas, enemigos, objetos, conexiones ni requisitos que no aparezcan aqui. Si falta una decision, debe marcarse como pendiente o preguntarse al equipo.

## 1. Contexto Academico

Proyecto final conjunto de las asignaturas Metodologia de la Programacion y Estructuras de Datos.

El objetivo no es crear un videojuego complejo, sino un sistema software claro, funcional, bien estructurado y justificable academicamente.

El juego debe demostrar:

- Diseno orientado a objetos.
- Separacion entre logica e interfaz.
- JavaFX para la interfaz grafica.
- Habitaciones representadas como matrices.
- Mapa general representado como grafo propio.
- Estructuras de datos propias.
- Movimiento por turnos.
- Inventario.
- Objetos e interacciones.
- Enemigos y combate.
- Persistencia en JSON.
- Carga de configuracion inicial desde JSON.
- Guardado y carga de partida desde JSON.
- Logs de operaciones.
- Excepciones y gestion de errores.
- Pruebas unitarias para clases no visuales.
- UML y documentacion metodologica.
- Diario de uso de IA.

## 2. Restricciones Del Profesor

No se pueden usar estructuras estandar equivalentes para resolver las estructuras evaluadas.

No usar para estructuras principales:

- ArrayList.
- HashMap.
- LinkedList.
- Estructuras equivalentes de java.util para sustituir listas, colas, pilas, grafos o matrices evaluadas.

Si se usa alguna clase de biblioteca, debe ser para tareas no evaluadas y debe justificarse. Por defecto, evitar `java.util.*` en la logica central.

Estructuras propias obligatorias o recomendadas:

- Lista enlazada propia.
- Cola propia.
- Pila propia si se necesita deshacer o historial operativo.
- Lista circular propia si se usa para turnos o entidades ciclicas.
- Grafo propio para el mapa de habitaciones.
- Matriz propia para las habitaciones.
- Arbol propio si se usa para acciones, jerarquia de objetos o ampliaciones.

Requisito clave:

- El mapa del casino debe ser un grafo propio.
- Cada sala debe ser una matriz interna, inicialmente 7x7.
- Las estructuras deben documentarse con justificacion y coste de operaciones.

## 3. Concepto Del Juego

Titulo provisional: Casino Escape.

Tematica: casino parodico.

Tono: parodico, con situaciones absurdas y peligros de casino exagerados.

Premisa:

El jugador es una persona normal que entra en un casino para rescatar a su amigo borracho y ludopata. El amigo funciona como la llave narrativa de la partida: no se puede ganar sin encontrarlo y sacarlo del casino.

Objetivo principal:

1. Entrar al casino.
2. Explorar salas conectadas.
3. Conseguir recursos y sobrevivir.
4. Rescatar al amigo en la sala 6.
5. Llegar a la sala 8.
6. Salir por la salida exterior llevando al amigo.

Condicion de victoria:

- El jugador interactua con la salida exterior de la sala 8 teniendo `amigoRescatado = true`.

Condiciones de derrota:

- La vida del jugador llega a 0.
- Se agotan los turnos disponibles.
- El jugador muere al jugar voluntariamente al minijuego de ruleta rusa de la sala 8.

## 4. Mapa General

El mapa es un grafo no dirigido de 8 salas. Cada sala es un nodo. Cada conexion entre salas es una arista. Cada arista se representa dentro de la sala mediante una casilla de puerta.

Grafo definitivo:

```text
Sala 1 -> Sala 2, Sala 4
Sala 2 -> Sala 1, Sala 3, Sala 5
Sala 3 -> Sala 2
Sala 4 -> Sala 1, Sala 5, Sala 6
Sala 5 -> Sala 2, Sala 4, Sala 6, Sala 7
Sala 6 -> Sala 4, Sala 5
Sala 7 -> Sala 5, Sala 8
Sala 8 -> Sala 7, salida exterior
```

Vista conceptual:

```text
        [3]
         |
[1] -- [2] -- [5] -- [7] -- [8] -> Salida
 |             /  
[4] ---------   [6]
  \_____________/
```

Nota: la vista conceptual es orientativa. La autoridad es la lista de conexiones anterior.

## 5. Salas

Todas las salas de la version base usan matriz 7x7.

El diseno debe permitir dimensiones distintas en el futuro, porque el enunciado lo permite y lo valora.

Cada sala contiene:

- Matriz de celdas.
- Puertas como casillas concretas.
- Obstaculos como casillas no transitables.
- Objetos.
- Enemigos si corresponde.
- Elementos interactivos.
- Posicion del jugador cuando esta dentro.

### 5.1 Sala 1: Hall / Entrada

Funcion:

- Tutorial inicial.
- Distribucion de rutas.
- Zona segura.

Debe ensenar:

- Movimiento.
- Interaccion basica.
- Observar puertas.
- Recoger objetos.

Contenido obligatorio:

- Casilla de inicio.
- NPC de bienvenida.
- Arma simple inicial.
- Puerta a sala 2.
- Puerta a sala 4.
- Obstaculos decorativos.
- Sin enemigos.

NPC de bienvenida:

- Ocupa una casilla.
- Al interactuar muestra un mensaje.
- Mensaje sugerido: "Bienvenido al Casino Fortuna. Si buscas a tu amigo, pregunta en el bar... si sobrevives."

Arma simple:

- Nombre sugerido: Botella rota.
- Efecto: mejora pequena de ataque.

Objetivo de la sala:

- Permitir al jugador empezar equipado y elegir camino.

### 5.2 Sala 2: Tragaperras

Funcion:

- Primer combate real.

Debe introducir:

- Enemigos basicos.
- Obstaculos.
- Movimiento tactico.

Contenido obligatorio:

- Enemigo basico tematizado.
- Objeto de mejora de movimiento.
- Obstaculos de maquinas tragaperras.
- Puerta a sala 1.
- Puerta a sala 3 bloqueada.
- Puerta a sala 5.

Enemigo sugerido:

- Nombre: Maquina Tragaperras Averiada.
- Rol: enemigo basico.
- Drop: fichas de casino al morir.

Objeto de movimiento:

- Nombre: Cajetilla de tabaco.
- Efecto sugerido: +1 movimiento durante varios turnos.

Puerta a sala 3:

- Esta cerrada inicialmente.
- Requiere Llave de Tesoreria.
- La Llave de Tesoreria se compra en el bar de la sala 5.

Objetivo de la sala:

- Aprender combate y navegacion en matriz.

### 5.3 Sala 3: Tesoreria / Caja Fuerte

Funcion:

- Sala de recompensa importante.
- Zona opcional bloqueada.

Acceso:

- Solo desde sala 2.
- Requiere comprar la Llave de Tesoreria en el bar.

Contenido obligatorio:

- Sin enemigos.
- Traje de oro.
- Arma muy potente.
- Puerta a sala 2.
- Obstaculos de caja fuerte, lingotes o vitrinas.

Objetos:

- Traje de oro: potencia estadisticas, especialmente defensa.
- Baston gitano: arma muy potente.

Objetivo de la sala:

- Recompensar al jugador por explorar, matar enemigos, obtener fichas y comprar la llave.

### 5.4 Sala 4: Blackjack

Funcion:

- Ruta alternativa tactica.

Debe introducir:

- Caminos alternativos.
- Uso del entorno.
- Combate tematico.

Contenido obligatorio:

- Puerta a sala 1.
- Puerta a sala 5.
- Puerta a sala 6.
- Un enemigo.
- Un objeto ofensivo o defensivo.
- Drop de traje con escudo al matar al enemigo.
- Obstaculos de mesas y sillas de blackjack.

Enemigo sugerido:

- Nombre: Crupier de Blackjack.
- Rol: enemigo medio.
- Drop obligatorio: Traje con escudo.
- Drop adicional: fichas de casino.

Objeto sugerido en sala:

- Baraja afilada.
- Efecto: mejora ataque.

Objetivo de la sala:

- Dar una ruta distinta hacia el centro del mapa y hacia la sala 6.

### 5.5 Sala 5: Bar

Funcion:

- Hub central.
- Recuperacion.
- Gestion de inventario.
- Tienda.

Debe introducir:

- Recursos.
- Compra con fichas.
- Interaccion con NPCs.
- Gestion de consumibles.

Contenido obligatorio:

- Bar interactivo estilo tienda.
- Varios NPCs que ocupan casillas.
- Un NPC concreto que entrega un objeto especial.
- Puerta a sala 2.
- Puerta a sala 4.
- Puerta a sala 6.
- Puerta a sala 7.
- Obstaculos del bar.

Tienda:

- El bar es una casilla interactiva.
- Al interactuar permite comprar objetos con fichas de casino.

Objetos de tienda sugeridos:

- Llave de Tesoreria: abre la puerta bloqueada de sala 2 hacia sala 3.
- Vodka Redbull: consumible de recuperacion o velocidad.
- Coctel curativo: consumible de curacion.
- Chaleco de portero: proteccion.
- Consumibles inutiles o perjudiciales de tono parodico.

Nota de presentacion academica:

- Evitar nombres demasiado explicitos de drogas en memoria o interfaz final si no son necesarios.
- Usar nombres como Pastilla de dudosa procedencia, Caramelo VIP o Sustancia sospechosa.

NPC especial:

- Ocupa una casilla.
- Al interactuar entrega una Pastilla de dudosa procedencia.
- Solo debe entregar el objeto una vez.

Pastilla de dudosa procedencia:

- Efecto: durante 7 turnos permite al jugador moverse en linea recta hasta encontrar pared, obstaculo, enemigo, puerta u otro bloqueo.
- No debe permitir atravesar obstaculos.
- No debe permitir movimiento diagonal.
- Es una mecanica especial de movimiento, pero acotada.

Enemigo opcional recomendado:

- Nombre: Borracho Agresivo.
- Rol: enemigo moderado.
- Drop: fichas.

Objetivo de la sala:

- Actuar como centro del casino y punto de gestion de recursos.

### 5.6 Sala 6: Zona Privada

Nombre formal recomendado:

- Zona Privada.

Funcion:

- Sala del objetivo principal.
- Sala de riesgo/trampa.

Debe introducir:

- Trampas o auras perjudiciales.
- Decisiones de posicionamiento.
- Rescate del amigo.

Contenido obligatorio:

- Amigo borracho/ludopata.
- Acompanante peligrosa con drenaje de vida.
- Objeto util.
- Puerta a sala 4.
- Puerta a sala 5.
- Obstaculos decorativos.

Amigo:

- Es el objetivo principal narrativo.
- Funciona como llave narrativa.
- Al interactuar con el se activa `amigoRescatado = true`.
- En la version base no se mueve fisicamente con el jugador.
- En la version base se elimina de la sala o queda marcado como rescatado.
- La salida de la sala 8 requiere `amigoRescatado = true`.

Acompanante peligrosa:

- Ocupa una casilla.
- Si el jugador se acerca a rango 1, drena un porcentaje pequeno de vida.
- Rango 1 significa adyacencia ortogonal, y puede decidirse si incluye la propia casilla de interaccion.
- Efecto recomendado: perder un porcentaje pequeno de la vida maxima o vida actual.
- Debe registrarse en el log.

Objeto util sugerido:

- Chupito revitalizante.
- Perfume caro.
- Consumible de curacion o defensa.

Objetivo de la sala:

- Premiar exploracion, introducir peligro ambiental y permitir rescatar al amigo.

### 5.7 Sala 7: Sala VIP

Funcion:

- Antesala final.
- Aumento de dificultad.

Debe introducir:

- Combate serio.
- Presion antes del final.

Contenido obligatorio:

- Enemigo poderoso.
- Secuaces pequenos.
- Puerta a sala 5.
- Puerta a sala 8.
- Pocos recursos o ninguno.
- Obstaculos de zona VIP.

Enemigo principal sugerido:

- Nombre: Mafioso Ruso.
- Rol: enemigo fuerte.
- Drop: fichas.

Secuaces sugeridos:

- Nombre: Maton VIP.
- Rol: enemigos menores.
- Cantidad recomendada: 1 o 2.

Objetivo de la sala:

- Preparar al jugador para la sala final mediante un combate dificil.

### 5.8 Sala 8: Ruleta / Final

Funcion:

- Conclusion del juego.

Debe introducir:

- Objetivo final.
- Comprobacion de progreso.
- Minijuego optativo de alto riesgo.

Contenido obligatorio:

- Puerta a sala 7.
- Salida exterior.
- Minijuego optativo de ruleta rusa.
- Obstaculos de ruleta o decoracion.
- Sin enemigos.

Salida exterior:

- Es una casilla interactiva.
- Si `amigoRescatado = true`, el jugador gana.
- Si `amigoRescatado = false`, no puede salir y se muestra mensaje.

Mensaje sugerido si no tiene al amigo:

- "No puedes abandonar el casino sin tu amigo."

Mensaje sugerido de victoria:

- "Has escapado del casino con tu amigo. Victoria."

Ruleta rusa:

- Es un minijuego opcional.
- El jugador puede rechazarlo.
- Puede matar al jugador.
- No es necesario para ganar.
- Debe registrarse en el log.

Regla base sugerida:

- Al interactuar con la ruleta rusa se ofrece jugar.
- Si acepta, se calcula un resultado aleatorio.
- Resultado favorable: obtiene fichas u objeto.
- Resultado desfavorable: recibe dano potencialmente letal.
- Si vida llega a 0, derrota inmediata.

Objetivo de la sala:

- Cerrar la partida y decidir victoria o derrota.

## 6. Elementos Del Tablero

Cada celda de la matriz puede representar uno de estos estados o contenidos:

- Vacia.
- Obstaculo.
- Jugador.
- Enemigo.
- Objeto.
- Puerta.
- NPC.
- Trampa.
- Tienda.
- Salida exterior.
- Minijuego.

Invariante importante:

- Una celda no debe contener multiples entidades principales incompatibles.
- Ejemplo: no debe haber enemigo y objeto en la misma celda.
- Las puertas son casillas.
- Los obstaculos bloquean el movimiento.

## 7. Movimiento

Movimiento base:

- Ortogonal: arriba, abajo, izquierda, derecha.
- No hay movimiento diagonal directo.
- Cada paso cuesta 1 punto de movimiento.
- El jugador solo puede hacer un movimiento por turno.
- El movimiento puede recorrer varias casillas segun la velocidad del jugador.
- El destino debe estar dentro de las casillas alcanzables.

Calculo de casillas alcanzables:

- Debe hacerse mediante BFS sobre la matriz de la sala actual.
- Se deben respetar obstaculos, paredes y celdas ocupadas.
- Deben iluminarse o marcarse en interfaz las casillas alcanzables.

Movimiento especial por Pastilla de dudosa procedencia:

- Dura 7 turnos.
- Permite movimiento en linea recta hasta encontrar bloqueo.
- No atraviesa obstaculos ni paredes.
- No permite diagonales.
- Debe coexistir con las reglas normales de turno.

## 8. Turnos

Regla general:

- En cada turno el jugador puede hacer como maximo 1 movimiento y 1 accion.
- Primero actua el jugador.
- Despues actuan los enemigos de la habitacion actual.
- Los enemigos no tienen que moverse entre habitaciones.

Acciones posibles del jugador:

- Moverse.
- Atacar.
- Usar objeto.
- Recoger objeto.
- Interactuar con NPC.
- Interactuar con puerta.
- Comprar en tienda.
- Rescatar amigo.
- Activar minijuego.
- Interactuar con salida.
- No hacer nada.

Orden del turno:

1. Seleccion de movimiento del jugador.
2. Resolucion del movimiento.
3. Seleccion de accion del jugador.
4. Resolucion de accion.
5. Comprobacion de victoria o derrota.
6. Turno de enemigos en la sala actual.
7. Aplicacion de efectos ambientales o temporales si corresponde.
8. Reduccion de contadores temporales.
9. Fin de turno.

Regla de cambio de sala:

- Si el jugador abre o usa una puerta valida y cambia de sala, el turno termina automaticamente.

## 9. Combate

Formula del enunciado:

```text
vidaDefensor = vidaDefensor - maximo(0, ataque * (aleatorio * 2) - defensa)
```

Donde:

- `ataque` incluye ataque base y modificadores.
- `defensa` incluye defensa base y modificadores.
- `aleatorio` es un numero entre 0 y 1.
- El dano no puede ser negativo.
- La vida nunca debe quedar por debajo de 0.

Reglas base:

- El ataque cuerpo a cuerpo requiere objetivo adyacente.
- Defenderse es automatico.
- Si un enemigo muere, desaparece de la sala.
- Al morir, los enemigos dan fichas de casino.
- Algunos enemigos pueden soltar objetos concretos.

Drops obligatorios:

- Enemigos en general: fichas.
- Crupier de Blackjack: Traje con escudo.

## 10. Economia

Recurso:

- Fichas de casino.

Obtencion:

- Matar enemigos.
- Posibles recompensas futuras.
- Posibles minijuegos futuros.

Uso:

- Comprar objetos en el bar de la sala 5.
- Comprar la Llave de Tesoreria para entrar en sala 3.

La economia debe ser sencilla en la base.

No implementar todavia:

- Sistema economico complejo.
- Varias tiendas.
- Inflacion o precios dinamicos.
- Minijuegos complejos para farmear fichas.

## 11. Objetos

Tipos de objetos:

- Armas.
- Armaduras o ropa defensiva.
- Consumibles.
- Llaves.
- Objetos narrativos.
- Objetos inutiles o perjudiciales de tono parodico.

Objetos definidos:

- Botella rota: arma inicial, sala 1.
- Cajetilla de tabaco: mejora de movimiento temporal, sala 2.
- Llave de Tesoreria: comprada en bar, abre sala 3.
- Traje de oro: recompensa de sala 3, mejora estadisticas.
- Baston gitano: recompensa de sala 3, arma muy potente.
- Baraja afilada: objeto ofensivo de sala 4.
- Traje con escudo: drop del Crupier de Blackjack.
- Vodka Redbull: consumible de tienda.
- Coctel curativo: consumible de tienda.
- Chaleco de portero: objeto defensivo de tienda.
- Pastilla de dudosa procedencia: objeto especial entregado por NPC del bar.
- Chupito revitalizante o Perfume caro: objeto util de sala 6.

Reglas de inventario:

- El jugador tiene inventario visible constantemente.
- Los objetos pueden recogerse desde casillas adyacentes o desde la propia casilla segun se defina en implementacion, pero debe ser consistente.
- Los consumibles desaparecen al usarse.
- Los objetos equipables modifican estadisticas mientras estan equipados.
- Debe haber limite de equipamiento si se implementan varias piezas, pero para la base puede simplificarse a arma activa y armadura activa.

## 12. Enemigos

Enemigos definidos:

- Maquina Tragaperras Averiada: sala 2, enemigo basico.
- Crupier de Blackjack: sala 4, enemigo medio, suelta Traje con escudo.
- Borracho Agresivo: sala 5, enemigo moderado recomendado.
- Mafioso Ruso: sala 7, enemigo fuerte.
- Maton VIP: sala 7, secuaz pequeno.

IA base:

- Si el enemigo esta adyacente al jugador, ataca.
- Si no esta adyacente, se mueve hacia el jugador.
- Para moverse puede usar BFS simple dentro de la sala.
- Los enemigos no cambian de sala.

## 13. NPCs E Interactivos

NPC de bienvenida:

- Sala 1.
- Da mensaje tutorial.

NPC especial del bar:

- Sala 5.
- Entrega Pastilla de dudosa procedencia una sola vez.

Otros NPCs del bar:

- Ocupan casillas.
- Pueden mostrar mensajes.
- No son enemigos.

Bar:

- Sala 5.
- Casilla interactiva de tienda.

Amigo:

- Sala 6.
- Objetivo principal.
- Interactuar con el activa `amigoRescatado`.

Acompanante peligrosa:

- Sala 6.
- Entidad pasiva o trampa con aura de drenaje.

Ruleta rusa:

- Sala 8.
- Minijuego optativo con posibilidad de muerte.

Salida exterior:

- Sala 8.
- Da victoria si el amigo fue rescatado.

## 14. Interfaz JavaFX

La interfaz debe ser clara antes que estetica.

Pantalla principal:

- Zona central: matriz de sala actual.
- Panel de jugador: vida, ataque, defensa, movimiento, fichas, turnos restantes, amigo rescatado si aplica.
- Panel de inventario: objetos disponibles y equipados.
- Panel de acciones: botones segun contexto.
- Registro de eventos: log visible.
- Indicador de sala actual.
- Indicador de distancia minima o informacion de ruta si se implementa.

Representacion:

- Usar GridPane para la matriz.
- Cada celda debe mostrar su tipo o contenido.
- Las casillas alcanzables deben resaltarse.
- Las puertas deben distinguirse visualmente.

No se requieren graficos complejos.

## 15. Persistencia JSON

Deben existir dos tipos de JSON:

### 15.1 Configuracion Inicial

Debe incluir:

- Grafo de habitaciones.
- Dimensiones de cada sala.
- Distribucion inicial de celdas.
- Puertas y destinos.
- Obstaculos.
- Objetos iniciales.
- Enemigos iniciales.
- NPCs e interactivos.
- Posicion inicial del jugador.
- Turnos iniciales.
- Objetivo de victoria.

### 15.2 Estado De Partida

Debe incluir:

- Sala actual.
- Posicion del jugador.
- Vida y estadisticas del jugador.
- Fichas.
- Inventario.
- Objetos equipados.
- Estado de enemigos vivos o muertos.
- Objetos recogidos.
- Puertas abiertas o bloqueadas.
- Si la Llave de Tesoreria fue comprada.
- Si el amigo fue rescatado.
- Turnos restantes.
- Log o referencia al log.
- Estado general: en curso, victoria, derrota.

## 16. Logs

Todas las operaciones relevantes deben registrarse:

- Movimiento.
- Cambio de sala.
- Recoger objeto.
- Usar objeto.
- Equipar objeto.
- Ataque.
- Dano recibido.
- Muerte de enemigo.
- Fichas ganadas.
- Compra en tienda.
- Intento de abrir puerta bloqueada.
- Apertura de puerta.
- Rescate del amigo.
- Activacion de trampa o drenaje.
- Resultado de ruleta rusa.
- Victoria.
- Derrota.

Al final de la partida debe poder visualizarse el log completo.

## 17. Excepciones

Errores que deben gestionarse:

- Movimiento fuera de matriz.
- Movimiento a celda bloqueada.
- Movimiento a celda no alcanzable.
- Ataque sin objetivo valido.
- Interaccion invalida.
- Intentar abrir puerta bloqueada sin llave.
- Comprar sin fichas suficientes.
- Usar objeto inexistente.
- Error al leer JSON.
- Error al escribir JSON.
- Configuracion JSON invalida.

Excepciones personalizadas sugeridas:

- InvalidMoveException.
- InvalidActionException.
- LockedDoorException.
- NotEnoughChipsException.
- InvalidConfigurationException.
- PersistenceException.

## 18. Caminos Minimos E Informacion Al Jugador

El enunciado exige informar constantemente al jugador de:

- Distancia a la puerta adecuada.
- Numero minimo de habitaciones para salir.
- Posibilidad de comprar/ver camino en pantalla.

Implementacion conceptual:

- Usar BFS en el grafo de habitaciones para calcular ruta minima hacia la salida.
- Usar BFS en la matriz para calcular distancia hasta la puerta recomendada dentro de la sala actual.
- Si una puerta esta bloqueada, el sistema debe tener en cuenta si el jugador tiene llave o no, segun alcance de la version.

Decision base:

- Mostrar distancia minima general hacia la salida.
- Permitir una accion o compra futura para revelar el camino recomendado.

## 19. Ampliaciones Planificadas Pero No Base

No implementar en la primera base salvo que todo lo obligatorio este terminado:

- Salida secreta.
- Minijuegos complejos de tragaperras, blackjack o ruleta normal.
- Sistema economico avanzado.
- Tiendas multiples.
- Amigo como acompanante fisico que ocupa celda.
- Habitaciones generadas aleatoriamente.
- IA avanzada.
- Habitaciones con tamanos diferentes.
- Arbol de habilidades.
- Sistema complejo de efectos negativos.

## 20. Criterios De Implementacion

Prioridad absoluta:

1. Base funcional completa.
2. Claridad del diseno.
3. Estructuras propias.
4. Separacion logica/interfaz.
5. Persistencia JSON.
6. Pruebas.
7. Ampliaciones solo si la base esta estable.

Si hay conflicto entre una mecanica divertida y una entrega academica robusta, se prioriza la entrega academica.
