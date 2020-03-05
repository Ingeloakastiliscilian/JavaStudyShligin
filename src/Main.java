import Lesson4.ChatMainWindow;

import javax.swing.*;

public class Main {
    static ChatMainWindow chat;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatMainWindow::new);
    }
}
