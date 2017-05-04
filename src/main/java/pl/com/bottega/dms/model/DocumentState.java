package pl.com.bottega.dms.model;


import pl.com.bottega.dms.model.commands.ChangeDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmDocumentCommand;
import pl.com.bottega.dms.model.commands.ConfirmForDocumentCommand;
import pl.com.bottega.dms.model.commands.PublishDocumentCommand;
import pl.com.bottega.dms.model.printing.PrintCostCalculator;

public interface DocumentState {

    void change(ChangeDocumentCommand cmd);

    void verify(EmployeeId employeeId);

    void archive(EmployeeId employeeId);

    void publish(PublishDocumentCommand cmd, PrintCostCalculator printCostCalculator);

    void confirm(ConfirmDocumentCommand cmd);

    void confirmFor(ConfirmForDocumentCommand cmd);

}
