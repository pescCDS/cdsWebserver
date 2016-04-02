INSERT INTO users (username,password,enabled,name,organization_id)
VALUES ('admin','$2a$10$Xdy5DAnmIa2pqAbi9rxyxOB/EqfymmVAsMZzXSb.mJ5BsCmAJLblm', true,'System Admin',2 );

INSERT INTO roles (name) VALUES ('ROLE_SYSTEM_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_ORG_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_SUPPORT');

INSERT INTO user_roles (user_id, role_id) VALUES (1,1);
INSERT INTO user_roles (user_id, role_id) VALUES (1,2);