package Lesson2;

public class StringArrSum {

  public static int getIntSum(String[][] strArr) {
    return (int) getSum(strArr);
  }

  private static double getSum(String[][] strArr) throws MyArraySizeException, MyArrayDataException {
    checkValidSize(strArr);
    double sum = 0;
    for (int y = 0; y <4; y++) {
      for (int x = 0; x <4; x++) {
        try {
          sum += toNumber(strArr[y][x]);
        }
        catch (MyArrayDataException e){
          throw new MyArrayDataException(y,x);
        }
      }
    }
    return sum;
  }

  private static void checkValidSize(String[][] strArr)/* throws MyArraySizeException*/{
    if (strArr == null || strArr.length != 4 ) {
      throw new MyArraySizeException();
    }
    for ( int i = 0; i < 4; i++ ) {
      if (strArr[i].length !=4)
        throw new MyArraySizeException(i);
      for ( int j = 0 ; j < 4 ; j++ ) {
        if (strArr[i][j].length() == 0)
          throw new MyArrayDataException( i, j);
      }
    }
  }

  private static double toNumber(String s){
    boolean isNegative = false;
    double num;
    if (s.charAt(0) == '-') {
      isNegative = true;
      s = (s.substring(1));
    }
    num = toDouble(s);
    if (isNegative)
      return -1*num;
    return num;
  }

  private static double toDouble(String s){
    double intPart , fractionPart = 0.;
    int term = s.indexOf(".");
    if (term != -1) {
      intPart = stringToNumber(s.substring(0, term), false);
      fractionPart = stringToNumber(s.substring(term +1), true);
    }
    else
      intPart = stringToNumber( s , false);
    return intPart + fractionPart;
  }

  private static double stringToNumber (String s , boolean fraction) /*throws MyArrayDataException*/{
    double num = 0.;
    int iStart = (!fraction) ? 0 : s.length()-1;
    int iFinish = (!fraction) ? s.length() : -1;
    double multiplier = (!fraction) ? 10 : 0.1;
    int step = (!fraction) ? 1 : -1;
    double digit;
    for (int i = iStart; i != iFinish; i+=step) {
      digit = s.charAt(i) - '0';
      if ((digit > 9) || (digit < 0)) {
        throw new MyArrayDataException();
      }
//      Time to think. Not universal code fragment
      num *= multiplier;
      if (fraction)
        digit *= multiplier;
      num += digit;
//
    }
    return num;
  }
}
