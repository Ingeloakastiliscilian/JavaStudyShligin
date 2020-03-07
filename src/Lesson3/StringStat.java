package Lesson3;

import java.util.ArrayList;
import java.util.LinkedList;

public class StringStat {
  static ArrayList<String> uniqueWords;
  static LinkedList<Integer> count;

  static public void printUniqueWords(String[] strings){
    uniqueWords = new ArrayList<>();
    count = new LinkedList<>();
    getStatistic( strings );
    printStatistic();
  }

  private static void getStatistic(String[] strings){
    if (strings == null || strings.length == 0){
      System.out.println("Строковый массив пуст!");
      return;
    }
    int index;
    for ( String string : strings ){
      index = uniqueWords.indexOf(string);
      if (index == -1){
        uniqueWords.add(string);
        count.add(1);
      }
      else {
        count.set(index, count.get(index) + 1);
      }
    }
  }

  private static void printStatistic(){
    for ( String string : uniqueWords ){
      System.out.printf("Слово \"%s\". Повторов: %d\n ", string, count.poll());
    }
  }

}
