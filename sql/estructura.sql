DROP TABLE IF EXISTS producto;

CREATE SCHEMA catalogo;

-- Crear la tabla producto
CREATE TABLE producto (
                          id_producto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          codigo      VARCHAR(10)   NOT NULL UNIQUE,
                          nombre      VARCHAR(80)   NOT NULL,
                          categoria   VARCHAR(20)   NOT NULL,
                          precio      NUMERIC(10,2) NOT NULL,
                          stock       INTEGER       NOT NULL,

    -- 1. CHECK código: de 1 a 10 caracteres, sin espacios laterales y en mayúsculas
                          CONSTRAINT chk_producto_codigo CHECK (
                              char_length(codigo) BETWEEN 1 AND 10
                                  AND codigo = btrim(codigo)
                                  AND codigo = upper(codigo)
                              ),

    -- 2. CHECK nombre: de 3 a 80 caracteres tras quitar espacios laterales
                          CONSTRAINT chk_producto_nombre CHECK (
                              char_length(btrim(nombre)) BETWEEN 3 AND 80
                              ),

    -- 3. CHECK categoría: solo permite los 3 valores exactos del catálogo
                          CONSTRAINT chk_producto_categoria CHECK (
                              categoria IN ('Papelería', 'Tecnología', 'Accesorios')
                              ),

    -- 4. CHECK precio: mayor que 0 y dentro del límite de NUMERIC(10,2)
                          CONSTRAINT chk_producto_precio CHECK (
                              precio > 0 AND precio <= 99999999.99
                              ),

    -- 5. CHECK stock: no negativo (el tope 2147483647 ya lo impone el tipo INTEGER)
                          CONSTRAINT chk_producto_stock CHECK (
                              stock >= 0
                              )
);