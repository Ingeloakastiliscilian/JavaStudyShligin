import Lesson2.MyArrayDataException;
import Lesson2.MyArraySizeException;
import Lesson2.StringArrSum;

public class Main {

    public static void main(String[] args) {
        String[][] s = {
            {"10.0" , "10", "10", "10"}
            ,{"10" , "10.15", "10.85", "0"}
            ,{"10" , "10", "30", "10"}
            ,{"10" , "10", "0", "10.5"}
        };

        try {
            System.out.println(StringArrSum.getIntSum(s));
        }
        catch (MyArraySizeException e){
            e.printExceptionMessage();
        }
        catch (MyArrayDataException e){
            e.printExceptionMessage();
        }
    }
}
