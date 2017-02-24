package pl.com.bottega.dms.application;


import java.time.LocalDateTime;

public class ConfirmationDto {

    private Long owner;
    private Long proxy;
    private LocalDateTime confirmationDate;

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Long getProxy() {
        return proxy;
    }

    public void setProxy(Long proxy) {
        this.proxy = proxy;
    }

    public LocalDateTime getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(LocalDateTime confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

}
