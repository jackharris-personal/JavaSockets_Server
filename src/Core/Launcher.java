package Core;

public class Launcher {

    public static void main(String[] args){

        int port = 6666;
        System.out.println( "Start server on port: " + port );

        Server server = new Server(port);
        new Loop(server);

        server.start();
    }
}
