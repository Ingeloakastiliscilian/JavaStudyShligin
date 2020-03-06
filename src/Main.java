import Lesson3.MyPhoneBook;
import Lesson3.StringStat;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        String[] surnames = {
            "Ivanov"
            ,"Petrov"
            ,"Sokolov"
            ,"Ivanov"
            ,"Sidorov"
            ,"Domogarov"
            ,"Ivanov"
            ,"Petrov"
            ,"Kotov"
            ,"Chehov"
            ,"Sidorov"
            ,"Petrov"
            ,"Sokolov"
            ,"Sidorov"
            ,"Raskolnikov"
            ,"Karpov"
            ,"Sharikov"
            ,};

        StringStat.printUniqueWords(surnames);

        MyPhoneBook phb = new MyPhoneBook();

        for ( String surname : surnames ) {
            phb.add( surname , randomPhoneNumber() );
        }

        phb.get("Ivanov");
    }

    static String randomPhoneNumber (){
        Random rand = new Random();
        String number = "+";
        number += rand.nextInt(99);
        number += " (";
        for (int i = 0; i < 3; i++) {
            number += (rand.nextInt(9));
        }
        number += ") ";
        for (int i = 0; i < 7; i++) {
            number += (rand.nextInt(9));
        }
        return number;
    }

}
