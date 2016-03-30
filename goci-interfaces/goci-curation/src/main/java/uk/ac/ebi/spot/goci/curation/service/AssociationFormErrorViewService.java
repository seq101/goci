package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.AssociationFormErrorView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.EffectAllele;
import uk.ac.ebi.spot.goci.model.Variant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by emma on 29/05/2015.
 *
 * @author emma
 *         <p>
 *         Service class that determines possible errors in curator entered fields. Also generates list of errors and
 *         their types from mapping pipeline.
 */
@Service
public class AssociationFormErrorViewService {

    private AssociationMappingErrorService associationMappingErrorService;
    private AssociationComponentsSyntaxChecks associationComponentsSyntaxChecks;

    @Autowired
    public AssociationFormErrorViewService(AssociationMappingErrorService associationMappingErrorService,
                                           AssociationComponentsSyntaxChecks associationComponentsSyntaxChecks) {
        this.associationMappingErrorService = associationMappingErrorService;
        this.associationComponentsSyntaxChecks = associationComponentsSyntaxChecks;
    }

    /**
     * Determine error prone attributes of association and then return them via controller to view
     *
     * @param association Association object
     */
    public AssociationFormErrorView checkAssociationForErrors(Association association) {

        AssociationFormErrorView associationErrorView = new AssociationFormErrorView();
        Collection<String> associationEffectAlleles = new ArrayList<String>();
        Collection<String> associationVariants = new ArrayList<String>();
        Collection<String> associationProxies = new ArrayList<String>();

        Collection<String> effectAlleleErrors = new ArrayList<String>();
        Collection<String> variantErrors = new ArrayList<String>();
        Collection<String> proxyErrors = new ArrayList<String>();

        // Store attributes of each loci
        for (Locus locus : association.getLoci()) {

            if (locus != null) {
                for (EffectAllele effectAllele : locus.getStrongestEffectAlleles()) {

                    if (effectAllele.getEffectAlleleName() != null) {
                        associationEffectAlleles.add(effectAllele.getEffectAlleleName());
                    }
                    if (effectAllele.getVariant().getExternalId() != null) {
                        associationVariants.add(effectAllele.getVariant().getExternalId());
                    }
                    if (effectAllele.getProxyVariants() != null) {
                        for (Variant proxyVariant : effectAllele.getProxyVariants()) {
                            associationProxies.add(proxyVariant.getExternalId());
                        }
                    }
                }
            }
        }

        String error = "";

        // Risk allele errors
        for (String effectAlleleName : associationEffectAlleles) {
            error = associationComponentsSyntaxChecks.checkEffectAllele(effectAlleleName);
            if (!error.isEmpty()) {
                effectAlleleErrors.add(error);
            }
        }

        // SNP errors
        for (String variantName : associationVariants) {
            error = associationComponentsSyntaxChecks.checkVariant(variantName);
            if (!error.isEmpty()) {
                variantErrors.add(error);
            }
        }

        // Proxy errors
        for (String proxyName : associationProxies) {
            error = associationComponentsSyntaxChecks.checkProxy(proxyName);
            if (!error.isEmpty()) {
                proxyErrors.add(error);
            }
        }

        // Check association report for errors from mapping pipeline
        Map<String, String> associationErrorMap =
                associationMappingErrorService.createAssociationErrorMap(association.getAssociationReport());

        // Set model attributes
        associationErrorView.setEffectAlleleErrors(formatErrors(effectAlleleErrors));
        associationErrorView.setVariantErrors(formatErrors(variantErrors));
        associationErrorView.setProxyErrors(formatErrors(proxyErrors));
        associationErrorView.setAssociationErrorMap(associationErrorMap);
        return associationErrorView;
    }


    public String formatErrors(Collection<String> errors) {
        String error = "";
        error = String.join(" ", errors);
        return error;
    }
}
