-- =============================================================================
-- growthhub SALON MANAGEMENT SYSTEM — PostgreSQL Schema
-- Migration: V1__initial_schema.sql
-- =============================================================================

-- ── EXTENSIONS ───────────────────────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";   -- for fuzzy text search

-- =============================================================================
-- ENUMS
-- =============================================================================
CREATE TYPE gender_type          AS ENUM ('MALE','FEMALE','OTHER','PREFER_NOT_TO_SAY');
CREATE TYPE staff_status         AS ENUM ('ACTIVE','ON_LEAVE','INACTIVE','TERMINATED');
CREATE TYPE appointment_status   AS ENUM ('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW');
CREATE TYPE invoice_status       AS ENUM ('DRAFT','PAID','PARTIAL','CANCELLED','REFUNDED');
CREATE TYPE payment_method       AS ENUM ('CASH','CARD','UPI','BANK_TRANSFER','WALLET','MEMBERSHIP_WALLET','GIFT_VOUCHER');
CREATE TYPE inventory_status     AS ENUM ('IN_STOCK','LOW_STOCK','OUT_OF_STOCK','DISCONTINUED');
CREATE TYPE expense_status       AS ENUM ('PENDING','APPROVED','REJECTED');
CREATE TYPE attendance_status    AS ENUM ('PRESENT','ABSENT','LATE','HALF_DAY','ON_LEAVE','HOLIDAY');
CREATE TYPE loyalty_program_type AS ENUM ('POINTS','MULTIPLIER','BONUS','REFERRAL');
CREATE TYPE membership_status    AS ENUM ('ACTIVE','EXPIRED','CANCELLED','SUSPENDED');
CREATE TYPE voucher_status       AS ENUM ('ACTIVE','PARTIAL','REDEEMED','EXPIRED','CANCELLED');
CREATE TYPE client_tier          AS ENUM ('BASIC','SILVER','GOLD','PLATINUM');
CREATE TYPE service_status       AS ENUM ('ACTIVE','INACTIVE','ARCHIVED');
CREATE TYPE user_role            AS ENUM ('SUPER_ADMIN','ADMIN','MANAGER','RECEPTIONIST','STYLIST','THERAPIST');
CREATE TYPE redeem_type          AS ENUM ('SERVICE','PRODUCT','ALL');
CREATE TYPE booking_source       AS ENUM ('WALK_IN','PHONE','APP','WEBSITE','WHATSAPP');

-- =============================================================================
-- 1. SALON SETTINGS (single-row config table)
-- =============================================================================
CREATE TABLE salon_settings (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    salon_name              VARCHAR(150)    NOT NULL DEFAULT 'My Salon',
    tagline                 VARCHAR(255),
    phone                   VARCHAR(20)     NOT NULL,
    email                   VARCHAR(150),
    address                 TEXT,
    gst_number              VARCHAR(20),
    currency_code           CHAR(3)         NOT NULL DEFAULT 'INR',
    timezone                VARCHAR(60)     NOT NULL DEFAULT 'Asia/Kolkata',
    logo_url                TEXT,
    working_start           TIME            NOT NULL DEFAULT '09:00:00',
    working_end             TIME            NOT NULL DEFAULT '21:00:00',
    slot_duration_minutes   INT             NOT NULL DEFAULT 15,
    advance_booking_days    INT             NOT NULL DEFAULT 30,
    cancellation_hours      INT             NOT NULL DEFAULT 2,
    auto_confirm            BOOLEAN         NOT NULL DEFAULT TRUE,
    reminder_sms            BOOLEAN         NOT NULL DEFAULT TRUE,
    reminder_hours          INT             NOT NULL DEFAULT 24,
    default_gst_percent     NUMERIC(5,2)    NOT NULL DEFAULT 18.0,
    invoice_prefix          VARCHAR(10)     NOT NULL DEFAULT 'INV',
    show_gst_breakdown      BOOLEAN         NOT NULL DEFAULT TRUE,
    round_off_total         BOOLEAN         NOT NULL DEFAULT TRUE,
    default_discount_pct    NUMERIC(5,2)    NOT NULL DEFAULT 0,
    sms_on_booking          BOOLEAN         NOT NULL DEFAULT TRUE,
    sms_on_reminder         BOOLEAN         NOT NULL DEFAULT TRUE,
    email_receipt           BOOLEAN         NOT NULL DEFAULT TRUE,
    low_stock_alert         BOOLEAN         NOT NULL DEFAULT TRUE,
    daily_summary           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- =============================================================================
-- 2. USERS (auth + staff portal access)
-- =============================================================================
CREATE TABLE users (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    email           VARCHAR(150)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    role            user_role       NOT NULL DEFAULT 'RECEPTIONIST',
    full_name       VARCHAR(150)    NOT NULL,
    phone           VARCHAR(20),
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMPTZ,
    refresh_token   TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);

-- =============================================================================
-- 3. SERVICE CATEGORIES
-- =============================================================================
CREATE TABLE service_categories (
    id          UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(100)    NOT NULL UNIQUE,
    icon        VARCHAR(10),
    color       VARCHAR(7),
    sort_order  INT             NOT NULL DEFAULT 0,
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- =============================================================================
-- 4. SERVICES
-- =============================================================================
CREATE TABLE services (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id     UUID            NOT NULL REFERENCES service_categories(id) ON DELETE RESTRICT,
    name            VARCHAR(150)    NOT NULL,
    description     TEXT,
    duration_mins   INT             NOT NULL DEFAULT 30,
    price           NUMERIC(10,2)   NOT NULL,
    mrp             NUMERIC(10,2),
    gst_percent     NUMERIC(5,2)    NOT NULL DEFAULT 18.0,
    status          service_status  NOT NULL DEFAULT 'ACTIVE',
    is_popular      BOOLEAN         NOT NULL DEFAULT FALSE,
    sort_order      INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_services_category ON services(category_id);
CREATE INDEX idx_services_status   ON services(status);
CREATE INDEX idx_services_name_trgm ON services USING GIN (name gin_trgm_ops);

-- =============================================================================
-- 5. STAFF
-- =============================================================================
CREATE TABLE staff (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID            REFERENCES users(id) ON DELETE SET NULL,
    full_name           VARCHAR(150)    NOT NULL,
    phone               VARCHAR(20)     NOT NULL,
    email               VARCHAR(150),
    gender              gender_type     NOT NULL DEFAULT 'PREFER_NOT_TO_SAY',
    date_of_birth       DATE,
    address             TEXT,
    role                VARCHAR(100)    NOT NULL,
    join_date           DATE            NOT NULL DEFAULT CURRENT_DATE,
    avatar_initials     VARCHAR(4),
    status              staff_status    NOT NULL DEFAULT 'ACTIVE',
    base_salary         NUMERIC(10,2)   NOT NULL DEFAULT 0,
    commission_rate     NUMERIC(5,2)    NOT NULL DEFAULT 0,    -- percentage
    target_revenue      NUMERIC(10,2)   NOT NULL DEFAULT 0,
    working_days        VARCHAR(50),    -- e.g. 'MON,TUE,WED,THU,FRI,SAT'
    shift_start         TIME            NOT NULL DEFAULT '09:00:00',
    shift_end           TIME            NOT NULL DEFAULT '19:00:00',
    rating              NUMERIC(3,2)    NOT NULL DEFAULT 0,
    total_clients       INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_staff_status ON staff(status);
CREATE INDEX idx_staff_user   ON staff(user_id);

-- Staff specializations (many-to-many via simple array is OK here)
CREATE TABLE staff_specializations (
    staff_id        UUID    NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    specialization  VARCHAR(100) NOT NULL,
    PRIMARY KEY (staff_id, specialization)
);

-- =============================================================================
-- 6. CLIENTS
-- =============================================================================
CREATE TABLE clients (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    name                VARCHAR(150)    NOT NULL,
    phone               VARCHAR(20)     NOT NULL UNIQUE,
    email               VARCHAR(150),
    date_of_birth       DATE,
    gender              gender_type     NOT NULL DEFAULT 'PREFER_NOT_TO_SAY',
    address             TEXT,
    avatar_initials     VARCHAR(4),
    membership_type     client_tier     NOT NULL DEFAULT 'BASIC',
    loyalty_points      INT             NOT NULL DEFAULT 0,
    wallet_balance      NUMERIC(10,2)   NOT NULL DEFAULT 0,
    total_visits        INT             NOT NULL DEFAULT 0,
    total_spend         NUMERIC(12,2)   NOT NULL DEFAULT 0,
    last_visit_at       DATE,
    notes               TEXT,
    tags                TEXT[],         -- e.g. {'VIP','Bridal','Regular'}
    referral_code       VARCHAR(20)     UNIQUE,
    referred_by_id      UUID            REFERENCES clients(id) ON DELETE SET NULL,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    join_date           DATE            NOT NULL DEFAULT CURRENT_DATE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_clients_phone     ON clients(phone);
CREATE INDEX idx_clients_email     ON clients(email);
CREATE INDEX idx_clients_tier      ON clients(membership_type);
CREATE INDEX idx_clients_name_trgm ON clients USING GIN (name gin_trgm_ops);

-- =============================================================================
-- 7. APPOINTMENTS
-- =============================================================================
CREATE TABLE appointments (
    id              UUID                PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id       UUID                NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    staff_id        UUID                NOT NULL REFERENCES staff(id)   ON DELETE RESTRICT,
    service_id      UUID                NOT NULL REFERENCES services(id) ON DELETE RESTRICT,
    appointment_date DATE               NOT NULL,
    start_time      TIME                NOT NULL,
    end_time        TIME                NOT NULL,
    duration_mins   INT                 NOT NULL,
    amount          NUMERIC(10,2)       NOT NULL,
    status          appointment_status  NOT NULL DEFAULT 'PENDING',
    source          booking_source      NOT NULL DEFAULT 'WALK_IN',
    notes           TEXT,
    reminder_sent   BOOLEAN             NOT NULL DEFAULT FALSE,
    invoice_id      UUID,               -- set after checkout (FK added below)
    created_by      UUID                REFERENCES users(id) ON DELETE SET NULL,
    cancelled_at    TIMESTAMPTZ,
    cancel_reason   TEXT,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_appt_date      ON appointments(appointment_date);
CREATE INDEX idx_appt_client    ON appointments(client_id);
CREATE INDEX idx_appt_staff     ON appointments(staff_id);
CREATE INDEX idx_appt_status    ON appointments(status);
CREATE INDEX idx_appt_date_status ON appointments(appointment_date, status);

-- =============================================================================
-- 8. INVOICES
-- =============================================================================
CREATE TABLE invoices (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_number      VARCHAR(30)     NOT NULL UNIQUE,
    client_id           UUID            NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    appointment_id      UUID            REFERENCES appointments(id) ON DELETE SET NULL,
    invoice_date        DATE            NOT NULL DEFAULT CURRENT_DATE,
    subtotal            NUMERIC(10,2)   NOT NULL DEFAULT 0,
    discount_amount     NUMERIC(10,2)   NOT NULL DEFAULT 0,
    discount_percent    NUMERIC(5,2)    NOT NULL DEFAULT 0,
    gst_amount          NUMERIC(10,2)   NOT NULL DEFAULT 0,
    total_amount        NUMERIC(10,2)   NOT NULL DEFAULT 0,
    amount_paid         NUMERIC(10,2)   NOT NULL DEFAULT 0,
    amount_due          NUMERIC(10,2)   GENERATED ALWAYS AS (total_amount - amount_paid) STORED,
    payment_method      payment_method  NOT NULL DEFAULT 'CASH',
    status              invoice_status  NOT NULL DEFAULT 'DRAFT',
    notes               TEXT,
    points_earned       INT             NOT NULL DEFAULT 0,
    points_redeemed     INT             NOT NULL DEFAULT 0,
    voucher_id          UUID,           -- FK added below
    membership_id       UUID,           -- FK added below
    created_by          UUID            REFERENCES users(id) ON DELETE SET NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoice_client  ON invoices(client_id);
CREATE INDEX idx_invoice_date    ON invoices(invoice_date);
CREATE INDEX idx_invoice_status  ON invoices(status);
CREATE INDEX idx_invoice_number  ON invoices(invoice_number);

-- =============================================================================
-- 9. INVOICE LINE ITEMS
-- =============================================================================
CREATE TABLE invoice_items (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_id      UUID            NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    service_id      UUID            REFERENCES services(id) ON DELETE SET NULL,
    item_name       VARCHAR(150)    NOT NULL,
    item_type       VARCHAR(20)     NOT NULL DEFAULT 'SERVICE',  -- SERVICE | PRODUCT
    staff_id        UUID            REFERENCES staff(id) ON DELETE SET NULL,
    quantity        INT             NOT NULL DEFAULT 1,
    unit_price      NUMERIC(10,2)   NOT NULL,
    discount_pct    NUMERIC(5,2)    NOT NULL DEFAULT 0,
    gst_percent     NUMERIC(5,2)    NOT NULL DEFAULT 18,
    gst_amount      NUMERIC(10,2)   NOT NULL DEFAULT 0,
    line_total      NUMERIC(10,2)   NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inv_items_invoice ON invoice_items(invoice_id);

-- =============================================================================
-- 10. INVENTORY CATEGORIES
-- =============================================================================
CREATE TABLE inventory_categories (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE
);

-- =============================================================================
-- 11. INVENTORY ITEMS
-- =============================================================================
CREATE TABLE inventory_items (
    id              UUID                PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id     UUID                REFERENCES inventory_categories(id) ON DELETE SET NULL,
    name            VARCHAR(200)        NOT NULL,
    brand           VARCHAR(100),
    sku             VARCHAR(50)         UNIQUE,
    description     TEXT,
    unit            VARCHAR(20)         NOT NULL DEFAULT 'piece',
    current_stock   INT                 NOT NULL DEFAULT 0,
    min_stock_level INT                 NOT NULL DEFAULT 5,
    cost_price      NUMERIC(10,2)       NOT NULL DEFAULT 0,
    selling_price   NUMERIC(10,2)       NOT NULL DEFAULT 0,
    mrp             NUMERIC(10,2),
    supplier        VARCHAR(150),
    barcode         VARCHAR(50)         UNIQUE,
    expiry_date     DATE,
    last_restocked  DATE,
    status          inventory_status    NOT NULL DEFAULT 'IN_STOCK',
    is_for_retail   BOOLEAN             NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inv_category ON inventory_items(category_id);
CREATE INDEX idx_inv_status   ON inventory_items(status);
CREATE INDEX idx_inv_sku      ON inventory_items(sku);
CREATE INDEX idx_inv_name_trgm ON inventory_items USING GIN (name gin_trgm_ops);

-- =============================================================================
-- 12. STOCK MOVEMENTS (audit trail for every stock change)
-- =============================================================================
CREATE TABLE stock_movements (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id         UUID        NOT NULL REFERENCES inventory_items(id) ON DELETE CASCADE,
    movement_type   VARCHAR(20) NOT NULL,  -- IN | OUT | ADJUSTMENT | WASTE | TRANSFER
    quantity        INT         NOT NULL,
    before_stock    INT         NOT NULL,
    after_stock     INT         NOT NULL,
    reference_type  VARCHAR(30),           -- INVOICE | PURCHASE_ORDER | MANUAL
    reference_id    UUID,
    notes           TEXT,
    performed_by    UUID        REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_stock_item ON stock_movements(item_id);
CREATE INDEX idx_stock_date ON stock_movements(created_at);

-- =============================================================================
-- 13. EXPENSES
-- =============================================================================
CREATE TABLE expense_categories (
    id      UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    name    VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE expenses (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id         UUID            REFERENCES expense_categories(id) ON DELETE SET NULL,
    description         VARCHAR(300)    NOT NULL,
    amount              NUMERIC(10,2)   NOT NULL,
    expense_date        DATE            NOT NULL DEFAULT CURRENT_DATE,
    paid_by             payment_method  NOT NULL DEFAULT 'CASH',
    receipt_url         TEXT,
    has_receipt         BOOLEAN         NOT NULL DEFAULT FALSE,
    notes               TEXT,
    status              expense_status  NOT NULL DEFAULT 'PENDING',
    approved_by         UUID            REFERENCES users(id) ON DELETE SET NULL,
    approved_at         TIMESTAMPTZ,
    created_by          UUID            REFERENCES users(id) ON DELETE SET NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_expense_date     ON expenses(expense_date);
CREATE INDEX idx_expense_category ON expenses(category_id);
CREATE INDEX idx_expense_status   ON expenses(status);

-- =============================================================================
-- 14. ATTENDANCE
-- =============================================================================
CREATE TABLE attendance (
    id              UUID                PRIMARY KEY DEFAULT uuid_generate_v4(),
    staff_id        UUID                NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    work_date       DATE                NOT NULL,
    check_in        TIME,
    check_out       TIME,
    hours_worked    NUMERIC(4,2)        NOT NULL DEFAULT 0,
    overtime_hours  NUMERIC(4,2)        NOT NULL DEFAULT 0,
    status          attendance_status   NOT NULL DEFAULT 'PRESENT',
    notes           TEXT,
    marked_by       UUID                REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    UNIQUE (staff_id, work_date)
);

CREATE INDEX idx_attendance_staff ON attendance(staff_id);
CREATE INDEX idx_attendance_date  ON attendance(work_date);

-- =============================================================================
-- 15. LOYALTY PROGRAMS
-- =============================================================================
CREATE TABLE loyalty_programs (
    id                  UUID                    PRIMARY KEY DEFAULT uuid_generate_v4(),
    name                VARCHAR(150)            NOT NULL,
    description         TEXT,
    program_type        loyalty_program_type    NOT NULL DEFAULT 'POINTS',
    status              VARCHAR(10)             NOT NULL DEFAULT 'ACTIVE',
    -- POINTS type
    points_per_rupee    NUMERIC(5,2)            NOT NULL DEFAULT 1,
    value_per_point     NUMERIC(5,2)            NOT NULL DEFAULT 0.5,
    min_redeem_points   INT                     NOT NULL DEFAULT 100,
    max_redeem_pct      NUMERIC(5,2)            NOT NULL DEFAULT 20,
    bonus_on_signup     INT                     NOT NULL DEFAULT 0,
    -- MULTIPLIER type
    multiplier          NUMERIC(4,2),
    tier_required       client_tier,
    -- BONUS type
    bonus_points        INT,
    -- REFERRAL type
    referrer_points     INT,
    referee_points      INT,
    -- Scope
    applicable_to       redeem_type             NOT NULL DEFAULT 'ALL',
    created_at          TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ             NOT NULL DEFAULT NOW()
);

-- =============================================================================
-- 16. LOYALTY POINT TRANSACTIONS
-- =============================================================================
CREATE TABLE loyalty_transactions (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id       UUID        NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    program_id      UUID        REFERENCES loyalty_programs(id) ON DELETE SET NULL,
    invoice_id      UUID        REFERENCES invoices(id) ON DELETE SET NULL,
    transaction_type VARCHAR(20) NOT NULL,  -- EARN | REDEEM | BONUS | EXPIRE | ADJUST
    points          INT         NOT NULL,   -- positive = earn, negative = redeem
    balance_after   INT         NOT NULL,
    notes           TEXT,
    created_by      UUID        REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_loyalty_client ON loyalty_transactions(client_id);
CREATE INDEX idx_loyalty_date   ON loyalty_transactions(created_at);

-- =============================================================================
-- 17. MEMBERSHIP PACKAGES
-- =============================================================================
CREATE TABLE membership_packages (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    price           NUMERIC(10,2) NOT NULL,
    validity_days   INT          NOT NULL DEFAULT 90,
    included_count  INT          NOT NULL DEFAULT 0,  -- no. of service uses
    bonus_wallet    NUMERIC(10,2) NOT NULL DEFAULT 0,
    discount_pct    NUMERIC(5,2)  NOT NULL DEFAULT 0,
    color_hex       VARCHAR(7)    NOT NULL DEFAULT '#c9a96e',
    status          VARCHAR(10)   NOT NULL DEFAULT 'ACTIVE',
    sort_order      INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- Services included in a package
CREATE TABLE membership_package_services (
    package_id  UUID    NOT NULL REFERENCES membership_packages(id) ON DELETE CASCADE,
    service_name VARCHAR(150) NOT NULL,
    PRIMARY KEY (package_id, service_name)
);

-- =============================================================================
-- 18. CLIENT MEMBERSHIPS (enrollment)
-- =============================================================================
CREATE TABLE client_memberships (
    id              UUID                PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id       UUID                NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    package_id      UUID                NOT NULL REFERENCES membership_packages(id) ON DELETE RESTRICT,
    invoice_id      UUID                REFERENCES invoices(id) ON DELETE SET NULL,
    start_date      DATE                NOT NULL DEFAULT CURRENT_DATE,
    expiry_date     DATE                NOT NULL,
    services_used   INT                 NOT NULL DEFAULT 0,
    wallet_balance  NUMERIC(10,2)       NOT NULL DEFAULT 0,
    status          membership_status   NOT NULL DEFAULT 'ACTIVE',
    cancelled_at    TIMESTAMPTZ,
    cancel_reason   TEXT,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cm_client  ON client_memberships(client_id);
CREATE INDEX idx_cm_status  ON client_memberships(status);
CREATE INDEX idx_cm_expiry  ON client_memberships(expiry_date);

-- =============================================================================
-- 19. GIFT VOUCHERS
-- =============================================================================
CREATE TABLE gift_vouchers (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    code            VARCHAR(30)     NOT NULL UNIQUE,
    face_value      NUMERIC(10,2)   NOT NULL,
    remaining_value NUMERIC(10,2)   NOT NULL,
    issued_to_id    UUID            REFERENCES clients(id) ON DELETE SET NULL,
    issued_to_name  VARCHAR(150),   -- for anonymous / non-client
    issue_date      DATE            NOT NULL DEFAULT CURRENT_DATE,
    expiry_date     DATE            NOT NULL,
    status          voucher_status  NOT NULL DEFAULT 'ACTIVE',
    notes           TEXT,
    created_by      UUID            REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_voucher_code   ON gift_vouchers(code);
CREATE INDEX idx_voucher_status ON gift_vouchers(status);

-- Voucher redemption ledger
CREATE TABLE voucher_redemptions (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    voucher_id  UUID        NOT NULL REFERENCES gift_vouchers(id) ON DELETE CASCADE,
    invoice_id  UUID        REFERENCES invoices(id) ON DELETE SET NULL,
    amount      NUMERIC(10,2) NOT NULL,
    redeemed_at TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- =============================================================================
-- CROSS-TABLE FK ADD-BACKS (circular refs)
-- =============================================================================
ALTER TABLE appointments
    ADD CONSTRAINT fk_appt_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE SET NULL;

ALTER TABLE invoices
    ADD CONSTRAINT fk_invoice_voucher
        FOREIGN KEY (voucher_id) REFERENCES gift_vouchers(id) ON DELETE SET NULL;

ALTER TABLE invoices
    ADD CONSTRAINT fk_invoice_membership
        FOREIGN KEY (membership_id) REFERENCES client_memberships(id) ON DELETE SET NULL;

-- =============================================================================
-- AUDIT / UPDATED_AT TRIGGER (reusable)
-- =============================================================================
CREATE OR REPLACE FUNCTION trigger_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply to all tables with updated_at
DO $$
DECLARE
  tbl TEXT;
BEGIN
  FOREACH tbl IN ARRAY ARRAY[
    'salon_settings','users','service_categories','services','staff',
    'clients','appointments','invoices','inventory_items','expenses',
    'attendance','loyalty_programs','membership_packages','client_memberships','gift_vouchers'
  ] LOOP
    EXECUTE format('
      CREATE TRIGGER trg_%s_updated_at
      BEFORE UPDATE ON %s
      FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
    ', tbl, tbl);
  END LOOP;
END;
$$;

-- =============================================================================
-- AUTO-UPDATE INVENTORY STATUS TRIGGER
-- =============================================================================
CREATE OR REPLACE FUNCTION trigger_update_inventory_status()
RETURNS TRIGGER AS $$
DECLARE
  setting_threshold INT;
BEGIN
  SELECT COALESCE(NEW.min_stock_level, 5) INTO setting_threshold;
  IF NEW.current_stock <= 0 THEN
    NEW.status = 'OUT_OF_STOCK';
  ELSIF NEW.current_stock <= setting_threshold THEN
    NEW.status = 'LOW_STOCK';
  ELSE
    NEW.status = 'IN_STOCK';
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_inventory_status
BEFORE INSERT OR UPDATE OF current_stock ON inventory_items
FOR EACH ROW EXECUTE FUNCTION trigger_update_inventory_status();

-- =============================================================================
-- AUTO-UPDATE CLIENT TIER TRIGGER
-- =============================================================================
CREATE OR REPLACE FUNCTION trigger_update_client_tier()
RETURNS TRIGGER AS $$
BEGIN
  IF NEW.total_spend >= 75000 THEN
    NEW.membership_type = 'PLATINUM';
  ELSIF NEW.total_spend >= 25000 THEN
    NEW.membership_type = 'GOLD';
  ELSIF NEW.total_spend >= 10000 THEN
    NEW.membership_type = 'SILVER';
  ELSE
    NEW.membership_type = 'BASIC';
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_client_tier
BEFORE INSERT OR UPDATE OF total_spend ON clients
FOR EACH ROW EXECUTE FUNCTION trigger_update_client_tier();

-- =============================================================================
-- SEED DATA
-- =============================================================================

-- Default salon settings
INSERT INTO salon_settings (salon_name, tagline, phone, email, address, gst_number)
VALUES ('Luxe & Co. Salon', 'Premium Beauty Experience',
        '+91 98765 43210', 'hello@luxeandco.in',
        '12, Connaught Place, New Delhi – 110001', '07AABCS1429B1ZB');

-- Default admin user (password: Admin@123 — change immediately)
INSERT INTO users (email, password_hash, role, full_name, phone)
VALUES ('admin@luxeandco.in',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/oe7Q5gx.4oG3Y0Xbe',
        'SUPER_ADMIN', 'System Admin', '+91 98765 43210');

-- Service categories
INSERT INTO service_categories (name, icon, color, sort_order) VALUES
  ('Hair Services',      '✂️', '#c9a96e', 1),
  ('Color & Treatment',  '🎨', '#8b5e3c', 2),
  ('Skin & Facial',      '✨', '#d4b8a5', 3),
  ('Nail Care',          '💅', '#e8a4c9', 4),
  ('Spa & Wellness',     '🧖', '#a8c5b5', 5),
  ('Makeup',             '💄', '#e87d8e', 6);

-- Expense categories
INSERT INTO expense_categories (name) VALUES
  ('Rent'),('Utilities'),('Supplies'),('Maintenance'),
  ('Marketing'),('Staff'),('Equipment'),('Miscellaneous');

-- Inventory categories
INSERT INTO inventory_categories (name) VALUES
  ('Hair Care'),('Skin Care'),('Nail Care'),
  ('Color & Chemical'),('Tools & Equipment'),('Consumables');

-- Default loyalty program
INSERT INTO loyalty_programs (name, description, program_type, points_per_rupee, value_per_point, min_redeem_points, max_redeem_pct, bonus_on_signup, applicable_to)
VALUES ('Welcome Points', 'Earn 1 point per ₹1 spent', 'POINTS', 1, 0.5, 100, 20, 50, 'ALL');
