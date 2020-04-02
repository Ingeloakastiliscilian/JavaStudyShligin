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
   private boolean alive;

   private volatile LinkedList<String> inBuffer, outBuffer;

   Thread receiver = new Thread( () -> {
      while ( alive ) {
         try {
            if ( in.available() > 0 )
               receive();
            Thread.sleep( 100 );
         } catch ( IOException | InterruptedException e ) {
            System.out.println( "message reader error" );
         }
      }
   } );
   Thread sender = new Thread( () -> {
      while ( alive ) {
         try {
            send();
            Thread.sleep( 100 );
         } catch ( InterruptedException e ) {
            e.printStackTrace();
         }
      }
   } );

   public ClientHandler( Socket socket ) {

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
         if ( ChatServer.DISCONNECT.equals( mess ) ) {
            mess = "Server was disconnected";
            this.stop();
         }
         this.inBuffer.add( mess );
      } catch ( IOException e ) {
         System.out.println( "client handler receive error" );
      }
   }

   private void send() {
      try {
//         System.out.println(outBuffer);
         while ( !outBuffer.isEmpty() ) {
            String mess = outBuffer.poll();
            System.out.println("Send: " + mess);
            out.writeUTF( mess );
         }
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
      if ( !inBuffer.isEmpty() ) {
         String mess = this.inBuffer.poll();
         if ( ChatServer.DISCONNECT.equals( mess ) )
            this.stop();
         return mess;
      }
      return null ;
   }

   public Socket getSocket() {
      return socket;
   }

   public boolean isAlive(){
      return alive;
   }

}
