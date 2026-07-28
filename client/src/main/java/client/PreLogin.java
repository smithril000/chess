package client;

import java.util.Arrays;
import java.util.Scanner;

public class PreLogin {

    private boolean loggedIn = false;
    public void run(){
        System.out.println("Welcome to chess. Type Help to get started");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            System.out.print("[Logged Out] >> ");
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.println(result);
                if(loggedIn){
                    loggedIn = false;
                }
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
                case "help" -> help(params);
                case "login" -> null;
                default -> throw new IllegalStateException("Unexpected value: " + cmd);
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String help(String[] params){
        System.out.println("help works");
        return null;
    }
}
