package cn.ligen.practice.chapter02.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author ligen
 * @date 2024/3/27 21:10
 * @description 同步阻塞IO TimeServer
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("port converter error");
            }
        }

        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            while (true) {
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }
        } catch (Exception e) {
            System.out.println("Socket error!");
        } finally {
            if (server != null) {
                System.out.println("Time Server is close");
                try {
                    server.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                server = null;
            }
        }
    }
}
