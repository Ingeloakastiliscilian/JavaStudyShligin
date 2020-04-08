package Java2.chat.Auth;

import Java2.chat.server.ChatServer;

import javax.print.DocFlavor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class BaseAuthService implements AuthService {

   private static final String DEFAULT_NICKNAME = "Guest";

   private static class UserData {

      String login;

      String password;
      String username;
      Socket socket;

      public UserData( String login, String password, String username ) {
         this.login = login;
         this.password = password;
         this.username = username;
         this.socket = null;
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

   private static final int AUTH_NOT_PASSED = -1;
   private static final int AUTH_FORM_ERROR = -2;
   private static final int AUTH_TIMEOUT_REACHED = -3;
   private static final int AUTH_LOGGED_IN = -4;
   private static final long MAX_AUTH_TIMEOUT = 500*1000;
   private static final String AUTH_COMMAND = "/auth";
   private ServerSocket serverSocket;

   private boolean isWorking;

   private volatile LinkedList<Socket> authQueue;
   private volatile LinkedList<Socket> approved;
   private volatile Thread authQueueService = new Thread( () -> {
      while ( isWorking ) {
         try {
            Socket clientSocket = serverSocket.accept();
            authQueue.add( clientSocket );
         } catch ( IOException e ) {
            System.out.println( "Queue service error" );
         }
      }
   } );


   private volatile Thread authenticator = new Thread( () -> {
      while ( isWorking ) {
         try {
            while ( !authQueue.isEmpty() )
               authenticate( authQueue.poll() );
         } catch ( IOException | InterruptedException e ) {
            e.printStackTrace();
         }
         try {
            Thread.sleep( ChatServer.refreshPeriod() );
         } catch ( InterruptedException e ) {
            e.printStackTrace();
         }
      }
   } );


   private static final List<UserData> USER_DATA_LIST = List.of(
        new UserData( "login1", "pass1", "user1" ),
        new UserData( "login2", "pass2", "user2" ),
        new UserData( "login3", "pass3", "user3" )
   );


   public BaseAuthService( ServerSocket serverSocket ) {
      this.serverSocket = serverSocket;
      authQueue = new LinkedList<>();
      approved = new LinkedList<>();
   }


   @Override
   public void start() {
      isWorking = true;
      authQueueService.start();
      authenticator.start();
   }


   @Override
   public void stop() {
      isWorking = false;
      try {
         authQueueService.join();
         authenticator.join();
      } catch ( InterruptedException e ) {
         e.printStackTrace();
      }
   }


   @Override
   public String getSocketID( Socket socket ) {
      for ( UserData ud: USER_DATA_LIST )
         if ( ud.socket.equals( socket ) )
            return ud.username;
      return "Guest";
   }


   @Override
   public String unplug( Socket socket ) {
      for ( UserData ud: USER_DATA_LIST )
         if ( ud.socket.equals( socket ) ) {
            ud.socket = null;
            return ud.username;
         }
      return DEFAULT_NICKNAME;
   }


   @Override
   public boolean isWorking() {
      return isWorking;
   }


   @Override
   public boolean hasApproved() {
      return !approved.isEmpty();
   }


   @Override
   public Socket getApproved() {
      return approved.pop();
   }


   private void authenticate( Socket socket ) throws IOException, InterruptedException {
      DataInputStream inputStream = new DataInputStream( socket.getInputStream() );
      DataOutputStream outputStream = new DataOutputStream( socket.getOutputStream() );
      String authMessage;
      long startAuthTime = System.currentTimeMillis();
      long authTime;
      int authState;
      boolean stopAuth = false;
      while ( !stopAuth ) {
         authTime = System.currentTimeMillis() - startAuthTime;
         authMessage = inputStream.readUTF();
         if ( ChatServer.SERVER_CMD_DISCONNECT.equals( authMessage ) )
            break;
         if ( authMessage.length() == 0 )
            if ( authTime < MAX_AUTH_TIMEOUT )
               continue;
         authState = authClient( authMessage, authTime );
         if ( authState >= 0 ) {
            USER_DATA_LIST.get( authState ).socket = socket;
            approved.add( socket );
            authMessage = "Welcome " + USER_DATA_LIST.get( authState ).username + "!";
            stopAuth = true;
         } else
            switch ( authState ) {
               case AUTH_TIMEOUT_REACHED:
                  authMessage = "Authentication timeout";
                  stopAuth = true;
                  break;
               case AUTH_NOT_PASSED:
                  authMessage = "Incorrect login/pass";
                  break;
               case AUTH_FORM_ERROR:
                  authMessage = "Incorrect auth message";
                  break;
               case AUTH_LOGGED_IN:
                  authMessage = "Already logged in";
                  break;
               default:
                  authMessage = "Unknown auth result";
            }
         outputStream.writeUTF( authMessage  + "\n");
         Thread.sleep( ChatServer.refreshPeriod() );
      }
   }


   private int authClient( String mess, long authTime ) {
      if ( authTime > MAX_AUTH_TIMEOUT )
         return AUTH_TIMEOUT_REACHED;
      return authClient( mess );
   }


   private int authClient( String mess ) {
      String[] authInfo = mess.split( "\\s+", 3 );
      if ( ( authInfo.length < 3 ) || !AUTH_COMMAND.equals( authInfo[0] ) )
         return AUTH_FORM_ERROR;
      for ( UserData ud: USER_DATA_LIST )
         if ( ( ud.login.equals( authInfo[1] ) ) && ( ud.password.equals( authInfo[2] ) ) )
            if ( ud.socket != null )
               return AUTH_LOGGED_IN;
            else return USER_DATA_LIST.indexOf( ud );
      return AUTH_NOT_PASSED;
   }

}
