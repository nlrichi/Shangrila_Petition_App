-- Create Database
CREATE DATABASE IF NOT EXISTS slpp_db;
USE slpp_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    dob DATE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    bioid VARCHAR(10) UNIQUE NOT NULL,
    role ENUM('Petitioner', 'Committee') NOT NULL DEFAULT 'Petitioner'
);



-- Signatures Table
CREATE TABLE IF NOT EXISTS signatures (
    id INT AUTO_INCREMENT PRIMARY KEY,
    petitioner_id INT NOT NULL,
    petition_id INT NOT NULL,
    UNIQUE KEY unique_sign (petitioner_id, petition_id),
    FOREIGN KEY (petitioner_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (petition_id) REFERENCES petitions(id) ON DELETE CASCADE
);

-- Settings Table (for signature threshold)
CREATE TABLE IF NOT EXISTS settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    signature_threshold INT DEFAULT 1000
);

-- BioID Table
CREATE TABLE IF NOT EXISTS bioid (
    code VARCHAR(10) PRIMARY KEY,
    used TINYINT(1) DEFAULT 0
);

-- Insert Default Admin User
INSERT INTO users (email, full_name, dob, password_hash, bioid, role)
SELECT 'admin@petition.parliament.sr', 'Admin', '1970-01-01', SHA2('2025%shangrila', 256), 'ADMIN00001', 'Committee'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@petition.parliament.sr'
);

-- Insert Default Signature Threshold
INSERT INTO settings (signature_threshold)
VALUES (1000)
ON DUPLICATE KEY UPDATE signature_threshold = 1000;

-- Insert BioIDs
INSERT INTO bioid (code, used) VALUES
('K1YL8VA2HG', 0), ('V30EPKZQI2', 0), ('QJXQOUPTH9', 0), ('CET8NUAE09', 0),
('BZW5WWDMUY', 0), ('7DMPYAZAP2', 0), ('O3WJFGR5WE', 0), ('GOYWJVDA8A', 0),
('VQKBGSE3EA', 0), ('340B1EOCMG', 0), ('D05HPPQNJ4', 0), ('SEIQTS1H16', 0),
('6EBQ28A62V', 0), ('E7D6YUPQ6J', 0), ('CG1I9SABLL', 0), ('2WYIM3QCK9', 0),
('X16V7LFHR2', 0), ('30MY51J1CJ', 0), ('BPX8O0YB5L', 0), ('49YFTUA96K', 0),
('DHKFIYHMAZ', 0), ('TLFDFY7RDG', 0), ('FH6260T08H', 0), ('AT66BX2FXM', 0),
('V2JX0IC633', 0), ('LZK7P0X0LQ', 0), ('PGPVG5RF42', 0), ('JHDCXB62SA', 0),
('1PUQV970LA', 0), ('C7IFP4VWIL', 0), ('H5C98XCENC', 0), ('FPALKDEL5T', 0),
('O0V55ENOT0', 0), ('CCU1D7QXDT', 0), ('RYU8VSS4N5', 0), ('6X6I6TSUFG', 0),
('2BIB99Z54V', 0), ('F3ATSRR5DQ', 0), ('TTK74SYYAN', 0), ('S22A588D75', 0),
('QTLCWUS8NB', 0), ('ABQYUQCQS2', 0), ('1K3JTWHA05', 0), ('4HTOAI9YKO', 0),
('88V3GKIVSF', 0), ('Y4FC3F9ZGS', 0), ('9JSXWO4LGH', 0), ('FINNMWJY0G', 0),
('PD6XPNB80J', 0), ('8OLYIE2FRC', 0);


