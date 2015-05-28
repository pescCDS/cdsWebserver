CREATE TABLE `organization_contact` (
  `contact_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `contact_name` VARCHAR(50) NOT NULL COMMENT '',
  `contact_title` VARCHAR(50) NOT NULL COMMENT '',
  `contact_type` VARCHAR(50) NOT NULL COMMENT 'Admin, Technical, Billing, etc...',
  `email` VARCHAR(128) NOT NULL COMMENT '',
  `phone_1` VARCHAR(20) NOT NULL COMMENT 'Supports International',
  `phone_2` VARCHAR(20) NULL COMMENT '',
  `street_address_1` VARCHAR(100) NOT NULL COMMENT '',
  `street_address_2` VARCHAR(100) NOT NULL COMMENT '',
  `street_address_3` VARCHAR(100) NULL COMMENT '',
  `street_address_4` VARCHAR(100) NULL COMMENT '',
  `city` VARCHAR(50) NOT NULL COMMENT '',
  `state` VARCHAR(50) NOT NULL COMMENT '',
  `zip` VARCHAR(20) NOT NULL COMMENT '',
  `country` VARCHAR(50) NOT NULL COMMENT '',
  `directory_id` INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to organization_directory',
  
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  `modified_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  PRIMARY KEY (`contact_id`),
  KEY `fk_contact_directory_idx` (`directory_id`),
  CONSTRAINT `fk_contact_directory` FOREIGN KEY (`directory_id`) REFERENCES `organization_directory` (`directory_id`) 
	ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;