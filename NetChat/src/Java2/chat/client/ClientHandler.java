package Java2.chat.client;

import Java2.chat.server.ChatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class ClientHandler {

   private Socket socket;
   private DataOutputStream out;
   private DataInputStream in;
   private boolean alive;

   private volatile LinkedList<String> inBuffer, outBuffer;

   private Thread receiver = new Thread( () -> {
      while ( alive ) {
         try {
            receive();
            Thread.sleep( ChatServer.refreshPeriod() );
         } catch ( InterruptedException e ) {
            System.out.println( "message reader error" );
         }
      }
   } );

   private Thread sender = new Thread( () -> {
      while ( alive ) {
         try {
            send();
            Thread.sleep( ChatServer.refreshPeriod() );
         } catch ( InterruptedException e ) {
            e.printStackTrace();
         }
      }
   } );

   ClientHandler( Socket socket ) {
      this.socket = socket;
      alive = false;
      inBuffer = new LinkedList<>();
      outBuffer = new LinkedList<>();
      try {
         in = new DataInputStream( socket.getInputStream() );
         out = new DataOutputStream( socket.getOutputStream() );
         alive = true;
         sender.start();
         receiver.start();
         System.out.println( "new handler created" );
      } catch ( IOException e ) {
         System.out.println( "client handler socket create error" );
      }
   }

   public void stop() {
      alive = false;
   }

   private void receive() {
      try {
         String mess = in.readUTF();
         if ( ChatServer.SERVER_CMD_DISCONNECT.equals( mess ) ) {
            mess = "Server was disconnected";
            this.stop();
         }
         this.inBuffer.add( mess );
      } catch ( IOException e ) {
         System.out.println( "client handler receive error" );
      }
   }

   private void send() {
      while ( !outBuffer.isEmpty() ) {
         try {
            out.writeUTF( outBuffer.poll() );
         } catch ( IOException e ) {
            System.out.println( "client handler send error" );
         }
      }
   }

   public void pushBuffer( String mess ) {
      mess = mess.trim();
      if ( mess.length() > 0 )
         outBuffer.add( mess );
   }

   public String readBuffer() {
      if ( inBuffer.isEmpty() )
         return "";
      if ( inBuffer.size() == 1 )
         return this.inBuffer.poll();
      StringBuilder temp = new StringBuilder();
      while ( inBuffer.size() > 0 )
         temp.append( this.inBuffer.poll() );
      return temp.toString();
   }

   public boolean isAlive() {
      return alive;
   }

}
