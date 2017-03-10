package pl.com.bottega.dms.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.com.bottega.dms.application.DocumentQuery;
import pl.com.bottega.dms.application.DocumentSearchResults;
import pl.com.bottega.dms.infrastructure.JPADocumentCatalog;
import pl.com.bottega.dms.infrastructure.JPQLDocumentCatalog;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JPADocumentCatalogTest {

    @Autowired
    private JPADocumentCatalog catalog;
    //private JPQLDocumentCatalog catalog;

    @Test
    @Sql("/fixtures/documentByPhrase.sql")
    @Transactional
    public void shouldFindDocumentByPhrase() {
        //when
        DocumentQuery documentQuery = new DocumentQuery();
        documentQuery.setPhrase("fancy");
        DocumentSearchResults searchResults = catalog.find(documentQuery);

        //then
        assertThat(searchResults.getDocuments().size()).isEqualTo(3);
    }

    @Test
    @Sql("/fixtures/documentByPhrase.sql")
    @Transactional
    public void shouldFindDocumentByPhraseAndStatus() {
        //when
        DocumentQuery documentQuery = new DocumentQuery();
        documentQuery.setPhrase("fancy");
        documentQuery.setStatus("DRAFT");
        DocumentSearchResults searchResults = catalog.find(documentQuery);

        //then
        assertThat(searchResults.getDocuments().size()).isEqualTo(2);
        assertThat(searchResults.getDocuments().get(0).getNumber()).isEqualTo("0");
        assertThat(searchResults.getDocuments().get(1).getNumber()).isEqualTo("fancy");
    }

    @Test
    @Sql("/fixtures/documentByPhrase.sql")
    @Transactional
    public void shouldFindDocumentByCreatorID() {
        //when
        DocumentQuery documentQuery = new DocumentQuery();
        documentQuery.setCreatorId(3L);
        DocumentSearchResults searchResults = catalog.find(documentQuery);

        //then
        assertThat(searchResults.getDocuments().size()).isEqualTo(2);
        assertThat(searchResults.getDocuments().get(0).getNumber()).isEqualTo("3");
        assertThat(searchResults.getDocuments().get(1).getNumber()).isEqualTo("fancy");
    }

    @Test
    @Sql("/fixtures/documentByPhrase.sql")
    @Transactional
    public void shouldFindDocumentByCreationDate() {
        //when
        DocumentQuery documentQuery = new DocumentQuery();
        documentQuery.setCreatedAfter(LocalDateTime.of(2017, Month.JANUARY, 1, 6, 1));
        documentQuery.setCreatedBefore(LocalDateTime.of(2017, Month.JANUARY, 1, 18, 1));
        DocumentSearchResults searchResults = catalog.find(documentQuery);

        //then
        assertThat(searchResults.getDocuments().size()).isEqualTo(2);
        assertThat(searchResults.getDocuments().get(0).getNumber()).isEqualTo("0");
        assertThat(searchResults.getDocuments().get(1).getNumber()).isEqualTo("1");
    }

    @Test
    @Sql("/fixtures/documentByPhrase.sql")
    @Transactional
    public void shouldFindDocumentByChangeDate() {
        //when
        DocumentQuery documentQuery = new DocumentQuery();
        documentQuery.setChangedAfter(LocalDateTime.of(2017, Month.JANUARY, 2, 10, 10));
        documentQuery.setChangedBefore(LocalDateTime.of(2017, Month.JANUARY, 2, 11, 11));
        DocumentSearchResults searchResults = catalog.find(documentQuery);
        //then
        assertThat(searchResults.getDocuments().size()).isEqualTo(2);
        assertThat(searchResults.getDocuments().get(0).getNumber()).isEqualTo("0");
        assertThat(searchResults.getDocuments().get(1).getNumber()).isEqualTo("1");
    }

    @Test
    @Sql("/fixtures/documentByPhrase.sql")
    @Transactional
    public void shouldReturnPaginatedResult() {
        //when
        DocumentQuery documentQuery = new DocumentQuery();
        documentQuery.setPageNumber(2);
        documentQuery.setPerPage(2);
        DocumentSearchResults searchResults = catalog.find(documentQuery);

        //then
        assertThat(searchResults.getDocuments().size()).isEqualTo(2);
        assertThat(searchResults.getDocuments().get(0).getNumber()).isEqualTo("3");
        assertThat(searchResults.getDocuments().get(1).getNumber()).isEqualTo("fancy");
        assertThat(searchResults.getPagesCount()).isEqualTo(2L);
        assertThat(searchResults.getPageNumber()).isEqualTo(2);
        assertThat(searchResults.getPerPage()).isEqualTo(2);
    }

}
