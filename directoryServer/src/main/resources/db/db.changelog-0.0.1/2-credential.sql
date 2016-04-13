CREATE TABLE credential (
  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  username VARCHAR(128) NOT NULL COMMENT 'The security principal name/eppn',
  password VARCHAR(128) NOT NULL,
  enabled BOOLEAN NOT NULL COMMENT 'enabled/disabled status of the security principal',
  public_key VARCHAR(1024) NOT NULL,
  digital_signature VARCHAR(1024) NOT NULL,
  authentication_type VARCHAR(56) NOT NULL COMMENT 'PASSWORD, PKI, ...',
  organization_id INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the organization table',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  modified_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  PRIMARY KEY (id),
  KEY fk_organization_idx (organization_id),
  CONSTRAINT fk_credential_organization FOREIGN KEY (organization_id) REFERENCES organization (id)
	ON DELETE NO ACTION ON UPDATE NO ACTION
);