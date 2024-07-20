package nl.me.easyclans.helpers;

public enum Status {
    SUCCESS("The action was successful"),
    ERROR("An error occurred"),
    WARNING("A warning occurred");

    private String message;

    Status(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
