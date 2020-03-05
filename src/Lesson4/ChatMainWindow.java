package Lesson4;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatMainWindow extends JFrame {
  private JList<Object> subscribers;
  private JTextField textField1;
  private JButton button1;
  private JPanel mainPanel;
  private JList<Object> chatList;
  private String currentSubscriber;
  private ChatHistory chatHistory;

  public ChatMainWindow() {

    chatHistory = new ChatHistory();

    chatHistory.addSubscriber("Alex");
    chatHistory.addSubscriber("Nagibator999");
    chatHistory.addSubscriber("Boom");
    chatHistory.addSubscriber("CJIOH");
    chatHistory.addSubscriber("PAK");
    chatHistory.addSubscriber("abstbaeqg142");

    subscribers.setListData(chatHistory.getSubscribers());

    setSize(500, 500);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setContentPane(mainPanel);
    setVisible(true);

    button1.addActionListener(e -> {
      chatHistory.sendMessage(subscribers.getSelectedValue().toString(), textField1.getText());
      chatList.setListData(chatHistory.getChat(currentSubscriber));
      textField1.setText(null);
    });

    subscribers.addListSelectionListener(e -> {
      currentSubscriber = subscribers.getSelectedValue().toString();
      chatList.setListData(chatHistory.getChat(currentSubscriber));
    });


    textField1.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == '\n') {
          chatHistory.sendMessage(subscribers.getSelectedValue().toString(), textField1.getText());
          chatList.setListData(chatHistory.getChat(currentSubscriber));
          textField1.setText(null);
        }
      }
    });
  }
}
