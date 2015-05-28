CREATE TABLE `document_format` (
  `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `format_name` VARCHAR(10) NOT NULL COMMENT 'The name of the format',
  `format_description` TEXT,
  `format_inuse_count` TINYINT(2) NOT NULL COMMENT 'Used for tracking format support',
  
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  `modified_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;