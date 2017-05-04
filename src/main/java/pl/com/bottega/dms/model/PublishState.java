package pl.com.bottega.dms.model;


import pl.com.bottega.dms.model.commands.ChangeDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmForDocumentCommand;
import pl.com.bottega.dms.model.commands.PublishDocumentCommand;
import pl.com.bottega.dms.model.printing.PrintCostCalculator;

import java.time.LocalDateTime;

import static pl.com.bottega.dms.model.DocumentStatus.ARCHIVED;
import static pl.com.bottega.dms.model.DocumentStatus.VERIFIED;

public class PublishState implements DocumentState {

    private Document document;

    public PublishState(Document document) {
        this.document = document;
    }

    @Override
    public void change(ChangeDocumentCommand cmd) {
        throw new DocumentStatusException("PUBLISHED document can not be changed");
    }

    @Override
    public void verify(EmployeeId employeeId) {
        throw new DocumentStatusException("PUBLISHED document can not be verified");
        /*document.status = VERIFIED;
        document.verifiedAt = LocalDateTime.now();
        document.verifierId = employeeId;
        document.documentState = new VerifiedState(document);*/
    }

    @Override
    public void archive(EmployeeId employeeId) {
        document.documentState = new ArchivedState(document);
        document.status = ARCHIVED;
    }

    @Override
    public void publish(PublishDocumentCommand cmd, PrintCostCalculator printCostCalculator) {
        throw new DocumentStatusException("Document is already PUBLISHED");
    }

    @Override
    public void confirm(ConfirmDocumentCommand cmd) {
        Confirmation confirmation = getConfirmation(cmd.getEmployeeId());
        confirmation.confirm();
    }

    @Override
    public void confirmFor(ConfirmForDocumentCommand cmd) {
        Confirmation confirmation = getConfirmation(cmd.getConfirmForEmployeeId());
        confirmation.confirmFor(cmd.getEmployeeId());
    }

    private Confirmation getConfirmation(EmployeeId employeeId) {
        for (Confirmation confirmation : document.confirmations) {
            if (confirmation.isOwnedBy(employeeId))
                return confirmation;
        }
        throw new DocumentStatusException(String.format("No confirmation for %s", employeeId));
    }
}
