import Lesson1.*;
import Lesson_6.MyClient;
import Lesson_6.MyServer;

import javax.swing.*;
import javax.swing.text.Utilities;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Thread server = new Thread(() -> new MyServer(8888));
        Thread client = new Thread(() -> new MyClient(8888));
        server.start();
        client.start();
    }
}
