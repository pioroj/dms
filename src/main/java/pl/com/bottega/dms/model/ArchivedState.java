package pl.com.bottega.dms.model;


import pl.com.bottega.dms.model.commands.ChangeDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmForDocumentCommand;
import pl.com.bottega.dms.model.commands.PublishDocumentCommand;
import pl.com.bottega.dms.model.printing.PrintCostCalculator;

public class ArchivedState implements DocumentState {

    private Document document;

    public ArchivedState(Document document) {
        this.document = document;
    }

    @Override
    public void change(ChangeDocumentCommand cmd) {
        throw new DocumentStatusException("ARCHIVED document can not be changed");
    }

    @Override
    public void verify(EmployeeId employeeId) {
        throw new DocumentStatusException("ARCHIVED document can not be verified");
    }

    @Override
    public void archive(EmployeeId employeeId) {
        throw new DocumentStatusException("Document is already archived");
    }

    @Override
    public void publish(PublishDocumentCommand cmd, PrintCostCalculator printCostCalculator) {
        throw new DocumentStatusException("ARCHIVED document can not be published");
    }

    @Override
    public void confirm(ConfirmDocumentCommand cmd) {
        throw new DocumentStatusException("ARCHIVED document can not be confirmed");
    }

    @Override
    public void confirmFor(ConfirmForDocumentCommand cmd) {
        throw new DocumentStatusException("ARCHIVED document can not be confirmed");
    }
}
