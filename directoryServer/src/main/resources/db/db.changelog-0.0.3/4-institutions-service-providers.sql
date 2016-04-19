CREATE TABLE institutions_service_providers (
  institution_id int(11) UNSIGNED NOT NULL,
  service_provider_id int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (institution_id,service_provider_id),
  KEY fk_instition_service_provider (institution_id),
  KEY fk_service_provider_institution (service_provider_id),
  CONSTRAINT fk_instition_service_provider FOREIGN KEY (institution_id) REFERENCES organization (id),
  CONSTRAINT fk_service_provider_institution FOREIGN KEY (service_provider_id) REFERENCES organization (id)
);
