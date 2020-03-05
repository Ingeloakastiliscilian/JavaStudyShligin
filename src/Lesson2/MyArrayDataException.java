package Lesson2;

public class MyArrayDataException extends ArrayStoreException {

  private String message;

  MyArrayDataException(){
    message = "Массив должен содержать только числа и не должно быть пустых ячеек!";
  }

  MyArrayDataException(int y , int x){
    message = "Ошибка преобразоания в число. Ячейка пуста или не содержит числового значения." +
        " Строка " + (y+1) + ", столбец " + (x+1) + ".";
//    this.printStackTrace();
  }

  public void printExceptionMessage (){
    System.out.println(message);
  }
}
