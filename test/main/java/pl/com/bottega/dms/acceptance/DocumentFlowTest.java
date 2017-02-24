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
import pl.com.bottega.dms.model.Document;
import pl.com.bottega.dms.model.DocumentNumber;
import pl.com.bottega.dms.model.DocumentStatus;
import pl.com.bottega.dms.model.EmployeeId;
import pl.com.bottega.dms.model.commands.ChangeDocumentCommand;
import pl.com.bottega.dms.model.commands.CreateDocumentCommand;
import pl.com.bottega.dms.model.commands.PublishDocumentCommand;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(DmsApplication.class)
public class DocumentFlowTest {

    @Autowired
    private DocumentFlowProcess documentFlowProcess;

    @Autowired
    private DocumentCatalog documentCatalog;

    @Test
    public void shouldCreateDocument() {
        //when - I create document
        CreateDocumentCommand cmd = new CreateDocumentCommand();
        cmd.setTitle("test");
        DocumentNumber documentNumber = documentFlowProcess.create(cmd);

        //then - the document is available in catalog
        DocumentDto dto = documentCatalog.get(documentNumber);
        assertThat(dto.getTitle()).isEqualTo("test");
        assertThat(dto.getNumber()).isEqualTo(documentNumber.getNumber());
    }

    @Test
    public void shouldChangeDocument() {
        //given
        CreateDocumentCommand cmd = new CreateDocumentCommand();
        cmd.setTitle("titleOne");
        DocumentNumber documentNumber = documentFlowProcess.create(cmd);

        //when
        ChangeDocumentCommand changeCmd = new ChangeDocumentCommand();
        changeCmd.setTitle("titleTwo");
        changeCmd.setNumber(documentNumber.getNumber());
        documentFlowProcess.change(changeCmd);

        //then
        DocumentDto dto = documentCatalog.get(documentNumber);
        assertThat(dto.getTitle()).isEqualTo("titleTwo");
    }

    @Test
    public void shouldVerifyDocument() {
        //given
        CreateDocumentCommand cmd = new CreateDocumentCommand();
        DocumentNumber documentNumber = documentFlowProcess.create(cmd);

        //when
        documentFlowProcess.verify(documentNumber);

        //then
        DocumentDto dto = documentCatalog.get(documentNumber);
        assertThat(dto.getDocumentStatus()).isEqualTo(DocumentStatus.VERIFIED);
    }

    @Test
    public void shouldPublishDocument() {
        //given
        CreateDocumentCommand cmd = new CreateDocumentCommand();
        DocumentNumber documentNumber = documentFlowProcess.create(cmd);
        documentFlowProcess.verify(documentNumber);
        EmployeeId employeeId = new EmployeeId(1L);

        //when
        PublishDocumentCommand publishDocumentCommand = new PublishDocumentCommand();
        publishDocumentCommand.setNumber(documentNumber.getNumber());
        publishDocumentCommand.setEmployeeId(employeeId);
        publishDocumentCommand.setRecipients(new ArrayList<>());
        documentFlowProcess.publish(publishDocumentCommand);

        //then
        DocumentDto dto = documentCatalog.get(documentNumber);
        assertThat(dto.getDocumentStatus()).isEqualTo(DocumentStatus.PUBLISHED);

    }

    @Test
    public void shouldArchiveDocument() {
        //given
        CreateDocumentCommand cmd = new CreateDocumentCommand();
        DocumentNumber documentNumber = documentFlowProcess.create(cmd);
        EmployeeId employeeId = new EmployeeId(1L);

        //when
        documentFlowProcess.archive(documentNumber, employeeId);

        //then
        DocumentDto dto = documentCatalog.get(documentNumber);
        assertThat(dto.getDocumentStatus()).isEqualTo(DocumentStatus.ARCHIVED);
    }

}

