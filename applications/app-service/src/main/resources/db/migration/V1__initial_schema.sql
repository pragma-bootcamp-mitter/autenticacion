-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    role_id INTEGER PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200)
);

-- Insert standard roles
INSERT INTO roles (role_id,name, description) VALUES
(1,'ADMIN', 'Administrator with full system access'),
(2,'ADVISOR', 'Advisor who can manage loans and users'),
(3,'CLIENT', 'Client who can manage their own loan applications')
ON CONFLICT (name) DO NOTHING;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    identification_document character varying(50) NOT NULL,
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    date_of_birth date NOT NULL,
    address character varying(200),
    phone_number character varying(20),
    email character varying(150) NOT NULL,
    password character varying(255) NOT NULL,
    base_salary numeric(15,2) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- Add the foreign key to link users to roles
ALTER TABLE users
ADD COLUMN role_id INTEGER,
ADD CONSTRAINT fk_user_role
FOREIGN KEY (role_id)
REFERENCES roles (role_id);

-- Insert sample users with assigned roles
-- Note: 'role_id' values (1, 2, 3) correspond to ADMIN, ADVISOR, and CLIENT respectively.
INSERT INTO users (
    identification_document,
    first_name,
    last_name,
    date_of_birth,
    address,
    phone_number,
    email,
    password,
    base_salary,
    role_id
) VALUES (
    '123456789',
    'Juan',
    'Perez',
    '1990-05-15',
    'Calle 123 #45-67, Bogotá',
    '+573101234567',
    'juan.perez@email.com',
    '$2a$10$tJ0j9l7n8G5K3y4z2f1o.1a0b9c8d7e6f5g4h3i2j1k.l0m9n8o7p6q5r4s3t2u1v',
    50000.00,
    1
), (
    '987654321',
    'Maria',
    'Gomez',
    '1985-11-20',
    'Carrera 98 #76-54, Medellín',
    '+573209876543',
    'maria.gomez@email.com',
    '$2a$10$tJ0j9l7n8G5K3y4z2f1o.1a0b9c8d7e6f5g4h3i2j1k.l0m9n8o7p6q5r4s3t2u1v',
    65000.00,
    2
), (
    '112233445',
    'Carlos',
    'Rodriguez',
    '1992-02-28',
    'Avenida Principal 321, Cali',
    '+573001122334',
    'carlos.rodriguez@email.com',
    '$2a$10$tJ0j9l7n8G5K3y4z2f1o.1a0b9c8d7e6f5g4h3i2j1k.l0m9n8o7p6q5r4s3t2u1v',
    75000.00,
    3
),(
      '9674a817-3300-449a-91d1-1b27048b7e2d',
      'Mauricio',
      'Rincon',
      '1994-05-15',
      'Calle 123 #45-67',
      '3101234567',
      'test-1@example.com',
      '$2a$10$WiFGO.wa4QhQfVcoOxMJSOaJGc2ipybDBijIDJoVk83LsNX8Ar4nC',
      15000000.00,
      1
 );