INSERT INTO LEVEL(name) VALUES('Ingénieur') ON CONFLICT DO NOTHING;
INSERT INTO LEVEL(name) VALUES('Ingénieur') ON CONFLICT DO NOTHING;
INSERT INTO LEVEL(name) VALUES('Master') ON CONFLICT DO NOTHING;

INSERT INTO DIPLOMA_NAME(name,level_id) VALUES('Ingénieur informatique imagerie',1) ON CONFLICT DO NOTHING;
INSERT INTO DIPLOMA_NAME(name,level_id) VALUES('Ingénieur informatique',2) ON CONFLICT DO NOTHING;
INSERT INTO DIPLOMA_NAME(name,level_id) VALUES('Master informatique',3) ON CONFLICT DO NOTHING;

INSERT INTO DIPLOMA_LOCATION(city,school) VALUES('Belfort','UTBM') ON CONFLICT DO NOTHING;
INSERT INTO DIPLOMA_LOCATION(city,school) VALUES('Nancy','Polytech Nancy') ON CONFLICT DO NOTHING;
INSERT INTO DIPLOMA_LOCATION(city,school) VALUES('Besançon','UFR Besançon') ON CONFLICT DO NOTHING;

INSERT INTO DIPLOMA(graduation_year,diploma_location_id,diploma_name_id) VALUES(2020,1,1) ON CONFLICT DO NOTHING;
INSERT INTO DIPLOMA(graduation_year,diploma_location_id,diploma_name_id) VALUES(2020,2,2)  ON CONFLICT DO NOTHING;
INSERT INTO DIPLOMA(graduation_year,diploma_location_id,diploma_name_id) VALUES(2020,3,3)  ON CONFLICT DO NOTHING;

INSERT INTO APP_USER(dtype, email, firstname, lastname, password, is_admin) VALUES('Manager', 'admin.un@alten.com', 'Admin', 'Un', '$2y$10$vahGp2WQXilHSfVDqEagNux2kZ1kUzQXQCGJHVkFnD2K/kHjm3Hkq', true) ON CONFLICT DO NOTHING;
INSERT INTO APP_USER(dtype, email, firstname, lastname, password, is_admin) VALUES('Manager', 'admin.deux@alten.com', 'Admin', 'Deux', '$2y$10$vahGp2WQXilHSfVDqEagNux2kZ1kUzQXQCGJHVkFnD2K/kHjm3Hkq', true) ON CONFLICT DO NOTHING;

INSERT INTO CONSULTANT(email, firstname, lastname, experience, manager_id) VALUES('julien.baudot@alten.com', 'Julien', 'Baudot', 7, 2) ON CONFLICT DO NOTHING;
INSERT INTO CONSULTANT(email, firstname, lastname, experience, manager_id) VALUES('adrien.begue@alten.com', 'Adrien', 'Bègue', 1, 1) ON CONFLICT DO NOTHING;
INSERT INTO CONSULTANT(email, firstname, lastname, experience, manager_id) VALUES('robin.jesson@alten.com', 'Robin', 'Jesson', 7, 1) ON CONFLICT DO NOTHING;

INSERT INTO CUSTOMER(name, activity_sector) VALUES('TESLA', 'Automobile') ON CONFLICT DO NOTHING;
INSERT INTO CUSTOMER(name, activity_sector) VALUES('ALTEN', 'Consulting technologique') ON CONFLICT DO NOTHING;

INSERT INTO MISSION(consultant_id, customer_id, state, last_update, reference) VALUES (1,1,'WAITING',NOW(),1) ON CONFLICT DO NOTHING;
INSERT INTO MISSION(consultant_id, customer_id, state, last_update, reference) VALUES (1,2,'WAITING',NOW(),1) ON CONFLICT DO NOTHING;
INSERT INTO MISSION(consultant_id, customer_id, state, last_update, reference) VALUES (2,2,'WAITING',NOW(),3) ON CONFLICT DO NOTHING;

INSERT INTO PROJECT(description,mission_id) VALUES ('projet 1 de mission 1',1) ON CONFLICT DO NOTHING;
INSERT INTO PROJECT(description,mission_id) VALUES ('projet 2 de mission 1',1) ON CONFLICT DO NOTHING;
INSERT INTO PROJECT(description,mission_id) VALUES ('projet 3 de mission 1',1) ON CONFLICT DO NOTHING;
INSERT INTO PROJECT(description,mission_id) VALUES ('projet 1 de mission 2',2) ON CONFLICT DO NOTHING;
INSERT INTO PROJECT(description,mission_id) VALUES ('projet 2 de mission 2',2) ON CONFLICT DO NOTHING;
INSERT INTO PROJECT(mission_id) VALUES (2) ON CONFLICT DO NOTHING;