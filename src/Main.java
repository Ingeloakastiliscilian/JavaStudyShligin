import Lesson5.Threads;

public class Main {

    public static void main(String[] args) {
        float[] arr = new float[(Threads.LENGTH)];
        for (int i = 0; i < Threads.LENGTH; i++) {
            arr[i] = 1;
        }
        Threads.parallelCalc(arr);
        Threads.serialCalc(arr);
    }
}
