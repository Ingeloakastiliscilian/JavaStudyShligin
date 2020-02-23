package Lesson1;

public class Robot implements Runner{
    private int sprintDistance;
    private double jumpHeight;
    private String name;
    private boolean onDistance;

    public Robot (String name){
        this.name = name;
        sprintDistance = 300;
        jumpHeight = 0.8;
        onDistance = true;
    }

    public Robot(String name, int sprintDistance, double jumpHeight) {
        this.sprintDistance = sprintDistance;
        this.jumpHeight = jumpHeight;
        this.name = name;
        this.onDistance = true;
    }

    @Override
    public void runs(Track track) {
        if (onDistance){
            if (this.sprintDistance >= track.length)
                System.out.println(this.name + " successfully ran " + track.length + "m");
            else {
                System.out.println(this.name + " leaves distance");
                this.onDistance = false;
            }
        }
    }

    @Override
    public void jumps(Boarder boarder) {
        if (onDistance){
            if (this.jumpHeight >= boarder.height)
                System.out.println(this.name + " successfully jumped over " + boarder.height + "m");
            else {
                System.out.println(this.name + " leaves distance");
                this.onDistance = false;
            }
        }
    }
}
