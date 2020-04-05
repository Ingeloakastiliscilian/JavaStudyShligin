package Java2.chat.client;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class ClientApp implements Runnable {

   private static final int DEFAULT_PORT = 8888;
   private final int port;
   private  ClientHandler handler;

   public ClientApp( int port ) {this.port = port;}

   @Override
   public void run() {
      try {
         Socket clientSocket = new Socket( "localhost", port );
         System.out.println("Client app running");
         handler = new ClientHandler( clientSocket );
         Chat clientChat = new Chat( "NetChat" , handler);
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
      SwingUtilities.invokeLater( new ClientApp( port ) );
      SwingUtilities.invokeLater( new ClientApp( port ) );
//      new ClientApp( port ).run();
//      new ClientApp( port ).run();
   }


}
