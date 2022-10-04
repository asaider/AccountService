package account.request.admin;

import javax.validation.constraints.NotEmpty;

public class Action {
    @NotEmpty
    private String user;

    @NotEmpty
    private String role;
    @NotEmpty
    private String operation;

    public String getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    public String getOperation() {
        return operation;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setRole(String role) {
        this.role = ("ROLE_".concat(role));
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
