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


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverURL){
        serverUrl = serverURL;
    }

    public Object register(Map<String, String> testUser) throws Exception {
        var request = buildRequest("POST", "/user", testUser);
        var response = sendRequest(request);
        return handleResponse(response, String.class);
    }

    public String getGames() throws Exception {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        return handleResponse(response, String.class);
    }
    private HttpRequest buildRequest(String method, String path, Object body){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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
                throw new Exception("error");
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
