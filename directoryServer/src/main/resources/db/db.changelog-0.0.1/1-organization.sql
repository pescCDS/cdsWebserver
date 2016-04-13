CREATE TABLE organization (
  id int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  organization_id VARCHAR(35) COMMENT 'Unique Identifier that represents an organization, for a given organization id type',
  organization_id_type VARCHAR(10) COMMENT 'The code that qualifies the organization id',
  name VARCHAR(60) NOT NULL COMMENT 'Fully Qualified name of the Organization',
  subcode VARCHAR(50) DEFAULT NULL COMMENT 'Further refinement of the delivery location. Ex, department\n(Math, Science)',
  ein VARCHAR(11) COMMENT 'Employer Identification Number (EIN) is also known as a Federal Tax Identification Number, and is used to identify a business entity',
  website VARCHAR(255) NOT NULL COMMENT 'Web Site address of organization',
  short_description VARCHAR(1024),
  terms_of_use TEXT COMMENT 'Rules which one must agree to abide by in order to use a service. Terms of service can also be merely a disclaimer, especially regarding the use of websites.',
  privacy_policy TEXT COMMENT 'A statement that discloses some or all of the ways a party gathers, uses, discloses and manages a customer or client''s data.',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  modified_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  telephone VARCHAR(32),
  type INT NOT NULL COMMENT 'Indicates the type of organization as defined by the application',
	active BIT(1) NOT NULL COMMENT 'Indicates whether the organization is active in the ed exchange program and actively supporting document exchange',
	enabled BIT(1) NOT NULL COMMENT 'Indicates whether the organization has been reviewed and enabled by an administrator',
  street VARCHAR(100) NOT NULL,
	city VARCHAR(50) NOT NULL,
	state VARCHAR(20) NOT NULL,
	zip VARCHAR(20) NOT NULL,
  PRIMARY KEY (id)
);