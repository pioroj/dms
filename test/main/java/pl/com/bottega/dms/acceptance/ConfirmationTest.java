package pl.com.bottega.dms.acceptance;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.com.bottega.dms.DmsApplication;
import pl.com.bottega.dms.application.DocumentCatalog;
import pl.com.bottega.dms.application.DocumentDto;
import pl.com.bottega.dms.application.DocumentFlowProcess;
import pl.com.bottega.dms.application.ReadingConfirmator;
import pl.com.bottega.dms.model.DocumentNumber;
import pl.com.bottega.dms.model.EmployeeId;
import pl.com.bottega.dms.model.commands.ConfirmDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmForDocumentCommand;
import pl.com.bottega.dms.model.commands.CreateDocumentCommand;
import pl.com.bottega.dms.model.commands.PublishDocumentCommand;

import java.util.Arrays;


import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(DmsApplication.class)
public class ConfirmationTest {

    @Autowired
    private DocumentCatalog documentCatalog;

    @Autowired
    private DocumentFlowProcess documentFlowProcess;

    @Autowired
    private ReadingConfirmator readingConfirmator;

    @Test
    public void shouldConfirmDocument() {
        //given
        DocumentNumber documentNumber = given().publishedDocument();
        EmployeeId employeeId = new EmployeeId(1L);

        //when
        ConfirmDocumentCommand cmd = new ConfirmDocumentCommand();
        cmd.setEmployeeId(employeeId);
        cmd.setNumber(documentNumber.getNumber());
        readingConfirmator.confirm(cmd);

        //then
        DocumentDto dto = documentCatalog.get(documentNumber);
        assertThat(dto.getConfirmations().size()).isEqualTo(1);
        assertThat(dto.getConfirmation(employeeId.getId()).getOwner()).isEqualTo(employeeId.getId());
        assertThat(dto.getConfirmation(employeeId.getId()).getConfirmationDate()).isNotNull();
    }

    @Test
    public void shouldConfirmForDocument() {
        //given
        DocumentNumber documentNumber = given().publishedDocument();
        EmployeeId employeeId = new EmployeeId(1L);
        EmployeeId employeeId2 = new EmployeeId(2L);

        //when
        ConfirmForDocumentCommand cmd = new ConfirmForDocumentCommand();
        cmd.setEmployeeId(employeeId);
        cmd.setConfirmingEmployeeId(employeeId2);
        cmd.setNumber(documentNumber.getNumber());
        readingConfirmator.confirmFor(cmd);

        //then
        DocumentDto dto = documentCatalog.get(documentNumber);
        assertThat(dto.getConfirmations().size()).isEqualTo(1);
        assertThat(dto.getConfirmation(employeeId.getId()).getOwner()).isEqualTo(employeeId.getId());
        assertThat(dto.getConfirmation(employeeId.getId()).getProxy()).isEqualTo(employeeId2.getId());
        assertThat(dto.getConfirmation(employeeId.getId()).getConfirmationDate()).isNotNull();
    }

    public DocumentAssembler given() {
        return new DocumentAssembler();
    }

    class DocumentAssembler {

        public DocumentNumber publishedDocument() {
            CreateDocumentCommand createCmd = new CreateDocumentCommand();
            DocumentNumber documentNumber = documentFlowProcess.create(createCmd);
            PublishDocumentCommand publishCmd = new PublishDocumentCommand();
            EmployeeId employeeId = new EmployeeId(1L);

            documentFlowProcess.verify(documentNumber);
            publishCmd.setNumber(documentNumber.getNumber());
            publishCmd.setRecipients(Arrays.asList(employeeId));
            documentFlowProcess.publish(publishCmd);
            return documentNumber;
        }

    }

}
