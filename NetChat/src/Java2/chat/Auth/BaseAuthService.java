package Java2.chat.Auth;

import Java2.chat.server.ChatServer;
import Java2.chat.server.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class BaseAuthService implements AuthService {

   private static class UserData {
      String login;
      String password;
      String username;

      public UserData( String login, String password, String username ) {
         this.login = login;
         this.password = password;
         this.username = username;
      }

      @Override
      public int hashCode() {
         return super.hashCode();
      }

      @Override
      public boolean equals( Object o ) {
         if ( this == o ) return true;
         if ( o == null || getClass() != o.getClass() ) return false;
         UserData userData = ( UserData ) o;
         return login.equals( userData.login ) &&
              password.equals( userData.password ) &&
              username.equals( userData.username );
      }
   }

   private final ChatServer chatServer;
   private final ServerSocket serverSocket;
   private boolean isRunning;

   private static final List<UserData> USER_DATA_LIST = List.of(
        new UserData( "login1", "pass1", "user1" ),
        new UserData( "login2", "pass2", "user2" ),
        new UserData( "login3", "pass3", "user3" )
   );

   public BaseAuthService( ChatServer chatServer ) {
      this.chatServer = chatServer;
      this.serverSocket = chatServer.getServerSocket();
      System.out.println( serverSocket.isClosed() );
   }

   @Override
   public ServerSocket getServerSocket() {
      return this.chatServer.getServerSocket();
   }

   @Override
   public void start() {
      isRunning = true;
      new Thread( () -> {
         while ( isRunning ) {
            try {
               Socket clientSocket = chatServer.getServerSocket().accept();
               ClientHandler clientHandler = new ClientHandler( clientSocket );
               authenticate( clientHandler );
            } catch ( IOException e ) {
//               e.printStackTrace();
               System.out.println( "error on start auth service" );
            }
         }
         System.out.println( "auth service stop" );
      } ).start();
   }

   @Override
   public void stop() {
      isRunning = false;
   }

   @Override
   public void authenticate( ClientHandler clientHandler ) {
      new Thread( () -> {
         boolean correct = false;
         System.out.println( "auth thread start" + clientHandler.getSocket().toString() );
         while ( !correct ) {
            String mess = clientHandler.readBuffer();
            if ( mess == null ) {
               try {
                  Thread.sleep( 50 );
               } catch ( InterruptedException e ) {
                  e.printStackTrace();
               }
               continue;
            }
            if ( mess.startsWith( "/auth" ) )
               correct = authClient( clientHandler, mess );
            else
               clientHandler.pushBuffer( "incorrect auth message" );
         }
         System.out.println( "auth thread stop" );
      } ).start();
   }

   private boolean authClient( ClientHandler clientHandler, String mess ) {
      System.out.println( "auth in process" );
      String[] authInfo = mess.split( "\\s+", 3 );
      for ( UserData ud: USER_DATA_LIST ) {
         if ( ( ud.login.equals( authInfo[1] ) ) && ( ud.password.equals( authInfo[2] ) ) ) {
            chatServer.addOnline( ud.username, clientHandler );
            return true;
         }
      }
         clientHandler.pushBuffer( "incorrect login/password" );
      return false;
   }
}
