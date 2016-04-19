CREATE TABLE endpoint (
    id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    organization_id INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the organization table',
    address VARCHAR(256),
    instructions VARCHAR(1024),
    delivery_confirm BIT(1) NULL,
    error BIT(1) NULL,
    operational_status ENUM('INACTIVE','ACTIVE','OUTAGE') NOT NULL,
    PRIMARY KEY (id),
    KEY fk_endpoint_ownser_organization_idx (organization_id),
    CONSTRAINT fk_endpoint_owner_organization FOREIGN KEY (organization_id) REFERENCES organization (id)
		ON DELETE RESTRICT ON UPDATE RESTRICT
);


CREATE TABLE endpoint_delivery_methods (
  endpoint_id int(11) UNSIGNED NOT NULL,
  delivery_methods_id int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (endpoint_id,delivery_methods_id),
  KEY fk_delivery_methods_endpoint (delivery_methods_id),
  KEY fk_endpoint_delivery_methods (endpoint_id),
  CONSTRAINT fk_delivery_methods_endpoint FOREIGN KEY (delivery_methods_id) REFERENCES delivery_methods (id),
  CONSTRAINT fk_endpoint_delivery_methods FOREIGN KEY (endpoint_id) REFERENCES endpoint (id)
);

CREATE TABLE endpoint_document_format (
  endpoint_id int(11) UNSIGNED NOT NULL,
  document_format_id int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (endpoint_id,document_format_id),
  KEY fk_document_format_endpoint (document_format_id),
  KEY fk_endpoint_document_format (endpoint_id),
  CONSTRAINT fk_document_format_endpoint FOREIGN KEY (document_format_id) REFERENCES document_format (id),
  CONSTRAINT fk_endpoint_document_format FOREIGN KEY (endpoint_id) REFERENCES endpoint (id)
);


CREATE TABLE endpoint_organization (
  endpoint_id int(11) UNSIGNED NOT NULL,
  organization_id int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (endpoint_id,organization_id),
  KEY fk_organization_endpoint (organization_id),
  KEY fk_endpoint_organization (endpoint_id),
  CONSTRAINT fk_organization_endpoint FOREIGN KEY (organization_id) REFERENCES organization (id),
  CONSTRAINT fk_endpoint_organization FOREIGN KEY (endpoint_id) REFERENCES endpoint (id)
);



