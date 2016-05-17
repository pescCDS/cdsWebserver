CREATE TABLE institution_uploads (
  id int(11) NOT NULL AUTO_INCREMENT,
  organization_id INT UNSIGNED NOT NULL COMMENT 'Foriegn key to the organization table. The organization whose user uploaded the file',
  user_id INT NOT NULL COMMENT 'Foriegn key to the user table. The user that uploaded the file.',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  start_time TIMESTAMP NULL COMMENT 'When the file processing started',
  end_time TIMESTAMP NULL COMMENT 'When the file finished processing',
  output_path VARCHAR(256) NULL COMMENT 'The file path to the result log that contains information about the processed file.',
  input_path VARCHAR(256) NULL COMMENT 'The file path to the uploaded file.',
  KEY fk_institution_uploads_organization_idx (organization_id),
  KEY fk_institution_uploads_users_idx (user_id),
  CONSTRAINT fk_institution_uploads_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
  CONSTRAINT fk_institution_uploads_users FOREIGN KEY (user_id) REFERENCES users (id),
  PRIMARY KEY (id)
);


CREATE TABLE institution_upload_results (
  id int(11) NOT NULL AUTO_INCREMENT,
  organization_id INT UNSIGNED NOT NULL COMMENT 'Foriegn key to the organization table. The organization whose institution upload was processed.',
  institution_upload_id INT NOT NULL COMMENT 'Foriegn key to the institution_uploads table.',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created Time',
  line_number INT COMMENT 'The line number from the CSV file used to create the institution.',
  outcome ENUM('SUCCESS','ERROR','WARNING') NOT NULL,
  institution_id INT UNSIGNED COMMENT 'Foreign key to the organization table referenced the institution.',
  institution_name VARCHAR(128) NULL COMMENT 'Denormalized name of the institution/organization for easy reference.',
  message VARCHAR(1024) NULL COMMENT 'A short description of the result.',
  KEY fk_institution_upload_results_organization_idx (organization_id),
  KEY fk_institution_upload_results_institution_uploads_idx (institution_upload_id),
  KEY fk_institution_upload_results_institution_idx (institution_id),
  CONSTRAINT fk_institution_upload_results_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
  CONSTRAINT fk_institution_upload_results_institution_uploads FOREIGN KEY (institution_upload_id) REFERENCES institution_uploads (id),
  PRIMARY KEY (id)
);