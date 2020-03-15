package Lesson_6;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer extends JFrame {

    ServerSocket server;
    Socket client;
    Chat serverChat;


    public MyServer( int port) {
        try {
            server = new ServerSocket( port );
            System.out.println("Ожидание клиента");
            client = server.accept();
            System.out.println("Клиент подключился");
            serverChat = new Chat("Server");
            serverChat.inputStream = new DataInputStream(client.getInputStream());
            serverChat.outputStream = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось создать сервер");
            System.exit(100);
        }
        do{}
        while (serverChat.getMessage("Client"));
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MyServer(8888);
    }

}
