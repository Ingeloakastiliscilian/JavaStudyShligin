package Java2.chat.Auth;

import java.io.IOException;
import java.net.Socket;

public interface AuthService {

   void start();

   void stop();

   void authenticate( Socket socket ) throws IOException, InterruptedException;

}
