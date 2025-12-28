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

-- ESTADOS PEDIDO (Fusionado: Incluimos todos los necesarios)
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'RECIBIDO');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'EN_COCINA');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'LISTO');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'ENTREGADO');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'PENDIENTE');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'CANCELADO');

-- TIPOS DE PRODUCTO (Fusionado)
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Hamburguesa');
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Bebida');
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Complemento');
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Postre');
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Menu'); 
INSERT INTO tipos_producto (id, nombre) VALUES (random_uuid(), 'Oferta');

-- ==========================================
-- 2. ACTORES (Propietarios, Negocios, Clientes)
-- ==========================================

-- Propietario PRINCIPAL (Login: admin@esi.es / pass: 1234)
INSERT INTO propietarios (id, dni, nombre, apellido, correo, password, fecha_nac) 
VALUES (random_uuid(), '12345678A', 'Jefe', 'Supremo', 'admin@esi.es', '{noop}1234', '1980-01-01');

-- Otros Propietarios
INSERT INTO propietarios (id, nombre, apellido, dni, correo, password) 
VALUES (random_uuid(), 'Juan', 'Dueño', '87654321B', 'juan@esi.es', '{noop}pass123');

-- Clientes
INSERT INTO clientes (id, dni, nombre, apellido, correo) 
VALUES (random_uuid(), '99999999C', 'Cliente', 'Gastón', 'cliente@test.com');

INSERT INTO clientes (id, nombre, apellido, dni, correo, telefono, password)
VALUES (random_uuid(), 'Pepito', 'Perez', '87654321Z', 'cliente@esi.es', '600123456', '{noop}1234');

-- Negocios del Jefe Supremo
INSERT INTO negocios (id, nombre, direccion, telefono, propietario_id) 
VALUES (random_uuid(), 'Burgers ESI Centro', 'Calle Falsa 123', '911223344', 
(SELECT id FROM propietarios WHERE correo='admin@esi.es'));

INSERT INTO negocios (id, nombre, direccion, telefono, propietario_id) 
VALUES (random_uuid(), 'Burgers ESI Playa', 'Paseo Marítimo 1', '955667788', 
(SELECT id FROM propietarios WHERE correo='admin@esi.es'));

-- ==========================================
-- 3. EMPLEADOS (Para Ranking y gestión)
-- ==========================================

-- Empleado 1: JUAN (Camarero)
INSERT INTO empleados (
    id, dtype, 
    dni, nombre, apellido, correo, salario, 
    negocio_id, estado_empleado_id, turno_id, propietario_id
)
VALUES (
    random_uuid(), 'Camarero',
    '11111111E', 'Juan', 'El Rápido', 'juan@esi.es', 1500.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Mañana'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es')
);

-- Empleado 2: ANA (Cocinero)
INSERT INTO empleados (
    id, dtype,
    dni, nombre, apellido, correo, salario, 
    negocio_id, estado_empleado_id, turno_id, propietario_id
)
VALUES (
    random_uuid(), 'Cocina', 
    '22222222E', 'Ana', 'Tranquila', 'ana@esi.es', 1200.00,
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Playa'),
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),
    (SELECT id FROM turnos WHERE nombre='Tarde'),
    (SELECT id FROM propietarios WHERE correo='admin@esi.es')
);

-- Empleado 3: LAURA 
INSERT INTO empleados (
    id, dtype, nombre, apellido, dni, correo, salario, 
    negocio_id, estado_empleado_id, turno_id, propietario_id
) 
VALUES (
    random_uuid(), 'Cocina', 'Laura', 'Chef', '22222222D', 'laura@chef.com', 1500.00, 
    (SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'), 
    (SELECT id FROM estados_empleado WHERE nombre='Activo'),      
    (SELECT id FROM turnos WHERE nombre='Noche'),                
    (SELECT id FROM propietarios WHERE correo='admin@esi.es')     
);

-- ==========================================
-- 4. PRODUCTOS
-- ==========================================

-- Productos Básicos 
INSERT INTO productos (id, nombre, descripcion, importe, stock, tipo_id, negocio_id)
VALUES (random_uuid(), 'Big Burger', 'Doble carne', 10.50, 100, 
(SELECT id FROM tipos_producto WHERE nombre='Hamburguesa'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

INSERT INTO productos (id, nombre, descripcion, importe, stock, tipo_id, negocio_id)
VALUES (random_uuid(), 'Coca Cola Zero', 'Sin azúcar', 2.50, 5, 
(SELECT id FROM tipos_producto WHERE nombre='Bebida'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

INSERT INTO productos (id, nombre, descripcion, importe, stock, tipo_id, negocio_id)
VALUES (random_uuid(), 'Helado Fresa', 'Postre', 3.00, 2, 
(SELECT id FROM tipos_producto WHERE nombre='Postre'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

INSERT INTO productos (id, nombre, descripcion, importe, stock, tipo_id, negocio_id)
VALUES (random_uuid(), 'Kevin Bacon', 'Carne con bacon crujiente', 12.50, 100, 
(SELECT id FROM tipos_producto WHERE nombre='Hamburguesa'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

INSERT INTO productos (id, nombre, descripcion, importe, stock, tipo_id, negocio_id)
VALUES (random_uuid(), 'Patatas Deluxe', 'Gajos especiados', 4.95, 50, 
(SELECT id FROM tipos_producto WHERE nombre='Complemento'),
(SELECT id FROM negocios WHERE nombre='Burgers ESI Centro'));

-- ==========================================
-- 5. PEDIDOS Y VENTAS
-- ==========================================

-- PEDIDO 1: HOY
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id)
VALUES (random_uuid(), CURRENT_TIMESTAMP, 65.00, 
(SELECT id FROM clientes WHERE dni='99999999C'),
(SELECT id FROM empleados WHERE nombre='Juan'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'));

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 5, 10.50, (SELECT id FROM pedidos WHERE total=65.00), (SELECT id FROM productos WHERE nombre='Big Burger'));

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 5, 2.50, (SELECT id FROM pedidos WHERE total=65.00), (SELECT id FROM productos WHERE nombre='Coca Cola Zero'));


-- PEDIDO 2: HOY
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id)
VALUES (random_uuid(), CURRENT_TIMESTAMP, 6.00, 
(SELECT id FROM clientes WHERE dni='99999999C'),
(SELECT id FROM empleados WHERE nombre='Ana'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'));

INSERT INTO lineas_pedido (id, cantidad, precio_unitario, pedido_id, producto_id)
VALUES (random_uuid(), 2, 3.00, (SELECT id FROM pedidos WHERE total=6.00), (SELECT id FROM productos WHERE nombre='Helado Fresa'));


-- PEDIDO 3: MES PASADO
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id)
VALUES (random_uuid(), DATEADD('MONTH', -1, CURRENT_TIMESTAMP), 200.00, 
(SELECT id FROM clientes WHERE dni='99999999C'),
(SELECT id FROM empleados WHERE nombre='Juan'), 
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'));


-- PEDIDO 4: AÑO PASADO
INSERT INTO pedidos (id, fecha_hora, total, cliente_id, empleado_id, estado_pedido_id)
VALUES (random_uuid(), DATEADD('YEAR', -1, CURRENT_TIMESTAMP), 500.00, 
(SELECT id FROM clientes WHERE dni='99999999C'),
(SELECT id FROM empleados WHERE nombre='Ana'),
(SELECT id FROM estados_pedido WHERE nombre='ENTREGADO'));
