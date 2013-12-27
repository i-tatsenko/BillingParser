
public class ServerFailedException extends Exception {

    private final String cause;

    public ServerFailedException(String cause) {
        this.cause = cause;
    }

    public void printException() {
        System.out.println(cause);
        printStackTrace();
    }

    public Exception getException() {
        return this;
    }

}
