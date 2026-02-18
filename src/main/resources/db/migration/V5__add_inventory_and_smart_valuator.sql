-- ============================================================
-- Phoenix Exchange - Migration V5
-- Description: Inventory and Smart Valuator
-- Tables: inventory_movements, smart_valuator_estimations, diagnostic_checklists, pricing_rules
-- Date: 2026-02-18
-- ============================================================

-- ============================================================
-- TABLE: inventory_movements
-- Stock movements and tracking
-- ============================================================
CREATE TABLE inventory_movements (
                                     id BIGSERIAL PRIMARY KEY,
                                     product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                                     warehouse_id BIGINT REFERENCES warehouses(id) ON DELETE SET NULL,
                                     movement_type VARCHAR(50) NOT NULL,
                                     quantity INTEGER DEFAULT 1,
                                     reference_type VARCHAR(50),
                                     reference_id BIGINT,
                                     notes TEXT,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
                                     CONSTRAINT chk_inventory_movements_type CHECK (movement_type IN ('IN', 'OUT', 'TRANSFER', 'RESERVED', 'SOLD'))
);

CREATE INDEX idx_inventory_movements_product_id ON inventory_movements(product_id);
CREATE INDEX idx_inventory_movements_warehouse_id ON inventory_movements(warehouse_id);
CREATE INDEX idx_inventory_movements_created_at ON inventory_movements(created_at);

-- ============================================================
-- TABLE: smart_valuator_estimations
-- AI Estimations (Powered by Llama 3.3-70B)
-- ============================================================
CREATE TABLE smart_valuator_estimations (
                                            id BIGSERIAL PRIMARY KEY,
                                            item_name VARCHAR(255) NOT NULL,
                                            brand VARCHAR(100),
                                            category VARCHAR(100),
                                            year INTEGER,
                                            condition_rating INTEGER CHECK (condition_rating >= 1 AND condition_rating <= 10),
                                            estimated_price DECIMAL(10,2),
                                            ai_description TEXT,
                                            confidence INTEGER CHECK (confidence >= 0 AND confidence <= 100),
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_smart_valuator_brand ON smart_valuator_estimations(brand);
CREATE INDEX idx_smart_valuator_category ON smart_valuator_estimations(category);
CREATE INDEX idx_smart_valuator_created_at ON smart_valuator_estimations(created_at);

COMMENT ON TABLE smart_valuator_estimations IS 'AI Estimations (Llama 3.3-70B via Hugging Face)';

-- ============================================================
-- TABLE: diagnostic_checklists
-- Pre-purchase diagnostic checklists
-- ============================================================
CREATE TABLE diagnostic_checklists (
                                       id BIGSERIAL PRIMARY KEY,
                                       estimation_id BIGINT REFERENCES smart_valuator_estimations(id) ON DELETE SET NULL,
                                       product_id BIGINT REFERENCES products(id) ON DELETE SET NULL,
                                       technician_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                                       blockers JSONB,
                                       hardware_tests JSONB,
                                       deductions_total DECIMAL(10,2) DEFAULT 0,
                                       adjusted_price DECIMAL(10,2),
                                       risk_level VARCHAR(20),
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT chk_diagnostic_risk_level CHECK (risk_level IN ('LOW', 'MODERATE', 'HIGH', 'BLOCKER'))
);

CREATE INDEX idx_diagnostic_estimation_id ON diagnostic_checklists(estimation_id);
CREATE INDEX idx_diagnostic_product_id ON diagnostic_checklists(product_id);
CREATE INDEX idx_diagnostic_risk_level ON diagnostic_checklists(risk_level);

COMMENT ON TABLE diagnostic_checklists IS 'Technical pre-purchase diagnostic checklists';

-- ============================================================
-- TABLE: pricing_rules
-- Pricing rules engine
-- ============================================================
CREATE TABLE pricing_rules (
                               id BIGSERIAL PRIMARY KEY,
                               category VARCHAR(100),
                               grade VARCHAR(5),
                               target_margin_pct INTEGER,
                               refurbishing_cost_avg DECIMAL(10,2),
                               is_active BOOLEAN DEFAULT true,
                               CONSTRAINT chk_pricing_rules_grade CHECK (grade IN ('A+', 'A', 'B', 'C'))
);

CREATE INDEX idx_pricing_rules_category ON pricing_rules(category);
CREATE INDEX idx_pricing_rules_grade ON pricing_rules(grade);

COMMENT ON TABLE pricing_rules IS 'Pricing rules by category and grade';