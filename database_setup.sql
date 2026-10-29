-- ========================================================
-- SMART GYM MANAGEMENT SYSTEM 2.0 - PROFESSIONAL DATABASE
-- Relational Schema, Rich Creative Profiles & Analytics Views
-- ========================================================

CREATE DATABASE IF NOT EXISTS smart_gym_db;
USE smart_gym_db;

-- --------------------------------------------------------
-- 1. Members Master Table (with rich fitness ERP attributes)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS members (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    gender VARCHAR(10) NOT NULL,
    plan VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    join_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Active',
    fitness_goal VARCHAR(100) DEFAULT 'General Fitness',
    assigned_trainer VARCHAR(100) DEFAULT 'Self-Guided',
    payment_status VARCHAR(50) DEFAULT 'Paid (UPI)',
    emergency_contact VARCHAR(100),
    blood_group VARCHAR(10),
    fee DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Migration checks in case columns need to be added to an existing table
ALTER TABLE members ADD COLUMN IF NOT EXISTS expiry_date DATE;
ALTER TABLE members ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'Active';
ALTER TABLE members ADD COLUMN IF NOT EXISTS fitness_goal VARCHAR(100) DEFAULT 'General Fitness';
ALTER TABLE members ADD COLUMN IF NOT EXISTS assigned_trainer VARCHAR(100) DEFAULT 'Self-Guided';
ALTER TABLE members ADD COLUMN IF NOT EXISTS payment_status VARCHAR(50) DEFAULT 'Paid (UPI)';
ALTER TABLE members ADD COLUMN IF NOT EXISTS emergency_contact VARCHAR(100);
ALTER TABLE members ADD COLUMN IF NOT EXISTS blood_group VARCHAR(10);

-- --------------------------------------------------------
-- 2. Staff & Trainers Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS staff (
    staff_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100),
    shift VARCHAR(50) NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 3. Member Daily Attendance Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    member_id INT NOT NULL,
    member_name VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    time_in VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    workout_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_att_date (date),
    INDEX idx_att_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 4. Member Fee Payments & Invoices Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS member_payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    member_id INT NOT NULL,
    invoice_no VARCHAR(50) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_date DATE NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'Success',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 5. Creative Views for Instant Analytics
-- --------------------------------------------------------

-- View 1: Active Member Roster with Days to Expiration
CREATE OR REPLACE VIEW v_active_roster AS
SELECT 
    m.member_id,
    m.name AS member_name,
    m.phone,
    m.plan,
    m.type AS tier,
    m.status,
    m.fitness_goal,
    m.assigned_trainer,
    m.expiry_date,
    DATEDIFF(m.expiry_date, CURDATE()) AS days_remaining,
    m.fee
FROM members m
ORDER BY m.member_id ASC;

-- View 2: High-Level Gym Executive Financial & Operational Summary
CREATE OR REPLACE VIEW v_gym_financial_summary AS
SELECT 
    COUNT(*) AS total_registered_members,
    SUM(CASE WHEN CURDATE() <= expiry_date AND status != 'Frozen' THEN 1 ELSE 0 END) AS total_active_members,
    SUM(CASE WHEN DATEDIFF(expiry_date, CURDATE()) BETWEEN 0 AND 7 THEN 1 ELSE 0 END) AS expiring_this_week,
    SUM(CASE WHEN CURDATE() > expiry_date THEN 1 ELSE 0 END) AS expired_members,
    SUM(fee) AS total_projected_revenue,
    AVG(fee) AS average_membership_value
FROM members;

-- View 3: Trainer Client Load Distribution
CREATE OR REPLACE VIEW v_trainer_workload AS
SELECT 
    assigned_trainer,
    COUNT(*) AS total_clients,
    SUM(CASE WHEN type = 'Premium' THEN 1 ELSE 0 END) AS premium_clients
FROM members
GROUP BY assigned_trainer
ORDER BY total_clients DESC;

-- --------------------------------------------------------
-- 6. Creative Seed Data (24+ Diverse & Realistic Members)
-- --------------------------------------------------------
INSERT INTO members (name, phone, gender, plan, type, join_date, expiry_date, status, fitness_goal, assigned_trainer, payment_status, emergency_contact, blood_group, fee)
VALUES 
    ('Rahul Sharma', '9876543210', 'Male', 'Monthly', 'Regular', '2026-10-01', '2026-11-01', 'Active', 'Weight Loss & Cardio', 'Self-Guided (No Personal Coach)', 'Paid (UPI / GPay / PhonePe)', '9876500001 (Brother)', 'B+', 1000.00),
    ('Anjali Menon', '9845123456', 'Female', 'Quarterly', 'Premium', '2026-09-15', '2026-12-15', 'Active', 'CrossFit / HIIT Endurance', 'Kabir Joshi (Fitness & HIIT Coach)', 'Paid (Credit / Debit Card)', '9845100002 (Mother)', 'O+', 4752.00),
    ('Vikram Das', '9712345678', 'Male', 'Yearly', 'Regular', '2025-11-10', '2026-11-10', 'Expiring Soon', 'Strength & Powerlifting', 'Self-Guided (No Personal Coach)', 'Paid (UPI / GPay / PhonePe)', '9712300003 (Father)', 'A+', 9600.00),
    ('Priya Nair', '9447123987', 'Female', 'Monthly', 'Premium', '2026-10-12', '2026-11-12', 'Active', 'Yoga, Mobility & Flexibility', 'Sneha Sen (Nutritionist & Diet)', 'Paid (UPI / GPay / PhonePe)', '9447100004 (Spouse)', 'AB+', 1800.00),
    ('Arjun Patel', '9988776655', 'Male', 'Yearly', 'Premium', '2026-07-01', '2027-07-01', 'Active', 'Muscle Building & Hypertrophy', 'Vikram Malhotra (Head Coach)', 'Paid (Credit / Debit Card)', '9988700005 (Brother)', 'O+', 16200.00),
    ('Sneha Iyer', '9895012345', 'Female', 'Quarterly', 'Regular', '2026-09-22', '2026-12-22', 'Active', 'General Fitness', 'Self-Guided (No Personal Coach)', 'Paid (UPI / GPay / PhonePe)', '9895000006 (Father)', 'B+', 2700.00),
    ('Rohan Verma', '9877012345', 'Male', 'Monthly', 'Regular', '2026-10-18', '2026-11-18', 'Active', 'Weight Loss & Cardio', 'Pooja Hegde (Floor & Calisthenics Trainer)', 'Paid (Cash)', '9877000007 (Spouse)', 'O-', 1000.00),
    ('Meera Krishnan', '9746012345', 'Female', 'Yearly', 'Premium', '2026-06-10', '2027-06-10', 'Active', 'Pre-Wedding Body Transformation', 'Sneha Sen (Nutritionist & Diet)', 'Paid (Credit / Debit Card)', '9746000008 (Sister)', 'A+', 16200.00),
    ('Aakash Gupta', '9822012345', 'Male', 'Monthly', 'Premium', '2026-10-20', '2026-11-20', 'Active', 'Strength & Powerlifting', 'Vikram Malhotra (Head Coach)', 'Paid (UPI / GPay / PhonePe)', '9822000009 (Friend)', 'B+', 1800.00),
    ('Divya Rao', '9840012345', 'Female', 'Yearly', 'Regular', '2025-10-05', '2026-10-05', 'Expired', 'General Fitness', 'Self-Guided (No Personal Coach)', 'Pending Renewal', '9840000010 (Mother)', 'O+', 9600.00),
    ('Karan Singh', '9819012345', 'Male', 'Quarterly', 'Premium', '2026-08-03', '2026-11-03', 'Expiring Soon', 'Muscle Building & Hypertrophy', 'Kabir Joshi (Fitness & HIIT Coach)', 'Paid (Credit / Debit Card)', '9819000011 (Brother)', 'A+', 4752.00),
    ('Neha Thomas', '9977012345', 'Female', 'Monthly', 'Regular', '2026-10-24', '2026-11-24', 'Active', 'Weight Loss & Cardio', 'Self-Guided (No Personal Coach)', 'Paid (UPI / GPay / PhonePe)', '9977000012 (Sister)', 'B+', 1000.00),
    ('Aditya Kulkarni', '9866012345', 'Male', 'Quarterly', 'Premium', '2026-09-01', '2026-12-01', 'Active', 'CrossFit / HIIT Endurance', 'Kabir Joshi (Fitness & HIIT Coach)', 'Paid (UPI / GPay / PhonePe)', '9866000013 (Father)', 'O+', 4752.00),
    ('Ritu Saxena', '9855012345', 'Female', 'Yearly', 'Premium', '2026-05-15', '2027-05-15', 'Active', 'Yoga, Mobility & Flexibility', 'Sneha Sen (Nutritionist & Diet)', 'Paid (Credit / Debit Card)', '9855000014 (Spouse)', 'AB+', 16200.00),
    ('Manav Sen', '9833012345', 'Male', 'Monthly', 'Regular', '2026-10-08', '2026-11-08', 'Active', 'Muscle Building & Hypertrophy', 'Self-Guided (No Personal Coach)', 'Paid (Cash)', '9833000015 (Friend)', 'B-', 1000.00),
    ('Tanvi Deshmukh', '9829012345', 'Female', 'Quarterly', 'Regular', '2026-08-20', '2026-11-20', 'Active', 'General Fitness', 'Pooja Hegde (Floor & Calisthenics Trainer)', 'Paid (UPI / GPay / PhonePe)', '9829000016 (Mother)', 'A+', 2700.00),
    ('Siddharth Roy', '9815012345', 'Male', 'Yearly', 'Regular', '2026-03-14', '2027-03-14', 'Active', 'Strength & Powerlifting', 'Self-Guided (No Personal Coach)', 'Paid (Credit / Debit Card)', '9815000017 (Brother)', 'O+', 9600.00),
    ('Kavita Reddy', '9801012345', 'Female', 'Monthly', 'Premium', '2026-10-25', '2026-11-25', 'Active', 'Pre-Wedding Body Transformation', 'Sneha Sen (Nutritionist & Diet)', 'Paid (UPI / GPay / PhonePe)', '9801000018 (Sister)', 'B+', 1800.00),
    ('Harsh Vardhan', '9799012345', 'Male', 'Monthly', 'Regular', '2026-09-30', '2026-10-30', 'Expiring Soon', 'Weight Loss & Cardio', 'Kabir Joshi (Fitness & HIIT Coach)', 'Paid (UPI / GPay / PhonePe)', '9799000019 (Father)', 'A+', 1000.00),
    ('Fatima Sheikh', '9788012345', 'Female', 'Yearly', 'Premium', '2026-04-18', '2027-04-18', 'Active', 'CrossFit / HIIT Endurance', 'Vikram Malhotra (Head Coach)', 'Paid (Credit / Debit Card)', '9788000020 (Spouse)', 'O+', 16200.00)
ON DUPLICATE KEY UPDATE 
    expiry_date = VALUES(expiry_date),
    status = VALUES(status),
    fitness_goal = VALUES(fitness_goal),
    assigned_trainer = VALUES(assigned_trainer),
    payment_status = VALUES(payment_status),
    emergency_contact = VALUES(emergency_contact),
    blood_group = VALUES(blood_group);

-- Seed Staff Directory
INSERT INTO staff (name, role, phone, email, shift, salary, status)
VALUES 
    ('Vikram Malhotra', 'Head Trainer', '9811122233', 'vikram@smartgym.com', 'Morning (06:00 - 14:00)', 45000.00, 'Active'),
    ('Sneha Sen', 'Nutritionist', '9822233344', 'sneha@smartgym.com', 'General (09:00 - 18:00)', 38000.00, 'Active'),
    ('Kabir Joshi', 'Fitness Coach', '9833344455', 'kabir@smartgym.com', 'Evening (14:00 - 22:00)', 32000.00, 'Active'),
    ('Pooja Hegde', 'Front Desk Manager', '9844455566', 'pooja@smartgym.com', 'Morning (06:00 - 14:00)', 28000.00, 'Active'),
    ('David Miller', 'Facility & Equipment Manager', '9855566677', 'david@smartgym.com', 'General (09:00 - 18:00)', 25000.00, 'Active')
ON DUPLICATE KEY UPDATE salary = VALUES(salary);

-- Seed Member Daily Attendance
INSERT INTO attendance (member_id, member_name, date, time_in, status, workout_type)
VALUES 
    (1, 'Rahul Sharma', CURDATE(), '06:30 AM', 'Present', 'Cardio & Stamina'),
    (2, 'Anjali Menon', CURDATE(), '07:15 AM', 'Present', 'CrossFit / HIIT'),
    (4, 'Priya Nair', CURDATE(), '08:00 AM', 'Present', 'Yoga & Flexibility'),
    (5, 'Arjun Patel', CURDATE(), '06:45 AM', 'Present', 'Weight Training'),
    (7, 'Rohan Verma', CURDATE(), '07:30 AM', 'Present', 'Cardio & Stamina'),
    (8, 'Meera Krishnan', CURDATE(), '09:00 AM', 'Present', 'General Fitness'),
    (9, 'Aakash Gupta', CURDATE(), '08:15 AM', 'Late', 'Weight Training'),
    (11, 'Karan Singh', CURDATE(), '07:00 AM', 'Present', 'Weight Training'),
    (13, 'Aditya Kulkarni', CURDATE(), '06:15 AM', 'Present', 'CrossFit / HIIT'),
    (14, 'Ritu Saxena', CURDATE(), '08:45 AM', 'Present', 'Yoga & Flexibility');

-- Verify Database Contents
SELECT * FROM v_gym_financial_summary;
SELECT * FROM v_active_roster LIMIT 10;
