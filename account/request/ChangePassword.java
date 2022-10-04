package account.request;

import account.validator.PasswordChecker;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class ChangePassword {
    @NotEmpty
    @PasswordChecker
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @JsonProperty("new_password")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
