package cn.ligen.practice.chapter02.aio.client;

/**
 * @author ligen
 * @date 2024/4/1 21:41
 * @description
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        try {
            if (args != null && args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {

        }

        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-001").start();
    }
}
