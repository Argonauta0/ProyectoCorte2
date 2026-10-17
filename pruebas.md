# Matriz de pruebas – Papelería Campus

- **Fecha de ejecución:** 16 de octubre de 2026

> Las capturas de cada caso se muestran en la sección [Evidencias de pruebas](#7-evidencias-de-pruebas) y están en la carpeta [`evidencias/`](evidencias/).
 
---

## Resumen

| Bloque | Casos | Pasaron | Fallaron |
|---|---|---|---|
| Carga inicial y recarga | L1–L4 | 4 | 0 |
| Búsqueda y filtros | B1–B7 | 7 | 0 |
| Ciclo CRUD | C1–C8 | 8 | 0 |
| Validaciones | V1–V10 | 10 | 0 |
| Excepciones | E1–E4 | 3 | 1 (E3) |
 
---

## 1. Carga inicial y recarga

| Caso | Entrada / acción | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| L1 | Ejecutar `SELECT * FROM catalogo.producto ORDER BY codigo;` en DataGrip | Devuelve los 12 productos iniciales | El SELECT devuelve los 12 registros iniciales | ✅ |
| L2 | Abrir la aplicación con los 12 datos iniciales | Se muestran 12 filas ordenadas por código, iguales al SELECT, con el contador en 12 | Se muestran las 12 filas ordenadas por código (A001…T004), iguales al SELECT, y "Registros visibles: 12" | ✅ |
| L3 | Insertar una fila desde DataGrip y pulsar **Recargar** | La fila nueva aparece sin reiniciar la aplicación | La fila externa aparece al pulsar Recargar y el contador sube a 13. Después se eliminó | ✅ |
| L4 | Seleccionar una fila de la tabla | El formulario se llena; el ID se muestra pero no es editable | Los cinco campos se cargan y el ID aparece deshabilitado | ✅ |
 
---

## 2. Búsqueda y filtros (B1–B7)

| Caso | Entrada | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| B1 | Texto: `CUADERNO`, sin otros filtros | P001 y P002; contador 2 | Muestra P001 y P002; contador 2 | ✅ |
| B2 | Texto: `T00`, sin otros filtros | T001 a T004; contador 4 | Muestra T001, T002, T003 y T004; contador 4 | ✅ |
| B3 | Categoría: Papelería, texto vacío | P001 a P004; contador 4 | Muestra P001 a P004; contador 4 | ✅ |
| B4 | Solo con existencias, categoría Todas | 9 productos; excluye P004, T004 y A003 | Muestra 9 productos; no aparecen P004, T004 ni A003 | ✅ |
| B5 | Texto `USB` + Tecnología + Solo con existencias | T001 y T003; contador 2 | Muestra solo T001 y T003; contador 2 | ✅ |
| B6 | Texto: `ZZZ999`, sin otros filtros | 0 filas, contador 0 y aviso "Sin coincidencias" | Tabla vacía, contador 0 y aviso "Sin coincidencias" | ✅ |
| B7 | Pulsar **Limpiar filtros** después de B5 | Texto vacío, Todas, casilla desmarcada y 12 filas | Los controles se restablecen y se muestran las 12 filas | ✅ |
 
---

## 3. Ciclo CRUD con X001

Todas las operaciones de esta sección se hacen con los filtros limpios.

| Caso | Entrada / acción | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| C1 | Insertar `X001`, `Producto de prueba`, Accesorios, `10.00`, stock `1` | Mensaje de éxito; 13 filas; el contador se actualiza sin reiniciar | Mensaje "El producto se guardó correctamente"; la tabla muestra 13 filas y el contador 13 | ✅ |
| C2 | Verificar C1 con `SELECT ... WHERE codigo = 'X001';` y reiniciando la aplicación | X001 existe en la BD y persiste | El SELECT devuelve X001; tras reiniciar la aplicación sigue en la tabla | ✅ |
| C3 | Seleccionar X001 y actualizar el precio a `15.50` y el stock a `3` | Mensaje de éxito; solo X001 cambia; 13 filas | Mensaje "El producto se actualizó correctamente"; solo X001 cambia; siguen 13 filas | ✅ |
| C4 | Verificar C3 con SELECT y reiniciando la aplicación | X001 tiene precio 15.50 y stock 3 | El SELECT muestra 15.50 y 3; los valores persisten tras reiniciar | ✅ |
| C5 | Seleccionar X001, pulsar **Eliminar** y luego **Cancelar** | X001 se conserva; 13 filas | Aparece el cuadro de confirmación; al cancelar, X001 permanece y siguen 13 filas | ✅ |
| C6 | Seleccionar X001, pulsar **Eliminar** y luego **Aceptar** | Mensaje de éxito; 12 filas; contador 12 | Mensaje "El producto se eliminó correctamente"; 12 filas y contador 12 | ✅ |
| C7 | Verificar C6 con SELECT y reiniciando la aplicación | X001 ya no existe | El SELECT no devuelve filas; tras reiniciar, X001 no aparece | ✅ |
| C8 | Con el filtro Papelería activo, actualizar P001 a la categoría Accesorios. Después, revertir el cambio | P001 desaparece del listado filtrado | Tras actualizar, P001 deja de aparecer en el filtro Papelería. Después se restauró su categoría | ✅ |
 
---

## 4. Validaciones (V1–V10)

V1–V9 se probaron al **Guardar** y al **Actualizar**. V10 se probó en **Actualizar** y **Eliminar**. En todos los casos se verificó que la base de datos no cambió.

| Caso | Entrada | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| V1 | Código: vacío, `"   "` (solo espacios) y `ABCDEFGHIJK` (11 caracteres) | Se rechaza; exige de 1 a 10 caracteres tras trim | Los tres casos se rechazan con "El código debe tener de 1 a 10 caracteres", al guardar y al actualizar | ✅ |
| V2 | Nombre: `"   "`, `AB` y 81 caracteres | Se rechaza; exige de 3 a 80 caracteres tras trim | Los tres casos se rechazan con "El nombre debe tener de 3 a 80 caracteres", al guardar y al actualizar | ✅ |
| V3 | Categoría sin seleccionar (el ComboBox no permite escribir otros valores) | Se rechaza o se impide una categoría no permitida | Se rechaza con "Seleccione una categoría"; el ComboBox no permite escribir valores ajenos | ✅ |
| V4 | Precio: `abc` | Se rechaza sin cerrar la aplicación | Se rechaza con "El precio debe ser un número"; la aplicación sigue abierta | ✅ |
| V5 | Precio: `0` y `-1` | Se rechazan ambos valores | Ambos se rechazan con "El precio debe ser mayor que 0" | ✅ |
| V6 | Precio: `12.345` y `100000000.00` | Se rechaza el exceso de decimales y de rango, sin redondear | `12.345` se rechaza por decimales y `100000000.00` por el máximo permitido; no se redondea | ✅ |
| V7 | Stock: `abc` y `2.5` | Se rechazan el texto y las cantidades no enteras | Ambos se rechazan con "El stock debe ser un número entero entre 0 y 2147483647" | ✅ |
| V8 | Stock: `-1` y `2147483648`; luego `0` | Se rechazan los dos primeros y se acepta `0` | `-1` y `2147483648` se rechazan; `0` se acepta | ✅ |
| V9 | Crear un producto con código `" p001 "`; actualizar otro producto a `P001`; editar P001 conservando su código | Se rechazan los duplicados tras normalizar; editar P001 con su propio código se permite | Los dos primeros se rechazan con "Ya existe otro producto con el código P001"; la edición de P001 con su propio código se guarda | ✅ |
| V10 | Pulsar **Actualizar** y **Eliminar** sin una fila seleccionada | Se bloquea la acción e informa que se debe seleccionar un producto | Ambos botones muestran "Seleccione un producto de la tabla" y no hacen cambios | ✅ |
 
---

## 5. Excepciones (E1–E4)

| Caso | Entrada / acción | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|
| E1 | Cambiar el puerto a `5433` en `config.properties` y abrir la aplicación | Aviso comprensible; la aplicación no se cierra; no se presenta como 0 resultados | Alerta "No se pudo conectar con la base de datos"; la aplicación sigue abierta y muestra "Registros visibles: —" y "Error de conexión" | ✅ |
| E2 | Llamada directa a `ProductoDAO.insertar()`, sin la interfaz, con el código `P001` duplicado y con stock `-5` | Se gestiona la SQLException y la BD conserva su integridad | Se capturaron las excepciones con SQLState `23505` (UNIQUE) y `23514` (CHECK); `SELECT COUNT(*)` sigue en 12 | ✅ |
| E3 | Seleccionar el producto temporal X002, eliminarlo desde DataGrip y pulsar **Actualizar** en la aplicación (repetir con **Eliminar**) | No anuncia éxito; advierte que el producto ya no existe y permite recargar | Al pulsar Actualizar, la aplicación no mostró ninguna alerta | ❌ |
| E4 | Revisar la consola y la alerta durante E1 y E2 | Se registran la operación y el SQLState; el usuario ve un mensaje sin contraseñas ni stacktrace | La consola registra `[ERROR BD] Operación: ... \| SQLState: ...`; la alerta no muestra datos técnicos | ✅ |
 
---

## 6. Restauración final

| Acción | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|
| Ejecutar `sql/datos.sql` y luego `SELECT COUNT(*) FROM catalogo.producto;` | 12 registros, exactamente los iniciales | Se restauraron los 12 productos originales | ✅ |
 
---

## 7. Evidencias de pruebas

### Carga inicial y recarga: pruebas L1 a L4

#### L1

- **Prueba:** `SELECT * FROM catalogo.producto ORDER BY codigo;` en DataGrip.
- **Resultado:** el SELECT devuelve los 12 registros iniciales.
  ![L1 – SELECT inicial](evidencias/L1_SELECT_inicial.png)

#### L2

- **Prueba:** abrir la aplicación con los 12 datos iniciales.
- **Resultado:** 12 filas ordenadas por código, iguales al SELECT; contador 12.
  ![L2 – Listado inicial](evidencias/L2_CRUD_Listado_inicial.png)

#### L3

- **Prueba:** insertar una fila desde DataGrip y pulsar Recargar.
- **Resultado:** la fila externa aparece sin reiniciar la aplicación y el contador sube a 13. Después se eliminó.
  ![L3 – INSERT externo](evidencias/L3_INSERT.png)

![L3 – Tabla luego del INSERT](evidencias/L3_Tabla_luego_del_Insert.png)

#### L4

- **Prueba:** seleccionar una fila de la tabla.
- **Resultado:** los cinco campos se cargan en el formulario y el ID aparece deshabilitado.
  ![L4 – Selección de fila con ID no modificable](evidencias/L4_Seleccionando_fila_ID_no_modificable.png)

---

### Búsqueda y filtros: pruebas B1 a B7

#### B1

- **Prueba:** texto `CUADERNO`; sin otros filtros.
- **Resultado:** P001 y P002; contador 2.
  ![B1](evidencias/B1.png)

#### B2

- **Prueba:** texto `T00`; sin otros filtros.
- **Resultado:** T001 a T004; contador 4.
  ![B2](evidencias/B2.png)

#### B3

- **Prueba:** categoría Papelería; texto vacío.
- **Resultado:** P001 a P004; contador 4.
  ![B3](evidencias/B3.png)

#### B4

- **Prueba:** Solo con existencias; categoría Todas.
- **Resultado:** 9 productos; se excluyen P004, T004 y A003.
  ![B4](evidencias/B4.png)

#### B5

- **Prueba:** texto `USB` + Tecnología + Solo con existencias.
- **Resultado:** T001 y T003; contador 2.
  ![B5](evidencias/B5.png)

#### B6

- **Prueba:** texto `ZZZ999`; sin otros filtros.
- **Resultado:** 0 filas, contador 0 y aviso "Sin coincidencias".
  ![B6](evidencias/B6.png)

#### B7

- **Prueba:** pulsar Limpiar filtros después de B5.
- **Resultado:** texto vacío, Todas, casilla desmarcada y 12 filas.
  ![B7](evidencias/B7.png)

---

### Operaciones CRUD: ciclo con X001

#### C1 – Crear

- **Prueba:** insertar `X001`, `Producto de prueba`, Accesorios, `10.00`, stock `1`.
- **Resultado:** mensaje de éxito; 13 filas y contador 13 sin reiniciar.
  ![C1 – Mensaje de guardado](evidencias/C1_guardar_producto_mensaje.png)

![C1 – Tabla con X001](evidencias/C1_Tabla.png)

#### C2 – Verificar creación

- **Prueba:** `SELECT * FROM catalogo.producto WHERE codigo = 'X001';` y reiniciar la aplicación.
- **Resultado:** X001 existe en la base de datos y persiste tras reiniciar.
  ![C2 – SELECT de X001](evidencias/C2_Select.png)

#### C3 – Actualizar

- **Prueba:** seleccionar X001 y actualizar el precio a `15.50` y el stock a `3`.
- **Resultado:** mensaje de éxito; solo X001 cambia; siguen 13 filas.
  ![C3 – Mensaje de actualización](evidencias/C3_Actualizar_producto.png)

![C3 – Tabla actualizada](evidencias/C3_Tabla.png)

#### C4 – Verificar actualización

- **Prueba:** repetir el SELECT de X001 y reiniciar la aplicación.
- **Resultado:** X001 tiene precio 15.50 y stock 3, y persiste tras reiniciar.
  ![C4 – SELECT de X001 actualizado](evidencias/C4_Select_Actualizado.png)

#### C5 – Cancelar eliminación

- **Prueba:** seleccionar X001, pulsar Eliminar y luego Cancelar.
- **Resultado:** aparece la confirmación; al cancelar, X001 permanece y siguen 13 filas.
  ![C5 – Confirmación de eliminación](evidencias/C5_Alerta_eliminar.png)

![C5 – X001 se conserva](evidencias/C5_Tabla.png)

#### C6 – Confirmar eliminación

- **Prueba:** seleccionar X001, pulsar Eliminar y luego Aceptar.
- **Resultado:** mensaje de éxito; 12 filas y contador 12.
  ![C6 – Mensaje de eliminación](evidencias/C6_Eliminar.png)

![C6 – Tabla con 12 filas](evidencias/C6_Tabla.png)

#### C7 – Verificar eliminación

- **Prueba:** repetir el SELECT de X001 y reiniciar la aplicación.
- **Resultado:** el SELECT no devuelve filas; X001 ya no aparece.
  ![C7 – SELECT sin X001](evidencias/C7_Select_sin_registro_13.png)

#### C8 – Respeta los filtros vigentes

- **Prueba:** con el filtro Papelería activo, actualizar P001 a la categoría Accesorios. Después se revirtió el cambio.
- **Resultado:** P001 deja de aparecer en el listado filtrado.
  ![C8 – Búsqueda con filtro](evidencias/C8_Busqueda_con_filtro.png)

![C8 – Tras actualizar P001](evidencias/C8_Actualizar.png)
 
---

### Validaciones: pruebas V1 a V10

#### V1

- **Prueba:** código vacío, solo espacios u 11 caracteres.
- **Resultado:** se rechaza con "El código debe tener de 1 a 10 caracteres".
  ![V1 – Código vacío](evidencias/V1_Codigo_vacio.png)

#### V2

- **Prueba:** nombre solo con espacios, `AB` u 81 caracteres.
- **Resultado:** se rechaza con "El nombre debe tener de 3 a 80 caracteres".
  ![V2 – Nombre sin longitud mínima](evidencias/V2_Nombre_sin_longitud_minima.png)

#### V3

- **Prueba:** categoría sin seleccionar.
- **Resultado:** se rechaza con "Seleccione una categoría".
  ![V3 – Sin categoría](evidencias/V3_Sin_Categoria.png)

#### V4

- **Prueba:** precio `abc`.
- **Resultado:** se rechaza sin cerrar la aplicación.
  ![V4 – Precio como carácter](evidencias/V4_Precio_como_caracter.png)

#### V5

- **Prueba:** precio `0` y `-1`.
- **Resultado:** ambos se rechazan con "El precio debe ser mayor que 0".
  ![V5 – Precio 0](evidencias/V5_Precio_valor_nulo.png)

![V5 – Precio negativo](evidencias/V5_Precio_valor_negativo.png)

#### V6

- **Prueba:** precio `12.345` y `100000000.00`.
- **Resultado:** se rechaza el exceso de decimales y de rango, sin redondear.
  ![V6 – Exceso de decimales](evidencias/V6_Precio_exceso_de_decimales.png)

![V6 – Fuera de rango](evidencias/V6_Precio_fuera_de_rango.png)

#### V7

- **Prueba:** stock `abc` y `2.5`.
- **Resultado:** ambos se rechazan; el stock debe ser un número entero.
  ![V7 – Stock como texto](evidencias/V7_Stock_como_texto.png)

![V7 – Stock como decimal](evidencias/V7_Stock_como_decimal.png)

#### V8

- **Prueba:** stock `-1` y `2147483648`; luego `0`.
- **Resultado:** se rechazan los dos primeros y se acepta `0`.
  ![V8 – Stock negativo](evidencias/V8_STOCK_negativo.png)

![V8 – Stock fuera de rango](evidencias/V8_STOCK_Fuera_de_rango.png)

#### V9

- **Prueba:** crear un producto con código `" p001 "`; actualizar otro producto a `P001`.
- **Resultado:** se rechazan los duplicados tras normalizar; editar P001 con su propio código sí se permite.
  ![V9 – Crear con código existente](evidencias/V9_Crear_producto_con_codigo_existente.png)

![V9 – Actualizar a código existente](evidencias/V9_Actualizar_producto.png)

#### V10

- **Prueba:** pulsar Actualizar o Eliminar sin una fila seleccionada.
- **Resultado:** se bloquea la acción con "Seleccione un producto de la tabla".
  ![V10 – Eliminar sin selección](evidencias/V10_Eliminar_fila_sin_seleccionar.png)

---

### Excepciones: pruebas E1 a E4

#### E1

- **Prueba:** puerto `5433` en `config.properties`.
- **Resultado:** alerta "No se pudo conectar con la base de datos"; la aplicación no se cierra ni muestra 0 resultados.
  ![E1 – Fallo de conexión](evidencias/E1_Fallo_de_conexion.png)

#### E2

- **Prueba:** llamada directa a `ProductoDAO.insertar()` con código duplicado y con stock negativo, sin la interfaz.
- **Resultado:** se capturan las SQLException con SQLState `23505` y `23514`; la base de datos no cambia.
  ![E2 – Restricción SQL](evidencias/E2_Restriccion_SQL.png)

#### E3

- **Prueba:** eliminar X002 desde DataGrip y pulsar Actualizar en la aplicación.
- **Resultado:** la aplicación no mostró ninguna alerta. **Prueba fallida**, sin evidencia.
#### E4

- **Prueba:** revisar la consola y la alerta durante E1 y E2.
- **Resultado:** la consola registra la operación y el SQLState; el usuario ve un mensaje sin datos técnicos.
  ![E4 – Diagnóstico en consola](evidencias/E4_Diagnostico.png)
 
