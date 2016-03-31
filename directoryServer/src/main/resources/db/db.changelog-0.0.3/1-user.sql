CREATE TABLE users (
  id int(11) NOT NULL AUTO_INCREMENT,
  username VARCHAR(128) UNIQUE NOT NULL,
  password VARCHAR(128) NOT NULL,
  organization_id INT UNSIGNED NOT NULL COMMENT 'Foriegn key to the organization table.',
  name VARCHAR(60) NOT NULL COMMENT 'First and last name.',
  title VARCHAR(32) COMMENT 'Professional title within the organization.',
  address VARCHAR(255) COMMENT 'Full address.',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  modified_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last Updated Time',
  telephone VARCHAR(32),
  email VARCHAR(128),
  enabled BIT(1) NOT NULL DEFAULT 1 COMMENT 'Indicates whether the the account is enabled.',
  KEY fk_user_organization_idx (organization_id),
  CONSTRAINT fk_user_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
  PRIMARY KEY (id)
);


CREATE TABLE roles (
  id int(11) NOT NULL AUTO_INCREMENT,
  user_id int(11) NOT NULL,
  role varchar(45) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_userid_role (role,id),
  KEY fk_roles_user_idx (user_id),
  CONSTRAINT fk_roles_user_idx FOREIGN KEY (user_id) REFERENCES users(id));