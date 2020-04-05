package Java2.chat.server;

import Java2.chat.Auth.BaseAuthService;
import Java2.chat.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.util.HashMap;
import java.util.Map;

// TODO: 05.04.2020 Переписать орос сокетов. не забыть про личку 
public class ChatServer {

   public final static String SERVER_CMD_DISCONNECT = "/exit";
   private final static long DEFAULT_REFRESH_PERIOD = 100;

   private final int port;
   private volatile ServerSocket chatServerSocket;

   private volatile Map<String, ClientListener> online;

   private BaseAuthService authService;

   private static long refreshPeriod;

   public ChatServer( int port ) {
      this.port = port;
   }

   public static long refreshPeriod() {
      return refreshPeriod;
   }

   public void start() {
      try /*( ServerSocket serverSocket = new ServerSocket(port))*/ {
         chatServerSocket = new ServerSocket( port );
         SocketOption<Long> refreshTimeout = new SocketOption<>() {
            @Override
            public String name() {
               return "RefreshTimeout";
            }

            @Override
            public Class<Long> type() {
               return Long.TYPE;
            }
         };
         chatServerSocket.setOption( refreshTimeout, DEFAULT_REFRESH_PERIOD );
         online = new HashMap<>();
         authService = new BaseAuthService( chatServerSocket );
         authService.start();
         refreshPeriod = DEFAULT_REFRESH_PERIOD;
      } catch ( IOException e ) {
         System.out.println( "server start error" );
      }
   }

   public void addOnline() {
      if ( authService.hasApproved() ) {
         Socket clientSocket = authService.getApproved();
         try {
            String nickname = authService.getSocketNickname( clientSocket );
            online.put( nickname, new ClientListener( nickname, clientSocket ) );
         } catch ( IOException e ) {
            System.out.println( "не удалось получить никнейм" );
         }
      }
   }

   public void stop() {
      broadcast( SERVER_CMD_DISCONNECT );
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

   private class ClientListener implements Runnable {

      ClientHandler clientHandler;
      String nickname;

      private ClientListener( String nickname, Socket socket ) {
         this.nickname = nickname;
         System.out.println( "new client listener" );
         new Thread( this ).start();
      }

      @Override
      public void run() {
         StringBuffer buffer;
         while ( clientHandler.isAlive() ) {
            String mess = clientHandler.readBuffer();
            if ( mess == null ) {
               try {
                  Thread.sleep( 50 );
               } catch ( InterruptedException e ) {
                  e.printStackTrace();
               }
               continue;
            }
            System.out.println( mess );
            if ( SERVER_CMD_DISCONNECT.equals( mess ) ) {
               online.remove( this.nickname );
               broadcast( nickname + " leaving chat" );
               break;
            }
            if ( mess.startsWith( "/w" ) ) {
               String[] direct = mess.split( "\\s+", 3 );
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
