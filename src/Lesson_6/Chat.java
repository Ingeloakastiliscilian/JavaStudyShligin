package Lesson_6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Chat extends JFrame{

    static final String END_CONNECTION = "/disconnection";

    JButton sendButton;
    JTextField message;
    JTextArea chat;
    DataOutputStream outputStream;
    DataInputStream inputStream;

    Chat (String name ){
        setTitle(name);
        buildGUI();
    }

    void buildGUI (){
        JPanel mainPanel = new JPanel(new BorderLayout());
        chat = new JTextArea();
        chat.setRows(20);
        chat.setLineWrap(true);
        chat.setEditable(false);
        message = new JTextField();
        message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed (KeyEvent e) {
                if ( e.getKeyCode() == '\n')
                    sendMessage();
            }
        }) ;
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        JPanel messagePanel = new JPanel(new BorderLayout());
        JScrollPane chatPane = new JScrollPane(chat);
        messagePanel.add(sendButton , BorderLayout.LINE_END);
        messagePanel.add(message , BorderLayout.CENTER);
        mainPanel.add(messagePanel, BorderLayout.PAGE_END);
        mainPanel.add(chatPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    outputStream.writeUTF(END_CONNECTION);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    void sendMessage() {
        String mess = message.getText().trim();
        if (mess.isEmpty())
            return;
        try {
            outputStream.writeUTF(mess);
            chat.append("Me : " + mess + "\n");
            message.setText("");
        } catch (IOException e) {
            chat.append("Что-то пошло не так при отправке сообщения!\n");
            e.printStackTrace();
        }
    }

    boolean getMessage (String fromWho){
        try {
            String text = fromWho + " : " + inputStream.readUTF();
            if (text.equals(END_CONNECTION)) {
                return false;
            }
            chat.append(text + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
