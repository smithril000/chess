package client;

import java.net.http.HttpClient;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(int port){
        serverUrl = "http://localhost:" + port;
    }

    public void register(Map<String, String> data){
        //we need to make what we pu tin look like the web ui
        //map should work

    }
}
