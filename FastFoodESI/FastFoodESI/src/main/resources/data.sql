-- ==========================================
-- 1. CATALOGOS (Tablas maestras)
-- ==========================================

-- TURNOS
INSERT INTO turnos (id, nombre) VALUES (random_uuid(), 'Mañana');
INSERT INTO turnos (id, nombre) VALUES (random_uuid(), 'Tarde');
INSERT INTO turnos (id, nombre) VALUES (random_uuid(), 'Noche');

-- ESTADOS EMPLEADO
INSERT INTO estados_empleado (id, nombre) VALUES (random_uuid(), 'Activo');
INSERT INTO estados_empleado (id, nombre) VALUES (random_uuid(), 'De Baja');
INSERT INTO estados_empleado (id, nombre) VALUES (random_uuid(), 'Vacaciones');

-- ESTADOS PEDIDO
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'PENDIENTE');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'RECIBIDO');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'EN_COCINA');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'LISTO');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'ENTREGADO');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'CANCELADO');

-- TIPOS DE PRODUCTO
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Hamburguesas');
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Pizzas'); 
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Bebidas');
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Complementos');
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Postres');
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Menus');
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Ofertas');

-- ==========================================
-- 2. ACTORES (Propietarios, Negocios, Clientes)
-- ==========================================

-- PROPIETARIO 1: Jefe Supremo
INSERT INTO propietarios (id, dni, nombre, apellido, correo, password, fecha_nac) 
VALUES (random_uuid(), '12345678A', 'Jefe', 'Supremo', 'admin@esi.es', '{noop}1234', '1980-01-01');

-- PROPIETARIO 2: Juan Dueño (TU USUARIO DE PRUEBA)
INSERT INTO propietarios (id, nombre, apellido, dni, correo, password, fecha_nac) 
VALUES (random_uuid(), 'Juan', 'Dueño', '87654321B', 'juan@esi.es', '{noop}pass123', '1985-05-05');

-- Clientes

INSERT INTO clientes (id, nombre, apellido, dni, correo, telefono, password)
VALUES (random_uuid(), 'Pepito', 'Perez', '87654321Z', 'cliente@esi.es', '600123456', '1234');

-- NEGOCIOS
INSERT INTO negocios (id, nombre, direccion, telefono, propietario_id) 
VALUES (random_uuid(), 'Burgers ESI Centro', 'Calle Falsa 123', '911223344', 
(SELECT id FROM propietarios WHERE correo='admin@esi.es'));

INSERT INTO negocios (id, nombre, direccion, telefono, propietario_id) 
VALUES (random_uuid(), 'Burgers ESI Playa', 'Paseo Marítimo 1', '955667788', 
(SELECT id FROM propietarios WHERE correo='admin@esi.es'));

INSERT INTO negocios (id, nombre, direccion, telefono, correo, propietario_id) 
VALUES (random_uuid(), 'Pizzería Campus', 'Calle Aulario 5', '956222222', 'pizza@esi.es',
(SELECT id FROM propietarios WHERE correo='juan@esi.es'));

-- ==========================================
-- 3. EMPLEADOS
-- ==========================================

-- Empleado 1: JUAN (Burgers Centro)
INSERT INTO empleados (
    id, dtype, dni, nombre, apellido, correo, salario, 
    negocio_id, estado_empleado_id, turno_id, propietario_id
)
VALUES (
    random_uuid(), 'Camarero', '11111111E', 'Juan', 'El Rápido', 'juan@esi.es', 1500.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Mañana'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es')
);

-- Empleado 2: ANA (Burgers Playa)
INSERT INTO empleados (
    id, dtype, dni, nombre, apellido, correo, salario, 
    negocio_id, estado_empleado_id, turno_id, propietario_id
)
VALUES (
    random_uuid(), 'Cocina', '22222222E', 'Ana', 'Tranquila', 'ana@esi.es', 1200.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Playa'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Tarde'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es')
);

-- ==========================================
-- 4. PRODUCTOS
-- ==========================================

INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Big Burger', 'Doble carne', 10.50, 100,
'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Hamburguesas'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

-- 2. Kevin Bacon (Con FOTO)
INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Kevin Bacon', 'Carne con bacon crujiente', 12.50, 100,
'https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Hamburguesas'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

-- 3. Coca Cola Zero (Con FOTO)
INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Coca Cola Zero', 'Sin azúcar', 2.50, 200,
'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Bebidas'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

-- 4. Pizza Margarita (Con FOTO)
INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Pizza Margarita', 'Tomate, mozzarella y albahaca', 8.50, 50,
'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Pizzas'),
(SELECT id FROM negocios WHERE nombre='Pizzería Campus'));

-- 1. HAMBURGUESAS (Usan 'Burgers ESI Centro')

INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Veggie Deluxe', 'Hamburguesa vegana de lentejas y aguacate', 11.00, 30,
'https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Hamburguesas'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));


-- 2. PIZZAS (Usan 'Pizzería Campus')
INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Pepperoni Lover', 'Doble de pepperoni y queso extra', 13.50, 40,
'https://images.unsplash.com/photo-1628840042765-356cda07504e?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Pizzas'),
(SELECT id FROM negocios WHERE nombre='Pizzería Campus'));

INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), '4 Quesos', 'Mozzarella, Gorgonzola, Parmesano y Cheddar', 12.00, 40,
'https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Pizzas'),
(SELECT id FROM negocios WHERE nombre='Pizzería Campus'));


-- 3. BEBIDAS
INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Fanta Naranja', 'Refresco de naranja con gas', 2.50, 100,
'https://images.unsplash.com/photo-1625772299848-391b6a87d7b3?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Bebidas'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Agua Mineral', 'Botella de 500ml', 1.50, 150,
'https://images.unsplash.com/photo-1564419320461-6870880221ad?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Bebidas'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));


-- 4. COMPLEMENTOS
INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Nuggets de Pollo', '6 unidades con salsa barbacoa', 4.50, 60,
'https://images.unsplash.com/photo-1562967914-608f82629710?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Complementos'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Aros de Cebolla', 'Crujientes y dorados', 3.50, 50,
'https://images.unsplash.com/photo-1639024471283-03518883512d?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Complementos'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));


-- 5. POSTRES

INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Brownie Chocolate', 'Con nueces y helado', 4.90, 35,
'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Postres'),
(SELECT id FROM negocios WHERE nombre='Pizzería Campus'));


-- 6. MENUS
INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Menú Estudiante', 'Burger Clásica + Patatas + Bebida', 12.00, 100,
'https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Menus'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));



-- 7. OFERTAS
INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Pack Fiesta', '5 Hamburguesas variadas', 35.00, 20,
'https://images.unsplash.com/photo-1551782450-a2132b4ba21d?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Ofertas'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Martes Loco', 'Pizza Margarita al 50%', 4.25, 200,
'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=500',
(SELECT id FROM tipos_producto WHERE nombre='Ofertas'),
(SELECT id FROM negocios WHERE nombre='Pizzería Campus'));

-- ==========================================
-- 5. PEDIDOS (CORREGIDO: Con negocio_id)
-- ==========================================

-- PEDIDO 1: HOY (En Burgers Centro)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id)
VALUES (random_uuid(), CURRENT_TIMESTAMP, 65.00, 
(SELECT id FROM clientes WHERE dni='99999999C'),
(SELECT id FROM empleados WHERE nombre='Juan'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 5, 10.50, (SELECT id FROM pedidos WHERE total=65.00), (SELECT id FROM productos WHERE nombre='Big Burger'));


-- PEDIDO 2: MES PASADO (En Burgers Centro)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id)
VALUES (random_uuid(), DATEADD('MONTH', -1, CURRENT_TIMESTAMP), 200.00, 
(SELECT id FROM clientes WHERE dni='99999999C'),
(SELECT id FROM empleados WHERE nombre='Juan'), 
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));


-- PEDIDO 3: AÑO PASADO (En Burgers Playa - Gestionado por Ana)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id)
VALUES (random_uuid(), DATEADD('YEAR', -1, CURRENT_TIMESTAMP), 500.00, 
(SELECT id FROM clientes WHERE dni='99999999C'),
(SELECT id FROM empleados WHERE nombre='Ana'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Playa'));