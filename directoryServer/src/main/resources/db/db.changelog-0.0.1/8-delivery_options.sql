CREATE TABLE `delivery_options` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `member_id` INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the organization_directory record primary key',
    `format_id` INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the document_format record primary key',
    `webservice_url` VARCHAR(256),
    `delivery_method_id` INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the delivery_methods record primary key',
    `delivery_confirm` BIT(1) NULL,
    `error` BIT(1) NULL,
    `operational_status` ENUM('INACTIVE','ACTIVE','OUTAGE') NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_deliveryOptions_directory_idx` (`member_id`),
    CONSTRAINT `fk_deliveryOptions_directory` FOREIGN KEY (`member_id`) REFERENCES `organization_directory` (`directory_id`) 
		ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY `fk_deliveryOptions_format_idx` (`format_id`),
    CONSTRAINT `fk_deliveryOptions_format` FOREIGN KEY (`format_id`) REFERENCES `document_format` (`id`)
		ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY `fk_deliveryOptions_method_idx` (`delivery_method_id`),
    CONSTRAINT `fk_deliveryOptions_method` FOREIGN KEY (`delivery_method_id`) REFERENCES `delivery_methods` (`id`)
		ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;