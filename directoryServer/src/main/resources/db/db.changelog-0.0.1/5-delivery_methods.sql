CREATE TABLE delivery_methods (
	id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    method VARCHAR(50) NOT NULL COMMENT 'A description of the delivery method',
    PRIMARY KEY (id)
);