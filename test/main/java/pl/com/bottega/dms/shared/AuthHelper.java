package pl.com.bottega.dms.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.com.bottega.dms.application.user.AuthProcess;
import pl.com.bottega.dms.application.user.CreateAccountCommand;
import pl.com.bottega.dms.application.user.LoginCommand;

@Component
public class AuthHelper {

    @Autowired
    private AuthProcess authProcess;

    public void authenticate() {
        authenticate(1L);
    }

    public void authenticate(Long employeeId) {
        CreateAccountCommand cmd = new CreateAccountCommand();
        cmd.setUserName("janek");
        cmd.setEmployeeId(employeeId);
        cmd.setPassword("xxx");
        authProcess.createAccount(cmd);

        LoginCommand loginCommand = new LoginCommand();
        loginCommand.setLogin("janek");
        loginCommand.setPassword("xxx");
        authProcess.login(loginCommand);
    }

}
