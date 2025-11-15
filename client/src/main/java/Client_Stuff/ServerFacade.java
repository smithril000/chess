package Client_Stuff;

import chess.ChessGame;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import server.ResponseException;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverURL){
        serverUrl = serverURL;
    }

    public Map<String, String> register(Map<String, String> testUser) throws Exception {
        var request = buildRequest("POST", "/user", testUser, null);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
    }
    public Map<String, String> login(Map<String, String> userData) throws Exception{
        var request = buildRequest("POST", "/session", userData, null);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
    }

    public Map<String, String> logout(String authToken) throws Exception{
        var serializer = new Gson();
        String json = serializer.toJson(authToken);
        var request = buildRequest("DELETE", "/session", authToken, authToken);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
    }
    public Map<String, String> create(Map<String, String> gameName, String authToken) throws Exception{
        var request = buildRequest("POST", "/game", gameName, authToken);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
    }
    public String join(Map<String, String> gameInfo, String authToken) throws Exception {
        var request = buildRequest("PUT", "/game", gameInfo, authToken);
        var response = sendRequest(request);
        return handleResponse(response, String.class);
    }

    public Map<String, String> getGames(String authToken) throws Exception {
        var serializer = new Gson();
        String json = serializer.toJson(authToken);
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
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
    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }
    private HttpResponse<String> sendRequest(HttpRequest request){
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            //throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
        //FIX THIS
        return null;
    }
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                //throw ResponseException.fromJson(body);
                throw ResponseException.fromJson(body);
            }

            //throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
            throw new Exception("error");
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
