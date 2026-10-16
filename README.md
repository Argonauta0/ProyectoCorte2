# Catálogo de productos – Papelería Campus

Aplicación de escritorio en **JavaFX** conectada a **PostgreSQL** mediante **JDBC** para consultar, buscar, filtrar, crear, actualizar y eliminar los productos de la Papelería Campus. La información se muestra en un `TableView` y se mantiene sincronizada con la base de datos.

- **Autor:** Valle Alonso Moises Abraham
- **Laboratorio:** Acceso a datos y visualización en TableView

---

## 1. Versiones utilizadas

| Herramienta | Versión |
|---|---|
| Java (JDK) | 21 (Microsoft Build of OpenJDK 21.0.12) |
| JavaFX | 21.0.6 |
| PostgreSQL | 18.6 |
| Driver JDBC PostgreSQL | 42.7.13 |
| Maven | Maven Wrapper incluido (`mvnw`), `maven-compiler-plugin` 3.13.0 |
| Plugin de ejecución | `javafx-maven-plugin` 0.0.8 |
| IDE | IntelliJ IDEA 2026.2.3 |

> **Nota sobre el entorno de desarrollo.** El proyecto se desarrolló en **Fedora 44 Workstation**, con PostgreSQL 18.6 en un contenedor de **Podman** (imagen `docker.io/library/postgres:18`, puerto `5432`) gestionado desde DataGrip. Este entorno no es necesario para ejecutar el proyecto. Las instrucciones siguientes están escritas para **Windows con PostgreSQL y pgAdmin instalados**.

---

## 2. Dependencias (`pom.xml`)

| Dependencia | Versión | Uso |
|---|---|---|
| `org.openjfx:javafx-controls` | 21.0.6 | Controles de la interfaz (TableView, ComboBox, etc.) |
| `org.openjfx:javafx-fxml` | 21.0.6 | Carga de la interfaz desde `producto-view.fxml` |
| `org.postgresql:postgresql` | 42.7.13 | Driver JDBC para PostgreSQL |
| `org.junit.jupiter` (api y engine) | 5.12.1 | Incluidas por la plantilla del proyecto (alcance `test`); no se usan |

Maven descarga las dependencias automáticamente al abrir o compilar el proyecto.

---

## 3. Requisitos previos

- **JDK 21** instalado.
- **PostgreSQL** (versión 13 o superior) instalado y en ejecución en `localhost:5432`.
- **pgAdmin 4**, que se instala junto con PostgreSQL en Windows.
- **IntelliJ IDEA**. Otro IDE compatible con Maven también funciona.

---

## 4. Crear la base de datos en pgAdmin

Los scripts están en la carpeta `sql/` del proyecto:

| Archivo | Contenido |
|---|---|
| `sql/estructura.sql` | Crea el esquema `catalogo` y la tabla `producto` (ver el detalle debajo de esta tabla). |
| `sql/datos.sql` | Vacía la tabla, reinicia el identificador y carga exactamente los 12 productos iniciales. |

La tabla `producto` se crea con estas restricciones:

- Clave primaria autogenerada (`GENERATED ALWAYS AS IDENTITY`).
- `NOT NULL` en todos los campos.
- `UNIQUE` en `codigo`.
- Cinco `CHECK`: código normalizado no vacío, nombre de 3 a 80 caracteres, categoría permitida, precio positivo y stock no negativo.

> La tabla se crea en el esquema **`catalogo`**, no en `public`.

### 4.1 Crear la base

1. Abra **pgAdmin 4** y conéctese a su servidor PostgreSQL.
2. Haga clic derecho en **Databases** y elija **Create → Database…**
3. En **Database** escriba `papeleria_campus` y pulse **Save**.

### 4.2 Crear la tabla

1. Seleccione la base `papeleria_campus`.
2. Abra **Tools → Query Tool**.
3. Haga clic en el ícono **Open File** (carpeta) y abra `sql/estructura.sql`.
4. Pulse **Execute** (▶) o presione **F5**.

### 4.3 Cargar los 12 productos

1. En el mismo **Query Tool**, abra `sql/datos.sql`.
2. Pulse **Execute** (▶) o presione **F5**.

### 4.4 Verificar

En el Query Tool ejecute:

```sql
SELECT * FROM catalogo.producto ORDER BY codigo;
```

Debe devolver **12 filas**: P001–P004, T001–T004 y A001–A004.

### 4.5 Restaurar los datos iniciales

Después de cada bloque de pruebas, vuelva a ejecutar **solo** `sql/datos.sql` en el Query Tool.

El `TRUNCATE ... RESTART IDENTITY` elimina los registros de prueba y reinicia los ID, de modo que la tabla queda con exactamente los 12 productos originales.

> Los archivos `.sql` están guardados en **UTF-8** para que *Papelería* y *Tecnología* se inserten con tilde y cumplan el `CHECK` de categoría.

---

## 5. Configurar la conexión

Los datos de conexión **no están en el código fuente**. La clase `ConexionBD` los lee desde el archivo `config.properties`, que va en la raíz del proyecto.

1. En la raíz del proyecto, haga una copia de `config.example.properties` y renómbrela como `config.properties`. También puede hacerlo desde el símbolo del sistema (CMD):
   ```cmd
   copy config.example.properties config.properties
   ```
2. Abra `config.properties` y escriba la contraseña de su usuario de PostgreSQL:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/papeleria_campus?currentSchema=catalogo
   db.user=postgres
   db.password=TU_CONTRASEÑA
   ```

Tenga en cuenta lo siguiente:

- `?currentSchema=catalogo` hace que las consultas usen el esquema `catalogo`.
- `config.properties` está excluido en `.gitignore` y **no se sube al repositorio**. Solo se versiona `config.example.properties`, que no contiene secretos.
- Para probar el fallo de conexión (E1), cambie temporalmente el puerto a `5433` en `config.properties`.

---

## 6. Ejecutar el proyecto

Antes de ejecutar, el servicio de PostgreSQL debe estar en marcha. En Windows, revise que esté iniciado en **Servicios** (`services.msc`), con un nombre como `postgresql-x64-18`.

### Opción A: desde IntelliJ IDEA

1. Abra la carpeta del proyecto con **File → Open**. IntelliJ detecta el `pom.xml`.
2. Espere a que Maven descargue las dependencias.
3. Abra la clase `com.desarrollo.proyectocorteii.Launcher` y pulse **Run** (▶).
4. En **Run → Edit Configurations**, verifique que el **Working directory** sea la raíz del proyecto, donde está `config.properties`.

### Opción B: desde el símbolo del sistema (CMD)

Desde la raíz del proyecto:

```cmd
mvnw.cmd clean javafx:run
```

---

## 7. Estructura del proyecto

```
ProyectoCorteII/
│
├── .gitignore                    ← excluye config.properties, target/ y .idea/
├── config.example.properties     ← ejemplo de configuración sin secretos
├── config.properties             ← configuración real (se crea localmente, no se sube)
├── mvnw                          ← Maven Wrapper (Linux / macOS)
├── mvnw.cmd                      ← Maven Wrapper (Windows)
├── pom.xml                       ← dependencias y configuración de Maven
├── pruebas.md                    ← matriz de pruebas
├── README.md                     ← este documento
│
├── evidencias/                   ← capturas de las pruebas
│
├── sql/
│   ├── estructura.sql            ← esquema, tabla y restricciones
│   └── datos.sql                 ← 12 productos iniciales
│
└── src/
    └── main/
        │
        ├── java/
        │   └── com/desarrollo/proyectocorteii/
        │       │
        │       ├── Launcher.java                 ← punto de entrada (main)
        │       ├── ProductoApplication.java      ← carga el FXML y muestra la ventana
        │       ├── module-info.java              ← configuración del módulo
        │       │
        │       ├── Controller/
        │       │   └── ProductoController.java   ← eventos, normalización y validaciones
        │       │
        │       ├── DAO/
        │       │   ├── ConexionBD.java           ← lee config.properties y abre la conexión
        │       │   └── ProductoDAO.java          ← todo el SQL (SELECT, INSERT, UPDATE, DELETE)
        │       │
        │       └── Model/
        │           └── Producto.java             ← entidad (precio en BigDecimal)
        │
        └── resources/
            └── com/desarrollo/proyectocorteii/
                └── producto-view.fxml            ← interfaz gráfica
```

---

## 8. Flujo de la aplicación

```
Interfaz (FXML) → Controlador → DAO → PostgreSQL
       ↑                                   │
       └──── ObservableList ← Producto ← ResultSet
```

1. **Interfaz (`producto-view.fxml`).** El usuario escribe en el formulario o en los filtros y pulsa un botón.
2. **Controlador (`ProductoController`).**
    - Normaliza los datos: aplica `trim()` al código y al nombre, y `toUpperCase()` al código.
    - Ejecuta las validaciones V1–V10 antes de llamar a la base de datos. Si un dato es inválido, muestra una alerta y no envía nada a PostgreSQL.
    - Llama al método correspondiente del DAO. El controlador no contiene SQL.
3. **DAO (`ProductoDAO`).**
    - Contiene todas las consultas SQL y usa `PreparedStatement` con parámetros `?`. Ningún valor ingresado por el usuario se concatena al SQL.
    - Usa `executeQuery()` para las lecturas y `executeUpdate()` para las escrituras.
    - Cierra `Connection`, `PreparedStatement` y `ResultSet` con try-with-resources.
    - Si ocurre una `SQLException`, imprime en consola la operación y el `SQLState`, y la propaga al controlador.
4. **PostgreSQL.** Ejecuta la consulta y aplica sus propias restricciones (`UNIQUE`, `CHECK`, `NOT NULL`) como segunda capa de protección.
5. **Regreso al TableView.**
    - Cada fila del `ResultSet` se convierte en un objeto `Producto`.
    - La lista de productos se envuelve en una `ObservableList<Producto>`, que alimenta el `TableView`.
    - Se actualiza el contador de registros visibles.
    - Después de guardar, actualizar o eliminar, la tabla se recarga desde PostgreSQL respetando los filtros activos.

### Búsqueda y filtros

La búsqueda usa tres filtros:

- Texto parcial en nombre **o** código, con `ILIKE`.
- Categoría.
- "Solo con existencias" (`stock > 0`).

Los tres se combinan con `AND` en **una sola consulta** a PostgreSQL; no se filtra en memoria. Si no hay resultados, la tabla queda vacía, el contador en 0 y se muestra el aviso **"Sin coincidencias"**.

### Manejo de errores

| Caso | Comportamiento |
|---|---|
| E1. Sin conexión | Muestra una alerta comprensible. La aplicación no se cierra y el error no se presenta como un listado vacío. |
| E2. Violación de `UNIQUE` o `CHECK` | La `SQLException` se captura y la base de datos no cambia. |
| E3. Cero filas afectadas | Si `executeUpdate()` devuelve 0 en `UPDATE` o `DELETE`, advierte que el producto ya no existe y recarga la tabla. No muestra mensaje de éxito. |
| E4. Diagnóstico | Registra en consola `[ERROR BD] Operación: ... \| SQLState: ...`. Al usuario le muestra un mensaje limpio, sin contraseñas ni trazas técnicas. |

---

## 9. Pruebas

La matriz completa está en [`pruebas.md`](pruebas.md) y cubre:

- B1–B7: búsqueda y filtros.
- El ciclo CRUD con el producto temporal X001.
- V1–V10: validaciones.
- E1–E4: excepciones.

Las capturas de pantalla están en la carpeta [`evidencias/`](evidencias/).

---

## 10. Fuentes y herramientas de apoyo

**Material de clase**

- La clase práctica de JavaFX con conexión a PostgreSQL.
- El ejercicio de búsqueda parametrizada y CRUD.

Gran parte del código se reutilizó con ligeras modificaciones.

**Gemini (Google), asistente de IA, usado en la planeación para:**

- Interpretar el documento del laboratorio y resumir los requisitos.
- Proponer la estructura del proyecto.
- Generar el script SQL de la tabla.
- Generar la vista FXML.

**Claude (Anthropic), asistente de IA, usado para:**

- Revisar la solución con base en la rúbrica de evaluación.
- Generar el código de Controller.
- Generar el código de DAO.
- Redactar este README.
- Configurar el `.gitignore`.
- Modificar `ConexionBD` para leer la conexión desde `config.properties`.

El código generado con apoyo de estas herramientas fue revisado, adaptado y probado por el autor.