package client;

import com.google.gson.Gson;
import dataaccess.ResponseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(int port){
        serverUrl = "http://localhost:" + port;
    }

    public String register(Map<String, String> data) {
        //we need to make what we pu tin look like the web ui
        //map should work
        var request = buildRequest("POST", "/user", data, null);
        try {
            var response = sendRequest(request);
            return responseHandler(response);
        }catch(ResponseException ex){
            //here we can handle all of the errors
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private String responseHandler(HttpResponse<String> response) {
        int code = response.statusCode();
        if(code!=200){
            return switch (code){
                case 403 -> "Sorry, username already taken";
                case 401 -> "Sorry, we can't authorize you. Maybe check your username and password";
                default -> "Something went wrong";
            };
        }
        return null;
    }

    public String login(Map<String, String> data){
        var req = buildRequest("POST", "/session", data, null);
        try{
            var response = sendRequest(req);
            return responseHandler(response);
        }catch(ResponseException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
    private HttpRequest buildRequest(String method, String path, Object body, String authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }
    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }
    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }


    }
}
