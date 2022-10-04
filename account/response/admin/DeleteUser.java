package account.response.admin;

public class DeleteUser {
    private String user;
    private String status;

    public DeleteUser(String user, String status) {
        this.user = user;
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }
}
