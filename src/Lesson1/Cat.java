package Lesson1;

public class Cat implements Runner{
    private int sprintDistance;
    private double jumpHeight;
    private String name;
    private boolean onDistance;

    public Cat (String name){
        this.name = name;
        sprintDistance = 150;
        jumpHeight = 1.0;
        onDistance = true;
    }

    public Cat (String name, int sprintDistance, double jumpHeight) {
        this.sprintDistance = sprintDistance;
        this.jumpHeight = jumpHeight;
        this.name = name;
        this.onDistance = true;
    }

    @Override
    public void jump(double height) {
        if (onDistance) {
            if (this.jumpHeight >= height)
                System.out.println(this.name + " successfully jumped "+ height + "m");
            else {
                System.out.println(this.name + " came off the distance");
                this.onDistance = false;
            }
        }
    }

    @Override
    public void run(int distance) {
        if (onDistance) {
            if (this.sprintDistance >= distance)
                System.out.println(this.name + " successfully ran "+ distance + "m");
            else {
                System.out.println(this.name + " came off the distance");
                this.onDistance = false;
            }
        }
    }
}
