CREATE TABLE messages (
  id int(11) NOT NULL AUTO_INCREMENT,
  organization_id INT UNSIGNED NOT NULL COMMENT 'Foriegn key to the organization table.',
  user_id INT NULL COMMENT 'Foreign key to the user table.  May be null if the message is not intended for a specific user.',
  content VARCHAR(4096) COMMENT 'The content of the message.',
  topic VARCHAR(32) COMMENT 'The topic of the message.',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  dismissed BIT(1) NOT NULL DEFAULT 1 COMMENT 'Indicates whether the message has been dismissed by a user.',
  action_required BIT(1) NOT NULL DEFAULT 1 COMMENT 'Indicates whether the message indicates if some action must be performed by the user.',
  KEY fk_message_organization_idx (organization_id),
  CONSTRAINT fk_message_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
  KEY fk_message_users_idx (user_id),
  CONSTRAINT fk_message_users FOREIGN KEY (user_id) REFERENCES users (id),
  PRIMARY KEY (id)
);
