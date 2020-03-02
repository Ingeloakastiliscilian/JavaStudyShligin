package Lesson2;

public class MyArraySizeException extends ArrayIndexOutOfBoundsException {

  MyArraySizeException(){
    super("Размер массива должен быть 4х4!");
  }
}
