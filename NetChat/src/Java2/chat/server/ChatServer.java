package Java2.chat.server;

import Java2.chat.Auth.BaseAuthService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ChatServer {

   public final static String SERVER_CMD_DISCONNECT = "/exit";
   public static final String SERVER_CMD_CLOSE_CONNECTION = "/close";
   public static final String SERVER_CMD_PRIVATE_MSG = "/w ";

   private final static long DEFAULT_REFRESH_PERIOD = 100;

   private final int port;
   private ServerSocket chatServerSocket;
   private static long refreshPeriod;

   private BaseAuthService authService;

   private Map<String, Socket> online;
   private volatile LinkedList<String> messageQueue;


   public ChatServer( int port ) {
      this.port = port;
   }

   public static long refreshPeriod() {
      return refreshPeriod;
   }

   public void start() {
      try {
         chatServerSocket = new ServerSocket( port );
         online = new HashMap<>();
         authService = new BaseAuthService( chatServerSocket );
         refreshPeriod = DEFAULT_REFRESH_PERIOD;
         authService.start();
         connectionService.start();
      } catch ( IOException e ) {
         System.out.println( "server start error" );
      }
   }

   public void stop() throws InterruptedException {
      authService.stop();
      this.messageQueue.add( SERVER_CMD_CLOSE_CONNECTION );
      broadcastingService.run();
      broadcastingService.join();
      try {
         chatServerSocket.close();
      } catch ( IOException e ) {
         System.out.println( "stop server error" );
      }
   }

   private void addOnline() {
      Socket clientSocket = authService.getApproved();
      String nickname = authService.getSocketID( clientSocket );
      online.put( nickname, clientSocket );
      if ( clientListeningService.getState() == Thread.State.NEW )
         clientListeningService.start();
      pushBroadcast( "Server", nickname + " joined to chat" );
      System.out.println( broadcastingService.getState() );
      broadcastingService.run();
      System.out.println( broadcastingService.getState() );
      System.out.println( " User logged in: " + nickname );
   }

   private void pushPrivate( String nickname, String direct ) {
      String[] temp = direct.split( "\\s+", 3 );
      StringBuilder message = new StringBuilder( SERVER_CMD_PRIVATE_MSG );
      if ( temp.length != 3 )
         message.append( temp[2] ).append( " Server: " ).append( "Invalid private message form" );
      else if ( online.containsKey( temp[1] ) )
         message.append( temp[1] ).append( " From " ).append( nickname ).append( " (private): " ).append( temp[2] );
      else
         message.append( nickname ).append( " Server: " ).append( "User offline or not exist" );
      this.messageQueue.add( message.toString() );
      System.out.println( "New private message: " + message.toString() );
   }

   private void pushBroadcast( String nickname, String mess ) {
      this.messageQueue.add( nickname + ": " + mess );
      System.out.println( "New broadcast message from " + nickname + ": " + mess );
   }

   private void broadcast( String mess ) throws IOException {
      if ( mess.startsWith( SERVER_CMD_PRIVATE_MSG ) ) {
         System.out.println( "Private message send" );
         sendPrivate( mess );
      } else {
         DataOutputStream out;
         System.out.println( "Broadkast message send" );
         for ( String nick: online.keySet() ) {
            out = new DataOutputStream( online.get( nick ).getOutputStream() );
            out.writeUTF( mess + "\n" );
         }
      }
   }

   private void sendPrivate( String mess ) throws IOException {
      String[] messParts = mess.split( "\\s+", 3 );
      String recipient = messParts[1];
      if ( online.containsKey( recipient ) ) {
         DataOutputStream out = new DataOutputStream( online.get( recipient ).getOutputStream() );
         out.writeUTF( messParts[2] + "\n" );
      }
   }

   private void disconnectSocket( Socket socket ) {
      String nickname = authService.unplug( socket );
      online.remove( nickname );
      try {
         socket.close();
      } catch ( IOException e ) {
         System.out.println( "Can't close socket" );
      }
      this.messageQueue.add( nickname + " leaved chat" );
   }

   private Thread broadcastingService = new Thread( () -> {
      System.out.println( "Broadcast service start" );
      try {
         while ( !messageQueue.isEmpty() ) {broadcast( this.messageQueue.pop() );}
         Thread.sleep( ChatServer.refreshPeriod() );
      } catch ( InterruptedException | IOException e ) {
         e.printStackTrace();
         System.out.println( "Error while broadcasting" );
      }
      System.out.println( "Broadcast service stop" );
   } );


   private Thread clientListeningService = new Thread( () -> {
      System.out.println( "Client listener service start" );
      this.messageQueue = new LinkedList<>();
      while ( !online.isEmpty() ) {
         String income;
         Socket socket;
         DataInputStream inputStream;
         for ( String nickname: online.keySet() ) {
            socket = online.get( nickname );
            try {
               inputStream = new DataInputStream( socket.getInputStream() );
               if ( socket.getInputStream().available() == 0 )
                  continue;
               income = inputStream.readUTF();
               System.out.println( income );
               if ( income.startsWith( SERVER_CMD_PRIVATE_MSG ) ) {
                  pushPrivate( nickname, income );
               } else if ( income.startsWith( SERVER_CMD_DISCONNECT ) ) {disconnectSocket( socket );} else {
                  pushBroadcast( nickname, income );
               }
               broadcastingService.run();
            } catch ( IOException e ) {
               e.printStackTrace();
            }
         }
      }
      System.out.println( "Client listener service stop" );
   } );


   private Thread connectionService = new Thread( () -> {
      System.out.println( "Connection service start" );
      while ( authService.isWorking() ) {
         if ( authService.hasApproved() ) {
            addOnline();
            if ( clientListeningService.getState() == Thread.State.NEW ) {clientListeningService.start();}
         }
         try {
            Thread.sleep( ChatServer.refreshPeriod() );
         } catch ( InterruptedException e ) {
            e.printStackTrace();
         }
      }
      System.out.println( "Connection service stop" );
   } );


}



