CREATE DATABASE IF NOT EXISTS ecommerce_productos CHARACTER SET utf8mb4;
USE ecommerce_productos;

DROP TABLE IF EXISTS productos;

CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    existencia INT NOT NULL,
    CONSTRAINT chk_precio CHECK (precio > 0),
    CONSTRAINT chk_existencia CHECK (existencia >= 0)
);

INSERT INTO productos (nombre, descripcion, precio, existencia) VALUES
('Teclado mecanico', 'Teclado switch rojo con luz RGB', 450.00, 15),
('Mouse inalambrico', 'Mouse 2.4 GHz de 1600 DPI', 125.50, 30),
('Monitor 24 pulgadas', 'Monitor IPS Full HD 75 Hz', 1350.00, 8),
('Audifonos', 'Audifonos con microfono y cable 3.5 mm', 210.00, 20),
('Memoria USB 64 GB', 'Memoria USB 3.0', 75.00, 50);
