-- ============================================================
-- Phoenix Exchange - Migration V2
-- Description: Products and Purchase Orders
-- Tables: purchase_orders, products
-- ============================================================

-- ============================================================
-- TABLE: purchase_orders
-- Supplier purchase orders
-- ============================================================
CREATE TABLE purchase_orders (
                                 id BIGSERIAL PRIMARY KEY,
                                 order_number VARCHAR(100) UNIQUE NOT NULL,
                                 supplier_id BIGINT NOT NULL REFERENCES suppliers(id) ON DELETE RESTRICT,
                                 total_amount DECIMAL(10,2),
                                 status VARCHAR(50) DEFAULT 'DRAFT',
                                 notes TEXT,
                                 created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
                                 approved_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
                                 received_at TIMESTAMP,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT chk_purchase_orders_status CHECK (status IN ('DRAFT', 'PENDING', 'APPROVED', 'RECEIVED', 'CANCELLED'))
);

CREATE INDEX idx_purchase_orders_order_number ON purchase_orders(order_number);
CREATE INDEX idx_purchase_orders_supplier_id ON purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_status ON purchase_orders(status);
CREATE INDEX idx_purchase_orders_created_by ON purchase_orders(created_by);

-- ============================================================
-- TABLE: products
-- Refurbished products (Full lifecycle tracking)
-- ============================================================
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          serial_number VARCHAR(255) UNIQUE NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          brand VARCHAR(100),
                          category VARCHAR(100),
                          purchase_price DECIMAL(10,2),
                          sell_price DECIMAL(10,2),
                          status VARCHAR(50) DEFAULT 'RECEIVED',
                          grade VARCHAR(5),
                          year INTEGER,
                          supplier_id BIGINT REFERENCES suppliers(id) ON DELETE SET NULL,
                          purchase_order_id BIGINT REFERENCES purchase_orders(id) ON DELETE SET NULL,
                          warehouse_id BIGINT REFERENCES warehouses(id) ON DELETE SET NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT chk_products_status CHECK (status IN ('RECEIVED', 'IN_REPAIR', 'REFURBISHED', 'IN_STOCK', 'RESERVED', 'SOLD')),
                          CONSTRAINT chk_products_grade CHECK (grade IN ('A+', 'A', 'B', 'C'))
);

CREATE INDEX idx_products_serial_number ON products(serial_number);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_grade ON products(grade);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_warehouse_id ON products(warehouse_id);

COMMENT ON TABLE products IS 'Refurbished products with full lifecycle tracking';
COMMENT ON COLUMN products.status IS 'RECEIVED → IN_REPAIR → REFURBISHED → IN_STOCK → RESERVED → SOLD';
COMMENT ON COLUMN products.grade IS 'A+ (Mint) | A (Excellent) | B (Good) | C (Fair)';