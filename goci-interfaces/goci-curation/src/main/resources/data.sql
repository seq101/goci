-- GWASGENE
INSERT INTO GWASGENE (ID, GENE) VALUES (11986,'OXER1');
INSERT INTO GWASGENE (ID, GENE) VALUES (12187,'TBX3');
INSERT INTO GWASGENE (ID, GENE) VALUES (12036,'PPM1F');

-- GWASETHNICITY
INSERT INTO GWASETHNICITY (ID, GWASID, TYPE, NUMINDIVIDUALS, ETHNICGROUP, COUNTRYORIGIN, COUNTRYRECRUITMENT, ADDLDESCRIPTION, PREVIOUSLYREPORTED, SAMPLESIZESMATCH, NOTES) VALUES (6450, '6012', 'replication', 1413, 'South East Asian', 'NULL', 'Thailand', 'Bangkok, Thailand', 'NULL', 'Y', 'NULL');
INSERT INTO GWASETHNICITY (ID, GWASID, TYPE, NUMINDIVIDUALS, ETHNICGROUP, COUNTRYORIGIN, COUNTRYRECRUITMENT, ADDLDESCRIPTION, PREVIOUSLYREPORTED, SAMPLESIZESMATCH, NOTES) VALUES (6156, '6012', 'initial', 1772, 'East Asian', 'NULL', 'China', 'Hefei, Anhui Province, China; Hong Kong, China;', 'NULL', 'Y', 'NULL');
INSERT INTO GWASETHNICITY (ID, GWASID, TYPE, NUMINDIVIDUALS, ETHNICGROUP, COUNTRYORIGIN, COUNTRYRECRUITMENT, ADDLDESCRIPTION, PREVIOUSLYREPORTED, SAMPLESIZESMATCH, NOTES) VALUES (6157, '6012', 'replication', 4071, 'East Asian', 'NULL', 'China', 'Anhui, China; Hong Kong, China;', 'NULL', 'Y', 'NULL');

-- GWASREGION
INSERT INTO GWASREGION (ID, REGION) VALUES (42234, '13q14.11');

-- GWASSNP
INSERT INTO GWASSNP (ID, SNP, CHROMOSOME_NAME, CHROMOSOME_POS, LASTUPDATEDATE) VALUES (16780, 'rs7329174', '19', '6546489897', '2010-05-27 14:27:19');

-- GWASEFOTRAITS
INSERT INTO GWASEFOTRAITS (ID, EFOTRAIT, EFOURI) VALUES (141, 'systemic lupus erythematosus', 'http://www.ebi.ac.uk/efo/EFO_0002690');

-- GWASCURATORS
INSERT INTO GWASCURATORS (ID,FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (1,'Joannella','Morales', 'jmorales@ebi.ac.uk','moralesj2');
INSERT INTO GWASCURATORS (ID,FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (2,'Jackie','MacArthur','jalm@ebi.ac.uk','macarthurja');
INSERT INTO GWASCURATORS (ID,FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (3,'Lucia','Hindorff','hindorffl@mail.nih.gov','hindorffl');
INSERT INTO GWASCURATORS (ID,FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (4,'Heather','Junkins','junkinsh@mail.nih.gov','junkinsh');
INSERT INTO GWASCURATORS (ID,FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (5,'Peggy','Hall','Peggy.Hall@nih.gov','kwongp');
INSERT INTO GWASCURATORS (ID,FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (6,'Dani','Welter','dwelter@ebi.ac.uk','welterd');
INSERT INTO GWASCURATORS (ID,FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (61,'Shaila','Chhibba','shaila.chhibba@nih.gov','chhibbas');
INSERT INTO GWASCURATORS (ID, FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (81, 'Emily', 'Bowler', 'NULL', 'NULL');
INSERT INTO GWASCURATORS (ID, FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (21, 'NULL', 'Unassigned', 'NULL', 'NULL');
INSERT INTO GWASCURATORS (ID, FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (22, 'NULL', 'GWAS Catalog', 'NULL', 'NULL');
INSERT INTO GWASCURATORS (ID, FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (41, 'NULL', 'Level 2 Curator', 'NULL', 'NULL');
INSERT INTO GWASCURATORS (ID, FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (42, 'NULL', 'Level 1 Curator', 'NULL', 'NULL');
INSERT INTO GWASCURATORS (ID, FIRSTNAME, LASTNAME, EMAIL, USERNAME) VALUES (43, 'NULL', 'Level 1 Ethnicity Curator', 'NULL', 'NULL');

-- GWASSTATUS
INSERT INTO GWASSTATUS (ID, STATUS, SEQNBR) VALUES (6,'Publish study','6');

-- GWASDISEASETRAITS
INSERT INTO GWASDISEASETRAITS (ID, DISEASETRAIT) VALUES (1164,'Systemic lupus erythematosus');

-- GWASSTUDIES
INSERT INTO GWASSTUDIES (ID, AUTHOR, STUDYDATE, PUBLICATION, TITLE, INITSAMPLESIZE, REPLICSAMPLESIZE, PLATFORM, PMID, CNV, GXE, GXG, DISEASEID)
VALUES (6012,'Yang J','2010-11-02 00:00:00', 'Hum Mol Genet','ELF1 is associated with systemic lupus erythematosus in Asian populations','612 Chinese ancestry cases, 1,160 Chinese ancestry controls','2,090 Chinese ancestry cases, 1,981 Chinese ancestry controls, 462 Thai ancestry cases, 951 Thai ancestry controls','Illumina [513,108]', '21044949', '0', '0', '0', 1164);

-- HOUSEKEEPING
INSERT INTO HOUSEKEEPING (ID, STUDYID, STUDYSNPCHECKEDL1, STUDYSNPCHECKEDL2, PUBLISH, PENDING, PUBLISHDATE, NOTES, ETHNICITYCHECKEDL1, ETHNICITYCHECKEDL2, SENDTONCBI, SENDTONCBIDATE, CHECKEDNCBIERROR, FILENAM, CURATORID, CURATORSTATUSID, ETHNICITYBACKFILLED, RECHECKSNPS, STUDYADDEDDATE, LASTUPDATEDATE) VALUES (1, 6012, '1', '1', '1', '0', '2011-01-03 15:19:20', 'NOTES', '1', '1','0', NULL,'0','/nfs/gwas/Yang.21044949.2010.pdf', 22 , 6 ,'1' ,'1','2011-01-03 00:00:00','2014-06-09 08:17:40' );

-- GWASASSOCIATIONS
INSERT INTO GWASASSOCIATIONS (ID, STUDYID, STRONGESTALLELE, RISKFREQUENCY, ALLELE, PVALUEFLOAT, PVALUETXT, ORPERCOPYNUM, ORTYPE, SNPTYPE, PVALUE_MANTISSA, PVALUE_EXPONENT, ORPERCOPYRECIP, ORPERCOPYSTDERROR, ORPERCOPYRANGE, ORPERCOPYUNITDESCR) VALUES (16617, '6012', 'rs7329174-G', '0.22', 'G', 1E-8, 'NULL', 1.26, '1','Novel', 1, -8, null, null,'[1.16-1.36]', 'NULL');

-- GWASEFOSTUDYXREF
INSERT INTO GWASEFOSTUDYXREF (ID, TRAITID, STUDYID) VALUES (2685,141,6012);

-- GWASREGIONXREF
INSERT INTO GWASREGIONXREF (ID, REGIONID, GWASSNPID) VALUES (4304061,42234,16780);

-- GWASGENEXREF
INSERT INTO GWASGENEXREF (ID, GENEID, GWASSNPID) VALUES (4304061,11986,16780);
INSERT INTO GWASGENEXREF (ID, GENEID, GWASSNPID) VALUES (4304062,12187,16780);
INSERT INTO GWASGENEXREF (ID, GENEID, GWASSNPID) VALUES (4304063,12036,16780);

-- GWASSNPXREF
INSERT INTO GWASSNPXREF (ID, SNPID, GWASSTUDIESSNPID) VALUES (20631, 16780,16617);





