# `@ConditionalAgent` vs `@SequenceAgent` en Quarkus LangChain4j

Dos formas de orquestar agentes: **secuencial** (siempre los mismos pasos) y
**condicional** (se elige una rama). Entender cuándo usar cada una es la clave del
diseño agentic.

## 1. Idea general

- **`@SequenceAgent`** → ejecuta los subagentes **en orden fijo**, uno detrás de otro.
- **`@ConditionalAgent`** → evalúa condiciones y ejecuta **solo la rama** que se
  cumple; las demás no corren.

Ambos se apoyan en el `AgenticScope`: los agentes leen entradas por nombre y escriben
su salida bajo su `outputKey`.

## 2. `@SequenceAgent`

- Import: `dev.langchain4j.agentic.declarative.SequenceAgent`.
- Atributos: `outputKey`, `subAgents`, `compensateOnError` (y `name`/`description`).
- **Siempre** se ejecutan todos los subagentes, en el orden declarado.
- Ideal cuando el proceso es un **pipeline**: cada paso depende del anterior.

```java
public interface TripPlannerWorkflow {

    @SequenceAgent(outputKey = "itinerary",
            subAgents = { DestinationResolverAgent.class, ForecastAgent.class,
                    ActivityPlannerAgent.class, ItineraryComposerAgent.class })
    ResultWithAgenticScope<String> plan(String destination, int days, String preferences);
}
```

## 3. `@ConditionalAgent`

- Import: `dev.langchain4j.agentic.declarative.ConditionalAgent`.
- Atributos: `outputKey`, `subAgents`, `compensateOnError`.
- Cada subagente va guardado por un **`@ActivationCondition`**.
- Se ejecuta el subagente cuya condición devuelve `true`; el resto se omite.

```java
public interface ExpertDispatcher {

    @ConditionalAgent(outputKey = "response",
            subAgents = { WeatherExpertAgent.class, GeocodingExpertAgent.class, TripExpertAgent.class })
    String dispatch(String request);

    @ActivationCondition(WeatherExpertAgent.class)
    static boolean weather(RequestIntent intent) { return intent == RequestIntent.WEATHER; }

    @ActivationCondition(GeocodingExpertAgent.class)
    static boolean geocoding(RequestIntent intent) { return intent == RequestIntent.GEOCODING; }

    @ActivationCondition(TripExpertAgent.class)
    static boolean trip(RequestIntent intent) { return intent == RequestIntent.TRIP; }
}
```

## 4. `@ActivationCondition`

- Import: `dev.langchain4j.agentic.declarative.ActivationCondition`.
- Debe ser un método **`static` que devuelve `boolean`**.
- Su `value` es la **clase del subagente** que guarda.
- Sus **parámetros se resuelven del `AgenticScope`** (por nombre). En el ejemplo, el
  parámetro `intent` lee la clave `intent` que escribió un agente previo.

> Regla: la condición **no inventa** el dato; lo lee del scope. Por eso, antes del
> condicional debe existir un paso (normalmente un clasificador) que escriba esa clave.

## 5. El patrón "router": secuencial + condicional

La forma más común combina ambos:

```java
public interface AgentWorkflow {

    @SequenceAgent(outputKey = "response",
            subAgents = { IntentRouterAgent.class, ExpertDispatcher.class })
    ResultWithAgenticScope<String> ask(@MemoryId String sessionId, String request);
}
```

1. `IntentRouterAgent` (un `@Agent`) clasifica y escribe `intent` en el scope.
2. `ExpertDispatcher` (`@ConditionalAgent`) lee `intent` y activa un solo experto.

Así, el **secuencial** garantiza el orden (clasificar → enrutar) y el **condicional**
decide la rama.

## 6. E1 vs E2 en este proyecto

| | **E1 `/agent/trip`** | **E2 `/agent/assistant`** |
| --- | --- | --- |
| Forma | `@SequenceAgent` | `@ConditionalAgent` + `@SequenceAgent` |
| ¿Cuántos agentes corren? | **Todos** (4) | **Uno** de los expertos (+ clasificador) |
| Orden | Fijo | Fijo hasta el router; luego la rama elegida |
| Entrada | Estructurada (`destination`, `days`, `preferences`) | Texto libre (`request`) |
| Decisión | No hay | `@ActivationCondition` sobre `intent` |
| Resultado | Determinista (mismo camino) | Depende de la clasificación del LLM |
| Coste | 4 llamadas al LLM (una por paso) | 2+ (clasificar + experto) |

- **E1** demuestra el **pipeline**: resolver destino → clima → actividades → itinerario.
- **E2** demuestra el **enrutado**: clasificar la intención y ejecutar el experto
  correspondiente.

## 7. ¿Cuándo usar cada uno?

**Usa `@SequenceAgent` cuando:**
- Todos los pasos deben ejecutarse siempre y en el mismo orden.
- Cada paso depende de la salida del anterior (pipeline/ETL).
- Quieres un flujo **determinista y fácil de depurar**.
- Ejemplos: generar → revisar → refinar → componer; validar → enriquecer → persistir.

**Usa `@ConditionalAgent` cuando:**
- Existen **variantes mutuamente excluyentes** y solo una aplica.
- La decisión depende de un dato calculado antes (intención, severidad, idioma, plan,
  tipo de usuario).
- Quieres ahorrar coste evitando ejecutar ramas innecesarias.
- Ejemplos: router de intenciones; por severidad (leve/grave); por canal (email/chat);
  por idioma; por plan (free/premium).

**Combínalos cuando:**
- Necesites un flujo fijo que en algún punto se bifurca (patrón router): secuencia
  para el armazón + condicional para la decisión.
- Cada rama sea a su vez un pipeline: `@ConditionalAgent` cuyos `subAgents` son
  workflows `@SequenceAgent`.

## 8. Reglas y errores frecuentes

- **Condiciones `static boolean`** y parámetros resolubles del scope; si el nombre no
  coincide con una clave existente, falla la resolución.
- El valor que leen las condiciones **debe estar en el scope antes** de evaluarlas
  (lo normal es que lo escriba un agente previo en la secuencia).
- La clase de `@ActivationCondition(X.class)` debe estar declarada en `subAgents`.
- Si **ninguna** condición se cumple, no corre ningún subagente y el `outputKey` del
  condicional puede quedar vacío; define siempre una rama por defecto (p. ej.
  `UNKNOWN`) o un `@ErrorHandler`.
- Evita que **varias** condiciones se cumplan a la vez si esperas una sola rama;
  documenta la precedencia.
- Nombra las claves con sentido (`intent`, `severity`, `language`) para que el flujo se
  lea sin mirar el código.
