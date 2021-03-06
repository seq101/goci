/*

################################################################################
Create a view table that joins across all the salient tables to produce a single
summary view over all the data in the catalogue.  A dump of the entire contents
of this view should produce something appropriate to expose as a catalogue
download

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    February 6rd 2015
version: 1.9.9.018 (pre 2.0)
################################################################################

*/

--------------------------------------------------------
--  Mods for Table GENOMIC_CONTEXT
--------------------------------------------------------

ALTER TABLE GENOMIC_CONTEXT ADD (
  ENTREZ_GENE_ID VARCHAR2(255 CHAR));

--------------------------------------------------------
-- Create temporary GENOMIC_CONTEXT_SEQUENCE
--------------------------------------------------------
CREATE SEQUENCE GC_SEQUENCE MINVALUE 1 MAXVALUE 9999999 START WITH 1 INCREMENT BY 1 NOCYCLE NOORDER CACHE 20;

--------------------------------------------------------
-- Create temporary trigger on GENOMIC_CONTEXT
--------------------------------------------------------
CREATE OR REPLACE TRIGGER GENOMIC_CONTEXT_TRG
BEFORE INSERT ON GENOMIC_CONTEXT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT GC_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER GENOMIC_CONTEXT_TRG ENABLE;

--------------------------------------------------------
-- Re-migrate data into GENOMIC_CONTEXT
--------------------------------------------------------

DELETE FROM GENOMIC_CONTEXT;

-- UPSTREAM
INSERT INTO GENOMIC_CONTEXT (SNP_ID, GENE_ID, IS_INTERGENIC, IS_UPSTREAM, IS_DOWNSTREAM, DISTANCE, ENTREZ_GENE_ID)
  SELECT DISTINCT s.ID AS SNP_ID, g.ID AS GENE_ID, 1 AS IS_INTERGENIC, 1 AS IS_UPSTREAM, 0 AS IS_DOWNSTREAM, gs.UPSTREAM_GENE_DISTANCE AS DISTANCE, gs.UPSTREAM_GENE_ID AS ENTREZ_GENE_ID
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENE g ON g.GENE = gs.UPSTREAM_GENE_SYMBOL
  WHERE UPSTREAM_GENE_DISTANCE IS NOT NULL
  AND INTERGENIC = 1;
-- DOWNSTREAM
INSERT INTO GENOMIC_CONTEXT (SNP_ID, GENE_ID, IS_INTERGENIC, IS_UPSTREAM, IS_DOWNSTREAM, DISTANCE, ENTREZ_GENE_ID)
  SELECT DISTINCT s.ID AS SNP_ID, g.ID AS GENE_ID, 1 AS IS_INTERGENIC, 0 AS IS_UPSTREAM, 1 AS IS_DOWNSTREAM, gs.DOWNSTREAM_GENE_DISTANCE AS DISTANCE, gs.DOWNSTREAM_GENE_ID AS ENTREZ_GENE_ID
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENE g ON g.GENE = gs.DOWNSTREAM_GENE_SYMBOL
  WHERE DOWNSTREAM_GENE_DISTANCE IS NOT NULL
  AND INTERGENIC = 1;
-- INTERGENIC
INSERT INTO GENOMIC_CONTEXT (SNP_ID, GENE_ID, IS_INTERGENIC, IS_UPSTREAM, IS_DOWNSTREAM, DISTANCE, ENTREZ_GENE_ID)
  SELECT DISTINCT s.ID AS SNP_ID, g.ID AS GENE_ID, 0 AS IS_INTERGENIC, 0 AS IS_UPSTREAM, 0 AS IS_DOWNSTREAM, NULL AS DISTANCE, gs.SNP_GENE_IDS AS ENTREZ_GENE_ID
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENE g ON g.GENE = gs.SNP_GENE_SYMBOLS
  WHERE INTERGENIC = 0;

--------------------------------------------------------
-- Recreate proper trigger on GENOMIC_CONTEXT
--------------------------------------------------------
CREATE OR REPLACE TRIGGER GENOMIC_CONTEXT_TRG
BEFORE INSERT ON GENOMIC_CONTEXT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER GENOMIC_CONTEXT_TRG ENABLE;

--------------------------------------------------------
-- Drop temporary GENOMIC_CONTEXT_SEQUENCE
--------------------------------------------------------
DROP SEQUENCE GC_SEQUENCE;

--------------------------------------------------------
-- Generate view
--------------------------------------------------------

CREATE VIEW CATALOG_SUMMARY_VIEW (
  CATALOG_ADDED_DATE,
  PUBMED_ID,
  AUTHOR,
  PUBLICATION_DATE,
  JOURNAL,
  LINK,
  STUDY,
  DISEASE_TRAIT,
  INITIAL_SAMPLE_DESCRIPTION,
  REPLICATE_SAMPLE_DESCRIPTION,
  REGION,
  CHROMOSOME_NAME,
  CHROMOSOME_POSITION,
  REPORTED_GENE,
  MAPPED_GENE,
  ENTREZ_GENE_ID,
  UPSTREAM_MAPPED_GENE,
  UPSTREAM_ENTREZ_GENE_ID,
  UPSTREAM_GENE_DISTANCE,
  DOWNSTREAM_MAPPED_GENE,
  DOWNSTREAM_ENTREZ_GENE_ID,
  DOWNSTREAM_GENE_DISTANCE,
  STRONGEST_SNP_RISK_ALLELE,
  SNP_RSID,
  MERGED,
  SNP_ID,
  CONTEXT,
  IS_INTERGENIC,
  RISK_ALLELE_FREQUENCY,
  P_VALUE,
  P_VALUE_QUALIFIER,
  OR_BETA,
  CI,
  CI_QUALIIFER,
  PLATFORM,
  CNV)
  AS SELECT
    h.STUDY_ADDED_DATE AS CATALOG_ADDED_DATE,
    s.PUBMED_ID,
    s.AUTHOR,
    s.STUDY_DATE AS PUBLICATION_DATE,
    s.PUBLICATION AS JOURNAL,
    CONCAT('http://europepmc.org/abstract/MED/', s.PUBMED_ID) AS LINK,
    s.TITLE AS STUDY,
    dt.TRAIT AS DISEASE_TRAIT,
    s.INITIAL_SAMPLE_SIZE AS INITIAL_SAMPLE_DESCRIPTION,
    s.REPLICATE_SAMPLE_SIZE AS REPLICATE_SAMPLE_DESCRIPTION,
    r.NAME AS REGION,
    snp.CHROMOSOME_NAME,
    snp.CHROMOSOME_POSITION,
    rg.GENE_NAME AS REPORTED_GENE,
    img.GENE_NAME AS MAPPED_GENE,
    igc.ENTREZ_GENE_ID,
    umg.GENE_NAME AS UPSTREAM_MAPPED_GENE,
    ugc.ENTREZ_GENE_ID AS UPSTREAM_ENTREZ_GENE_ID,
    ugc.DISTANCE AS UPSTREAM_GENE_DISTANCE,
    dmg.GENE_NAME AS DOWNSTREAM_MAPPED_GENE,
    dgc.ENTREZ_GENE_ID AS DOWNSTREAM_ENTREZ_GENE_ID,
    dgc.DISTANCE AS DOWNSTREAM_GENE_DISTANCE,
    ra.RISK_ALLELE_NAME AS STRONGEST_SNP_RISK_ALLELE,
    snp.RS_ID AS SNP_RS_ID,
    snp.MERGED,
    snp.ID AS SNP_ID,
    snp.FUNCTIONAL_CLASS AS CONTEXT,
    igc.IS_INTERGENIC,
    a.RISK_FREQUENCY AS RISK_ALLELE_FREQUENCY,
    a.PVALUE_FLOAT AS P_VALUE,
    a.PVALUE_TEXT AS P_VALUE_QUALIFIER,
    a.OR_PER_COPY_NUM AS OR_BETA,
    a.OR_PER_COPY_RANGE AS CI,
    a.OR_PER_COPY_UNIT_DESCR AS CI_QUALIFIER,
    s.PLATFORM,
    s.CNV
    FROM STUDY s
    JOIN HOUSEKEEPING h ON h.ID = s.HOUSEKEEPING_ID
    JOIN STUDY_DISEASE_TRAIT sdt ON sdt.STUDY_ID = s.ID
    JOIN DISEASE_TRAIT dt ON dt.ID = sdt.DISEASE_TRAIT_ID
    JOIN ASSOCIATION a ON a.STUDY_ID = s.ID
    JOIN ASSOCIATION_LOCUS al ON al.ASSOCIATION_ID = a.ID
    JOIN LOCUS_RISK_ALLELE lra ON lra.LOCUS_ID = al.LOCUS_ID
    JOIN RISK_ALLELE ra ON ra.ID = lra.RISK_ALLELE_ID
    JOIN RISK_ALLELE_SNP ras ON ras.RISK_ALLELE_ID = lra.RISK_ALLELE_ID
    JOIN SINGLE_NUCLEOTIDE_POLYMORPHISM snp ON snp.ID = ras.SNP_ID
    JOIN SNP_REGION sr ON sr.SNP_ID = snp.ID
    JOIN REGION r ON r.ID = sr.REGION_ID
    LEFT JOIN AUTHOR_REPORTED_GENE arg ON arg.LOCUS_ID = al.LOCUS_ID
    LEFT JOIN GENE rg ON rg.ID = arg.REPORTED_GENE_ID
    LEFT JOIN GENOMIC_CONTEXT ugc ON ugc.SNP_ID = snp.ID AND ugc.IS_UPSTREAM = '1'
    LEFT JOIN GENOMIC_CONTEXT dgc ON dgc.SNP_ID = snp.ID AND dgc.IS_DOWNSTREAM = '1'
    LEFT JOIN GENOMIC_CONTEXT igc ON igc.SNP_ID = snp.ID AND igc.IS_INTERGENIC = '0'
    LEFT JOIN GENE umg ON umg.ID = ugc.GENE_ID
    LEFT JOIN GENE dmg ON dmg.ID = dgc.GENE_ID
    LEFT JOIN GENE img ON img.ID = igc.GENE_ID
    ORDER BY s.STUDY_DATE DESC, CHROMOSOME_NAME ASC, CHROMOSOME_POSITION ASC;