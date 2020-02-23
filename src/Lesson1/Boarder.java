package Lesson1;

public class Boarder implements Obstruction {
    double height;

    public Boarder() {
        height = 0.3;
    }

    public Boarder(double height) {
        this.height = height;
    }


    @Override
    public void overcome(Runner rn) {
       rn.jump(height);
    }
}
