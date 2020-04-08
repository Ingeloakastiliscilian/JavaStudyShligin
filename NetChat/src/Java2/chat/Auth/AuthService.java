package Java2.chat.Auth;

import java.net.Socket;

public interface AuthService {

   void start();

   void stop();

   boolean isWorking();

   boolean hasApproved();

   <T> T getSocketID( Socket socket );

   <T> T unplug( Socket socket );

   Socket getApproved();
}
