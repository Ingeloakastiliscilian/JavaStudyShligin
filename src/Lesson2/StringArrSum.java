package Lesson2;

public class StringArrSum {

  static void getStringArrSum(String[][] strMass) throws MyArrayDataException{
    long sum =0;

  }

  private void check(String[][] strArr) throws MyArraySizeException{
    int x = strArr.length;
    if ( x > 0 ){
      int y = strArr[0].length;
      if (x != 4 && y !=4)
        throw new MyArraySizeException();
    }
  }

  private int toNumber(String s){
    if (s.length()<1)
      throw new MyArrayDataException();
    boolean isNeg =false;
    boolean isDouble = false;
    if (s.charAt(0) == '-') {
      isNeg = true;
      s = s.substring(1);
    }
    if ( s.indexOf('.') != -1)
      isDouble = true;
    int num = StringArrSum.toInt(s, isNeg);
  }

  private static int toInt (String s, boolean isNegative) {
    int num = 0;
    int digit;
    for (int i = 0; i < s.length(); i++) {
      digit = s.charAt(i) - '0';
      if (digit > 9 || digit < 0)
        throw new MyArrayDataException();
      num += (digit*Math.pow(10, i));
    }
    if (isNegative)
      return -1*num;
    return num;
  }

  private static double toDouble(){

  }

}
