CREATE TABLE `document_format` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `format_name` varchar(10) NOT NULL COMMENT 'Primary Key',
  `format_description` text,
  `format_inuse_count` tinyint(2) NOT NULL COMMENT 'Used for tracking format support',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
