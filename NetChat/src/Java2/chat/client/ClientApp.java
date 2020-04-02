package Java2.chat.client;

import Java2.chat.server.ClientHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientApp implements Runnable {

   private static final int DEFAULT_PORT = 8888;
   private final int port;

   public ClientApp( int port ) {this.port = port;}

   @Override
   public void run() {
      try {
         Socket clientSocket = new Socket( "localhost", port );
         Chat clientChat = new Chat( "NetChat" );
         clientChat.setInputStream( new DataInputStream( clientSocket.getInputStream() ) );
         clientChat.setOutputStream( new DataOutputStream( clientSocket.getOutputStream() ) );
      } catch ( UnknownHostException e ) {
         e.printStackTrace();
      } catch ( IOException e ) {
         e.printStackTrace();
         System.out.println( "error on socket constructor" );
      }
   }

   public static void main( String[] args ) {
      int port = DEFAULT_PORT;
      if ( args.length > 0 )
         try {
            port = Integer.parseInt( args[0] );
         } catch ( NumberFormatException e ) {
            System.out.println( "invalid port for client" );
         }
      new ClientApp( port ).run();
      new ClientApp( port ).run();
   }


}
