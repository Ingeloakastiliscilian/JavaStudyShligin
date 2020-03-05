package Lesson4;

import java.util.LinkedList;

class ChatLog {

  private LinkedList<String> log;
  private int capacity;


  ChatLog(String recipient){
    log = new LinkedList<>();
    log.addLast("New chat with " + recipient);
    capacity = 20;
  }

  Object[] getLog() {
    return this.log.toArray();
  }

  void newMessage(String mes){
    if (mes == null)
      return;
    mes = mes.trim();
    if (mes.length() == 0 )
      return;
    log.addLast( "Me" + ":\t" + mes);
    if (log.size() >capacity)
      log.removeFirst();
  }

}
