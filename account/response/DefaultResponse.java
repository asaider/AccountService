package account.response;

public class DefaultResponse {
    private final String status;

    public DefaultResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
