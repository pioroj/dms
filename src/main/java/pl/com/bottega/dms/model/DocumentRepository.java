package pl.com.bottega.dms.model;


import pl.com.bottega.dms.application.DocumentDto;

public interface DocumentRepository {

    void put(Document document);

    Document get(DocumentNumber documentNumber);
}
