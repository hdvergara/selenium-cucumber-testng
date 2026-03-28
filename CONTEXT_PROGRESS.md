# Contexto de refactorización — QA Automation (memoria técnica)

Documento de seguimiento de la sesión de refactorización del framework **Selenium + Cucumber + TestNG** (POM). Sirve para retomar el hilo sin perder lo ya validado ni las líneas de trabajo abiertas.

---

## 1. WebActions: de `static WebDriver` a `private final WebDriver`

**Problema:** Un `WebDriver` estático en `WebActions` hacía que la última instancia **pisara** el driver de todos los hilos. Con **TestNG + Cucumber en paralelo** (`@DataProvider(parallel = true)` + `ThreadLocal` en `DriverManager`), varios escenarios podían usar **el mismo navegador** o uno incorrecto.

**Cambio:** Cada instancia de `WebActions` recibe un **`private final WebDriver driver`** asignado **solo en el constructor** (`Objects.requireNonNull(driver)`). Así, cada hilo usa su propio `WebDriver` alineado con `DriverManager` + `ThreadLocal`.

**Extra:** `getText` dejó de usar `elementToBeClickable` y pasó a esperar **visibilidad** (`visibilityOf`), adecuado para leer texto en enlaces/tablas sin exigir interactividad completa.

---

## 2. Configuración: `ConfigLoader` y `config.properties`

**Objetivo:** Centralizar URL base, timeouts y flags sin hardcodear en steps ni en páginas.

**Implementación:**

- **`ConfigLoader`** (`framework.automation.utils`): carga **`config.properties`** una vez en un **bloque estático** (`Properties` + classpath `config.properties`).
- **Ubicación del archivo:** `src/main/resources/config.properties` (Maven lo copia a `target/classes` y está disponible en runtime).
- **API expuesta:** `getBaseUrl()`, `getExplicitWait()`, `getBrowser()`, `getImplicitWait()`, `isHeadless()` — con **valores por defecto** si falta clave o archivo.
- **Uso en el proyecto:** `StoreStepDefinition` abre la URL con `ConfigLoader.getBaseUrl()`; `HomePage` usa `getExplicitWait()` para los timeouts; `DriverManager` respeta `headless` vía propiedad de sistema o `ConfigLoader.isHeadless()`.

**Nota:** Si existe un `config.properties` duplicado bajo `src/test/java/resources`, no es la ruta estándar de Maven para tests; la fuente de verdad acordada es **`src/main/resources/config.properties`**.

---

## 3. Lógica de aserciones: `HomePage` vs `StoreStepDefinition`

**Antes:** Métodos como `validateItemRemoved` e `isDisplayedLabelCartEmpty` en el Page Object lanzaban **`AssertionError`** internamente — mezcla de **página** y **criterio de negocio del test**.

**Después:**

- **`HomePage`** devuelve **datos o booleanos** (`validateItemRemoved(String)`, `isDisplayedLabelCartEmpty()`, `getCartProductNameText(String)`), sin aserciones de framework de test.
- **`StoreStepDefinition`** concentra **TestNG `Assert`** (`assertTrue`, `assertEquals`) con **mensajes descriptivos** en inglés en los pasos actuales.

**Beneficio:** Page Object reutilizable, aserciones visibles en la capa BDD/steps y reportes más claros.

---

## 4. Parametrización: menos `iPhone` fijo en localizadores

**Antes:** Un `@FindBy` con `text()='iPhone'` acoplaba el PO a un producto concreto.

**Después:**

- En **`ShoppingCartTableComponent`**, **`getProductLocator(String productName)`** y **`removeButtonForProduct(String productName)`** construyen **`By`** dinámicos con **`xpathLiteral(...)`** para escapar comillas en el nombre del producto.
- **`validateItemRemoved`** (en el componente de tabla) espera **`invisibilityOfElementLocated`** para el `By` del producto (más robusto que solo `findElements().isEmpty()`).
- **`getCartProductNameText`** usa la **primera fila visible** de la tabla del carrito (`FIRST_CART_PRODUCT_LINK`) para leer el texto y comparar en el step con el `{string}` del Gherkin (flujo un ítem por carrito).

**Gherkin:** pasos con **`{string}`** (ej. `"iPhone"`), sin depender del nombre del producto en los `@FindBy` estáticos.

---

## 5. Componentización del buscador (completado)

**Qué se hizo:** Los localizadores y acciones de la barra de búsqueda (`inputSearch`, `btnSearch`) viven en **`SearchComponent`** (`framework.automation.components`). El constructor recibe `WebDriver` e inicializa elementos con **`PageFactory.initElements`**. **`HomePage`** compone el componente y expone los mismos métodos públicos (`setInputSearch`, `clickOnSearchButton`) por **delegación**, de modo que los steps no cambian.

**Referencias:** `SearchComponent.java`, `HomePage.java`.

---

## 6. Componentización del mini-carrito (completado)

**Qué se hizo:** El widget de cabecera (`cart-total`), el enlace **View Cart** y el párrafo de mensaje de carrito vacío (junto al `h1` Shopping Cart) viven en **`MiniCartComponent`**. El constructor recibe el **`WebDriver` del hilo actual** (mismo patrón que `SearchComponent`: `PageFactory.initElements(driver, this)` + `new WebActions(driver)`). **`HomePage`** compone el componente y mantiene por **delegación** `clickOnItemsCartButton()`, `clickOnViewCartLabel()` e `isDisplayedLabelCartEmpty()` para no romper los steps.

**Referencias:** `MiniCartComponent.java`, `HomePage.java`.

---

## 7. Componentización de la tabla del carrito — Shopping Cart Table (completado)

**Qué se hizo:** La lógica de la **tabla de productos** del carrito (localizadores dinámicos `xpathLiteral` / `getProductLocator`, fila con botón **Remove**, `getCartProductNameText`, `validateItemRemoved` y `clickOnRemoveItem`) vive en **`ShoppingCartTableComponent`**. El constructor recibe el **`WebDriver` del hilo actual** (`Objects.requireNonNull`) y usa **`WebActions(driver)`** — sin `WebDriver` estático; coherente con `DriverManager` + `ThreadLocal`. **`HomePage`** delega esos métodos y solo conserva el **`@FindBy`** del primer resultado de búsqueda (**Add to cart**) más la composición de los cuatro bloques.

**Referencias:** `ShoppingCartTableComponent.java`, `HomePage.java`.

---

## 8. HomePage: totalmente refactorizada (estructura de componentes)

En términos de **estructura por componentes**, la página principal queda **cerrada**: **`SearchComponent`**, **`MiniCartComponent`**, **`ShoppingCartTableComponent`** y el propio **`HomePage`** (add-to-cart desde resultados de búsqueda) cubren el flujo actual sin mezclar responsabilidades en un solo PO monolítico. Los steps siguen hablando con **`HomePage`**; el **driver** se inyecta por constructor desde cada hilo como hasta ahora.

---

## 9. Genericidad de steps (completado)

**Qué se hizo:** Los pasos Gherkin usan **Cucumber expressions** con **`{string}`** para el nombre de producto y la navegación inicial no está acoplada a un texto fijo de “homepage”:

- **`Given the user is on the store page`** — abre **`ConfigLoader.getBaseUrl()`** (misma URL configurable que el resto del framework).
- **`When the user searches for {string}`** — delega en **`homePage.getSearchComponent()`** (`setInputSearch` / `clickOnSearchButton`) sin depender de frases rígidas del paso anterior.
- **Validación en carrito** — **`Then the shopping cart should contain product {string}`** y **`Then product {string} should no longer appear in the shopping cart`** enlazan con **`getCartProductNameText`**, **`validateItemRemoved(productName)`** y mensaje de carrito vacío para **cualquier** artículo representado en el feature entre comillas.

**Referencias:** `StoreStepDefinition.java`, `store.feature`, `HomePage.getSearchComponent()`.

---

## 10. Data-driven testing (enfoque)

**Objetivo:** Reutilizar el mismo flujo de steps con **distintos datos** (p. ej. varios productos o URLs) sin duplicar implementación Java.

**Cómo encaja con el proyecto actual:**

- Los pasos ya son **genéricos** (`{string}` en Gherkin); el producto sale del feature o de tablas **Examples** en el mismo escenario.
- **Próximo paso típico:** añadir una **Scenario Outline** + **`Examples`** en `store.feature` con columnas `product` (y opcionalmente otras variables) para ejecutar el mismo escenario con `"iPhone"`, `"MacBook"`, etc., sin nuevos métodos en `StoreStepDefinition`.
- Los datos pueden alimentarse también desde **TestNG DataProvider** o archivos externos si el equipo prioriza mantener datos fuera del `.feature`.

**Convención:** mantener **`config.properties`** + **`ConfigLoader`** para entorno; los datos de negocio del caso de prueba viven en Gherkin o en fuentes de datos explícitas, no hardcodeados en el Page Object.

---

## 11. CI/CD: GitHub Actions — `maven_tests.yml` (completado)

**Motor de ejecución** del framework en GitHub Actions (`.github/workflows/maven_tests.yml`):

| Aspecto | Detalle |
|--------|---------|
| **Disparadores** | `push` y `pull_request` hacia **`master`** y **`main`** (soporte multi-rama); **`schedule`** con cron `0 9 */14 * *` (aprox. **quincenal**, 09:00 **UTC**). |
| **Entorno** | `ubuntu-latest`. |
| **JDK** | **22**, distribución **Eclipse Temurin** (`actions/setup-java@v4`). |
| **Maven** | **Caché de dependencias** habilitado (`cache: maven`) para acelerar pipelines. |
| **Tests** | `mvn test -Dheadless=true` (Chrome instalado en el runner). |
| **Artefactos** | `actions/upload-artifact@v4` sube **`target/allure-results/`** con **`if: always()`** para conservar resultados Allure **aunque el job falle** (`if-no-files-found: warn`). |

**Nota:** El workflow anterior `ci.yml` (Java 18) fue **reemplazado** por este archivo como pipeline único.

---

## 12. Arquitectura Enterprise Finalizada

Estado declarado del proyecto en esta línea base:

- **Framework:** Selenium 4 + Cucumber + TestNG + Maven, POM con **componentes**, **configuración externalizada**, **steps genéricos** y **paralelismo** por hilo.
- **Calidad en CI:** **Java 22** alineado con `pom.xml` y enforcer; ejecución **headless** en nube; **reportes Allure** versionados como artefactos.
- **Operación:** integración continua en **ramas principales** (`master` / `main`) más **regresión programada** quincenal vía cron.

No implica cierre de mejoras funcionales futuras (más escenarios, más páginas), sino **cierre de arquitectura** de automatización lista para equipos enterprise.

---

## 13. Estado actual y siguientes mejoras (opcional)

**Hecho y validado en esta sesión (entre otros):**

- Paralelismo seguro (`TestRunnerStore` + `@DataProvider(parallel = true)` + `ThreadLocal` en `DriverManager`).
- Allure: captura en fallo con `Allure.addAttachment` en `@After` (cuando aplica).
- Ajustes de esperas en carrito (`visibilityOfElementLocated` / `invisibilityOfElementLocated`) para estabilizar el flujo OpenCart.
- **Componentización del buscador:** `SearchComponent` + delegación en `HomePage` (sección 5).
- **Componentización del mini-carrito:** `MiniCartComponent` + delegación en `HomePage` (sección 6).
- **Componentización de la tabla del carrito:** `ShoppingCartTableComponent` + delegación en `HomePage` (sección 7).
- **Genericidad de steps** y expresiones `{string}` (sección 9); línea base para **data-driven** descrita en sección 10.
- **CI/CD** con `maven_tests.yml` (sección 11).

**Posibles extensiones (no bloqueantes):** `Scenario Outline` + `Examples`, nuevos flujos (checkout, cuenta), más features Cucumber, u otras páginas top-level fuera de esta home.

**Convención:** `config.properties` + `ConfigLoader` como capa de configuración única.

---

## Referencias rápidas en el repo

| Tema | Dónde mirar |
|------|-------------|
| `WebActions` final driver | `src/main/java/framework/automation/utils/WebActions.java` |
| `ConfigLoader` | `src/main/java/framework/automation/utils/ConfigLoader.java` |
| `config.properties` | `src/main/resources/config.properties` |
| Steps + aserciones | `src/test/java/framework/automation/steps/StoreStepDefinition.java` |
| Page principal (composición) | `src/main/java/framework/automation/pages/HomePage.java` |
| Tabla del carrito (componente) | `src/main/java/framework/automation/components/ShoppingCartTableComponent.java` |
| Buscador (componente) | `src/main/java/framework/automation/components/SearchComponent.java` |
| Mini-carrito (componente) | `src/main/java/framework/automation/components/MiniCartComponent.java` |
| Runner paralelo | `src/test/java/framework/automation/runners/TestRunnerStore.java` |
| CI GitHub Actions | `.github/workflows/maven_tests.yml` |

---

*Última actualización: sesión de refactorización documentada para continuidad del trabajo.*
