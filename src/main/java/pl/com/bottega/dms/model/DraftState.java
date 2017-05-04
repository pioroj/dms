package pl.com.bottega.dms.model;

import pl.com.bottega.dms.model.commands.ChangeDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmForDocumentCommand;
import pl.com.bottega.dms.model.commands.PublishDocumentCommand;
import pl.com.bottega.dms.model.printing.PrintCostCalculator;

import java.time.LocalDateTime;

import static pl.com.bottega.dms.model.DocumentStatus.VERIFIED;


public class DraftState implements DocumentState {

    private Document document;

    public DraftState(Document document) {
        this.document = document;
    }

    @Override
    public void change(ChangeDocumentCommand cmd) {
        document.title = cmd.getTitle();
        document.content = cmd.getContent();
        document.changedAt = LocalDateTime.now();
        document.editorId = cmd.getEmployeeId();
        document.expiresAt = cmd.getExpiresAt();
    }

    @Override
    public void verify(EmployeeId employeeId) {
        document.status = VERIFIED;
        document.verifiedAt = LocalDateTime.now();
        document.verifierId = employeeId;
        document.documentState = new VerifiedState(document);
    }

    @Override
    public void archive(EmployeeId employeeId) {
        document.status = DocumentStatus.ARCHIVED;
        document.documentState = new ArchivedState(document);
    }

    @Override
    public void publish(PublishDocumentCommand cmd, PrintCostCalculator printCostCalculator) {
        throw new DocumentStatusException("Document should be VERIFIED to PUBLISH");
    }

    @Override
    public void confirm(ConfirmDocumentCommand cmd) {
        throw new DocumentStatusException("Document should be VERIFIED to PUBLISH");
    }

    @Override
    public void confirmFor(ConfirmForDocumentCommand cmd) {
        throw new DocumentStatusException("Document should be VERIFIED to PUBLISH");
    }
}
