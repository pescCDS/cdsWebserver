CREATE TABLE contact (
  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  name VARCHAR(50) NOT NULL COMMENT '',
  title VARCHAR(50) NOT NULL COMMENT '',
  type VARCHAR(50) NOT NULL COMMENT 'Admin, Technical, Billing, etc...',
  email VARCHAR(128) NOT NULL COMMENT '',
  phone_1 VARCHAR(20) NOT NULL COMMENT 'Supports International',
  phone_2 VARCHAR(20) NULL COMMENT '',
  street_address VARCHAR(256) NOT NULL COMMENT 'Full address of contact',
  organization_id INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the organization table',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  modified_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  PRIMARY KEY (id),
  KEY fk_contact_organization_idx (organization_id),
  CONSTRAINT fk_contact_organization FOREIGN KEY (organization_id) REFERENCES organization (id)
	ON DELETE NO ACTION ON UPDATE NO ACTION
);