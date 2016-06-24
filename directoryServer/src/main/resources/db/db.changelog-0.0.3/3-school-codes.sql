CREATE TABLE school_codes (
  id int(11) NOT NULL AUTO_INCREMENT,
  code VARCHAR(32) NOT NULL,
  code_type ENUM('FICE', 'ATP',	'ACT', 'IPEDS', 'OPEID') NOT NULL,
  organization_id INT UNSIGNED NOT NULL COMMENT 'Foriegn key to the organization table.',
  KEY fk_school_codes_organization_idx (organization_id),
  CONSTRAINT fk_school_codes_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
  PRIMARY KEY (id)
);


CREATE UNIQUE INDEX unique_code_per_school_schoolcodes ON school_codes(code_type, organization_id);
CREATE UNIQUE INDEX unique_code_pair ON school_codes(code_type, code);

INSERT INTO school_codes (code, code_type, organization_id) VALUES ('008073','FICE',2);
INSERT INTO school_codes (code, code_type, organization_id) VALUES ('004226','ATP',2);
INSERT INTO school_codes (code, code_type, organization_id) VALUES ('0165','ACT',2);
INSERT INTO school_codes (code, code_type, organization_id) VALUES ('110246','IPEDS',2);