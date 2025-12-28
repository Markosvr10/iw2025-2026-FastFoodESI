INSERT INTO PROPIETARIOS (ID, NOMBRE, APELLIDO, DNI, CORREO, PASSWORD) 
VALUES ('11111111-1111-1111-1111-111111111111', 'Juan', 'Dueño', '12345678A', 'juan@esi.es', '{noop}pass123');

INSERT INTO PROPIETARIOS (ID, NOMBRE, APELLIDO, DNI, CORREO, PASSWORD) 
VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Maria', 'Jefa', '87654321B', 'maria@esi.es', '{noop}pass123');

INSERT INTO PROPIETARIOS (ID, NOMBRE, APELLIDO, DNI, CORREO, PASSWORD, TELEFONO, FECHA_NAC) 
VALUES ('88888888-8888-8888-8888-888888888888', 'Sistema', 'Automatico', '00000000X', 'sistema_tpv', '{noop}pass123', '000000000', '2000-01-01');

INSERT INTO NEGOCIOS (ID, NOMBRE, DIRECCION, TELEFONO, CORREO, PROPIETARIO_ID) 
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Hamburguesas ESI', 'Av. Universidad 1', '956111111', 'burguer@esi.es', '11111111-1111-1111-1111-111111111111');

INSERT INTO NEGOCIOS (ID, NOMBRE, DIRECCION, TELEFONO, CORREO, PROPIETARIO_ID) 
VALUES (
    'b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2', 
    'Pizzería Campus', 
    'Calle Aulario 5', 
    '956222222', 
    'pizza@esi.es', 
    '11111111-1111-1111-1111-111111111111'
);

INSERT INTO TURNOS (ID, NOMBRE) VALUES ('11111111-1111-1111-1111-111111111111', 'Mañana');
INSERT INTO TURNOS (ID, NOMBRE) VALUES ('22222222-2222-2222-2222-222222222222', 'Tarde');
INSERT INTO TURNOS (ID, NOMBRE) VALUES ('33333333-3333-3333-3333-333333333333', 'Noche');



INSERT INTO ESTADOS_EMPLEADO (ID, NOMBRE) VALUES ('44444444-4444-4444-4444-444444444444', 'Activo');
INSERT INTO ESTADOS_EMPLEADO (ID, NOMBRE) VALUES ('55555555-5555-5555-5555-555555555555', 'De Baja');
INSERT INTO ESTADOS_EMPLEADO (ID, NOMBRE) VALUES ('66666666-6666-6666-6666-666666666666', 'Vacaciones');


INSERT INTO EMPLEADOS (
    ID, DTYPE, NOMBRE, APELLIDO, DNI, SALARIO, 
    PROPIETARIO_ID, 
    NEGOCIO_ID,     
    TURNO_ID, ESTADO_EMPLEADO_ID
) VALUES (
    random_uuid(), 'Cocina', 'Laura', 'García', '1234', 1500.00, 
    '11111111-1111-1111-1111-111111111111', 
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    (SELECT ID FROM TURNOS LIMIT 1), (SELECT ID FROM ESTADOS_EMPLEADO LIMIT 1)
);

INSERT INTO EMPLEADOS (
    ID, DTYPE, NOMBRE, APELLIDO, DNI, SALARIO, 
    PROPIETARIO_ID, 
    NEGOCIO_ID,     
    TURNO_ID, ESTADO_EMPLEADO_ID
) VALUES (
    random_uuid(), 'Repartidor', 'Carlos', 'López', '5678', 1200.00, 
    '11111111-1111-1111-1111-111111111111', 
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    (SELECT ID FROM TURNOS LIMIT 1), (SELECT ID FROM ESTADOS_EMPLEADO LIMIT 1)
);


-- 3. Mostrador (Maria)
INSERT INTO EMPLEADOS (
    ID, DTYPE, NOMBRE, APELLIDO, DNI, SALARIO, 
    PROPIETARIO_ID, -- Dueño: Juan
    NEGOCIO_ID,     -- Local: Hamburguesas Juan
    TURNO_ID, ESTADO_EMPLEADO_ID
) VALUES (
    random_uuid(), 'Mostrador', 'Maria', 'Pantalla', '0000', 1100.00, 
    '11111111-1111-1111-1111-111111111111', -- ID DE JUAN
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', -- ID DE LA HAMBURGUESERÍA
    (SELECT ID FROM TURNOS LIMIT 1), 
    (SELECT ID FROM ESTADOS_EMPLEADO LIMIT 1)
);

-- CLIENTE DE PRUEBA
INSERT INTO CLIENTES (ID, NOMBRE, APELLIDO, DNI, CORREO, TELEFONO, PASSWORD)
VALUES (random_uuid(), 'Pepito', 'Perez', '87654321Z', 'cliente@esi.es', '600123456', '{noop}1234');




INSERT INTO TIPOS_PRODUCTO (ID, NOMBRE) VALUES ('c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1', 'Hamburguesas');
INSERT INTO TIPOS_PRODUCTO (ID, NOMBRE) VALUES ('c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2', 'Bebidas');
INSERT INTO TIPOS_PRODUCTO (ID, NOMBRE) VALUES ('c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', 'Complementos');
INSERT INTO TIPOS_PRODUCTO (ID, NOMBRE) VALUES ('c4c4c4c4-c4c4-c4c4-c4c4-c4c4c4c4c4c4', 'Menus');
INSERT INTO TIPOS_PRODUCTO (ID, NOMBRE) VALUES ('c5c5c5c5-c5c5-c5c5-c5c5-c5c5c5c5c5c5', 'Ofertas');

-- BLOQUE 1
INSERT INTO PRODUCTOS (ID, NOMBRE, DESCRIPCION, IMPORTE, STOCK, IMAGEN_URL, NEGOCIO_ID, TIPO_ID)
VALUES (random_uuid(), 'Kevin Bacon', 'Carne picada con mucho bacon crujiente', 12.50, 100, 'https://dummyimage.com/300x200/000/fff&text=Kevin+Bacon', 
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1');

INSERT INTO PRODUCTOS (ID, NOMBRE, DESCRIPCION, IMPORTE, STOCK, IMAGEN_URL, NEGOCIO_ID, TIPO_ID)
VALUES (random_uuid(), 'Patatas Deluxe', 'Gajos de patata especiados', 4.95, 50, 'https://dummyimage.com/300x200/000/fff&text=Patatas', 
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3');

INSERT INTO PRODUCTOS (ID, NOMBRE, DESCRIPCION, IMPORTE, STOCK, IMAGEN_URL, NEGOCIO_ID, TIPO_ID)
VALUES (random_uuid(), 'Coca-Cola Zero', 'Lata 33cl bien fría', 2.00, 200, 'https://dummyimage.com/300x200/000/fff&text=CocaCola', 
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2');


-- BLOQUE 2
INSERT INTO PRODUCTOS (ID, NOMBRE, DESCRIPCION, IMPORTE, STOCK, IMAGEN_URL, NEGOCIO_ID, TIPO_ID)
VALUES (random_uuid(), 'Kevin Bacon Doble', 'Doble de todo para los valientes', 15.50, 100, 'https://dummyimage.com/300x200/000/fff&text=Kevin+Doble', 
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1');

INSERT INTO PRODUCTOS (ID, NOMBRE, DESCRIPCION, IMPORTE, STOCK, IMAGEN_URL, NEGOCIO_ID, TIPO_ID)
VALUES (random_uuid(), 'Patatas Bravas', 'Con salsa picante casera', 5.50, 50, 'https://dummyimage.com/300x200/000/fff&text=Bravas', 
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3');

INSERT INTO PRODUCTOS (ID, NOMBRE, DESCRIPCION, IMPORTE, STOCK, IMAGEN_URL, NEGOCIO_ID, TIPO_ID)
VALUES (random_uuid(), 'Coca-Cola Normal', 'La de toda la vida', 2.00, 200, 'https://dummyimage.com/300x200/000/fff&text=CocaCola+Red', 
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2');


-- BLOQUE 3
INSERT INTO PRODUCTOS (ID, NOMBRE, DESCRIPCION, IMPORTE, STOCK, IMAGEN_URL, NEGOCIO_ID, TIPO_ID)
VALUES (random_uuid(), 'Hamburguesa de Pollo', 'Pollo crujiente con mayonesa', 9.50, 100, 'https://dummyimage.com/300x200/000/fff&text=Pollo', 
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1');

INSERT INTO PRODUCTOS (ID, NOMBRE, DESCRIPCION, IMPORTE, STOCK, IMAGEN_URL, NEGOCIO_ID, TIPO_ID)
VALUES (random_uuid(), 'Aros de Cebolla', 'Crujientes y dorados', 3.95, 50, 'https://dummyimage.com/300x200/000/fff&text=Aros', 
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3');

INSERT INTO PRODUCTOS (ID, NOMBRE, DESCRIPCION, IMPORTE, STOCK, IMAGEN_URL, NEGOCIO_ID, TIPO_ID)
VALUES (random_uuid(), 'Fanta Naranja', 'Refresco de naranja', 2.00, 200, 'https://dummyimage.com/300x200/000/fff&text=Fanta', 
'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2');



INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'RECIBIDO');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'EN_COCINA');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'LISTO');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'ENTREGADO');
INSERT INTO estados_pedido (id, nombre) VALUES (random_uuid(), 'CANCELADO');