-- ============================================================
-- Phoenix Exchange - Migration V3
-- Description: Repair Management System
-- Tables: repair_tickets, interventions
-- Date: 2026-02-18
-- ============================================================

-- ============================================================
-- TABLE: repair_tickets
-- Repair workflow tracking
-- ============================================================
CREATE TABLE repair_tickets (
                                id BIGSERIAL PRIMARY KEY,
                                ticket_number VARCHAR(100) UNIQUE NOT NULL,
                                product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
                                assigned_to BIGINT REFERENCES users(id) ON DELETE SET NULL,
                                status VARCHAR(50) DEFAULT 'RECEIVED',
                                priority VARCHAR(20) DEFAULT 'NORMAL',
                                estimated_cost DECIMAL(10,2),
                                actual_cost DECIMAL(10,2),
                                estimated_duration_hours INTEGER,
                                actual_duration_hours INTEGER,
                                diagnosis TEXT,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                closed_at TIMESTAMP,
                                CONSTRAINT chk_repair_tickets_status CHECK (status IN ('RECEIVED', 'DIAGNOSTIC', 'IN_REPAIR', 'TESTING', 'CLOSED')),
                                CONSTRAINT chk_repair_tickets_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT'))
);

CREATE INDEX idx_repair_tickets_ticket_number ON repair_tickets(ticket_number);
CREATE INDEX idx_repair_tickets_product_id ON repair_tickets(product_id);
CREATE INDEX idx_repair_tickets_assigned_to ON repair_tickets(assigned_to);
CREATE INDEX idx_repair_tickets_status ON repair_tickets(status);
CREATE INDEX idx_repair_tickets_priority ON repair_tickets(priority);

COMMENT ON TABLE repair_tickets IS 'Repair tickets with full workflow tracking';
COMMENT ON COLUMN repair_tickets.status IS 'RECEIVED → DIAGNOSTIC → IN_REPAIR → TESTING → CLOSED';

-- ============================================================
-- TABLE: interventions
-- Technical interventions performed on tickets
-- ============================================================
CREATE TABLE interventions (
                               id BIGSERIAL PRIMARY KEY,
                               ticket_id BIGINT NOT NULL REFERENCES repair_tickets(id) ON DELETE CASCADE,
                               technician_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                               type VARCHAR(100),
                               description TEXT,
                               parts_used TEXT,
                               duration_minutes INTEGER,
                               cost DECIMAL(10,2),
                               status VARCHAR(50) DEFAULT 'PENDING',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT chk_interventions_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED'))
);

CREATE INDEX idx_interventions_ticket_id ON interventions(ticket_id);
CREATE INDEX idx_interventions_technician_id ON interventions(technician_id);
CREATE INDEX idx_interventions_status ON interventions(status);

COMMENT ON TABLE interventions IS 'Technical interventions performed on repair tickets';