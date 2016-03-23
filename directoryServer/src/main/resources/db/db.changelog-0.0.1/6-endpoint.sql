CREATE TABLE endpoint (
    id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    organization_id INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the organization table',
    document_format_id INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the document_format table',
    webservice_url VARCHAR(256),
    delivery_method_id INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the delivery_methods record primary key',
    delivery_confirm BIT(1) NULL,
    error BIT(1) NULL,
    operational_status ENUM('INACTIVE','ACTIVE','OUTAGE') NOT NULL,
    PRIMARY KEY (id),
    KEY fk_endpoint_organization_idx (organization_id),
    CONSTRAINT fk_endpoint_organization FOREIGN KEY (organization_id) REFERENCES organization (id)
		ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY fk_endpoint_document_format_idx (document_format_id),
    CONSTRAINT fk_endpoint_document_format FOREIGN KEY (document_format_id) REFERENCES document_format (id)
		ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY fk_endpoint_delivery_method_idx (delivery_method_id),
    CONSTRAINT fk_endpoint_delivery_method FOREIGN KEY (delivery_method_id) REFERENCES delivery_methods (id)
		ON DELETE RESTRICT ON UPDATE RESTRICT
);