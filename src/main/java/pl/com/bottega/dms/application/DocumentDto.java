package pl.com.bottega.dms.application;


import pl.com.bottega.dms.model.Confirmation;
import pl.com.bottega.dms.model.DocumentStatus;

import java.util.Set;

public class DocumentDto {

    private String title;
    private String number;
    private DocumentStatus documentStatus;
    private Set<ConfirmationDto> confirmations;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }

    public Set<ConfirmationDto> getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Set<ConfirmationDto> confirmations) {
        this.confirmations = confirmations;
    }

    public ConfirmationDto getConfirmation(Long employeeId) {
        for (ConfirmationDto confirmationDto : confirmations) {
            if (confirmationDto.getOwner().equals(employeeId))
                return confirmationDto;
        }
        return null;
    }

}
