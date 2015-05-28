CREATE TABLE `filter_sets` (
  `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `credential_id` INT(11) UNSIGNED NOT NULL COMMENT 'Foreign key to organization_credential record',
  `category` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(256) DEFAULT NULL,
  `table` VARCHAR(45) NOT NULL,
  `filters_json` TEXT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_filtersets_credential_idx` (`credential_id`),
  CONSTRAINT `fk_filtersets_credential` FOREIGN KEY (`credential_id`) REFERENCES `organization_credential` (`authentication_id`) 
	ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;