--DROP DATABASE IF EXISTS gestion_citas;
--CREATE DATABASE gestion_citas;

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


INSERT INTO cliente (nombres, apellidos, telefono, correo, activo) VALUES
('Carlos Eduardo', 'Mendoza López', '8888-1111', 'carlos.mendoza@email.com', true),
('María Fernanda', 'Gutiérrez Silva', '8765-4321', 'maria.gutierrez@email.com', true),
('Juan José', 'Pérez Blandón', '8555-9999', 'juan.perez@email.com', true),
('Ana Lucía', 'Torres Roble', '8333-2222', 'ana.torres@email.com', false),
('Roberto Antonio', 'Gómez Ruíz', '8444-6666', 'roberto.gomez@email.com', true);

INSERT INTO cita_tecnica (cliente_id, fecha, hora, tipo_servicio, descripcion, estado) VALUES
(1, '2026-10-15', '09:00:00', 'Mantenimiento Preventivo', 'Limpieza interna y cambio de pasta térmica de laptop', 'Pendiente'),
(2, '2026-10-15', '10:30:00', 'Instalación de Red', 'Configuración de router y cableado estructurado en oficina', 'En proceso'),
(3, '2026-10-14', '14:00:00', 'Reparación de Hardware', 'Reemplazo de pantalla dañada en equipo All-in-One', 'Finalizada'),
(4, '2026-10-16', '11:00:00', 'Diagnóstico de Software', 'Revisión por presencia de virus y optimización del sistema', 'Cancelada'),
(5, '2026-10-17', '15:15:00', 'Soporte Técnico', 'Instalación y activación de paquete ofimático', 'Pendiente');