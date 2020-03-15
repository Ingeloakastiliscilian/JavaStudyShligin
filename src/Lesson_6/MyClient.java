package Lesson_6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyClient {

    Socket client;
    Chat clientChat;

    public MyClient( int port ) {
        try {
            client = new Socket("localhost", port);
            clientChat = new Chat("Client");
            clientChat.inputStream = new DataInputStream(client.getInputStream());
            clientChat.outputStream = new DataOutputStream(client.getOutputStream());
            System.out.println("Создан новый клиент");
        } catch (
                IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось подключится к серверу");
            System.exit(100);
        }
        do{}
        while (clientChat.getMessage("Server"));
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MyClient(8888 );
    }

}
