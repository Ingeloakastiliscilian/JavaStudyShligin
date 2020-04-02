package Java2.chat.client;

import Java2.chat.server.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Chat extends JFrame {

   private JTextField message;
   private JTextArea chat;
   private DataOutputStream outputStream;
   private DataInputStream inputStream;

   private Thread receiver = new Thread( () -> {
      while ( true ) {
         try {
            String mess = inputStream.readUTF();
            if ( mess == null )
               return;
            if ( ChatServer.DISCONNECT.equals( mess ) ) {
               addMessage( "Server was disconnected" );
               outputStream.close();
               inputStream.close();
               break;
            }
            addMessage( mess );
         } catch ( Exception e ) {
            System.out.println( "error reading input stream" );
         }
      }
      System.out.println( "receiver stop" );
   } );

   private void addMessage( String mess ) {
      chat.append( mess + "\n" );
   }

   Chat( String name ) {
      setTitle( name );
      buildGUI();
   }

   public void setOutputStream( DataOutputStream outputStream ) {
      this.outputStream = outputStream;
   }

   public void setInputStream( DataInputStream inputStream ) {
      this.inputStream = inputStream;
      receiver.start();
   }

   private void sendMessage( String mess ) {
      if ( mess.isEmpty() )
         return;
      try {
         outputStream.writeUTF( mess );
         if ( ChatServer.DISCONNECT.equals( mess ) ) {
            outputStream.close();
            inputStream.close();
            return;
         }
         message.setText( "" );
      } catch ( IOException e ) {
         System.out.println( "Error client send message" );
      }
   }

   private void buildGUI() {
      JPanel mainPanel = new JPanel( new BorderLayout() );
      chat = new JTextArea();
      chat.setRows( 20 );
      chat.setLineWrap( true );
      chat.setEditable( false );
      message = new JTextField();
      message.addKeyListener( new KeyAdapter() {
         @Override
         public void keyPressed( KeyEvent e ) {
            if ( e.getKeyCode() == '\n' )
               sendMessage( message.getText().trim() );
         }
      } );
      JButton sendButton = new JButton( "Send" );
      sendButton.addActionListener( e -> sendMessage( message.getText().trim() ) );
      JPanel messagePanel = new JPanel( new BorderLayout() );
      JScrollPane chatPane = new JScrollPane( chat );
      messagePanel.add( sendButton, BorderLayout.LINE_END );
      messagePanel.add( message, BorderLayout.CENTER );
      mainPanel.add( messagePanel, BorderLayout.PAGE_END );
      mainPanel.add( chatPane, BorderLayout.CENTER );
      setContentPane( mainPanel );
      setSize( 600, 400 );
      setDefaultCloseOperation( EXIT_ON_CLOSE );
      setVisible( true );
      addWindowListener( new WindowAdapter() {
         @Override
         public void windowClosing( WindowEvent e ) {
            sendMessage( ChatServer.DISCONNECT );
         }
      } );
   }

}
