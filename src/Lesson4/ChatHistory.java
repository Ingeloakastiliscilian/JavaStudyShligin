package Lesson4;

import com.intellij.util.containers.ArrayListSet;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.*;

public class ChatHistory implements ListModel {

  private Set<String> nickName;
  private Map <String, ChatLog> chatArchive;

  ChatHistory (){
    nickName = new ArrayListSet<>();
    chatArchive = new HashMap<>();
  }

  Object[] getSubscribers(){
    return nickName.toArray();
  }

  void addSubscriber(String nickname){
    this.nickName.add(nickname);
    this.chatArchive.put(nickname, new ChatLog(nickname));
  }

  Object[] getChat(String nickName){
    return this.chatArchive.get(nickName).getLog();
  }

  void sendMessage(String whom, String mess){
    chatArchive.get(whom).newMessage(mess);
  }

  @Override
  public int getSize() {
    return chatArchive.size();
  }

  @Override
  public Object getElementAt(int index) {
    String[] nickNames = new String[nickName.size()];
    nickName.toArray(nickNames);
    return chatArchive.get(nickNames[index]).toString();
  }

  @Override
  public void addListDataListener(ListDataListener l) {
  }

  @Override
  public void removeListDataListener(ListDataListener l) {
  }
}
