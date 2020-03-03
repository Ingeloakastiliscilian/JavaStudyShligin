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
    int num = (int) stringToNumber(s, false);
    if (isNegative)
      return -1*num;
    return num;
  }

  private static double toDouble(String s, boolean isNegative){
    double intPart, fractPart;
    int term = s.indexOf('.');
    intPart = stringToNumber(s.substring(0, term), false);
    fractPart = stringToNumber(s.substring(term+1), true);
    if (isNegative)
      return -1*(intPart + fractPart);
    return intPart + fractPart;
  }

  private static double stringToNumber (String s , boolean fraction){
    double num = 0;
    int iStart = (!fraction) ? 0 : s.length();
    int iFinish = (!fraction) ? s.length() : 0;
    double multiplier = (!fraction) ? 10 : 0.1;
    int step = (!fraction) ? 1 : -1;
    int digit;
    for (int i = iStart; i < iFinish; i+=step) {
      digit = s.charAt(i) - '0';
      if (digit > 9 || digit < 0) {
        throw new MyArrayDataException();
      }
//      Time to think. Not universal code fragment
      if (fraction)
        num *= multiplier;
      num += digit;
      if (fraction)
        num *= multiplier;
//
    }
    return num;
  }
}
