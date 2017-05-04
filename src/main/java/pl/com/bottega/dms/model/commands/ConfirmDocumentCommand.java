package pl.com.bottega.dms.model.commands;

import pl.com.bottega.dms.model.EmployeeId;

public class ConfirmDocumentCommand implements EmployeeAware, Validatable {
    private EmployeeId employeeId;
    private String number;

    public void setEmployeeId(EmployeeId employeeId) {
        this.employeeId = employeeId;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public void validate(ValidationErrors errors) {
        if (isEmpty(number))
            errors.add("number", "can't be blank");
    }
}
