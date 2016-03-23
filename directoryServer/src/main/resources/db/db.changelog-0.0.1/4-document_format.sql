CREATE TABLE document_format (
  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  name VARCHAR(18) NOT NULL COMMENT 'The name of the format',
  description TEXT,
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  modified_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  inuse_count TINYINT(2) NOT NULL DEFAULT 0 COMMENT 'Used for tracking format support',
  PRIMARY KEY (id)
);