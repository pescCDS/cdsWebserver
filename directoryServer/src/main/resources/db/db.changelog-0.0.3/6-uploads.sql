CREATE TABLE uploads (
  id int(11) NOT NULL AUTO_INCREMENT,
  organization_id INT UNSIGNED NOT NULL COMMENT 'Foriegn key to the organization table. The organization whose user uploaded the file',
  user_id INT NOT NULL COMMENT 'Foriegn key to the user table. The user that uploaded the file.',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  start_time TIMESTAMP NULL COMMENT 'When the file processing started',
  end_time TIMESTAMP NULL COMMENT 'When the file finished processing',
  output_path VARCHAR(256) NULL COMMENT 'The file path to the result log that contains information about the processed file.',
  input_path VARCHAR(256) NULL COMMENT 'The file path to the uploaded file.',
  KEY fk_uploads_organization_idx (organization_id),
  KEY fk_uploads_users_idx (user_id),
  CONSTRAINT fk_uploads_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
  CONSTRAINT fk_uploads_users FOREIGN KEY (user_id) REFERENCES users (id),
  PRIMARY KEY (id)
);
