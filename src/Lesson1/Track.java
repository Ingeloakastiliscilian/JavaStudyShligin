package Lesson1;

public class Track implements Obstruction{

    int length;

    public Track (){
        length = 100;
    }

    public Track (int length){
        this.length = length;
    }

    @Override
    public void overcome(Runner rn) {
        rn.run(length);
    }
}
