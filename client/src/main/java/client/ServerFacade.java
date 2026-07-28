package client;

import com.google.gson.Gson;
import dataaccess.ResponseException;
import model.AuthData;
import model.GameID;
import model.GamesReturned;
import model.JoinGameRequest;

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

    public AuthData register(Map<String, String> data) {
        //we need to make what we pu tin look like the web ui
        //map should work
        var request = buildRequest("POST", "/user", data, null);
        try {
            var response = sendRequest(request);
            return responseHandler(response, AuthData.class);
        }catch(Exception ex){
            //here we can handle all the errors
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private <T> T responseHandler(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (status == 200) {
            var body = response.body();
            if (body == null) {
                //throw ResponseException.fromJson(body);
                throw new ResponseException(status, "something went wrong");
            }

            //throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    public AuthData login(Map<String, String> data){
        var req = buildRequest("POST", "/session", data, null);
        try{
            var response = sendRequest(req);
            return responseHandler(response, AuthData.class);
        }catch(Exception ex){
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

    public void logout(Map<String, String> data, String auth) {
        var req = buildRequest("DELETE", "/session", auth, auth);
        try{
            var response = sendRequest(req);

        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    public GameID createGame(Map<String, String> data, String authToken) {
        var req = buildRequest("POST", "/game", data, authToken);
        try{
            var response = sendRequest(req);
            return responseHandler(response, GameID.class);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public GamesReturned listGames(String authToken) {
        var req = buildRequest("GET", "/game", authToken, authToken);
        try{
            var response = sendRequest(req);
            return responseHandler(response, GamesReturned.class);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public void joinGame(JoinGameRequest join, String auth) {
        var req = buildRequest("PUT", "/game", join, auth);
        try{
            var response = sendRequest(req);
            responseHandler(response, String.class);
            //now we need to display the game
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
