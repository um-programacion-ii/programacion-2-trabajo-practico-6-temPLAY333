# Datos de prueba para H2
INSERT INTO categorias (nombre, descripcion) VALUES ('Electrónica', 'Dispositivos electrónicos y gadgets');
INSERT INTO categorias (nombre, descripcion) VALUES ('Ropa', 'Prendas de vestir y accesorios');
INSERT INTO categorias (nombre, descripcion) VALUES ('Alimentos', 'Productos alimenticios');
INSERT INTO categorias (nombre, descripcion) VALUES ('Libros', 'Libros y material de lectura');

INSERT INTO productos (nombre, descripcion, precio, categoria_id) VALUES ('Laptop Dell', 'Laptop Dell XPS 15 con 16GB RAM', 1200.00, 1);
INSERT INTO productos (nombre, descripcion, precio, categoria_id) VALUES ('Mouse Logitech', 'Mouse inalámbrico Logitech MX Master 3', 99.99, 1);
INSERT INTO productos (nombre, descripcion, precio, categoria_id) VALUES ('Teclado Mecánico', 'Teclado mecánico RGB para gaming', 150.00, 1);
INSERT INTO productos (nombre, descripcion, precio, categoria_id) VALUES ('Camiseta Nike', 'Camiseta deportiva Nike Dri-FIT', 35.00, 2);
INSERT INTO productos (nombre, descripcion, precio, categoria_id) VALUES ('Jeans Levis', 'Jeans Levis 501 Original Fit', 80.00, 2);
INSERT INTO productos (nombre, descripcion, precio, categoria_id) VALUES ('Café Premium', 'Café en grano 1kg Premium', 25.50, 3);
INSERT INTO productos (nombre, descripcion, precio, categoria_id) VALUES ('Chocolate Lindt', 'Chocolate suizo Lindt Excellence 70%', 8.99, 3);
INSERT INTO productos (nombre, descripcion, precio, categoria_id) VALUES ('El Quijote', 'Don Quijote de la Mancha - Edición completa', 45.00, 4);
INSERT INTO productos (nombre, descripcion, precio, categoria_id) VALUES ('Cien Años de Soledad', 'Gabriel García Márquez', 32.00, 4);

INSERT INTO inventario (producto_id, cantidad, stock_minimo, fecha_actualizacion) VALUES (1, 15, 5, CURRENT_TIMESTAMP());
INSERT INTO inventario (producto_id, cantidad, stock_minimo, fecha_actualizacion) VALUES (2, 50, 10, CURRENT_TIMESTAMP());
INSERT INTO inventario (producto_id, cantidad, stock_minimo, fecha_actualizacion) VALUES (3, 8, 10, CURRENT_TIMESTAMP());
INSERT INTO inventario (producto_id, cantidad, stock_minimo, fecha_actualizacion) VALUES (4, 100, 20, CURRENT_TIMESTAMP());
INSERT INTO inventario (producto_id, cantidad, stock_minimo, fecha_actualizacion) VALUES (5, 45, 15, CURRENT_TIMESTAMP());
INSERT INTO inventario (producto_id, cantidad, stock_minimo, fecha_actualizacion) VALUES (6, 200, 50, CURRENT_TIMESTAMP());
INSERT INTO inventario (producto_id, cantidad, stock_minimo, fecha_actualizacion) VALUES (7, 3, 10, CURRENT_TIMESTAMP());
INSERT INTO inventario (producto_id, cantidad, stock_minimo, fecha_actualizacion) VALUES (8, 25, 5, CURRENT_TIMESTAMP());
INSERT INTO inventario (producto_id, cantidad, stock_minimo, fecha_actualizacion) VALUES (9, 18, 5, CURRENT_TIMESTAMP());
