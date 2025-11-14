package Client_Stuff;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import static java.awt.Color.BLUE;

public class ClientLogin {
    private final ServerFacade server;
    private final String regString = "register <USERNAME> <PASSWORD> <EMAIL> - creates a new user";
    private final String loginString = "login <USERNAME> <PASSWORD> - logges into an existing user";
    public ClientLogin(String serverURL){
        server = new ServerFacade(serverURL);
    }
    public void run(){
        System.out.println("Welcome to chess. Type Help to get started");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.print(result);
            }catch (Throwable ex){
                var errorMessage = ex.toString();
                System.out.println(errorMessage);
            }
        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);

                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        return String.format("%s\n%s\nhelp - shows helpful instructions :)\nquit - quits operation", regString, loginString);
    }
    public String login(String[] params){
        return null;
    }
}
