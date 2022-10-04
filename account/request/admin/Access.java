package account.request.admin;

import javax.validation.constraints.NotEmpty;

public class Access {
    @NotEmpty
    private String user;
    @NotEmpty
    private String operation;

    public String getUser() {
        return user;
    }

    public String getOperation() {
        return operation;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
