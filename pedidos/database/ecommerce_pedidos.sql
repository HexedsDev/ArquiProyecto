CREATE DATABASE IF NOT EXISTS ecommerce_pedidos CHARACTER SET utf8mb4;
USE ecommerce_pedidos;

DROP TABLE IF EXISTS pedidos;

CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL,
    producto_id INT NOT NULL,
    nombre_producto VARCHAR(100) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    fecha DATETIME NOT NULL,
    CONSTRAINT chk_cantidad CHECK (cantidad > 0)
);
