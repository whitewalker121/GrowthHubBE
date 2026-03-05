-- ============================================================
--  INVOY SALON — Master Data Seed Script
--  Run after schema.sql
--  psql -U postgres -d invoy_salon -f seed_master_data.sql
-- ============================================================

-- ── Service Categories ────────────────────────────────────

-- ============================================================
--  INVOY SALON MANAGEMENT SYSTEM — PostgreSQL Schema
--  Database: invoy_salon
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
  '$2a$12$FQpOZBqgR5Q5c8bTVz3QNOknFPT0Pm5qALH5.rQYcNl2Ql9vLV/Iy',  
  'admin@invoy.in',
  'ADMIN',
  TRUE
) ON CONFLICT (username) DO NOTHING;



INSERT INTO service_categories (name, icon, color, is_active) VALUES
  ('Hair Services',      '✂️',  '#c9a96e', true),
  ('Color & Treatment',  '🎨',  '#8b5e3c', true),
  ('Skin & Facial',      '✨',  '#d4b8a5', true),
  ('Nail Care',          '💅',  '#e8a4c9', true),
  ('Spa & Wellness',     '🧖',  '#a8c5b5', true),
  ('Makeup',             '💄',  '#e87d8e', true)
ON CONFLICT (name) DO NOTHING;

-- ── Services ─────────────────────────────────────────────
INSERT INTO services (category_id, name, description, duration, price, mrp, gst_rate, status, popular)
SELECT c.id, s.name, s.description, s.duration, s.price, s.mrp, s.gst_rate, 'active', s.popular
FROM (VALUES
  ('Hair Services',     'Full Haircut & Blowout',  'Shampoo, cut, blow dry & style',              60,  1800, 2200, 18, true),
  ('Hair Services',     'Hair Trim',               'Trim split ends & shape',                     30,   600,  800, 18, false),
  ('Hair Services',     'Beard Trim & Facial',     'Beard shaping + hydrating facial',             45,   950, 1200, 18, false),
  ('Color & Treatment', 'Keratin Treatment',       'Smoothing & frizz control (full)',            120,  4500, 5500, 18, true),
  ('Color & Treatment', 'Hair Color (Global)',     'Full head single color application',          150,  5500, 6500, 18, true),
  ('Color & Treatment', 'Highlights / Balayage',  'Partial or full highlights',                  120,  6500, 8000, 18, false),
  ('Color & Treatment', 'Deep Conditioning',       'Intensive moisture treatment',                 60,  1500, 2000, 18, false),
  ('Skin & Facial',     'Classic Facial',          'Cleansing, exfoliation & mask',               60,  1800, 2200, 18, true),
  ('Skin & Facial',     'Gold Facial',             'Anti-aging gold leaf treatment',              75,  3500, 4200, 18, false),
  ('Skin & Facial',     'Eyebrow Threading',       'Precise eyebrow shaping',                     10,   120,  150,  5, false),
  ('Skin & Facial',     'Full Face Waxing',        'Face wax including upper lip',                30,   450,  600,  5, false),
  ('Nail Care',         'Manicure & Pedicure',     'Classic mani-pedi combo',                     90,  2200, 2800, 18, true),
  ('Nail Care',         'Gel Nails',               'Gel application (hands)',                      60,  1800, 2200, 18, false),
  ('Nail Care',         'Nail Art (per nail)',     'Custom design per nail',                       10,   150,  200, 18, false),
  ('Spa & Wellness',    'Head Massage + Hair Wash','Relaxing oil massage & wash',                  45,   800, 1000, 18, false),
  ('Spa & Wellness',    'Full Body Massage',       'Swedish or deep tissue (60 min)',              60,  3000, 3800, 18, true),
  ('Makeup',            'Bridal Makeup',           'Full HD bridal look + hair',                  180, 12000,15000, 18, true),
  ('Makeup',            'Party Makeup',            'Evening / party look',                        60,  3500, 4500, 18, false)
) AS s(cat_name, name, description, duration, price, mrp, gst_rate, popular)
JOIN service_categories c ON c.name = s.cat_name
ON CONFLICT DO NOTHING;

-- ── Staff ─────────────────────────────────────────────────
INSERT INTO staff (name, role, phone, email, join_date, avatar, gender, salary, commission_rate, target_revenue, work_start_time, work_end_time, status, rating, total_clients, leaves_count)
VALUES
  ('Meera Singh',   'Senior Stylist',  '+919900121001', 'meera.singh@luxeandco.in',   '2022-01-15', 'MS', 'Female', 35000, 15, 90000, '09:00', '19:00', 'active', 4.9, 148, 2),
  ('Arjun Patel',   'Hair Colorist',   '+919900121002', 'arjun.patel@luxeandco.in',   '2021-08-10', 'AP', 'Male',   38000, 15, 85000, '09:00', '19:00', 'active', 4.8, 134, 0),
  ('Divya Sharma',  'Nail Technician', '+919900121003', 'divya.sharma@luxeandco.in',  '2023-03-01', 'DS', 'Female', 28000, 12, 50000, '09:00', '19:00', 'active', 4.7,  89, 1),
  ('Priti Verma',   'Makeup Artist',   '+919900121004', 'priti.verma@luxeandco.in',   '2022-06-20', 'PV', 'Female', 32000, 18, 65000, '09:00', '19:00', 'active', 4.9,  62, 3),
  ('Karan Batra',   'Spa Therapist',   '+919900121005', 'karan.batra@luxeandco.in',   '2023-09-12', 'KB', 'Male',   26000, 10, 40000, '09:00', '19:00', 'active', 4.6,  74, 0),
  ('Sonal Agarwal', 'Esthetician',     '+919900121006', 'sonal.agarwal@luxeandco.in', '2024-01-08', 'SA', 'Female', 24000, 10, 35000, '09:00', '19:00', 'on-leave', 4.5, 43, 5)
ON CONFLICT (phone) DO NOTHING;

-- Staff specializations
INSERT INTO staff_specializations (staff_id, specialization)
SELECT s.id, spec FROM staff s
JOIN (VALUES
  ('+919900121001', 'Hair Cutting'),
  ('+919900121001', 'Keratin'),
  ('+919900121001', 'Balayage'),
  ('+919900121001', 'Bridal'),
  ('+919900121002', 'Hair Color'),
  ('+919900121002', 'Highlights'),
  ('+919900121002', 'Beard Grooming'),
  ('+919900121002', 'Men''s Cut'),
  ('+919900121003', 'Gel Nails'),
  ('+919900121003', 'Nail Art'),
  ('+919900121003', 'Manicure'),
  ('+919900121003', 'Pedicure'),
  ('+919900121004', 'Bridal Makeup'),
  ('+919900121004', 'Party Makeup'),
  ('+919900121004', 'HD Makeup'),
  ('+919900121004', 'Airbrush'),
  ('+919900121005', 'Deep Tissue'),
  ('+919900121005', 'Swedish Massage'),
  ('+919900121005', 'Head Massage'),
  ('+919900121006', 'Facials'),
  ('+919900121006', 'Threading'),
  ('+919900121006', 'Waxing')
) AS t(phone, spec) ON s.phone = t.phone
ON CONFLICT DO NOTHING;

-- Staff working days
INSERT INTO staff_working_days (staff_id, day)
SELECT s.id, d FROM staff s
JOIN (VALUES
  ('+919900121001','Mon'),('+919900121001','Tue'),('+919900121001','Wed'),
  ('+919900121001','Thu'),('+919900121001','Fri'),('+919900121001','Sat'),
  ('+919900121002','Mon'),('+919900121002','Tue'),('+919900121002','Wed'),
  ('+919900121002','Thu'),('+919900121002','Fri'),('+919900121002','Sat'),
  ('+919900121003','Mon'),('+919900121003','Wed'),('+919900121003','Fri'),
  ('+919900121003','Sat'),('+919900121003','Sun'),
  ('+919900121004','Tue'),('+919900121004','Thu'),('+919900121004','Fri'),
  ('+919900121004','Sat'),('+919900121004','Sun'),
  ('+919900121005','Mon'),('+919900121005','Tue'),('+919900121005','Thu'),
  ('+919900121005','Fri'),('+919900121005','Sat'),
  ('+919900121006','Mon'),('+919900121006','Tue'),('+919900121006','Wed'),
  ('+919900121006','Thu'),('+919900121006','Fri')
) AS t(phone, d) ON s.phone = t.phone
ON CONFLICT DO NOTHING;

-- ── Clients ───────────────────────────────────────────────
INSERT INTO clients (name, phone, email, date_of_birth, gender, address, avatar, total_visits, total_spend, last_visit, membership_type, join_date, notes, is_active)
VALUES
  ('Priya Sharma',   '+919800111001', 'priya.sharma@gmail.com',  '1992-03-15', 'Female', 'A-14, Lajpat Nagar, Delhi',    'PS', 18, 42500,  '2025-01-15', 'Gold',     '2023-01-10', 'Prefers mild products, sensitive scalp', true),
  ('Rahul Gupta',    '+919800111002', 'rahul.g@outlook.com',     '1988-07-22', 'Male',   'B-7, Vasant Vihar, Delhi',     'RG',  9, 12800,  '2025-01-10', 'Silver',   '2023-06-20', '', true),
  ('Sneha Mehta',    '+919800111003', 'sneha.mehta@yahoo.com',   '1995-11-08', 'Female', 'C-21, South Extension, Delhi', 'SM', 24, 68200,  '2025-01-20', 'Platinum', '2022-08-15', 'Allergic to ammonia-based products', true),
  ('Ananya Kapoor',  '+919800111004', 'ananya.k@gmail.com',      '1998-05-30', 'Female', 'D-3, Hauz Khas, Delhi',        'AK',  6, 15400,  '2025-01-05', 'Basic',    '2024-04-01', '', true),
  ('Vikram Singh',   '+919800111005', 'vikram.s@gmail.com',      '1985-09-14', 'Male',   'E-9, Greater Kailash, Delhi',  'VS', 12, 28900,  '2025-01-18', 'Gold',     '2023-02-28', 'Interested in hair spa packages', true),
  ('Nisha Reddy',    '+919800111006', 'nisha.r@hotmail.com',     '1993-12-01', 'Female', 'F-18, Safdarjung, Delhi',      'NR',  8, 18700,  '2025-01-12', 'Silver',   '2023-09-15', '', true),
  ('Kavya Nair',     '+919800111007', 'kavya.nair@gmail.com',    '1997-02-14', 'Female', 'G-5, Janakpuri, Delhi',        'KN',  3, 32500,  '2025-01-08', 'Gold',     '2024-12-20', 'Bridal package – wedding Feb 2025', true),
  ('Amit Joshi',     '+919800111008', 'amit.j@gmail.com',        '1990-06-25', 'Male',   'H-12, Rohini, Delhi',          'AJ', 15, 22100,  '2025-01-15', 'Silver',   '2022-11-05', 'Comes every 3 weeks', true),
  ('Deepa Pillai',   '+919800111009', 'deepa.p@gmail.com',       '1994-08-19', 'Female', 'I-7, Mayur Vihar, Delhi',      'DP', 10, 19800,  '2025-01-10', 'Silver',   '2023-04-10', '', true),
  ('Rohan Malhotra', '+919800111010', 'rohan.m@gmail.com',       '1989-01-30', 'Male',   'J-2, Dwarka, Delhi',           'RM',  5,  7200,  '2025-01-02', 'Basic',    '2024-07-20', '', true)
ON CONFLICT (phone) DO NOTHING;

-- Loyalty accounts for all clients
INSERT INTO client_loyalty_accounts (client_id, points_balance, total_points_earned, total_points_redeemed, tier, join_date)
SELECT c.id,
  CASE c.membership_type WHEN 'Platinum' THEN 4800 WHEN 'Gold' THEN 2200 WHEN 'Silver' THEN 890 ELSE 320 END,
  CASE c.membership_type WHEN 'Platinum' THEN 5800 WHEN 'Gold' THEN 2700 WHEN 'Silver' THEN 1200 ELSE 320 END,
  CASE c.membership_type WHEN 'Platinum' THEN 1000 WHEN 'Gold' THEN 500 WHEN 'Silver' THEN 310 ELSE 0 END,
  c.membership_type,
  c.join_date
FROM clients c
ON CONFLICT (client_id) DO NOTHING;

-- ── Inventory ─────────────────────────────────────────────
INSERT INTO inventory_items (name, category, brand, sku, stock, min_stock, unit, cost_price, selling_price, supplier, last_restocked, expiry_date, status)
VALUES
  ('Loreal Keratin Cream 500ml',        'Hair Care',         'L''Oreal',      'LOR-KER-500',    12, 5,  'bottle',  850.00, 1200.00, 'Beauty Wholesale Hub', '2025-01-20', '2026-06-30', 'in-stock'),
  ('Schwarzkopf Color 60ml Dark Brown', 'Color & Chemical',  'Schwarzkopf',   'SCH-CLR-DB-60',   3,10,  'tube',    180.00,  280.00, 'Pro Color Supplies',   '2025-01-10', '2026-01-15', 'low-stock'),
  ('Matrix Biolage Shampoo 1L',         'Hair Care',         'Matrix',        'MAT-SHP-1L',      8, 6,  'bottle',  650.00,  950.00, 'Beauty Wholesale Hub', '2025-01-25', '2026-12-31', 'in-stock'),
  ('Wella INVIGO Conditioner 500ml',    'Hair Care',         'Wella',         'WEL-CON-500',     0, 5,  'bottle',  490.00,  750.00, 'Wella Official',       '2024-12-01', '2026-08-20', 'out-of-stock'),
  ('OPI Gel Polish Set 12 colors',      'Nail Care',         'OPI',           'OPI-GEL-SET12',   4, 2,  'set',    3200.00, 4800.00, 'Nail Art World',       '2025-01-15', '2027-01-01', 'in-stock'),
  ('Hydrogen Peroxide 6% 1L',           'Color & Chemical',  'Generic',       'GEN-HP6-1L',      2, 8,  'bottle',  120.00,  180.00, 'Chemical Mart',        '2024-11-30', '2025-11-30', 'low-stock'),
  ('Disposable Face Towels 100pcs',     'Consumables',       'Generic',       'GEN-DFT-100',   350,100, 'piece',     3.50,    0.00, 'Hygiene Plus',         '2025-01-28', NULL,          'in-stock'),
  ('Moroccan Argan Oil 100ml',          'Hair Care',         'Argania',       'ARG-OIL-100',     6, 5,  'bottle',  420.00,  680.00, 'Beauty Wholesale Hub', '2025-01-18', '2026-06-01', 'in-stock'),
  ('UV Nail Lamp 36W',                  'Tools & Equipment', 'SunOne',        'SUN-UV-36W',      3, 1,  'piece',  1200.00,    0.00, 'Nail Art World',       '2024-09-01', NULL,          'in-stock'),
  ('Lotus Facial Kit Bleach+Cleanup',   'Skin Care',         'Lotus',         'LOT-FAC-KIT',    15,10,  'kit',     380.00,  550.00, 'Lotus Distributor',    '2025-01-22', '2026-03-31', 'in-stock')
ON CONFLICT (sku) DO NOTHING;

-- ── Loyalty Programs ──────────────────────────────────────
INSERT INTO loyalty_programs (name, type, status, description, points_per_rupee, value_per_point, min_redeem_points, max_redeem_pct, bonus_on_signup, applicable_to)
VALUES
  ('Welcome Points', 'points', 'active', 'Earn 1 point per ₹1 spent', 1.00, 0.50, 100, 20.00, 50, 'services,products')
ON CONFLICT DO NOTHING;

INSERT INTO loyalty_programs (name, type, status, description, multiplier, tier_required, applicable_to)
VALUES
  ('Gold Members Special', 'multiplier', 'active', '2x points for Gold & above', 2.00, 'Gold', 'services')
ON CONFLICT DO NOTHING;

INSERT INTO loyalty_programs (name, type, status, description, bonus_points, applicable_to)
VALUES
  ('Birthday Bonus', 'bonus', 'active', '200 bonus points on birthday month', 200, 'all')
ON CONFLICT DO NOTHING;

INSERT INTO loyalty_programs (name, type, status, description, referrer_points, referee_points, applicable_to)
VALUES
  ('Referral Reward', 'referral', 'active', '100 pts for referrer, 50 for new client', 100, 50, 'all')
ON CONFLICT DO NOTHING;

-- ── Membership Packages ───────────────────────────────────
INSERT INTO membership_packages (name, price, validity, included_service_count, bonus_wallet, discount_pct, status, color)
VALUES
  ('Silver Glow',     5000,  90, 5, 500.00,  10.00, 'active',   '#9ba3af'),
  ('Gold Radiance',  10000, 180, 8,1200.00,  15.00, 'active',   '#c9a96e'),
  ('Platinum Luxury',20000, 365,20,3000.00,  20.00, 'active',   '#5b9bd5'),
  ('Bridal Package', 35000,  60, 6,5000.00,  25.00, 'active',   '#d4848a'),
  ('Express Monthly', 2500,  30, 4, 200.00,   5.00, 'inactive', '#7fb896')
ON CONFLICT DO NOTHING;

-- Package included services
INSERT INTO membership_package_services (package_id, service_name)
SELECT p.id, t.svc
FROM membership_packages p
JOIN (
  VALUES
  ('Silver Glow',     'Hair Wash'),
  ('Silver Glow',     'Basic Haircut'),
  ('Silver Glow',     'Blowdry'),
  ('Gold Radiance',   'Hair Color'),
  ('Gold Radiance',   'Keratin'),
  ('Gold Radiance',   'Facial'),
  ('Gold Radiance',   'Haircut'),
  ('Platinum Luxury', 'All Services'),
  ('Bridal Package',  'Bridal Makeup'),
  ('Bridal Package',  'Pre-bridal'),
  ('Bridal Package',  'Hair Styling'),
  ('Express Monthly', 'Haircut'),
  ('Express Monthly', 'Eyebrow'),
  ('Express Monthly', 'Face Clean-up')
) AS t(pkg_name, svc)
ON p.name = t.pkg_name
ON CONFLICT DO NOTHING;

-- ── Expenses ──────────────────────────────────────────────
INSERT INTO expenses (category, description, amount, date, paid_by, has_receipt, status)
VALUES
  ('Rent',        'Monthly Rent - February',              45000.00, CURRENT_DATE - 15, 'Bank Transfer', true,  'approved'),
  ('Utilities',   'Electricity Bill - January',            8500.00, CURRENT_DATE - 10, 'Cash',         true,  'approved'),
  ('Supplies',    'Loreal Professional Shampoo (10 units)',12000.00, CURRENT_DATE -  9, 'UPI',          true,  'approved'),
  ('Maintenance', 'AC Service & Filter Replacement',       3500.00, CURRENT_DATE -  8, 'Cash',         false, 'pending'),
  ('Marketing',   'Instagram Ads - Feb Campaign',          5000.00, CURRENT_DATE -  7, 'Card',         true,  'approved'),
  ('Staff',       'Staff Uniform Purchase',                7200.00, CURRENT_DATE - 14, 'Cash',         true,  'approved'),
  ('Supplies',    'Wella Color 20 shades set',            18000.00, CURRENT_DATE -  6, 'UPI',          true,  'approved'),
  ('Equipment',   'Hair Dryer Replacement',               4500.00, CURRENT_DATE -  3, 'Card',         true,  'pending'),
  ('Miscellaneous','Office Stationery',                    1200.00, CURRENT_DATE -  2, 'Cash',         false, 'pending')
ON CONFLICT DO NOTHING;

-- ── Gift Vouchers ─────────────────────────────────────────
INSERT INTO gift_vouchers (code, issued_to_name, original_value, remaining_value, issued_date, expiry_date, status, occasion)
VALUES
  ('GIFT-A3X9K', 'Priya Sharma',  2000.00, 2000.00, CURRENT_DATE - 20, CURRENT_DATE + 160, 'active',  'Birthday'),
  ('GIFT-B7M2P', 'Anonymous',     5000.00, 3500.00, CURRENT_DATE -  3, CURRENT_DATE + 177, 'partial', 'Wedding Gift'),
  ('GIFT-C4N8Q', 'Sneha Reddy',   1000.00,    0.00, CURRENT_DATE - 40, CURRENT_DATE + 140, 'redeemed', NULL)
ON CONFLICT (code) DO NOTHING;

-- ── Admin user (password: Admin@123) ──────────────────────
INSERT INTO app_users (username, password, email, role, is_active)
VALUES (
  'admin',
  '$2a$12$FQpOZBqgR5Q5c8bTVz3QNOknFPT0Pm5qALH5.rQYcNl2Ql9vLV/Iy',
  'admin@invoy.in', 'ADMIN', true
) ON CONFLICT (username) DO NOTHING;

-- Staff user account for Meera Singh (password: Staff@123)
INSERT INTO app_users (username, password, email, role, staff_id, is_active)
SELECT 'meera', '$2a$12$XzN5Mz4Q8p6kH2wRmZ7UMOrPsAjNLqCDE1iT3vKX0e9YfuW8bVQ3e', 'meera.singh@luxeandco.in', 'STAFF', s.id, true
FROM staff s WHERE s.phone = '+919900121001'
ON CONFLICT (username) DO NOTHING;


ALTER TABLE loyalty_transactions
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

update app_users set password='$2a$10$sLe2crG93MSNQ9eIblvG1.8cv3or2nt74eqjB7uSb/kriFhbRO/tW'
