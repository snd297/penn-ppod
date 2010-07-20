CREATE TABLE `ATTACHMENT` (
  `PPOD_ID` varchar(36) NOT NULL,
  `BYTES_VALUE` longblob,
  `LABEL` varchar(255) DEFAULT NULL,
  `STRING_VALUE` varchar(255) DEFAULT NULL,
  `ID` bigint(20) NOT NULL,
  `PPOD_ENTITY_ID` bigint(20) NOT NULL,
  `ATTACHMENT_TYPE_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FKA7E14523829B88F4` (`ID`),
  KEY `FKA7E14523A76FEAE6` (`PPOD_ENTITY_ID`),
  KEY `FKA7E14523C4142B82` (`ATTACHMENT_TYPE_ID`),
  CONSTRAINT `FKA7E14523C4142B82` FOREIGN KEY (`ATTACHMENT_TYPE_ID`) REFERENCES `attachment_type` (`ID`),
  CONSTRAINT `FKA7E14523829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`),
  CONSTRAINT `FKA7E14523A76FEAE6` FOREIGN KEY (`PPOD_ENTITY_ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `ATTACHMENT_NAMESPACE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `VERSION` int(11) DEFAULT NULL,
  `LABEL` varchar(64) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `LABEL` (`LABEL`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `ATTACHMENT_TYPE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `VERSION` int(11) DEFAULT NULL,
  `LABEL` varchar(64) NOT NULL,
  `ATTACHMENT_NAMESPACE_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `LABEL_IDX` (`LABEL`),
  KEY `FK1C3DB8D6A7B438D2` (`ATTACHMENT_NAMESPACE_ID`),
  CONSTRAINT `FK1C3DB8D6A7B438D2` FOREIGN KEY (`ATTACHMENT_NAMESPACE_ID`) REFERENCES `attachment_namespace` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_CELL` (
  `POSITION` int(11) NOT NULL,
  `TYPE` int(11) NOT NULL,
  `ELEMENT` int(11) DEFAULT NULL,
  `ID` bigint(20) NOT NULL,
  `DNA_ROW_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK2A3B33AA829B88F4` (`ID`),
  KEY `FK2A3B33AA3C4EA36C` (`DNA_ROW_ID`),
  CONSTRAINT `FK2A3B33AA3C4EA36C` FOREIGN KEY (`DNA_ROW_ID`) REFERENCES `dna_row` (`ID`),
  CONSTRAINT `FK2A3B33AA829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_CELL_ELEMENTS` (
  `DNA_CELL_ID` bigint(20) NOT NULL,
  `ELEMENT` int(11) DEFAULT NULL,
  KEY `FK35FF2DEC225BA4C8` (`DNA_CELL_ID`),
  CONSTRAINT `FK35FF2DEC225BA4C8` FOREIGN KEY (`DNA_CELL_ID`) REFERENCES `dna_cell` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_MATRIX` (
  `PPOD_ID` varchar(36) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `LABEL` varchar(255) NOT NULL,
  `ID` bigint(20) NOT NULL,
  `OTU_SET_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FK9918CC09829B88F4` (`ID`),
  KEY `FK9918CC0966F2217A` (`OTU_SET_ID`),
  CONSTRAINT `FK9918CC0966F2217A` FOREIGN KEY (`OTU_SET_ID`) REFERENCES `otu_set` (`ID`),
  CONSTRAINT `FK9918CC09829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_MATRIX_DNA_ROW` (
  `DNA_MATRIX_ID` bigint(20) NOT NULL,
  `DNA_ROW_ID` bigint(20) NOT NULL,
  `OTU_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`DNA_MATRIX_ID`,`OTU_ID`),
  UNIQUE KEY `DNA_ROW_ID` (`DNA_ROW_ID`),
  KEY `FKE2867C3C93E26F9` (`OTU_ID`),
  KEY `FKE2867C3CE80BB8E8` (`DNA_MATRIX_ID`),
  KEY `FKE2867C3C3C4EA36C` (`DNA_ROW_ID`),
  CONSTRAINT `FKE2867C3C3C4EA36C` FOREIGN KEY (`DNA_ROW_ID`) REFERENCES `dna_row` (`ID`),
  CONSTRAINT `FKE2867C3C93E26F9` FOREIGN KEY (`OTU_ID`) REFERENCES `otu` (`ID`),
  CONSTRAINT `FKE2867C3CE80BB8E8` FOREIGN KEY (`DNA_MATRIX_ID`) REFERENCES `dna_matrix` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_MATRIX_VERSION_INFO` (
  `DNA_MATRIX_ID` bigint(20) NOT NULL,
  `VERSION_INFO_ID` bigint(20) NOT NULL,
  `VERSION_INFO_POSITION` int(11) NOT NULL,
  PRIMARY KEY (`DNA_MATRIX_ID`,`VERSION_INFO_POSITION`),
  KEY `FKA3BF544BE80BB8E8` (`DNA_MATRIX_ID`),
  KEY `FKA3BF544B834273CA` (`VERSION_INFO_ID`),
  CONSTRAINT `FKA3BF544B834273CA` FOREIGN KEY (`VERSION_INFO_ID`) REFERENCES `version_info` (`ID`),
  CONSTRAINT `FKA3BF544BE80BB8E8` FOREIGN KEY (`DNA_MATRIX_ID`) REFERENCES `dna_matrix` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_ROW` (
  `ID` bigint(20) NOT NULL,
  `PPOD_ENTITY_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK96022272829B88F4` (`ID`),
  KEY `FK960222724323A2C4` (`PPOD_ENTITY_ID`),
  CONSTRAINT `FK960222724323A2C4` FOREIGN KEY (`PPOD_ENTITY_ID`) REFERENCES `dna_matrix` (`ID`),
  CONSTRAINT `FK96022272829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_SEQUENCE` (
  `ACCESSION` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `SEQUENCE` longtext NOT NULL,
  `ID` bigint(20) NOT NULL,
  `parent_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKF3FFB609829B88F4` (`ID`),
  KEY `FKF3FFB6092037477B` (`parent_ID`),
  CONSTRAINT `FKF3FFB6092037477B` FOREIGN KEY (`parent_ID`) REFERENCES `dna_sequence_set` (`ID`),
  CONSTRAINT `FKF3FFB609829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_SEQUENCE_PHRED_PHRAP_SCORES` (
  `DNA_SEQUENCE_ID` bigint(20) NOT NULL,
  `ELEMENT` double DEFAULT NULL,
  KEY `FK28B80B1336D03D68` (`DNA_SEQUENCE_ID`),
  CONSTRAINT `FK28B80B1336D03D68` FOREIGN KEY (`DNA_SEQUENCE_ID`) REFERENCES `dna_sequence` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_SEQUENCE_SET` (
  `PPOD_ID` varchar(36) NOT NULL,
  `LABEL` varchar(255) NOT NULL,
  `ID` bigint(20) NOT NULL,
  `OTU_SET_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FKE1DFF90C829B88F4` (`ID`),
  KEY `FKE1DFF90C66F2217A` (`OTU_SET_ID`),
  CONSTRAINT `FKE1DFF90C66F2217A` FOREIGN KEY (`OTU_SET_ID`) REFERENCES `otu_set` (`ID`),
  CONSTRAINT `FKE1DFF90C829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `DNA_SEQUENCE_SET_DNA_SEQUENCE` (
  `DNA_SEQUENCE_SET_ID` bigint(20) NOT NULL,
  `DNA_SEQUENCE_ID` bigint(20) NOT NULL,
  `OTU_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`DNA_SEQUENCE_SET_ID`,`OTU_ID`),
  UNIQUE KEY `DNA_SEQUENCE_ID` (`DNA_SEQUENCE_ID`),
  KEY `FK26A13A5C93E26F9` (`OTU_ID`),
  KEY `FK26A13A5C36D03D68` (`DNA_SEQUENCE_ID`),
  KEY `FK26A13A5C712D9FD9` (`DNA_SEQUENCE_SET_ID`),
  CONSTRAINT `FK26A13A5C712D9FD9` FOREIGN KEY (`DNA_SEQUENCE_SET_ID`) REFERENCES `dna_sequence_set` (`ID`),
  CONSTRAINT `FK26A13A5C36D03D68` FOREIGN KEY (`DNA_SEQUENCE_ID`) REFERENCES `dna_sequence` (`ID`),
  CONSTRAINT `FK26A13A5C93E26F9` FOREIGN KEY (`OTU_ID`) REFERENCES `otu` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `OTU` (
  `PPOD_ID` varchar(36) NOT NULL,
  `LABEL` varchar(255) NOT NULL,
  `ID` bigint(20) NOT NULL,
  `OTU_SET_ID` bigint(20) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FK13310829B88F4` (`ID`),
  KEY `FK1331066F2217A` (`OTU_SET_ID`),
  CONSTRAINT `FK1331066F2217A` FOREIGN KEY (`OTU_SET_ID`) REFERENCES `otu_set` (`ID`),
  CONSTRAINT `FK13310829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `OTU_SET` (
  `PPOD_ID` varchar(36) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `LABEL` varchar(255) NOT NULL,
  `ID` bigint(20) NOT NULL,
  `STUDY_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FKE73D9A93829B88F4` (`ID`),
  KEY `FKE73D9A93526A2499` (`STUDY_ID`),
  CONSTRAINT `FKE73D9A93526A2499` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`),
  CONSTRAINT `FKE73D9A93829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `PPOD_ENTITY` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `VERSION` int(11) DEFAULT NULL,
  `HAS_ATTACHMENTS` bit(1) NOT NULL,
  `VERSION_INFO_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK52FA378D834273CA` (`VERSION_INFO_ID`),
  CONSTRAINT `FK52FA378D834273CA` FOREIGN KEY (`VERSION_INFO_ID`) REFERENCES `version_info` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `STANDARD_CELL` (
  `POSITION` int(11) NOT NULL,
  `TYPE` int(11) NOT NULL,
  `ID` bigint(20) NOT NULL,
  `STANDARD_STATE_ID` bigint(20) DEFAULT NULL,
  `STANDARD_ROW_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK44B3D9A4829B88F4` (`ID`),
  KEY `FK44B3D9A45066577E` (`STANDARD_ROW_ID`),
  KEY `FK44B3D9A41C3A571E` (`STANDARD_STATE_ID`),
  CONSTRAINT `FK44B3D9A41C3A571E` FOREIGN KEY (`STANDARD_STATE_ID`) REFERENCES `standard_state` (`ID`),
  CONSTRAINT `FK44B3D9A45066577E` FOREIGN KEY (`STANDARD_ROW_ID`) REFERENCES `standard_row` (`ID`),
  CONSTRAINT `FK44B3D9A4829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `STANDARD_CELL_STANDARD_STATE` (
  `STANDARD_CELL_ID` bigint(20) NOT NULL,
  `STANDARD_STATE_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`STANDARD_CELL_ID`,`STANDARD_STATE_ID`),
  KEY `FK308D146A1C3A571E` (`STANDARD_STATE_ID`),
  KEY `FK308D146A913A72F6` (`STANDARD_CELL_ID`),
  CONSTRAINT `FK308D146A913A72F6` FOREIGN KEY (`STANDARD_CELL_ID`) REFERENCES `standard_cell` (`ID`),
  CONSTRAINT `FK308D146A1C3A571E` FOREIGN KEY (`STANDARD_STATE_ID`) REFERENCES `standard_state` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `STANDARD_CHARACTER` (
  `PPOD_ID` varchar(36) NOT NULL,
  `LABEL` varchar(255) NOT NULL,
  `ID` bigint(20) NOT NULL,
  `STANDARD_MATRIX_ID` bigint(20) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FK16564C071A6FB396` (`STANDARD_MATRIX_ID`),
  KEY `FK16564C07829B88F4` (`ID`),
  CONSTRAINT `FK16564C07829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`),
  CONSTRAINT `FK16564C071A6FB396` FOREIGN KEY (`STANDARD_MATRIX_ID`) REFERENCES `standard_matrix` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `STANDARD_MATRIX` (
  `PPOD_ID` varchar(36) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `LABEL` varchar(255) NOT NULL,
  `ID` bigint(20) NOT NULL,
  `OTU_SET_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FKF7FFDB83829B88F4` (`ID`),
  KEY `FKF7FFDB8366F2217A` (`OTU_SET_ID`),
  CONSTRAINT `FKF7FFDB8366F2217A` FOREIGN KEY (`OTU_SET_ID`) REFERENCES `otu_set` (`ID`),
  CONSTRAINT `FKF7FFDB83829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `STANDARD_MATRIX_STANDARD_ROW` (
  `STANDARD_MATRIX_ID` bigint(20) NOT NULL,
  `STANDARD_ROW_ID` bigint(20) NOT NULL,
  `OTU_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`STANDARD_MATRIX_ID`,`OTU_ID`),
  UNIQUE KEY `STANDARD_ROW_ID` (`STANDARD_ROW_ID`),
  KEY `FK343970741A6FB396` (`STANDARD_MATRIX_ID`),
  KEY `FK3439707493E26F9` (`OTU_ID`),
  KEY `FK343970745066577E` (`STANDARD_ROW_ID`),
  CONSTRAINT `FK343970745066577E` FOREIGN KEY (`STANDARD_ROW_ID`) REFERENCES `standard_row` (`ID`),
  CONSTRAINT `FK343970741A6FB396` FOREIGN KEY (`STANDARD_MATRIX_ID`) REFERENCES `standard_matrix` (`ID`),
  CONSTRAINT `FK3439707493E26F9` FOREIGN KEY (`OTU_ID`) REFERENCES `otu` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `STANDARD_MATRIX_VERSION_INFO` (
  `STANDARD_MATRIX_ID` bigint(20) NOT NULL,
  `VERSION_INFO_ID` bigint(20) NOT NULL,
  `VERSION_INFO_POSITION` int(11) NOT NULL,
  PRIMARY KEY (`STANDARD_MATRIX_ID`,`VERSION_INFO_POSITION`),
  KEY `FK96BABB111A6FB396` (`STANDARD_MATRIX_ID`),
  KEY `FK96BABB11834273CA` (`VERSION_INFO_ID`),
  CONSTRAINT `FK96BABB11834273CA` FOREIGN KEY (`VERSION_INFO_ID`) REFERENCES `version_info` (`ID`),
  CONSTRAINT `FK96BABB111A6FB396` FOREIGN KEY (`STANDARD_MATRIX_ID`) REFERENCES `standard_matrix` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `STANDARD_ROW` (
  `ID` bigint(20) NOT NULL,
  `STANDARD_MATRIX_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK233FD5381A6FB396` (`STANDARD_MATRIX_ID`),
  KEY `FK233FD538829B88F4` (`ID`),
  CONSTRAINT `FK233FD538829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`),
  CONSTRAINT `FK233FD5381A6FB396` FOREIGN KEY (`STANDARD_MATRIX_ID`) REFERENCES `standard_matrix` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `STANDARD_STATE` (
  `LABEL` varchar(255) NOT NULL,
  `STATE_NUMBER` int(11) NOT NULL,
  `ID` bigint(20) NOT NULL,
  `STANDARD_CHARACTER_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK52AF7C6F829B88F4` (`ID`),
  KEY `FK52AF7C6FBD568E1E` (`STANDARD_CHARACTER_ID`),
  CONSTRAINT `FK52AF7C6FBD568E1E` FOREIGN KEY (`STANDARD_CHARACTER_ID`) REFERENCES `standard_character` (`ID`),
  CONSTRAINT `FK52AF7C6F829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `STUDY` (
  `PPOD_ID` varchar(36) NOT NULL,
  `LABEL` varchar(255) NOT NULL,
  `ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FK4B915A9829B88F4` (`ID`),
  CONSTRAINT `FK4B915A9829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `TREE` (
  `PPOD_ID` varchar(36) NOT NULL,
  `LABEL` varchar(255) NOT NULL,
  `NEWICK` longtext NOT NULL,
  `ID` bigint(20) NOT NULL,
  `TREE_SET_ID` bigint(20) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FK276B9E829B88F4` (`ID`),
  KEY `FK276B9ED3EDF17C` (`TREE_SET_ID`),
  CONSTRAINT `FK276B9ED3EDF17C` FOREIGN KEY (`TREE_SET_ID`) REFERENCES `tree_set` (`ID`),
  CONSTRAINT `FK276B9E829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `TREE_SET` (
  `PPOD_ID` varchar(36) NOT NULL,
  `LABEL` varchar(255) NOT NULL,
  `ID` bigint(20) NOT NULL,
  `OTU_SET_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_ID` (`PPOD_ID`),
  KEY `FK8158DC21829B88F4` (`ID`),
  KEY `FK8158DC2166F2217A` (`OTU_SET_ID`),
  CONSTRAINT `FK8158DC2166F2217A` FOREIGN KEY (`OTU_SET_ID`) REFERENCES `otu_set` (`ID`),
  CONSTRAINT `FK8158DC21829B88F4` FOREIGN KEY (`ID`) REFERENCES `ppod_entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
CREATE TABLE `VERSION_INFO` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `VERSION` int(11) DEFAULT NULL,
  `CREATED` datetime NOT NULL,
  `PPOD_VERSION` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PPOD_VERSION` (`PPOD_VERSION`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;
