INSERT INTO PPOD_VERSION_INFO (ID,OBJ_VERSION,CREATED,PPOD_VERSION) VALUES (1,0,{ts '2010-03-04 15:13:53.'},1);

INSERT INTO PPOD_ENTITY (ID,OBJ_VERSION,HAS_ATTACHMENTS,PPOD_VERSION_INFO_ID) VALUES (1,0,0,1);
INSERT INTO PPOD_ENTITY (ID,OBJ_VERSION,HAS_ATTACHMENTS,PPOD_VERSION_INFO_ID) VALUES (2,0,0,1);
INSERT INTO PPOD_ENTITY (ID,OBJ_VERSION,HAS_ATTACHMENTS,PPOD_VERSION_INFO_ID) VALUES (3,0,0,1);
INSERT INTO PPOD_ENTITY (ID,OBJ_VERSION,HAS_ATTACHMENTS,PPOD_VERSION_INFO_ID) VALUES (4,0,0,1);
INSERT INTO PPOD_ENTITY (ID,OBJ_VERSION,HAS_ATTACHMENTS,PPOD_VERSION_INFO_ID) VALUES (5,0,0,1);

INSERT INTO PHYLO_CHARACTER (PPOD_ID,LABEL,ID) VALUES ('eb64236a-8d6c-42f3-8e60-cff2aabad7fb','DNA Character',1);

INSERT INTO DNA_CHARACTER (MOLECULAR_CHARACTER_LABEL,ID) VALUES ('DNA Character',1);

INSERT INTO CHARACTER_STATE (LABEL,STATE_NUMBER,ID,PHYLO_CHARACTER_ID) VALUES ('A',0,2,1);
INSERT INTO CHARACTER_STATE (LABEL,STATE_NUMBER,ID,PHYLO_CHARACTER_ID) VALUES ('C',1,3,1);
INSERT INTO CHARACTER_STATE (LABEL,STATE_NUMBER,ID,PHYLO_CHARACTER_ID) VALUES ('G',2,4,1);
INSERT INTO CHARACTER_STATE (LABEL,STATE_NUMBER,ID,PHYLO_CHARACTER_ID) VALUES ('T',3,5,1);

INSERT INTO DNA_STATE (MOLECULAR_STATE_LABEL,ID) VALUES ('A',2);
INSERT INTO DNA_STATE (MOLECULAR_STATE_LABEL,ID) VALUES ('C',3);
INSERT INTO DNA_STATE (MOLECULAR_STATE_LABEL,ID) VALUES ('G',4);
INSERT INTO DNA_STATE (MOLECULAR_STATE_LABEL,ID) VALUES ('T',5);
