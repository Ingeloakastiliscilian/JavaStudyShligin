package Lesson5;

import java.util.concurrent.TimeUnit;

public class Threads{

    public static int LENGTH = 10000000;
    public static int HALF_L = LENGTH/2;

    public static void serialCalc (float[] arr){
        long t0 = System.nanoTime();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        long t1 = System.nanoTime();
        System.out.println("Serial execution time: " + TimeUnit.NANOSECONDS.toMillis( t1 - t0));
    }

    public static void parallelCalc (float[] arr) {
        long start = System.nanoTime();
        long stop;
        float[] arr1, arr2;
        arr1 = new float[(HALF_L)];
        arr2 = new float[(HALF_L)];
        Thread thr1 = new Thread(() -> {
            long t0 = System.nanoTime();
            System.arraycopy(arr, 0 , arr1 , 0 , HALF_L);
            for (int i = 0; i < arr1.length; i++) {
                arr1[i] = (float)(arr1[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }
            long t1 = System.nanoTime();
        });
        Thread thr2 = new Thread(() -> {
            long t0 = System.nanoTime();
            System.arraycopy(arr, 0 , arr2 , 0 , HALF_L);
            for (int i = 0; i < arr2.length; i++) {
                arr2[i] = (float)(arr2[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }
            long t1 = System.nanoTime();
        });
        thr1.start();
        thr2.start();
        while  ( thr1.isAlive() || thr2.isAlive() ) {
        }
        System.arraycopy(arr, 0 , arr1 , 0 , HALF_L);
        System.arraycopy(arr, HALF_L , arr2 , 0 , HALF_L);
        stop = System.nanoTime();
        System.out.println("Parallel execution time: " + TimeUnit.NANOSECONDS.toMillis( stop - start));
    }

}
