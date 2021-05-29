package main;

public class Task {

    /**
     * 计算斐波那契数
     */
    public static int fibo(int num) {
        if (num < 2)
            return 1;
        return fibo(num-1) + fibo(num-2);
    }
}
