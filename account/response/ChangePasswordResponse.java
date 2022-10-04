package account.response;

import account.entity.User;

public class ChangePasswordResponse {
    private final String email;

    public ChangePasswordResponse(User user) {
        this.email = user.getEmail();
    }

    public String getStatus() {
        String status = "The password has been updated successfully";
        return status;
    }

    public String getEmail() {
        return email;
    }
}
