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

-- PROPIETARIO SISTEMA AUXILIAR PARA ACCESO A NEGOCIOS
INSERT INTO propietarios (id, nombre, apellido, dni,  correo, password, fecha_nac)
VALUES (random_uuid(), 'Terminal', 'Establecimiento','00000000X', 'sistema_tpv', '{noop}pass123', '2024-01-01');

-- Clientes

-- CLIENTE 1: Pepito (El que usamos para probar)
INSERT INTO clientes (id, nombre, apellido, dni, correo, telefono, password, verificado)
VALUES (random_uuid(), 'Pepito', 'Perez', '87654321Z', 'cliente@esi.es', '600123456', '{noop}1234', true);

-- OTROS CLIENTES
INSERT INTO clientes (id, nombre, apellido, dni, correo, telefono, password, direccion, verificado)
VALUES (random_uuid(), 'Alejandro', 'Fernández', '87654321A', 'alefdez@esi.es', '600123457', '{noop}12345', 'Calle Principal 123', true);

INSERT INTO clientes (id, nombre, apellido, dni, correo, telefono, password, direccion, verificado)
VALUES (random_uuid(), 'Marta', 'Sánchez', '87654321B', 'martasan@esi.es', '600123458', '{noop}12345', 'Calle Cervantes 49', true);

-- CLIENTE GENERICO (Para pedidos en local)
INSERT INTO clientes (id, nombre, apellido, dni, correo, telefono, password, verificado)
VALUES (random_uuid(), 'Cliente', 'Generico', '99999999X', 'clienteGenerico@esi.es', '00000000', '{noop}clientegenerico', true);

-- NEGOCIOS
INSERT INTO negocios (id, nombre, direccion, telefono, correo, propietario_id) 
VALUES ('fa57f00d-e510-4000-ce11-000000000001', 'Burgers ESI Centro', 'Calle Falsa 123', '911223344', 'burgers@esi.es',
(SELECT id FROM propietarios WHERE correo='admin@esi.es'));

INSERT INTO negocios (id, nombre, direccion, telefono, propietario_id) 
VALUES ('fa57f00d-e510-4000-5ea1-000000000002', 'Burgers ESI Playa', 'Paseo Marítimo 1', '955667788',
(SELECT id FROM propietarios WHERE correo='juan@esi.es'));

INSERT INTO negocios (id, nombre, direccion, telefono, correo, propietario_id) 
VALUES ('fa57f00d-e510-4000-ca22-000000000003', 'Pizzería Campus', 'Calle Aulario 5', '956222222', 'pizza@esi.es',
(SELECT id FROM propietarios WHERE correo='admin@esi.es'));

-- ==========================================
-- 3. EMPLEADOS
-- ==========================================

-- Empleados Burgers ESI Centro
-- --- TIPO: COCINA ---
INSERT INTO empleados (id, dtype, dni, nombre, apellido, correo, salario, negocio_id, estado_empleado_id, turno_id, propietario_id)
VALUES (random_uuid(), 'Cocina', '30000001A', 'Carlos', 'Ruiz', 'carlos.cocina@esi.es', 1300.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Mañana'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es'));

INSERT INTO empleados (id, dtype, dni, nombre, apellido, correo, salario, negocio_id, estado_empleado_id, turno_id, propietario_id)
VALUES (random_uuid(), 'Cocina', '30000002B', 'Laura', 'Gil', 'laura.cocina@esi.es', 1350.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Noche'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es'));


-- --- TIPO: CAMARERO ---
INSERT INTO empleados (id, dtype, dni, nombre, apellido, correo, salario, negocio_id, estado_empleado_id, turno_id, propietario_id)
VALUES (random_uuid(), 'Camarero', '30000003C', 'Pedro', 'Gómez', 'pedro.camarero@esi.es', 1100.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Tarde'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es'));

INSERT INTO empleados (id, dtype, dni, nombre, apellido, correo, salario, negocio_id, estado_empleado_id, turno_id, propietario_id)
VALUES (random_uuid(), 'Camarero', '30000004D', 'Sofía', 'López', 'sofia.camarero@esi.es', 1100.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Mañana'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es'));


-- --- TIPO: REPARTIDOR ---
INSERT INTO empleados (id, dtype, dni, nombre, apellido, correo, salario, negocio_id, estado_empleado_id, turno_id, propietario_id)
VALUES (random_uuid(), 'Repartidor', '30000005E', 'Miguel', 'Ángel', 'miguel.reparto@esi.es', 1150.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Noche'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es'));

INSERT INTO empleados (id, dtype, dni, nombre, apellido, correo, salario, negocio_id, estado_empleado_id, turno_id, propietario_id)
VALUES (random_uuid(), 'Repartidor', '30000006F', 'Elena', 'Torres', 'elena.reparto@esi.es', 1150.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Tarde'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es'));


-- --- TIPO: MOSTRADOR ---
INSERT INTO empleados (id, dtype, dni, nombre, apellido, correo, salario, negocio_id, estado_empleado_id, turno_id, propietario_id)
VALUES (random_uuid(), 'Mostrador', '30000007G', 'David', 'Díaz', 'david.mostrador@esi.es', 1200.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Mañana'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es'));

INSERT INTO empleados (id, dtype, dni, nombre, apellido, correo, salario, negocio_id, estado_empleado_id, turno_id, propietario_id)
VALUES (random_uuid(), 'Mostrador', '30000008H', 'María', 'Núñez', 'maria.mostrador@esi.es', 1200.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Tarde'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es'));

-- Empleado Pizzeria : ANA
INSERT INTO empleados (
    id, dtype, dni, nombre, apellido, correo, salario, 
    negocio_id, estado_empleado_id, turno_id, propietario_id
)
VALUES (
    random_uuid(), 'Camarero', '22222222E', 'Ana', 'Sánchez', 'ana@esi.es', 1200.00,
    (SELECT id FROM negocios WHERE nombre='Pizzería Campus'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Tarde'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es')
);

-- ==========================================
-- 4. PRODUCTOS
-- ==========================================

INSERT INTO productos (id, nombre, descripcion, importe, stock, imagen_url, tipo_id, negocio_id)
VALUES (random_uuid(), 'Big Burger', 'Doble carne', 10.50, 5,
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
VALUES (random_uuid(), 'Veggie Deluxe', 'Hamburguesa vegana de lentejas y aguacate', 11.00, 18,
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
VALUES (random_uuid(), 'Fanta Naranja', 'Refresco de naranja con gas', 2.50, 12,
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
VALUES (random_uuid(), 'Aros de Cebolla', 'Crujientes y dorados', 3.50, 10,
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
-- 1. HOY - Pedro (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), CURRENT_TIMESTAMP, 21.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Pedro'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 2, 10.50, 
(SELECT id FROM pedidos WHERE total=21.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Pedro')), 
(SELECT id FROM productos WHERE nombre='Big Burger' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 2. HOY (Hace 2 horas) - Pedro (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 25.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Pedro'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'EFECTIVO', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 2, 12.50, 
(SELECT id FROM pedidos WHERE total=25.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Pedro')), 
(SELECT id FROM productos WHERE nombre='Kevin Bacon' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 3. AYER - Sofía (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('DAY', -1, CURRENT_TIMESTAMP), 12.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Sofía'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 1, 12.00, 
(SELECT id FROM pedidos WHERE total=12.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Sofía')), 
(SELECT id FROM productos WHERE nombre='Menú Estudiante' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 4. HACE 3 DÍAS - David (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('DAY', -3, CURRENT_TIMESTAMP), 35.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='David'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 1, 35.00, 
(SELECT id FROM pedidos WHERE total=35.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='David')), 
(SELECT id FROM productos WHERE nombre='Pack Fiesta' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 5. HACE 5 DÍAS - David (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('DAY', -5, CURRENT_TIMESTAMP), 13.50, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='David'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'EFECTIVO', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 3, 4.50, 
(SELECT id FROM pedidos WHERE total=13.50 AND empleado_id=(SELECT id FROM empleados WHERE nombre='David')), 
(SELECT id FROM productos WHERE nombre='Nuggets de Pollo' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 6. SEMANA PASADA - María (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('DAY', -7, CURRENT_TIMESTAMP), 10.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='María'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 4, 2.50, 
(SELECT id FROM pedidos WHERE total=10.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='María')), 
(SELECT id FROM productos WHERE nombre='Coca Cola Zero' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 7. SEMANA PASADA - Juan/Sofía (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('DAY', -8, CURRENT_TIMESTAMP), 22.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Sofía'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'EFECTIVO', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 2, 11.00, 
(SELECT id FROM pedidos WHERE total=22.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Sofía')), 
(SELECT id FROM productos WHERE nombre='Veggie Deluxe' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 8. HACE 2 SEMANAS - María (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('DAY', -14, CURRENT_TIMESTAMP), 14.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='María'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 4, 3.50, 
(SELECT id FROM pedidos WHERE total=14.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='María')), 
(SELECT id FROM productos WHERE nombre='Aros de Cebolla' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 9. MES PASADO - Pedro (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('MONTH', -1, CURRENT_TIMESTAMP), 37.50, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Pedro'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 3, 12.50, 
(SELECT id FROM pedidos WHERE total=37.50 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Pedro')), 
(SELECT id FROM productos WHERE nombre='Kevin Bacon' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 10. MES PASADO - Sofía (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('MONTH', -1, DATEADD('DAY', -2, CURRENT_TIMESTAMP)), 12.50, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Sofía'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'EFECTIVO', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 5, 2.50, 
(SELECT id FROM pedidos WHERE total=12.50 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Sofía')), 
(SELECT id FROM productos WHERE nombre='Fanta Naranja' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 11. MES PASADO - María (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('MONTH', -1, DATEADD('DAY', -5, CURRENT_TIMESTAMP)), 10.50, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='María'),
(SELECT id FROM estados_pedido WHERE nombre='CANCELADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
false, 'TARJETA', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 1, 10.50, 
(SELECT id FROM pedidos WHERE total=10.50 AND empleado_id=(SELECT id FROM empleados WHERE nombre='María')), 
(SELECT id FROM productos WHERE nombre='Big Burger' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 12. HACE 2 MESES - Pedro (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('MONTH', -2, CURRENT_TIMESTAMP), 48.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Pedro'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 4, 12.00, 
(SELECT id FROM pedidos WHERE total=48.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Pedro')), 
(SELECT id FROM productos WHERE nombre='Menú Estudiante' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 13. HACE 3 MESES - David (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('MONTH', -3, CURRENT_TIMESTAMP), 15.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='David'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'EFECTIVO', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 10, 1.50, 
(SELECT id FROM pedidos WHERE total=15.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='David')), 
(SELECT id FROM productos WHERE nombre='Agua Mineral' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 14. HACE 6 MESES - María (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('MONTH', -6, CURRENT_TIMESTAMP), 105.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='María'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 3, 35.00, 
(SELECT id FROM pedidos WHERE total=105.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='María')), 
(SELECT id FROM productos WHERE nombre='Pack Fiesta' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 15. HACE 8 MESES - David (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('MONTH', -8, CURRENT_TIMESTAMP), 22.50, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='David'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'EFECTIVO', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 5, 4.50, 
(SELECT id FROM pedidos WHERE total=22.50 AND empleado_id=(SELECT id FROM empleados WHERE nombre='David')), 
(SELECT id FROM productos WHERE nombre='Nuggets de Pollo' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 16. AÑO PASADO - Pedro (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('YEAR', -1, CURRENT_TIMESTAMP), 125.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Pedro'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 10, 12.50, 
(SELECT id FROM pedidos WHERE total=125.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Pedro')), 
(SELECT id FROM productos WHERE nombre='Kevin Bacon' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 17. AÑO PASADO - Sofía (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('YEAR', -1, DATEADD('MONTH', 2, CURRENT_TIMESTAMP)), 50.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Sofía'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 20, 2.50, 
(SELECT id FROM pedidos WHERE total=50.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Sofía')), 
(SELECT id FROM productos WHERE nombre='Coca Cola Zero' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 18. AÑO PASADO - David (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('YEAR', -1, DATEADD('MONTH', 5, CURRENT_TIMESTAMP)), 55.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='David'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'EFECTIVO', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 5, 11.00, 
(SELECT id FROM pedidos WHERE total=55.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='David')), 
(SELECT id FROM productos WHERE nombre='Veggie Deluxe' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 19. AÑO PASADO - Sofía (Camarero)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('YEAR', -1, DATEADD('MONTH', 7, CURRENT_TIMESTAMP)), 38.50, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Sofía'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 11, 3.50, 
(SELECT id FROM pedidos WHERE total=38.50 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Sofía')), 
(SELECT id FROM productos WHERE nombre='Aros de Cebolla' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- 20. HACE 2 AÑOS - David (Mostrador)
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('YEAR', -2, CURRENT_TIMESTAMP), 120.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='David'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
true, 'TARJETA', 'Recoger');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 10, 12.00, 
(SELECT id FROM pedidos WHERE total=120.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='David')), 
(SELECT id FROM productos WHERE nombre='Menú Estudiante' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro')));


-- =================================================================================
-- PEDIDOS PARA: ANA (Burgers ESI Playa)
-- =================================================================================

-- Pedido 21: ANA (Playa) - Pizza
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), CURRENT_TIMESTAMP, 17.00, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Ana'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Pizzería Campus'),
true, 'TARJETA', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 2, 8.50, 
(SELECT id FROM pedidos WHERE total=17.00 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Ana')), 
(SELECT id FROM productos WHERE nombre='Pizza Margarita' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Pizzería Campus')));

-- Pedido 22: ANA (Playa) - Pepperoni
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id, negocio_id, pagado, metodo_pago, tipo_entrega)
VALUES (random_uuid(), DATEADD('DAY', -1, CURRENT_TIMESTAMP), 13.50, 
(SELECT id FROM clientes WHERE dni='99999999X'),
(SELECT id FROM empleados WHERE nombre='Ana'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'),
(SELECT id FROM negocios WHERE nombre='Pizzería Campus'),
true, 'EFECTIVO', 'Mesa');

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 1, 13.50, 
(SELECT id FROM pedidos WHERE total=13.50 AND empleado_id=(SELECT id FROM empleados WHERE nombre='Ana')), 
(SELECT id FROM productos WHERE nombre='Pepperoni Lover' AND negocio_id=(SELECT id FROM negocios WHERE nombre='Pizzería Campus')));