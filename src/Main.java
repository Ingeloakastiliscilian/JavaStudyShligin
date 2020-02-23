import Lesson1.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Human h1 = new Human("Vasya");
        Human h2 = new Human("Yorik", 150,1.5);
        Cat c1 = new Cat("Murzik");
        Cat c2 = new Cat("Pushok", 180, 1.8);
        Robot r1 = new Robot("T-800");
        Robot r2 = new Robot("T-1000", 5000, 25);

        ArrayList<Runner> rl = new ArrayList<>();
        rl.add(h1);
        rl.add(h2);
        rl.add(c1);
        rl.add(c2);
        rl.add(r1);
        rl.add(r2);

        ArrayList<Obstruction> obstruction = new ArrayList<>();
        obstruction.add(new Track(100));
        obstruction.add(new Boarder());
        obstruction.add(new Track(50));
        obstruction.add(new Boarder(0.75));
        obstruction.add(new Track(220));
        obstruction.add(new Track());
        obstruction.add(new Boarder(1.8));

        for (Obstruction obs : obstruction ) {
            for (Runner rn : rl ) {
                obs.overcome(rn);
            }
        }
    }
}
