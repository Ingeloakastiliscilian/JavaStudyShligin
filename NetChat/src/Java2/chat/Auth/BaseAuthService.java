package Java2.chat.Auth;

import Java2.chat.server.ChatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.util.LinkedList;
import java.util.List;

public class BaseAuthService implements AuthService {

   private static final String DEFAULT_NICKNAME = "Guest";

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

   private static final int AUTH_NOT_PASSED = -1;
   private static final int AUTH_FORM_ERROR = -2;
   private static final int AUTH_TIMEOUT_REACHED = -3;
   private static final long MAX_AUTH_TIMEOUT = 100000;

   private static final String AUTH_COMMAND = "/auth";

   private ServerSocket serverSocket;
   private boolean isRunning;
   private volatile LinkedList<Socket> authQueue;
   private volatile LinkedList<Socket> approved;

   private volatile SocketOption<String> nickname = new SocketOption<>() {
      @Override
      public String name() {
         return "nickname";
      }

      @Override
      public Class<String> type() {
         return String.class;
      }
   };

   private Thread authQueueService = new Thread( () -> {
      while ( isRunning ) {
         try {
            Socket clientSocket = serverSocket.accept();
            clientSocket.setOption( nickname, DEFAULT_NICKNAME );
            authQueue.add( clientSocket );
         } catch ( IOException e ) {
            System.out.println( "Queue service error" );
         }
      }
   } );

   private Thread authenticator = new Thread( () -> {
      while ( isRunning ) {
         while ( !authQueue.isEmpty() ) {
            try {
               authenticate( authQueue.poll() );
            } catch ( IOException | InterruptedException e ) {
               e.printStackTrace();
            }
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
      isRunning = true;
      authQueueService.start();
      authenticator.start();
   }

   @Override
   public void stop() {
      isRunning = false;
   }

   @Override
   public void authenticate( Socket socket ) throws IOException, InterruptedException {
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
         authState = authClient( authMessage, authTime );
         if ( authState >= 0 ) {
            socket.setOption( nickname, USER_DATA_LIST.get( authState ).username );
            authMessage = "Welcome " + nickname + "!";
            stopAuth = true;
         } else
            switch ( authState ) {
               case AUTH_NOT_PASSED:
                  authMessage = "Incorrect login/pass";
                  break;
               case AUTH_FORM_ERROR:
                  authMessage = "Incorrect auth message";
                  break;
               case AUTH_TIMEOUT_REACHED:
                  authMessage = "Authentication timeout";
                  stopAuth = true;
                  break;
               default:
                  authMessage = "Unknown auth result";
            }
         outputStream.writeUTF( authMessage );
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
            return USER_DATA_LIST.indexOf( ud );
      return AUTH_NOT_PASSED;
   }

   public String getSocketNickname(Socket socket) throws IOException {
      return socket.getOption( nickname );
   }

   public boolean hasApproved() {
      return !approved.isEmpty();
   }

   public Socket getApproved() {
      return approved.poll();
   }

}
