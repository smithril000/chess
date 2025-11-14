import Client_Stuff.ClientLogin;
import chess.*;
//this runs the acutaly client
public class Main {
    public static void main(String[] args) {

        try{
            new ClientLogin("http://localhost:8080").run();
        }catch(Throwable ex){

        }
    }
}