-- ============================================================
--  growthhub SALON MANAGEMENT SYSTEM — PostgreSQL Schema
--  Database: growthhub_salon
--  Charset:  UTF-8
-- ============================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ────────────────────────────────────────────────────────────
-- 1. USERS & AUTH
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS staff (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(120) NOT NULL,
    role                VARCHAR(60)  NOT NULL,
    phone               VARCHAR(20)  NOT NULL UNIQUE,
    email               VARCHAR(120) UNIQUE,
    join_date           DATE,
    avatar              VARCHAR(10),
    gender              VARCHAR(10),
    salary              NUMERIC(12,2),
    commission_rate     NUMERIC(5,2)  DEFAULT 0,
    target_revenue      NUMERIC(14,2),
    work_start_time     VARCHAR(5),
    work_end_time       VARCHAR(5),
    status              VARCHAR(20)  NOT NULL DEFAULT 'active',  -- active | on-leave | inactive
    rating              NUMERIC(3,1) DEFAULT 0,
    total_clients       INT          DEFAULT 0,
    leaves_count        INT          DEFAULT 0,
    created_at          TIMESTAMPTZ  DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS staff_specializations (
    staff_id        BIGINT       NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    specialization  VARCHAR(60)  NOT NULL
);

CREATE TABLE IF NOT EXISTS staff_working_days (
    staff_id  BIGINT      NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    day       VARCHAR(10) NOT NULL   -- Mon, Tue, etc.
);

CREATE TABLE IF NOT EXISTS app_users (
    id          BIGSERIAL   PRIMARY KEY,
    username    VARCHAR(60) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,              -- BCrypt hash
    email       VARCHAR(120) NOT NULL UNIQUE,
    role        VARCHAR(20)  NOT NULL DEFAULT 'STAFF',  -- ADMIN | MANAGER | STAFF
    staff_id    BIGINT       REFERENCES staff(id),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  DEFAULT NOW()
);

-- ────────────────────────────────────────────────────────────
-- 2. CLIENTS
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS clients (
    id               BIGSERIAL    PRIMARY KEY,
    name             VARCHAR(120) NOT NULL,
    phone            VARCHAR(20)  NOT NULL UNIQUE,
    email            VARCHAR(120),
    date_of_birth    DATE,
    gender           VARCHAR(10),
    address          TEXT,
    avatar           VARCHAR(10),
    total_visits     INT          DEFAULT 0,
    total_spend      NUMERIC(14,2) DEFAULT 0,
    last_visit       DATE,
    membership_type  VARCHAR(30)  DEFAULT 'Basic',  -- Basic | Silver | Gold | Platinum
    join_date        DATE,
    notes            TEXT,
    is_active        BOOLEAN      DEFAULT TRUE,
    created_at       TIMESTAMPTZ  DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS client_tags (
    client_id  BIGINT      NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    tag        VARCHAR(30) NOT NULL
);

-- ────────────────────────────────────────────────────────────
-- 3. SERVICE CATALOGUE
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS service_categories (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(80)  NOT NULL UNIQUE,
    icon       VARCHAR(10),
    color      VARCHAR(20),
    is_active  BOOLEAN      DEFAULT TRUE,
    created_at TIMESTAMPTZ  DEFAULT NOW(),
    updated_at TIMESTAMPTZ  DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS services (
    id           BIGSERIAL     PRIMARY KEY,
    category_id  BIGINT        NOT NULL REFERENCES service_categories(id),
    name         VARCHAR(120)  NOT NULL,
    description  TEXT,
    duration     INT           NOT NULL,              -- minutes
    price        NUMERIC(12,2) NOT NULL,
    mrp          NUMERIC(12,2),
    gst_rate     NUMERIC(5,2)  DEFAULT 18,
    status       VARCHAR(20)   NOT NULL DEFAULT 'active',  -- active | inactive
    popular      BOOLEAN       DEFAULT FALSE,
    created_at   TIMESTAMPTZ   DEFAULT NOW(),
    updated_at   TIMESTAMPTZ   DEFAULT NOW()
);

-- ────────────────────────────────────────────────────────────
-- 4. APPOINTMENTS
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS appointments (
    id          BIGSERIAL     PRIMARY KEY,
    client_id   BIGINT        NOT NULL REFERENCES clients(id),
    service_id  BIGINT        NOT NULL REFERENCES services(id),
    staff_id    BIGINT        NOT NULL REFERENCES staff(id),
    date        DATE          NOT NULL,
    time        TIME          NOT NULL,
    duration    INT           NOT NULL,             -- minutes
    amount      NUMERIC(12,2),
    status      VARCHAR(20)   NOT NULL DEFAULT 'upcoming',
    -- upcoming | confirmed | in-progress | completed | cancelled | no-show
    notes       TEXT,
    source      VARCHAR(20)   DEFAULT 'walkin',   -- walkin | online | phone
    created_at  TIMESTAMPTZ   DEFAULT NOW(),
    updated_at  TIMESTAMPTZ   DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_appointments_date        ON appointments(date);
CREATE INDEX IF NOT EXISTS idx_appointments_client      ON appointments(client_id);
CREATE INDEX IF NOT EXISTS idx_appointments_staff_date  ON appointments(staff_id, date);
CREATE INDEX IF NOT EXISTS idx_appointments_status      ON appointments(status);

-- ────────────────────────────────────────────────────────────
-- 5. INVOICES / POS BILLING
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS invoices (
    id                       BIGSERIAL     PRIMARY KEY,
    invoice_number           VARCHAR(20)   NOT NULL UNIQUE,  -- e.g. INV-2025-0001
    client_id                BIGINT        NOT NULL REFERENCES clients(id),
    staff_id                 BIGINT        REFERENCES staff(id),
    date                     DATE          NOT NULL,
    subtotal                 NUMERIC(14,2) NOT NULL DEFAULT 0,
    discount                 NUMERIC(14,2) DEFAULT 0,
    discount_type            VARCHAR(10)   DEFAULT 'flat',   -- flat | percent
    gst_amount               NUMERIC(14,2) DEFAULT 0,
    total                    NUMERIC(14,2) NOT NULL DEFAULT 0,
    payment_method           VARCHAR(20),   -- cash | card | upi | wallet | mixed
    status                   VARCHAR(20)   NOT NULL DEFAULT 'paid',  -- paid | pending | cancelled
    loyalty_points_earned    INT           DEFAULT 0,
    loyalty_points_redeemed  INT           DEFAULT 0,
    notes                    TEXT,
    created_at               TIMESTAMPTZ   DEFAULT NOW(),
    updated_at               TIMESTAMPTZ   DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS invoice_items (
    id                  BIGSERIAL     PRIMARY KEY,
    invoice_id          BIGINT        NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    item_name           VARCHAR(120)  NOT NULL,
    item_type           VARCHAR(15),               -- service | product
    quantity            INT           DEFAULT 1,
    unit_price          NUMERIC(12,2) NOT NULL,
    gst_rate            NUMERIC(5,2)  DEFAULT 18,
    line_total          NUMERIC(14,2) NOT NULL,
    service_id          BIGINT        REFERENCES services(id),
    inventory_item_id   BIGINT,                    -- FK to inventory_items
    staff_id            BIGINT        REFERENCES staff(id)
);

CREATE INDEX IF NOT EXISTS idx_invoices_date      ON invoices(date);
CREATE INDEX IF NOT EXISTS idx_invoices_client    ON invoices(client_id);
CREATE INDEX IF NOT EXISTS idx_invoices_status    ON invoices(status);

-- ────────────────────────────────────────────────────────────
-- 6. INVENTORY
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS inventory_items (
    id               BIGSERIAL     PRIMARY KEY,
    name             VARCHAR(120)  NOT NULL,
    category         VARCHAR(60),
    brand            VARCHAR(60),
    sku              VARCHAR(40)   UNIQUE,
    stock            INT           NOT NULL DEFAULT 0,
    min_stock        INT           NOT NULL DEFAULT 0,
    unit             VARCHAR(20),
    cost_price       NUMERIC(12,2),
    selling_price    NUMERIC(12,2),
    supplier         VARCHAR(120),
    last_restocked   DATE,
    expiry_date      DATE,
    status           VARCHAR(20)   NOT NULL DEFAULT 'in-stock',
    -- in-stock | low-stock | out-of-stock
    created_at       TIMESTAMPTZ   DEFAULT NOW(),
    updated_at       TIMESTAMPTZ   DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_inventory_category ON inventory_items(category);
CREATE INDEX IF NOT EXISTS idx_inventory_status   ON inventory_items(status);

-- ────────────────────────────────────────────────────────────
-- 7. ATTENDANCE
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS attendance (
    id             BIGSERIAL     PRIMARY KEY,
    staff_id       BIGINT        NOT NULL REFERENCES staff(id),
    date           DATE          NOT NULL,
    check_in       TIME,
    check_out      TIME,
    hours_worked   NUMERIC(5,2)  DEFAULT 0,
    overtime_hours NUMERIC(5,2)  DEFAULT 0,
    status         VARCHAR(20)   NOT NULL DEFAULT 'present',
    -- present | absent | late | half-day
    notes          TEXT,
    created_at     TIMESTAMPTZ   DEFAULT NOW(),
    updated_at     TIMESTAMPTZ   DEFAULT NOW(),
    UNIQUE(staff_id, date)
);

CREATE INDEX IF NOT EXISTS idx_attendance_date     ON attendance(date);
CREATE INDEX IF NOT EXISTS idx_attendance_staff    ON attendance(staff_id);

-- ────────────────────────────────────────────────────────────
-- 8. EXPENSES
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS expenses (
    id                    BIGSERIAL     PRIMARY KEY,
    category              VARCHAR(40)   NOT NULL,
    -- Rent | Utilities | Supplies | Maintenance | Marketing | Staff | Equipment | Miscellaneous
    description           TEXT          NOT NULL,
    amount                NUMERIC(14,2) NOT NULL,
    date                  DATE          NOT NULL,
    paid_by               VARCHAR(30),              -- cash | upi | card | bank_transfer
    has_receipt           BOOLEAN       DEFAULT FALSE,
    receipt_url           TEXT,
    status                VARCHAR(20)   NOT NULL DEFAULT 'pending',
    -- pending | approved | rejected
    approved_by_staff_id  BIGINT        REFERENCES staff(id),
    notes                 TEXT,
    created_at            TIMESTAMPTZ   DEFAULT NOW(),
    updated_at            TIMESTAMPTZ   DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_expenses_date     ON expenses(date);
CREATE INDEX IF NOT EXISTS idx_expenses_category ON expenses(category);
CREATE INDEX IF NOT EXISTS idx_expenses_status   ON expenses(status);

-- ────────────────────────────────────────────────────────────
-- 9. LOYALTY PROGRAMS
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS loyalty_programs (
    id                BIGSERIAL     PRIMARY KEY,
    name              VARCHAR(100)  NOT NULL,
    type              VARCHAR(20),   -- points | multiplier | bonus | referral
    status            VARCHAR(20)   DEFAULT 'active',
    description       TEXT,
    -- Points config
    points_per_rupee  NUMERIC(8,2),
    value_per_point   NUMERIC(8,4),
    min_redeem_points INT,
    max_redeem_pct    NUMERIC(5,2),
    bonus_on_signup   INT,
    -- Multiplier config
    multiplier        NUMERIC(5,2),
    tier_required     VARCHAR(20),
    -- Bonus config
    bonus_points      INT,
    -- Referral config
    referrer_points   INT,
    referee_points    INT,
    applicable_to     VARCHAR(50)   DEFAULT 'all',  -- all | services | products
    created_at        TIMESTAMPTZ   DEFAULT NOW(),
    updated_at        TIMESTAMPTZ   DEFAULT NOW()
);

-- ────────────────────────────────────────────────────────────
-- 10. MEMBERSHIP PACKAGES
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS membership_packages (
    id                     BIGSERIAL     PRIMARY KEY,
    name                   VARCHAR(100)  NOT NULL,
    price                  NUMERIC(14,2) NOT NULL,
    validity               INT           NOT NULL,   -- days
    included_service_count INT,
    bonus_wallet           NUMERIC(12,2) DEFAULT 0,
    discount_pct           NUMERIC(5,2)  DEFAULT 0,
    status                 VARCHAR(20)   DEFAULT 'active',
    color                  VARCHAR(20),
    created_at             TIMESTAMPTZ   DEFAULT NOW(),
    updated_at             TIMESTAMPTZ   DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS membership_package_services (
    package_id    BIGINT      NOT NULL REFERENCES membership_packages(id) ON DELETE CASCADE,
    service_name  VARCHAR(120) NOT NULL
);

-- Client enrolled memberships
CREATE TABLE IF NOT EXISTS client_memberships (
    id                 BIGSERIAL     PRIMARY KEY,
    client_id          BIGINT        NOT NULL REFERENCES clients(id),
    package_id         BIGINT        NOT NULL REFERENCES membership_packages(id),
    purchase_date      DATE          NOT NULL,
    expiry_date        DATE          NOT NULL,
    services_remaining INT,
    wallet_balance     NUMERIC(12,2) DEFAULT 0,
    status             VARCHAR(20)   DEFAULT 'active',  -- active | expired | exhausted
    invoice_id         BIGINT        REFERENCES invoices(id),
    created_at         TIMESTAMPTZ   DEFAULT NOW(),
    updated_at         TIMESTAMPTZ   DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_client_memberships_client ON client_memberships(client_id);
CREATE INDEX IF NOT EXISTS idx_client_memberships_status ON client_memberships(status);

-- ────────────────────────────────────────────────────────────
-- 11. LOYALTY ACCOUNTS & TRANSACTIONS
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS client_loyalty_accounts (
    id                     BIGSERIAL    PRIMARY KEY,
    client_id              BIGINT       NOT NULL UNIQUE REFERENCES clients(id),
    points_balance         INT          DEFAULT 0,
    total_points_earned    INT          DEFAULT 0,
    total_points_redeemed  INT          DEFAULT 0,
    tier                   VARCHAR(20)  DEFAULT 'Basic',   -- Basic | Silver | Gold | Platinum
    join_date              DATE,
    created_at             TIMESTAMPTZ  DEFAULT NOW(),
    updated_at             TIMESTAMPTZ  DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS loyalty_transactions (
    id                BIGSERIAL     PRIMARY KEY,
    client_id         BIGINT        NOT NULL REFERENCES clients(id),
    transaction_type  VARCHAR(25)   NOT NULL,
    -- earned | redeemed | bonus | expired | manual_credit | manual_debit
    points            INT           NOT NULL,           -- positive = credit, negative = debit
    description       TEXT,
    invoice_id        BIGINT        REFERENCES invoices(id),
    transaction_date  TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_loyalty_tx_client ON loyalty_transactions(client_id);
CREATE INDEX IF NOT EXISTS idx_loyalty_tx_date   ON loyalty_transactions(transaction_date DESC);

-- ────────────────────────────────────────────────────────────
-- 12. GIFT VOUCHERS
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS gift_vouchers (
    id               BIGSERIAL     PRIMARY KEY,
    code             VARCHAR(20)   NOT NULL UNIQUE,
    client_id        BIGINT        REFERENCES clients(id),
    issued_to_name   VARCHAR(100),
    original_value   NUMERIC(12,2) NOT NULL,
    remaining_value  NUMERIC(12,2) NOT NULL,
    issued_date      DATE          NOT NULL,
    expiry_date      DATE          NOT NULL,
    status           VARCHAR(20)   NOT NULL DEFAULT 'active',
    -- active | partial | redeemed | expired
    occasion         VARCHAR(60),
    created_at       TIMESTAMPTZ   DEFAULT NOW(),
    updated_at       TIMESTAMPTZ   DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_gift_vouchers_code   ON gift_vouchers(code);
CREATE INDEX IF NOT EXISTS idx_gift_vouchers_status ON gift_vouchers(status);

-- ────────────────────────────────────────────────────────────
-- TRIGGERS: auto-update updated_at
-- ────────────────────────────────────────────────────────────

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$;

DO $$
DECLARE t TEXT;
BEGIN
  FOREACH t IN ARRAY ARRAY[
    'staff','app_users','clients','service_categories','services',
    'appointments','invoices','inventory_items','attendance','expenses',
    'loyalty_programs','membership_packages','client_memberships',
    'client_loyalty_accounts','gift_vouchers'
  ] LOOP
    EXECUTE format(
      'CREATE OR REPLACE TRIGGER trg_%s_updated_at BEFORE UPDATE ON %s
       FOR EACH ROW EXECUTE FUNCTION update_updated_at();', t, t);
  END LOOP;
END;
$$;

-- ────────────────────────────────────────────────────────────
-- SEED: Admin user (password: Admin@123)
-- ────────────────────────────────────────────────────────────

INSERT INTO app_users (username, password, email, role, is_active)
VALUES (
  'admin',
  '$2a$12$FQpOZBqgR5Q5c8bTVz3QNOknFPT0Pm5qALH5.rQYcNl2Ql9vLV/Iy',  -- Admin@123
  'admin@growthhub.in',
  'ADMIN',
  TRUE
) ON CONFLICT (username) DO NOTHING;

