package Java2.chat.client;

import Java2.chat.server.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Chat extends JFrame {

   private JTextField message;
   private JTextArea chat;
   private ClientHandler handler;

   Thread refresher = new Thread( ()-> {
      while ( handler.isAlive() ) {
         getMessage();
         try {
            Thread.sleep( ChatServer.refreshPeriod() );
         } catch ( InterruptedException e ) {
            e.printStackTrace();
         }
      }
   });

   Chat( String name , ClientHandler handler) {
      setTitle( name );
      buildGUI();
      this.handler = handler;
      refresher.start();
   }

   public void getMessage() {
      String income = handler.readBuffer();
      if ( !income.isEmpty() )
         chat.append( income );
   }

   public void sendMessage( String mess ) {
      if ( !mess.isEmpty() )
         handler.pushBuffer( mess + "\n");
      message.setText( "" );
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
      chatPane.setAutoscrolls( true );
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
            handler.stop();
         }
      } );
   }

}
