package uk.ac.ebi.spot.goci.model;

import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/01/15
 */
public class ObjectDocumentMapper<O, D> {
    private ObjectConverter<O, D> objectConverter;
    private DocumentIndexer<D> documentIndexer;

    public ObjectDocumentMapper(Class<D> documentType, SolrCrudRepository<D, String> index) {
        this.objectConverter = new ObjectConverter<>(documentType);
        this.documentIndexer = new DocumentIndexer<>(index);
    }

    public D map(O object) {
        D document = objectConverter.convert(object);
        documentIndexer.index(document);
        return document;
    }

    public List<D> map(List<O> objects) {
        List<D> documents = new ArrayList<>();
        objects.stream().map(objectConverter::convert).forEach(documents::add);
        documentIndexer.index(documents);
        return documents;
    }
}
