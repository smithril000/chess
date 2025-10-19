package server;

public class ResponseException {

    private final int code;
    private final String message;

    public ResponseException(int code, String message){
        this.code = code;
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
