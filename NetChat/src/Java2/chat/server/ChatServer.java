package Java2.chat.server;

import Java2.chat.Auth.BaseAuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {

   public final static String DISCONNECT = "/exit";

   private final int port;
   private volatile Map<String, ClientListener> online;
   private BaseAuthService authService;
   private ServerSocket chatServerSocket;

   public ChatServer( int port ) {
      this.port = port;
   }

   public void start() {
      try /*( ServerSocket serverSocket = new ServerSocket(port))*/ {
         this.chatServerSocket = new ServerSocket( port );
         online = new HashMap<>();
         authService = new BaseAuthService( this );
         authService.start();
      } catch ( IOException e ) {
         System.out.println( "server start error" );
      }
   }

   public void addOnline( String nickname, ClientHandler clientHandler ) {
      broadcast( nickname + " connected to chat" );
      online.put( nickname, new ClientListener( nickname, clientHandler ) );
      clientHandler.pushBuffer( "Server : welcome to chat" );
   }

   public void stop() {
      broadcast( DISCONNECT );
      try {
         authService.stop();
         chatServerSocket.close();
      } catch ( IOException e ) {
         System.out.println( "stop server error" );
      }
   }

   private void broadcast( String mess ) {
      for ( String nickname: online.keySet() ) {
         online.get( nickname ).send( mess );
      }
   }

   public ServerSocket getServerSocket() {
      return chatServerSocket;
   }

   private class ClientListener implements Runnable {

      ClientHandler clientHandler;
      String nickname;

      private ClientListener( String nickname, ClientHandler clientHandler ) {
         this.nickname = nickname;
         this.clientHandler = clientHandler;
         System.out.println( "new client listener" );
         new Thread( this ).start();
      }

      @Override
      public void run() {
         while ( true ) {
//            if ( clientHandler.noMessages() )
//               return;
            String mess = clientHandler.readBuffer();
            if ( mess == null ) {
               try {
                  Thread.sleep( 50 );
               } catch ( InterruptedException e ) {
                  e.printStackTrace();
               }
               continue;}
            System.out.println( mess );
            if ( DISCONNECT.equals( mess ) ) {
               online.remove( this.nickname );
               broadcast( nickname + " leaving chat" );
               break;
            }
            if ( mess.startsWith( "/w" ) ) {
               String[] direct = mess.split( "\\s+", 3 );
//               System.out.println( direct );
               directMessage( direct[1].trim(), direct[2] );
            } else {
               broadcast( nickname + ": " + mess );
            }
         }
      }

      private void directMessage( String nickname, String mess ) {
//         System.out.println( online.keySet() );
         System.out.println( nickname );
         if ( online.containsKey( nickname ) )
            online.get( nickname ).send( nickname + "(private): " + mess );
         else clientHandler.pushBuffer( "target not online or not exist" );
      }

      private void send( String mess ) {
         this.clientHandler.pushBuffer( mess );
      }
   }

}
