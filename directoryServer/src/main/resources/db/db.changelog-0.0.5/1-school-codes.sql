ALTER TABLE school_codes MODIFY COLUMN code_type ENUM('FICE', 'ATP', 'ACT', 'IPEDS', 'OPEID', 'CEEB', 'CDS') NOT NULL;

INSERT INTO school_codes (code, code_type, organization_id) VALUES ('04614160451153','CDS',2);