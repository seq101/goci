/*
################################################################################
Migration script to modify Monthly_Totals_Summary_View to add columns for
PubMed_ID, Accession_ID, and Author and remove columns for Curator_Total
and Monthly_Total.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Trish Whetzel
date:   22 February 2018
version: 2.2.0.040
################################################################################
*/

--------------------------------------------------------
-- CREATE NEW VIEW DEFINITION
--------------------------------------------------------
CREATE OR REPLACE VIEW MONTHLY_TOTALS_SUMMARY_VIEW (ID, YEAR, MONTH, PUBMED_ID, ACCESSION_ID, AUTHOR, CURATOR, CURATION_STATUS, CURATOR_TOTAL)
  AS SELECT ROWNUM, V.* FROM (SELECT YEAR_E AS YEAR, MONTH_E AS MONTH, PUBMED_ID, ACCESSION_ID, AUTHOR, CURATOR, CURATION_STATUS, CURATOR_TOTAL
                              FROM (
                                SELECT EXTRACT (YEAR FROM (TRUNC(TO_DATE(S.PUBLICATION_DATE), 'YEAR'))) AS YEAR_E,
                                       EXTRACT (MONTH FROM (TRUNC(TO_DATE(S.PUBLICATION_DATE), 'MONTH'))) AS MONTH_E,
                                       S.PUBMED_ID AS PUBMED_ID,
                                       S.ACCESSION_ID AS ACCESSION_ID,
                                       S.AUTHOR AS AUTHOR,
                                       C.LAST_NAME AS CURATOR,
                                       CS.STATUS AS CURATION_STATUS,
                                       COUNT(C.LAST_NAME) AS CURATOR_TOTAL
                                FROM STUDY S, HOUSEKEEPING H, CURATOR C, CURATION_STATUS CS
                                WHERE S.HOUSEKEEPING_ID = H.ID
                                      AND H.CURATION_STATUS_ID = CS.ID
                                      AND H.CURATOR_ID = C.ID
                                GROUP BY TRUNC(TO_DATE(S.PUBLICATION_DATE), 'YEAR'), TRUNC(TO_DATE(S.PUBLICATION_DATE), 'MONTH'),
                                  S.PUBMED_ID, S.ACCESSION_ID, S.AUTHOR, C.LAST_NAME, CS.STATUS
                                ORDER BY TRUNC(TO_DATE(S.PUBLICATION_DATE), 'YEAR') DESC, TRUNC(TO_DATE(S.PUBLICATION_DATE), 'MONTH') DESC, C.LAST_NAME)) V;