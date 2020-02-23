package Lesson1;

public class Robot implements Runner{
    private int sprintDistance;
    private double jumpHeight;
    private String model;
    private boolean onDistance;

    public Robot (String name){
        this.model = name;
        sprintDistance = 2000;
        jumpHeight = 15.0;
        onDistance = true;
    }

    public Robot(String name, int sprintDistance, double jumpHeight) {
        this.sprintDistance = sprintDistance;
        this.jumpHeight = jumpHeight;
        this.model = name;
        this.onDistance = true;
    }

    @Override
    public void jump(double height) {
        if (onDistance) {
            if (this.jumpHeight >= height)
                System.out.println(this.model + " successfully jumped "+ height+ "m");
            else {
                System.out.println(this.model + " came off the distance");
                this.onDistance = false;
            }
        }
    }

    @Override
    public void run(int distance) {
        if (onDistance) {
            if (this.sprintDistance >= distance)
                System.out.println(this.model + " successfully ran "+ distance + "m");
            else {
                System.out.println(this.model + " came off the distance");
                this.onDistance = false;
            }
        }
    }
}
