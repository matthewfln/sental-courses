package exception;

public class ErrorMessage {

    private String error;
    private String message;

    public ErrorMessage(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
