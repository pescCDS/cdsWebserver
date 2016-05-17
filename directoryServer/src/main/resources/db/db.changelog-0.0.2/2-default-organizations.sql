INSERT INTO organization (name, street, city, state, zip, website, created_time, modified_time, telephone, active, enabled) VALUES
('PESC', '1250 Connecticut Avenue NW', 'Washington', 'DC', '20036', 'http://www.pesc.org/', NOW(), NOW(), '(202) 261-6514', true, true);

INSERT INTO organization (name, street, city, state, zip, website, created_time, modified_time, telephone, active, enabled) VALUES
('Butte College', '3536 Butte Campus Dr.', 'Oroville', 'CA', '95965', 'http://www.butte.edu/', NOW(), NOW(), '(530) 895-2511', true, false);

INSERT INTO organization (name, street, city, state, zip, website, created_time, modified_time, telephone, active, enabled) VALUES
('Parchment, Inc.', '3000 Lava Ridge Ct.', 'Roseville', 'CA', '95965', 'http://www.parchment.com/', NOW(), NOW(), '(530) 895-2511', true, false);


INSERT INTO org_orgtype (organization_id,organization_type_id) VALUES (1,1);
INSERT INTO org_orgtype (organization_id, organization_type_id) VALUES (2,2);
INSERT INTO org_orgtype (organization_id, organization_type_id) VALUES (3,3);