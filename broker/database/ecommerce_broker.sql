CREATE DATABASE IF NOT EXISTS ecommerce_broker CHARACTER SET utf8mb4;
USE ecommerce_broker;

DROP TABLE IF EXISTS usuarios;

-- La contrasena se guarda cifrada con BCrypt.
-- El usuario administrador lo crea el broker al iniciar si la tabla esta vacia.
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);
