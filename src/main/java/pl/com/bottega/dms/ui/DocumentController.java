package pl.com.bottega.dms.ui;


import org.springframework.web.bind.annotation.*;
import pl.com.bottega.dms.application.*;
import pl.com.bottega.dms.model.Document;
import pl.com.bottega.dms.model.DocumentNumber;
import pl.com.bottega.dms.model.EmployeeId;
import pl.com.bottega.dms.model.commands.*;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private DocumentFlowProcess documentFlowProcess;
    private DocumentCatalog documentCatalog;
    private ReadingConfirmator readingConfirmator;

    public DocumentController(DocumentFlowProcess documentFlowProcess,
                              DocumentCatalog documentCatalog,
                              ReadingConfirmator readingConfirmator) {
        this.documentFlowProcess = documentFlowProcess;
        this.documentCatalog = documentCatalog;
        this.readingConfirmator = readingConfirmator;
    }

    @RequestMapping(method = RequestMethod.POST)
    public DocumentNumber create(@RequestBody CreateDocumentCommand cmd) {
        return documentFlowProcess.create(cmd);
    }

    @PutMapping("/{documentNumber}")
    public void update(@PathVariable String documentNumber, @RequestBody ChangeDocumentCommand cmd) {
        cmd.setNumber(documentNumber);
        documentFlowProcess.change(cmd);
    }

    @GetMapping("/{documentNumber}")
    public DocumentDto show(@PathVariable String documentNumber) {
        return documentCatalog.get(new DocumentNumber(documentNumber));
    }

    @GetMapping
    public DocumentSearchResults search(DocumentQuery documentQuery) {
        return documentCatalog.find(documentQuery);
    }

    @PostMapping("/{documentNumber}/verification")
    public void verify(@PathVariable String documentNumber) {
        documentFlowProcess.verify(new DocumentNumber(documentNumber));
    }

    @PostMapping("/{documentNumber}/publication")
    public void publish(@PathVariable String documentNumber, @RequestBody PublishDocumentCommand cmd) {
        cmd.setNumber(documentNumber);
        documentFlowProcess.publish(cmd);
    }

    @PostMapping("/{documentNumber}/archivization")
    public void archive(@PathVariable String documentNumber) {
        documentFlowProcess.archive(new DocumentNumber(documentNumber));
    }

    @PostMapping("/{documentNumber}/confirmation")
    public void confirm(@PathVariable String documentNumber, @RequestBody ConfirmDocumentCommand cmd) {
        cmd.setEmployeeId(new EmployeeId(15L));
        cmd.setNumber(documentNumber);
        readingConfirmator.confirm(cmd);
    }

    @PostMapping("/{documentNumber}/confirmationFor/{employee}")
    public void confirmFor(@PathVariable String documentNumber,
                           @RequestBody ConfirmForDocumentCommand cmd,
                           @PathVariable String employee) {
        cmd.setConfirmingEmployeeId(new EmployeeId(1L));
        cmd.setEmployeeId(new EmployeeId(Long.valueOf(employee)));
        cmd.setNumber(documentNumber);
        readingConfirmator.confirmFor(cmd);
    }

}
