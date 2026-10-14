DROP DATABASE IF EXISTS gestion_citas;
CREATE DATABASE gestion_citas;

CREATE TABLE cliente (
                         id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         nombres VARCHAR(80) NOT NULL,
                         apellidos VARCHAR(80) NOT NULL,
                         telefono VARCHAR(20) NOT NULL,
                         correo VARCHAR(120) UNIQUE NOT NULL,
                         activo BOOLEAN NOT NULL
);

CREATE TABLE cita_tecnica (
                              id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              cliente_id INT NOT NULL,
                              fecha DATE NOT NULL,
                              hora TIME NOT NULL,
                              tipo_servicio VARCHAR(80) NOT NULL,
                              descripcion VARCHAR(255) NOT NULL,
                              estado VARCHAR(20) NOT NULL CHECK (estado IN ('Pendiente', 'En proceso', 'Finalizada', 'Cancelada')),
                              CONSTRAINT fk_cita_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id) ON DELETE RESTRICT
);

