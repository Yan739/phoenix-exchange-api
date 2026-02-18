-- ============================================================
-- Phoenix Exchange - Migration V1
-- Description: Initial complete schema creation
-- Tables: users, suppliers, customers, warehouses
-- ============================================================

-- ============================================================
-- TABLE: users
-- System users with role management
-- ============================================================
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       role VARCHAR(50) NOT NULL,
                       is_active BOOLEAN DEFAULT true,
                       phone VARCHAR(50),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       last_login TIMESTAMP,
                       CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'MANAGER', 'TECHNICIAN', 'COMMERCIAL', 'CLIENT'))
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_is_active ON users(is_active);

COMMENT ON TABLE users IS 'System users with role-based access control';
COMMENT ON COLUMN users.role IS 'ADMIN | MANAGER | TECHNICIAN | COMMERCIAL | CLIENT';

-- ============================================================
-- TABLE: suppliers
-- Suppliers (individuals, companies, public entities)
-- ============================================================
CREATE TABLE suppliers (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           type VARCHAR(50) NOT NULL,
                           email VARCHAR(255),
                           phone VARCHAR(50),
                           address TEXT,
                           rating DECIMAL(3,2) CHECK (rating >= 0 AND rating <= 5),
                           total_purchases DECIMAL(12,2) DEFAULT 0,
                           vat_number VARCHAR(100),
                           is_active BOOLEAN DEFAULT true,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT chk_suppliers_type CHECK (type IN ('INDIVIDUAL', 'COMPANY', 'COLLECTIVITY'))
);

CREATE INDEX idx_suppliers_name ON suppliers(name);
CREATE INDEX idx_suppliers_type ON suppliers(type);
CREATE INDEX idx_suppliers_is_active ON suppliers(is_active);

COMMENT ON TABLE suppliers IS 'Product suppliers (individuals, companies, and local authorities)';

-- ============================================================
-- TABLE: customers
-- B2C and B2B Customers
-- ============================================================
CREATE TABLE customers (
                           id BIGSERIAL PRIMARY KEY,
                           type VARCHAR(20) NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           email VARCHAR(255),
                           phone VARCHAR(50),
                           address TEXT,
                           vat_number VARCHAR(100),
                           loyalty_points INTEGER DEFAULT 0,
                           total_spent DECIMAL(12,2) DEFAULT 0,
                           segment VARCHAR(50),
                           is_active BOOLEAN DEFAULT true,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT chk_customers_type CHECK (type IN ('B2C', 'B2B')),
                           CONSTRAINT chk_customers_segment CHECK (segment IN ('VIP', 'REGULAR', 'NEW') OR segment IS NULL)
);

CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_type ON customers(type);
CREATE INDEX idx_customers_segment ON customers(segment);

COMMENT ON TABLE customers IS 'B2C (individuals) and B2B (business) customers';

-- ============================================================
-- TABLE: warehouses
-- Storage facilities (main, repair, spare parts)
-- ============================================================
CREATE TABLE warehouses (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            location VARCHAR(255),
                            type VARCHAR(50) NOT NULL,
                            capacity INTEGER,
                            current_stock INTEGER DEFAULT 0,
                            is_active BOOLEAN DEFAULT true,
                            CONSTRAINT chk_warehouses_type CHECK (type IN ('MAIN', 'REPAIR', 'PARTS'))
);

CREATE INDEX idx_warehouses_type ON warehouses(type);
CREATE INDEX idx_warehouses_is_active ON warehouses(is_active);

COMMENT ON TABLE warehouses IS 'Storage warehouses (main, repair, spare parts)';