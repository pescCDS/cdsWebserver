CREATE TABLE delivery_methods (
	  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    name VARCHAR(20) NOT NULL COMMENT 'The name of the delivery method',
    description VARCHAR(256) NOT NULL COMMENT 'A description of the delivery method',
    PRIMARY KEY (id)
);

INSERT INTO delivery_methods (name,description) VALUES
('Web Service','Deliver through a web service.'),
('SFTP','Deliver through a Secure File Transfer Protocol server.'),
('USPS','Deliver using the United States Postal Service.');