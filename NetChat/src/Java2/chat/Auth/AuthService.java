package Java2.chat.Auth;

import Java2.chat.server.ClientHandler;

import java.net.ServerSocket;

public interface AuthService {

    void start();
    void stop();

    void authenticate ( ClientHandler clientHandler );

    ServerSocket getServerSocket();

}
