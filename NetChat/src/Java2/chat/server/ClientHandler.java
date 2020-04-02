package Java2.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class ClientHandler {

   private Socket socket;
   private DataOutputStream out;
   private DataInputStream in;
   private volatile boolean alive;

   private volatile LinkedList<String> inBuffer, outBuffer;

   private Thread messageReader = new Thread( () -> {
      while ( alive ) {
         receive();
         try {
//            if ( in.available() > 0 )
            Thread.sleep( 100 );
         } catch ( /*IOException |*/ InterruptedException e ) {
            System.out.println( "message reader error" );
         }
      }
   } );

   private Thread messageWriter = new Thread( () -> {
      while ( alive ) {
         send();
         try {
            Thread.sleep( 100 );
         } catch ( InterruptedException e ) {
            e.printStackTrace();
         }
      }
   } );

   public ClientHandler( Socket socket ) {
      this.socket = socket;
      try {
         in = new DataInputStream( socket.getInputStream() );
         out = new DataOutputStream( socket.getOutputStream() );
      } catch ( IOException e ) {
         System.out.println( "client handler socket create error" );
      }
      messageReader.start();
      messageWriter.start();
      alive = true;
      inBuffer = new LinkedList<>();
      outBuffer = new LinkedList<>();
      System.out.println( "new handler created" );
   }

   public void stop() {
      alive = false;
   }

   private void receive() {
      try {
         String mess = in.readUTF();
//         if ( mess.length() == 0 )
//            return;
         if ( ChatServer.DISCONNECT.equals( mess ) ) {
            mess = "Server was disconnected";
            this.stop();
            socket.close();
         }
         this.inBuffer.add( mess );
      } catch ( IOException e ) {
         try {
            in.close();
         } catch ( IOException ex ) {
            System.out.println( "can't close socket" );
         }
         System.out.println( "client handler receive error" );
      }
   }

   private void send() {
      if ( outBuffer.isEmpty() )
         return;
      try {
         out.writeUTF( outBuffer.poll() );
      } catch ( IOException e ) {
         System.out.println( "client handler send error" );
      }
   }

   public void pushBuffer( String mess ) {
      mess = mess.trim();
      if ( mess.length() > 0 )
         outBuffer.add( mess );
   }

   public String readBuffer() {
      if ( this.inBuffer.isEmpty() )
         return null;
      String mess = this.inBuffer.poll();
      if ( ChatServer.DISCONNECT.equals( mess ) )
         this.stop();
      return mess;
   }

   public Socket getSocket() {
      return socket;
   }

}
