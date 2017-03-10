package pl.com.bottega.dms.infrastructure;


import org.hibernate.jpa.criteria.OrderImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.com.bottega.dms.application.*;
import pl.com.bottega.dms.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
@Primary
public class JPADocumentCatalog implements DocumentCatalog {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DocumentSearchResults find(DocumentQuery documentQuery) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        DocumentSearchResults results = new DocumentSearchResults();

        List<DocumentDto> dtos = queryDocuments(documentQuery, criteriaBuilder);

        Query countQuery = queryTotalCount(documentQuery, criteriaBuilder);

        Long total = (Long) countQuery.getSingleResult();
        results.setPagesCount(total / documentQuery.getPerPage() + (total % documentQuery.getPerPage() == 0 ? 0 : 1));

        results.setDocuments(dtos);
        results.setPerPage(documentQuery.getPerPage());
        results.setPageNumber(documentQuery.getPageNumber());
        return results;
    }

    private Query queryTotalCount(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder) {
        CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class); //count
        Root<Document> countRoot = countCriteriaQuery.from(Document.class); //count
        Set<Predicate> countPredicates = createPredicates(documentQuery, criteriaBuilder, countRoot);
        countCriteriaQuery.select(criteriaBuilder.count(countRoot)); //count
        countCriteriaQuery.where(countPredicates.toArray(new Predicate[]{})); //count
        return entityManager.createQuery(countCriteriaQuery);
    }

    private List<DocumentDto> queryDocuments(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder) {
        CriteriaQuery<Document> criteriaQuery = criteriaBuilder.createQuery(Document.class); //query
        Root<Document> root = criteriaQuery.from(Document.class); //query
        root.fetch("confirmations", JoinType.LEFT); //query
        Set<Predicate> predicates = createPredicates(documentQuery, criteriaBuilder, root); //query
        criteriaQuery.where(predicates.toArray(new Predicate[]{})); //query
        Query query = entityManager.createQuery(criteriaQuery);  //query
        query.setMaxResults(documentQuery.getPerPage());
        query.setFirstResult(getFirstResultOffset(documentQuery));
        List<Document> documents = query.getResultList();
        return getDocumentDtos(documents);
    }

    private List<DocumentDto> getDocumentDtos(List<Document> documents) {
        List<DocumentDto> dtos = new LinkedList<>();
        for (Document document : documents) {
            dtos.add(createDocumentDto(document));
        }
        return dtos;
    }

    private int getFirstResultOffset(DocumentQuery documentQuery) {
        return (documentQuery.getPageNumber() - 1) * documentQuery.getPerPage();
    }

    private Set<Predicate> createPredicates(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder, Root<Document> root) {
        Set<Predicate> predicates = new HashSet<>();
        addPhrasePredicate(documentQuery, criteriaBuilder, root, predicates);
        addStatusPredicate(documentQuery, criteriaBuilder, root, predicates);
        addCreatorPredicate(documentQuery, criteriaBuilder, root, predicates);
        addCreatedAfterPredicate(documentQuery, criteriaBuilder, root, predicates);
        addCreatedBeforePredicate(documentQuery, criteriaBuilder, root, predicates);
        addChangedAfterPredicate(documentQuery, criteriaBuilder, root, predicates);
        addChangedBeforePredicate(documentQuery, criteriaBuilder, root, predicates);
        return predicates;
    }

    private void addChangedBeforePredicate(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder, Root<Document> root, Set<Predicate> predicates) {
        if (documentQuery.getChangedBefore() != null) {
            predicates.add(criteriaBuilder.lessThan(root.get("changedAt"), documentQuery.getChangedBefore()));
        }
    }

    private void addChangedAfterPredicate(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder, Root<Document> root, Set<Predicate> predicates) {
        if (documentQuery.getChangedAfter() != null) {
            predicates.add(criteriaBuilder.greaterThan(root.get("changedAt"), documentQuery.getChangedAfter()));
        }
    }

    private void addCreatedBeforePredicate(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder, Root<Document> root, Set<Predicate> predicates) {
        if (documentQuery.getCreatedBefore() != null) {
            predicates.add(criteriaBuilder.lessThan(root.get("createdAt"), documentQuery.getCreatedBefore()));
        }
    }

    private void addCreatedAfterPredicate(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder, Root<Document> root, Set<Predicate> predicates) {
        if (documentQuery.getCreatedAfter() != null) {
            predicates.add(criteriaBuilder.greaterThan(root.get("createdAt"), documentQuery.getCreatedAfter()));
        }
    }

    private void addCreatorPredicate(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder, Root<Document> root, Set<Predicate> predicates) {
        if (documentQuery.getCreatorId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("creatorId").get("id"), documentQuery.getCreatorId()));
        }
    }

    private void addStatusPredicate(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder, Root<Document> root, Set<Predicate> predicates) {
        if (documentQuery.getStatus() != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), DocumentStatus.valueOf(documentQuery.getStatus())));
        }
    }

    private void addPhrasePredicate(DocumentQuery documentQuery, CriteriaBuilder criteriaBuilder, Root<Document> root, Set<Predicate> predicates) {
        if (documentQuery.getPhrase() != null) {
            String likeExpression = "%" + documentQuery.getPhrase() + "%";
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("title"), likeExpression),
                    criteriaBuilder.like(root.get("content"), likeExpression),
                    criteriaBuilder.like(root.get("number").get("number"), likeExpression)
            ));
        }
    }

    @Override
    public DocumentDto get(DocumentNumber documentNumber) {
        Query query = entityManager.createQuery("FROM Document d LEFT JOIN FETCH d.confirmations WHERE d.number = :nr");
        query.setParameter("nr", documentNumber);
        Document document = (Document) query.getResultList().get(0);
        DocumentDto documentDto = createDocumentDto(document);
        return documentDto;
    }

    private DocumentDto createDocumentDto(Document document) {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setNumber(document.getNumber().getNumber());
        documentDto.setTitle(document.getTitle());
        documentDto.setContent(document.getContent());
        documentDto.setStatus(document.getStatus().name());
        documentDto.setCreatedAt(document.getCreatedAt());
        List<ConfirmationDto> confirmationDtos = new LinkedList<>();
        for (Confirmation confirmation : document.getConfirmations()) {
            ConfirmationDto dto = createConfirmationDto(confirmation);
            confirmationDtos.add(dto);
        }
        documentDto.setConfirmations(confirmationDtos);
        return documentDto;
    }

    private ConfirmationDto createConfirmationDto(Confirmation confirmation) {
        ConfirmationDto dto = new ConfirmationDto();
        dto.setConfirmed(confirmation.isConfirmed());
        dto.setConfirmedAt(confirmation.getConfirmationDate());
        dto.setOwnerEmployeeId(confirmation.getOwner().getId());
        if (confirmation.hasProxy())
            dto.setProxyEmployeeId(confirmation.getProxy().getId());
        return dto;
    }
}
