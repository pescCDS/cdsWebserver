ALTER TABLE `pesc_edexchange`.`document_format` 
CHANGE COLUMN `format_name` `format_name` VARCHAR(18) NOT NULL COMMENT 'The name of the format' ,
CHANGE COLUMN `format_inuse_count` `format_inuse_count` TINYINT(2) NOT NULL DEFAULT 0 COMMENT 'Used for tracking format support' ;