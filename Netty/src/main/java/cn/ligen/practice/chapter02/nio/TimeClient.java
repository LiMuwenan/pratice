package cn.ligen.practice.chapter02.nio;

/**
 * @author ligen
 * @date 2024/3/30 10:30
 * @description nio client
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {

            } finally {
                System.out.println("The server port : " + port);
            }
        }
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
    }
}
