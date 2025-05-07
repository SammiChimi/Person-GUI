public class InvalidOCCCDateException extends IllegalArgumentException {

    private final String msg;

    public InvalidOCCCDateException() {
        super();
        msg = "This date is invalid.";
    }

    public InvalidOCCCDateException(String msg) {
        super(msg);
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + msg;
    }
}
