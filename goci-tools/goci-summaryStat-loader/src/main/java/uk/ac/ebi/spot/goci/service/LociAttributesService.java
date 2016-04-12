package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.EffectAllele;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Variant;
import uk.ac.ebi.spot.goci.repository.EffectAlleleRepository;
import uk.ac.ebi.spot.goci.repository.GeneRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.VariantRepository;

import java.util.ArrayList;
import java.util.Collection;



/**
 * Created by emma on 13/04/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates the attributes of a loci, used by AssociationController
 */
@Service
public class LociAttributesService {

    private VariantRepository variantRepository;
    private GeneRepository geneRepository;
    private EffectAlleleRepository effectAlleleRepository;
    private LocusRepository locusRepository;

    private Logger log = LoggerFactory.getLogger(getClass());
    protected Logger getLog() {
        return log;
    }

    // Constructor
    @Autowired
    public LociAttributesService(VariantRepository variantRepository,
                                 GeneRepository geneRepository,
                                 EffectAlleleRepository effectAlleleRepository,
                                 LocusRepository locusRepository) {
        this.variantRepository = variantRepository;
        this.geneRepository = geneRepository;
        this.effectAlleleRepository = effectAlleleRepository;
        this.locusRepository = locusRepository;
    }

    public Collection<Gene> createGene(Collection<String> authorReportedGenes) {
        Collection<Gene> locusGenes = new ArrayList<Gene>();
        for (String authorReportedGene : authorReportedGenes) {

            authorReportedGene = tidy_curator_entered_string(authorReportedGene);

            // Check if gene already exists, note we may have duplicates so for moment just take first one
            Gene geneInDatabase = geneRepository.findByGeneName(authorReportedGene);
            Gene gene;

            // Exists in database already
            if (geneInDatabase != null) {
                getLog().debug("Gene "+ geneInDatabase +" already exists in database");
                gene = geneInDatabase;
            }

            // If gene doesn't exist then create and save
            else {
                // Create new gene
                getLog().debug("Gene "+ geneInDatabase +" not found in database. Creating and saving new gene.");
                Gene newGene = new Gene();
                newGene.setGeneName(authorReportedGene);

                // Save gene
                gene = geneRepository.save(newGene);
            }

            // Add genes to collection
            locusGenes.add(gene);
        }
        return locusGenes;
    }

    public EffectAllele createEffectAllele(String effectAllele, String otherAllele, Variant variant) {

        //Create new risk allele, at present we always create a new risk allele for each locus within an association
        EffectAllele newEffectAllele = new EffectAllele();
        newEffectAllele.setEffectAlleleName(tidy_curator_entered_string(effectAllele));
        newEffectAllele.setOtherAlleleName(tidy_curator_entered_string(otherAllele));
        newEffectAllele.setVariant(variant);

        // Save risk allele
        effectAlleleRepository.save(newEffectAllele);
        return newEffectAllele;
    }


    public EffectAllele createEffectAllele(String effectAllele, Variant variant) {

        //Create new risk allele, at present we always create a new risk allele for each locus within an association
        EffectAllele newEffectAllele = new EffectAllele();
        newEffectAllele.setEffectAlleleName(tidy_curator_entered_string(effectAllele));
        newEffectAllele.setVariant(variant);

        // Save risk allele
        effectAlleleRepository.save(newEffectAllele);
        return newEffectAllele;
    }


    public void deleteEffectAllele(EffectAllele effectAllele) {
        effectAlleleRepository.delete(effectAllele);
    }

    public void deleteLocus(Locus locus) {
        locusRepository.delete(locus);
    }

    public Variant createVariant(String curatorEnteredVariant) {

        curatorEnteredVariant = tidy_curator_entered_string(curatorEnteredVariant);

        // Check if SNP already exists database
        Variant variantInDatabase =
                variantRepository.findByExternalIdIgnoreCase(curatorEnteredVariant);
        Variant variant;
        if (variantInDatabase != null) {
            variant = variantInDatabase;
        }

        // If SNP doesn't exist, create and save
        else {
            // Create new SNP
            Variant newVARIANT = new Variant();
            newVARIANT.setExternalId(curatorEnteredVariant);

            // Save SNP
            variant = variantRepository.save(newVARIANT);
        }

        return variant;

    }
    public Variant createVariant(String curatorEnteredVariant, String chromosome, String position) {

        curatorEnteredVariant = tidy_curator_entered_string(curatorEnteredVariant);

        // Check if SNP already exists database
        Variant variantInDatabase =
                variantRepository.findByExternalIdIgnoreCase(curatorEnteredVariant);
        Variant variant;
        if (variantInDatabase != null) {
            variant = variantInDatabase;
        }

        // If SNP doesn't exist, create and save
        else {
            // Create new SNP
            Variant newVARIANT = new Variant();
            newVARIANT.setExternalId(curatorEnteredVariant);
            newVARIANT.setChromosomeName(chromosome);
            newVARIANT.setChromosomePosition(position);

            // Save SNP
            variant = variantRepository.save(newVARIANT);
        }

        return variant;

    }


    public String tidy_curator_entered_string(String string) {

        String newString = string.trim();
        String newline = System.getProperty("line.separator");

        if (newString.contains(newline)) {
            newString = newString.replace(newline, "");
        }

        return newString;
    }
}
