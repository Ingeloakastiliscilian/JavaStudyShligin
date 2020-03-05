package Lesson2;

public class MyArraySizeException extends ArrayIndexOutOfBoundsException {

  private String message;

  MyArraySizeException(){
    message = "Количество строк должно быть 4.";
//    this.printStackTrace();
  }

  MyArraySizeException(int i){
    message = "Строка " + (i+1) + " содержит неверное число элементов, должно быть 4.";
//    this.printStackTrace();
  }

  public void printExceptionMessage (){
    System.out.println(message);
  }
}
