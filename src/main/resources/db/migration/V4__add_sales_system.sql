-- ============================================================
-- Phoenix Exchange - Migration V4
-- Description: Sales System
-- Tables: sales_orders, order_items, payments
-- Date: 2026-02-18
-- ============================================================

-- ============================================================
-- TABLE: sales_orders
-- Customer sales orders
-- ============================================================
CREATE TABLE sales_orders (
                              id BIGSERIAL PRIMARY KEY,
                              order_number VARCHAR(100) UNIQUE NOT NULL,
                              customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
                              total_amount DECIMAL(10,2),
                              tax_amount DECIMAL(10,2),
                              discount_amount DECIMAL(10,2) DEFAULT 0,
                              status VARCHAR(50) DEFAULT 'DRAFT',
                              payment_method VARCHAR(50),
                              payment_status VARCHAR(50) DEFAULT 'PENDING',
                              notes TEXT,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              delivered_at TIMESTAMP,
                              CONSTRAINT chk_sales_orders_status CHECK (status IN ('DRAFT', 'PENDING_PAYMENT', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
                              CONSTRAINT chk_sales_orders_payment_status CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED'))
);

CREATE INDEX idx_sales_orders_order_number ON sales_orders(order_number);
CREATE INDEX idx_sales_orders_customer_id ON sales_orders(customer_id);
CREATE INDEX idx_sales_orders_status ON sales_orders(status);
CREATE INDEX idx_sales_orders_payment_status ON sales_orders(payment_status);

-- ============================================================
-- TABLE: order_items
-- Order line items
-- ============================================================
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES sales_orders(id) ON DELETE CASCADE,
                             product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
                             quantity INTEGER DEFAULT 1,
                             unit_price DECIMAL(10,2),
                             total_price DECIMAL(10,2),
                             CONSTRAINT chk_order_items_quantity CHECK (quantity > 0)
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- ============================================================
-- TABLE: payments
-- Payments
-- ============================================================
CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          order_id BIGINT NOT NULL REFERENCES sales_orders(id) ON DELETE RESTRICT,
                          amount DECIMAL(10,2) NOT NULL,
                          method VARCHAR(50),
                          status VARCHAR(50) DEFAULT 'PENDING',
                          transaction_id VARCHAR(255),
                          reference VARCHAR(255),
                          paid_at TIMESTAMP,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT chk_payments_method CHECK (method IN ('CREDIT_CARD', 'BANK_TRANSFER', 'PAYPAL', 'CASH')),
                          CONSTRAINT chk_payments_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'))
);

CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);