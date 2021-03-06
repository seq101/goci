/*
################################################################################
Migration script to update CATALOG_SUMMARY_VIEW with new columns required for
mapping pipeline.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    August 12th 2015
version: 2.0.1.030
################################################################################
*/
--------------------------------------------------------
--  Regenerate CATALOG_SUMMARY_VIEW
--------------------------------------------------------

CREATE OR REPLACE VIEW CATALOG_SUMMARY_VIEW (
  ID,
  STUDY_ADDED_DATE,
  PUBMED_ID,
  AUTHOR,
  PUBLICATION_DATE,
  JOURNAL,
  LINK,
  STUDY,
  DISEASE_TRAIT,
  EFO_TRAIT,
  EFO_URI,
  INITIAL_SAMPLE_DESCRIPTION,
  REPLICATE_SAMPLE_DESCRIPTION,
  REGION,
  CHROMOSOME_NAME,
  CHROMOSOME_POSITION,
  REPORTED_GENE,
  ENTREZ_MAPPED_GENE,
  ENTREZ_MAPPED_GENE_ID,
  ENSEMBL_MAPPED_GENE,
  ENSEMBL_MAPPED_GENE_ID,
  ENTREZ_UPSTREAM_MAPPED_GENE,
  ENTREZ_UPSTREAM_GENE_ID,
  ENTREZ_UPSTREAM_GENE_DIST,
  ENSEMBL_UPSTREAM_MAPPED_GENE,
  ENSEMBL_UPSTREAM_GENE_ID,
  ENSEMBL_UPSTREAM_GENE_DIST,
  ENTREZ_DOWNSTREAM_MAPPED_GENE,
  ENTREZ_DOWNSTREAM_GENE_ID,
  ENTREZ_DOWNSTREAM_GENE_DIST,
  ENSEMBL_DOWNSTREAM_MAPPED_GENE,
  ENSEMBL_DOWNSTREAM_GENE_ID,
  ENSEMBL_DOWNSTREAM_GENE_DIST,
  STRONGEST_SNP_RISK_ALLELE,
  SNP_RSID,
  MERGED,
  SNP_ID,
  CONTEXT,
  IS_INTERGENIC_ENTREZ,
  IS_INTERGENIC_ENSEMBL,
  RISK_ALLELE_FREQUENCY,
  P_VALUE_MANTISSA,
  P_VALUE_EXPONENT,
  P_VALUE_QUALIFIER,
  OR_BETA,
  CI,
  CI_QUALIFIER,
  PLATFORM,
  CNV,
  ASSOCIATION_ID,
  STUDY_ID,
  CATALOG_PUBLISH_DATE,
  CATALOG_UNPUBLISH_DATE,
  CURATION_STATUS)
  AS SELECT ROWNUM, V.* FROM
  (SELECT
  h.STUDY_ADDED_DATE,
  s.PUBMED_ID,
  s.AUTHOR,
  s.PUBLICATION_DATE,
  s.PUBLICATION AS JOURNAL,
  CONCAT('http://europepmc.org/abstract/MED/', s.PUBMED_ID) AS LINK,
  s.TITLE AS STUDY,
  dt.TRAIT AS DISEASE_TRAIT,
  et.TRAIT AS EFO_TRAIT,
  et.URI AS EFO_URI,
  s.INITIAL_SAMPLE_SIZE AS INITIAL_SAMPLE_DESCRIPTION,
  s.REPLICATE_SAMPLE_SIZE AS REPLICATE_SAMPLE_DESCRIPTION,
  r.NAME AS REGION,
  loc.CHROMOSOME_NAME,
  loc.CHROMOSOME_POSITION,
  rg.GENE_NAME AS REPORTED_GENE,
  inmg.GENE_NAME AS ENTREZ_MAPPED_GENE,
  ieg.ENTREZ_GENE_ID AS ENTREZ_MAPPED_GENE_ID,
  iemg.GENE_NAME AS ENSEMBL_MAPPED_GENE,
  ieng.ENSEMBL_GENE_ID AS ENSEMBL_MAPPED_GENE_ID,
  unmg.GENE_NAME AS ENTREZ_UPSTREAM_MAPPED_GENE,
  ueg.ENTREZ_GENE_ID AS ENTREZ_UPSTREAM_GENE_ID,
  ungc.DISTANCE AS ENTREZ_UPSTREAM_GENE_DIST,
  uemg.GENE_NAME AS ENSEMBL_UPSTREAM_MAPPED_GENE,
  ueng.ENSEMBL_GENE_ID AS ENSEMBL_UPSTREAM_GENE_ID,
  uegc.DISTANCE AS ENSEMBL_UPSTREAM_GENE_DIST,
  dnmg.GENE_NAME AS ENTREZ_DOWNSTREAM_MAPPED_GENE,
  deg.ENTREZ_GENE_ID AS ENTREZ_DOWNSTREAM_GENE_ID,
  dngc.DISTANCE AS ENTREZ_DOWNSTREAM_GENE_DIST,
  demg.GENE_NAME AS ENSEMBL_DOWNSTREAM_MAPPED_GENE,
  deng.ENSEMBL_GENE_ID ENSEMBL_DOWNSTREAM_GENE_ID,
  degc.DISTANCE AS ENSEMBL_DOWNSTREAM_GENE_DIST,
  ra.RISK_ALLELE_NAME AS STRONGEST_SNP_RISK_ALLELE,
  snp.RS_ID AS SNP_RS_ID,
  snp.MERGED,
  snp.ID AS SNP_ID,
  snp.FUNCTIONAL_CLASS AS CONTEXT,
  (CASE WHEN ingc.IS_INTERGENIC IS NOT NULL THEN ingc.IS_INTERGENIC ELSE ungc.IS_INTERGENIC END) AS IS_INTERGENIC_ENTREZ,
  (CASE WHEN iegc.IS_INTERGENIC IS NOT NULL THEN iegc.IS_INTERGENIC ELSE uegc.IS_INTERGENIC END) AS IS_INTERGENIC_ENSEMBL,
  a.RISK_FREQUENCY AS RISK_ALLELE_FREQUENCY,
  a.PVALUE_MANTISSA AS P_VALUE_MANTISSA,
  a.PVALUE_EXPONENT AS P_VALUE_EXPONENT,
  a.PVALUE_TEXT AS P_VALUE_QUALIFIER,
  a.OR_PER_COPY_NUM AS OR_BETA,
  a.OR_PER_COPY_RANGE AS CI,
  a.OR_PER_COPY_UNIT_DESCR AS CI_QUALIFIER,
  s.PLATFORM,
  s.CNV,
  a.ID AS ASSOCIATION_ID,
  s.ID AS STUDY_ID,
  h.CATALOG_PUBLISH_DATE,
  h.CATALOG_UNPUBLISH_DATE,
  cs.STATUS as CURATION_STATUS
  FROM STUDY s
  JOIN HOUSEKEEPING h ON h.ID = s.HOUSEKEEPING_ID
  JOIN CURATION_STATUS cs ON h.CURATION_STATUS_ID = cs.ID
  LEFT JOIN STUDY_DISEASE_TRAIT sdt ON sdt.STUDY_ID = s.ID
  LEFT JOIN DISEASE_TRAIT dt ON dt.ID = sdt.DISEASE_TRAIT_ID
  LEFT JOIN STUDY_EFO_TRAIT seft ON seft.STUDY_ID = s.ID
  LEFT JOIN EFO_TRAIT et ON et.ID = seft.EFO_TRAIT_ID
  LEFT JOIN ASSOCIATION a ON a.STUDY_ID = s.ID
  LEFT JOIN ASSOCIATION_LOCUS al ON al.ASSOCIATION_ID = a.ID
  LEFT JOIN LOCUS_RISK_ALLELE lra ON lra.LOCUS_ID = al.LOCUS_ID
  LEFT JOIN RISK_ALLELE ra ON ra.ID = lra.RISK_ALLELE_ID
  LEFT JOIN RISK_ALLELE_SNP ras ON ras.RISK_ALLELE_ID = lra.RISK_ALLELE_ID
  LEFT JOIN SINGLE_NUCLEOTIDE_POLYMORPHISM snp ON snp.ID = ras.SNP_ID
  LEFT JOIN SNP_LOCATION sl ON sl.SNP_ID = snp.ID
  LEFT JOIN LOCATION loc ON sl.LOCATION_ID = loc.id
  LEFT JOIN REGION r ON r.ID = loc.REGION_ID
  LEFT JOIN AUTHOR_REPORTED_GENE arg ON arg.LOCUS_ID = al.LOCUS_ID
  LEFT JOIN GENE rg ON rg.ID = arg.REPORTED_GENE_ID
  -- NCBI genes
  LEFT JOIN GENOMIC_CONTEXT ungc ON ungc.SNP_ID = snp.ID AND ungc.LOCATION_ID = loc.id AND ungc.IS_UPSTREAM = '1' AND ungc.IS_CLOSEST_GENE ='1' AND ungc.SOURCE ='NCBI'
  LEFT JOIN GENOMIC_CONTEXT dngc ON dngc.SNP_ID = snp.ID AND dngc.LOCATION_ID = loc.id AND dngc.IS_DOWNSTREAM = '1' AND dngc.IS_CLOSEST_GENE = '1' AND dngc.SOURCE ='NCBI'
  LEFT JOIN GENOMIC_CONTEXT ingc ON ingc.SNP_ID = snp.ID AND ingc.LOCATION_ID = loc.id AND ingc.IS_INTERGENIC = '0' AND ingc.SOURCE ='NCBI'
  LEFT JOIN GENE unmg ON unmg.ID = ungc.GENE_ID
  LEFT JOIN GENE dnmg ON dnmg.ID = dngc.GENE_ID
  LEFT JOIN GENE inmg ON inmg.ID = ingc.GENE_ID
  LEFT JOIN GENE_ENTREZ_GENE ugeg ON ugeg.GENE_ID = unmg.ID
  LEFT JOIN GENE_ENTREZ_GENE dgeg ON dgeg.GENE_ID = dnmg.ID
  LEFT JOIN GENE_ENTREZ_GENE igeg ON igeg.GENE_ID = inmg.ID
  LEFT JOIN ENTREZ_GENE ueg ON ueg.ID = ugeg.ENTREZ_GENE_ID
  LEFT JOIN ENTREZ_GENE ieg ON ieg.ID = igeg.ENTREZ_GENE_ID
  LEFT JOIN ENTREZ_GENE deg ON deg.ID = dgeg.ENTREZ_GENE_ID
  -- Ensembl genes
  LEFT JOIN GENOMIC_CONTEXT uegc ON uegc.SNP_ID = snp.ID AND uegc.LOCATION_ID = loc.id AND uegc.IS_UPSTREAM = '1' AND uegc.IS_CLOSEST_GENE ='1' AND uegc.SOURCE ='Ensembl'
  LEFT JOIN GENOMIC_CONTEXT degc ON degc.SNP_ID = snp.ID AND degc.LOCATION_ID = loc.id AND degc.IS_DOWNSTREAM = '1' AND degc.IS_CLOSEST_GENE = '1' AND degc.SOURCE ='Ensembl'
  LEFT JOIN GENOMIC_CONTEXT iegc ON iegc.SNP_ID = snp.ID AND iegc.LOCATION_ID = loc.id AND iegc.IS_INTERGENIC = '0'  AND iegc.SOURCE ='Ensembl'
  LEFT JOIN GENE uemg ON uemg.ID = uegc.GENE_ID
  LEFT JOIN GENE demg ON demg.ID = degc.GENE_ID
  LEFT JOIN GENE iemg ON iemg.ID = iegc.GENE_ID
  LEFT JOIN GENE_ENSEMBL_GENE ugeng ON ugeng.GENE_ID = uemg.ID
  LEFT JOIN GENE_ENSEMBL_GENE dgeng ON dgeng.GENE_ID = demg.ID
  LEFT JOIN GENE_ENSEMBL_GENE igeng ON igeng.GENE_ID = iemg.ID
  LEFT JOIN ENSEMBL_GENE ueng ON ueng.ID = ugeng.ENSEMBL_GENE_ID
  LEFT JOIN ENSEMBL_GENE ieng ON ieng.ID = igeng.ENSEMBL_GENE_ID
  LEFT JOIN ENSEMBL_GENE deng ON deng.ID = dgeng.ENSEMBL_GENE_ID
  ORDER BY s.PUBLICATION_DATE DESC, CHROMOSOME_NAME ASC, CHROMOSOME_POSITION ASC) V