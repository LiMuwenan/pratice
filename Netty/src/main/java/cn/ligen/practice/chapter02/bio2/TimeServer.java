package cn.ligen.practice.chapter02.bio2;

import cn.ligen.practice.chapter02.bio.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author ligen
 * @date 2024/3/27 21:50
 * @description
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
            System.out.println("The time serve is start in port : " + port);
            Socket socket = null;
            TimeServerHandleExecutePool singleExecutor = new TimeServerHandleExecutePool(50, 10000);
            while (true) {
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } catch (IOException e) {

        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
