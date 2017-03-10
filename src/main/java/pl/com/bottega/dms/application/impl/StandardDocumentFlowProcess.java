package pl.com.bottega.dms.application.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.com.bottega.dms.application.DocumentFlowProcess;
import pl.com.bottega.dms.model.DocumentRepository;
import pl.com.bottega.dms.model.Document;
import pl.com.bottega.dms.model.DocumentNumber;
import pl.com.bottega.dms.model.EmployeeId;
import pl.com.bottega.dms.model.commands.ChangeDocumentCommand;
import pl.com.bottega.dms.model.commands.CreateDocumentCommand;
import pl.com.bottega.dms.model.commands.PublishDocumentCommand;
import pl.com.bottega.dms.model.numbers.NumberGenerator;
import pl.com.bottega.dms.model.printing.PrintCostCalculator;

@Transactional
@Component
public class StandardDocumentFlowProcess implements DocumentFlowProcess {

    @Autowired
    private NumberGenerator numberGenerator;

    @Autowired
    @Qualifier("bw")
    private PrintCostCalculator printCostCalculator;

    @Autowired
    private DocumentRepository documentRepository;

    /*public StandardDocumentFlowProcess(NumberGenerator numberGenerator, @Qualifier("bw") PrintCostCalculator printCostCalculator, DocumentRepository documentRepository) {
        this.numberGenerator = numberGenerator;
        this.printCostCalculator = printCostCalculator;
        this.documentRepository = documentRepository;
    }*/

    @Override
    public DocumentNumber create(CreateDocumentCommand cmd) {
        Document document = new Document(cmd, numberGenerator);
        documentRepository.put(document);
        return document.getNumber();
    }

    @Override
    public void change(ChangeDocumentCommand cmd) {
        DocumentNumber documentNumber = new DocumentNumber(cmd.getNumber());
        Document document = documentRepository.get(documentNumber);
        document.change(cmd);
    }

    @Override
    public void verify(DocumentNumber documentNumber) {
        Document document = documentRepository.get(documentNumber);
        document.verify(document.getVerifierId());
    }

    @Override
    public void publish(PublishDocumentCommand cmd) {
        DocumentNumber documentNumber = new DocumentNumber(cmd.getNumber());
        Document document = documentRepository.get(documentNumber);
        document.publish(cmd, printCostCalculator);
    }

    @Override
    public void archive(DocumentNumber documentNumber) {
        Document document = documentRepository.get(documentNumber);
        document.archive(new EmployeeId(1L));
    }
}
