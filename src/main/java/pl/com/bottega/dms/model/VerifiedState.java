package pl.com.bottega.dms.model;

import pl.com.bottega.dms.model.commands.ChangeDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmForDocumentCommand;
import pl.com.bottega.dms.model.commands.PublishDocumentCommand;
import pl.com.bottega.dms.model.printing.PrintCostCalculator;

import java.time.LocalDateTime;

import static pl.com.bottega.dms.model.DocumentStatus.ARCHIVED;
import static pl.com.bottega.dms.model.DocumentStatus.DRAFT;
import static pl.com.bottega.dms.model.DocumentStatus.PUBLISHED;


public class VerifiedState implements DocumentState {

    private Document document;

    public VerifiedState(Document document) {
        this.document = document;
    }

    @Override
    public void change(ChangeDocumentCommand cmd) {
        document.title = cmd.getTitle();
        document.content = cmd.getContent();
        document.status = DRAFT;
        document.changedAt = LocalDateTime.now();
        document.editorId = cmd.getEmployeeId();
        document.expiresAt = cmd.getExpiresAt();
        document.documentState = new DraftState(document);
    }

    @Override
    public void verify(EmployeeId employeeId) {
        throw new DocumentStatusException("Document must be DRAFT to verify");
    }

    @Override
    public void archive(EmployeeId employeeId) {
        document.documentState = new ArchivedState(document);
        document.status = ARCHIVED;
    }

    @Override
    public void publish(PublishDocumentCommand cmd, PrintCostCalculator printCostCalculator) {
        document.status = PUBLISHED;
        document.documentState = new PublishState(document);
        document.publishedAt = LocalDateTime.now();
        document.publisherId = cmd.getEmployeeId();
        document.printCost = printCostCalculator.calculateCost(document);
        createConfirmations(cmd);
    }

    private void createConfirmations(PublishDocumentCommand cmd) {
        for (EmployeeId employeeId : cmd.getRecipients()) {
            document.confirmations.add(new Confirmation(employeeId));
        }
    }

    @Override
    public void confirm(ConfirmDocumentCommand cmd) {
        throw new DocumentStatusException("Document should be PUBLISHED to confirm");
    }

    @Override
    public void confirmFor(ConfirmForDocumentCommand cmd) {
        throw new DocumentStatusException("Document should be PUBLISHED to confirm");
    }
}
