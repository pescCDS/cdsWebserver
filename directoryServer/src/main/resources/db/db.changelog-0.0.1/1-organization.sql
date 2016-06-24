CREATE TABLE organization_type (
  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  name VARCHAR(18) NOT NULL COMMENT 'The name of the organization type',
  description VARCHAR(512),
  PRIMARY KEY (id)
);


INSERT INTO organization_type (name,description) VALUES
('System','The organization that manages the Directory server.'),
('Institution','An educational entity that can receive documents through a service provider and send digitally signed documents after providing a valid SSL certificate to EDExchange.'),
('Service Provider','An organization that sends and receives documents on behalf of itself and/or other institutions.  Requires a network server implementation.');


CREATE TABLE organization (
  id int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  organization_id VARCHAR(35) COMMENT 'Unique Identifier that represents an organization, for a given organization id type',
  organization_id_type VARCHAR(10) COMMENT 'The code that qualifies the organization id',
  name VARCHAR(60) NOT NULL COMMENT 'Fully Qualified name of the Organization',
  subcode VARCHAR(50) DEFAULT NULL COMMENT 'Further refinement of the delivery location. Ex, department\n(Math, Science)',
  ein VARCHAR(11) COMMENT 'Employer Identification Number (EIN) is also known as a Federal Tax Identification Number, and is used to identify a business entity',
  website VARCHAR(255) COMMENT 'Web Site address of organization',
  short_description VARCHAR(1024),
  signing_certificate VARCHAR(4096) COMMENT 'The SSL certificate and public key used to verify documents signed with the paired private key.',
  network_certificate VARCHAR(4096) COMMENT 'The SSL certificate used to configure HTTPS on the network server.',
  network_domain VARCHAR(256) COMMENT 'The domain name for the network server',
  public_key VARCHAR(2048) COMMENT 'The PEM encoded public key extracted from the signing certificate, used to verify digital signatures.',
  terms_of_use TEXT COMMENT 'Rules which one must agree to abide by in order to use a service. Terms of service can also be merely a disclaimer, especially regarding the use of websites.',
  privacy_policy TEXT COMMENT 'A statement that discloses some or all of the ways a party gathers, uses, discloses and manages a customer or client''s data.',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  modified_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  telephone VARCHAR(32),
	active BIT(1) NOT NULL COMMENT 'Indicates whether the organization is active in the ed exchange program and actively supporting document exchange',
	enabled BIT(1) NOT NULL COMMENT 'Indicates whether the organization has been reviewed and enabled by an administrator',
  street VARCHAR(100),
	city VARCHAR(50) NOT NULL,
	state VARCHAR(20) NOT NULL,
	zip VARCHAR(20),
  PRIMARY KEY (id)
);

CREATE TABLE org_orgtype (
  organization_id int(11) UNSIGNED NOT NULL,
  organization_type_id int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (organization_id,organization_type_id),
  KEY fk_orgtype_org (organization_type_id),
  KEY fk_org_orgtype (organization_id),
  CONSTRAINT fk_orgtype_org FOREIGN KEY (organization_type_id) REFERENCES organization_type (id),
  CONSTRAINT fk_org_orgtype FOREIGN KEY (organization_id) REFERENCES organization (id)
);


