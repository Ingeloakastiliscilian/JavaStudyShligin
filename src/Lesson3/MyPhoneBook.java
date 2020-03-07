package Lesson3;

import java.util.ArrayList;
import java.util.HashMap;

public class MyPhoneBook {

  private HashMap<String, ArrayList<String>> phoneBook;

  public MyPhoneBook() {
    phoneBook = new HashMap<>();
  }

  public void add(String name, String phoneNumber) {
    if (name == null ) {
      System.out.println("Невозможно добавить пустое имя в книгу");
      return;
    }
    if (phoneNumber == null) {
      System.out.println("Номер телефона не может быть пустым");
    }
    if (!phoneBook.containsKey(name)) {
      phoneBook.put(name, new ArrayList<>());
    }
    phoneBook.get(name).add(phoneNumber);
  }

  private String[] getPhones(String name) {
    if (phoneBook.containsKey(name)){
      return phoneBook.get(name).toArray(String[]::new);
    }
    return null;
  }

  public void get(String name) {
    String[] phones = this.getPhones(name);
    if (phones == null){
      System.out.println("Нет такой записи!");
      return;
    }
//    System.out.printf("%s pones : \n", name);
    for (String s : phones ) {
        System.out.printf("%12s > %s\n", name, s);
        name = name.replaceAll(name,"");
    }
  }


}
