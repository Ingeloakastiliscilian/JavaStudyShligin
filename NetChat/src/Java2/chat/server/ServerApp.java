package Java2.chat.server;

public class ServerApp {

    private final static int DEFAULT_PORT = 8888;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if ( args.length  > 0 ){
            try {
                port = Integer.parseInt( args[0] );
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        new ChatServer( port ).start();
    }
}
