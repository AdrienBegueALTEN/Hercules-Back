INSERT INTO DIPLOMA(entitled, establishment, level, year) VALUES('Ingénieur informatique imagerie', 'UTBM Belfort', 'BAC +5', 2020) ON CONFLICT DO NOTHING;
INSERT INTO DIPLOMA(entitled, establishment, level, year) VALUES('Ingénieur informatique', 'Polytech Nancy', 'BAC +5', 2020) ON CONFLICT DO NOTHING;
INSERT INTO DIPLOMA(entitled, establishment, level, year) VALUES('Master Ingénierie Système & Logiciel', 'UFR ST Besançon', 'BAC +5', 2020) ON CONFLICT DO NOTHING;

INSERT INTO APP_USER(dtype, email, firstname, lastname, password, is_admin) VALUES('Manager', 'admin.admin@alten.com', 'Admin', 'Admin', '$2y$10$Go90NvdWdA4EwarDvcKC8eVkYVjQmTBVsxWn9iwl07iUOzHppItbW', true) ON CONFLICT DO NOTHING;
INSERT INTO APP_USER(dtype, email, firstname, lastname, password, is_admin) VALUES('Manager', 'manager.manager@alten.com', 'Manager', 'Manager', '$2y$10$Go90NvdWdA4EwarDvcKC8eVkYVjQmTBVsxWn9iwl07iUOzHppItbW', false) ON CONFLICT DO NOTHING;
INSERT INTO APP_USER(dtype, email, firstname, lastname, password, is_admin) VALUES('RecruitmentOfficer', 'recruitment.officer@alten.com', 'Recruitment', 'Officer', '$2y$10$Go90NvdWdA4EwarDvcKC8eVkYVjQmTBVsxWn9iwl07iUOzHppItbW', false) ON CONFLICT DO NOTHING;

INSERT INTO CONSULTANT(email, firstname, lastname, experience, manager_id) VALUES('julien.baudot@alten.com', 'Julien', 'Baudot', 7, 2) ON CONFLICT DO NOTHING;
INSERT INTO CONSULTANT(email, firstname, lastname, experience, manager_id) VALUES('adrien.begue@alten.com', 'Adrien', 'Bègue', 1, 1) ON CONFLICT DO NOTHING;
INSERT INTO CONSULTANT(email, firstname, lastname, experience, manager_id) VALUES('robin.jesson@alten.com', 'Robin', 'Jesson', 7, 1) ON CONFLICT DO NOTHING;

INSERT INTO CONSULTANT_DIPLOMAS(consultant_id,diplomas_id) VALUES(3,1) ON CONFLICT DO NOTHING;
INSERT INTO CONSULTANT_DIPLOMAS(consultant_id,diplomas_id) VALUES(1,2) ON CONFLICT DO NOTHING;
INSERT INTO CONSULTANT_DIPLOMAS(consultant_id,diplomas_id) VALUES(2,3) ON CONFLICT DO NOTHING;

INSERT INTO CUSTOMER(name, activity_sector) VALUES('TESLA', 'Automobile') ON CONFLICT DO NOTHING;
INSERT INTO CUSTOMER(name, activity_sector) VALUES('ALTEN', 'Consulting technologique') ON CONFLICT DO NOTHING;

INSERT INTO MISSION(consultant_id, customer_id, sheet_status) VALUES (1,1,'VALIDATED') ON CONFLICT DO NOTHING;
INSERT INTO MISSION(consultant_id, customer_id, sheet_status) VALUES (2,2,'ON_GOING') ON CONFLICT DO NOTHING;
INSERT INTO MISSION(consultant_id, customer_id, sheet_status) VALUES (3,2,'ON_WAITING') ON CONFLICT DO NOTHING;

INSERT INTO MISSION_SHEET(mission_id, version_date, city, comment, country, consultant_start_xp, team_size, title, contract_type) VALUES (1, '2020-03-02', 'Strasbourg', 'Commentaire pour la v1', 'France', 7, 4, 'Titre v1', 'services_center') ON CONFLICT DO NOTHING;
INSERT INTO MISSION_SHEET(mission_id, version_date, city, comment, country, consultant_start_xp, team_size, title, contract_type) VALUES (1, '2020-03-10', 'Metz', 'Commentaire pour la v2', 'France', 3, 1, 'ATitre v2.0', 'technical_assistance') ON CONFLICT DO NOTHING;
INSERT INTO MISSION_SHEET(mission_id, version_date, city, comment, country, consultant_start_xp, team_size, title, contract_type) VALUES (2, '2020-04-10', 'Schiltigheim', 'Commentaire pour la v2', 'France', 3, 1, 'BTitre v2', 'technical_assistance') ON CONFLICT DO NOTHING;
INSERT INTO MISSION_SHEET(mission_id, version_date, city, comment, country, consultant_start_xp, team_size, title, contract_type) VALUES (3, '2020-04-10', 'Schiltigheim', 'Commentaire pour la v2', 'France', 3, 1, 'CTitre v2', 'technical_assistance') ON CONFLICT DO NOTHING;

INSERT INTO PROJECT(description, mission_sheet_id) VALUES ('projet 1-1', 1) ON CONFLICT DO NOTHING;
INSERT INTO PROJECT(description, mission_sheet_id) VALUES ('projet 1-2', 1) ON CONFLICT DO NOTHING;
INSERT INTO PROJECT(description, mission_sheet_id) VALUES ('projet 2-1', 2) ON CONFLICT DO NOTHING;
INSERT INTO PROJECT(description, mission_sheet_id) VALUES ('projet 2-2', 2) ON CONFLICT DO NOTHING;