package pl.com.bottega.dms.infrastructure;


import pl.com.bottega.dms.application.*;
import pl.com.bottega.dms.model.Confirmation;
import pl.com.bottega.dms.model.Document;
import pl.com.bottega.dms.model.DocumentNumber;
import pl.com.bottega.dms.model.EmployeeId;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.Set;

public class JPADocumentCatalog implements DocumentCatalog {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DocumentSearchResults find(DocumentQuery documentQuery) {
        return null;
    }

    @Override
    public DocumentDto get(DocumentNumber documentNumber) {
        Query query = entityManager.createQuery(
                "SELECT d FROM Document d LEFT JOIN FETCH d.confirmations WHERE d.number = :number");
        query.setParameter("number", documentNumber);
        Document document = (Document) query.getSingleResult();

        DocumentDto documentDto = new DocumentDto();
        documentDto.setNumber(documentNumber.getNumber());
        documentDto.setTitle(document.getTitle());
        documentDto.setDocumentStatus(document.getStatus());
        documentDto.setConfirmations(changeConfirmationsToDtos(document.getConfirmations()));
        return documentDto;
    }

    private Set<ConfirmationDto> changeConfirmationsToDtos(Set<Confirmation> confirmations) {
        Set<ConfirmationDto> dtos = new HashSet<>();
        for (Confirmation confirmation : confirmations) {
            ConfirmationDto confirmationDto = new ConfirmationDto();
            confirmationDto.setOwner(confirmation.getOwner().getId());
            EmployeeId proxy = confirmation.getProxy();
            if (proxy != null) confirmationDto.setProxy(proxy.getId());
            confirmationDto.setConfirmationDate(confirmation.getConfirmationDate());
            dtos.add(confirmationDto);
        }
        return dtos;
    }
}
