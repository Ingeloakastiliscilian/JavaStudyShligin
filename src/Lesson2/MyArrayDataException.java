package Lesson2;

public class MyArrayDataException extends ArrayStoreException {

  MyArrayDataException(){
    super("Массив должен содержать только числа и не должно быть пустых ячеек!");
  }
}
