package server;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends Exception{

    private final int code;
    public final String message;

    public ResponseException(int code, String message){
        this.code = code;
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public int getCode(){
        return code;
    }
    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }
}
