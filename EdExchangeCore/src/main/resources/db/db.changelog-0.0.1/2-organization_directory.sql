CREATE TABLE `organization_directory` (
  `directory_id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `organization_id` VARCHAR(35) NOT NULL COMMENT 'Unique Identifier that represents an organization, for a given organization id type',
  `organization_id_type` VARCHAR(10) NOT NULL COMMENT 'The code that qualifies the organization id',
  `organization_name` VARCHAR(60) NOT NULL COMMENT 'Fully Qualified name of the Organization',
  `organization_subcode` VARCHAR(50) DEFAULT NULL COMMENT 'Further refinement of the delivery location. Ex, department\n(Math, Science)',
  `entity_id` INT(11) UNSIGNED NOT NULL DEFAULT 1 COMMENT 'foreign key to the entity_code table record primary key',
  `organization_ein` VARCHAR(11) NOT NULL COMMENT 'Employer Identification Number (EIN) is also known as a Federal Tax Identification Number, and is used to identify a business entity',
  `organization_site_url` VARCHAR(255) NOT NULL COMMENT 'Web Site address of organization',
  `description` TEXT,
  `terms_of_use` TEXT NOT NULL COMMENT 'Rules which one must agree to abide by in order to use a service. Terms of service can also be merely a disclaimer, especially regarding the use of websites.',
  `privacy_policy` TEXT NOT NULL COMMENT 'A statement that discloses some or all of the ways a party gathers, uses, discloses and manages a customer or client''s data.',
  
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  `modified_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  PRIMARY KEY (`directory_id`),
  KEY `fk_directory_entity_idx` (`entity_id`),
  CONSTRAINT `fk_directory_entity` FOREIGN KEY (`entity_id`) REFERENCES `organization_entity_code` (`id`)
	ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;