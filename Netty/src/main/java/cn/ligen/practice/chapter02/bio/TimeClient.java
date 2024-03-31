package cn.ligen.practice.chapter02.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author ligen
 * @date 2024/3/27 21:34
 * @description 同步阻塞IO TimeClient
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("port converter error");
            }
        }

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket("127.0.0.1", port);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()
            ));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("QUERY TIME ORDER");
            System.out.println("Send order 2 server succeed.");
//            int read = in.read();
//            while (read!=-1) {
//                System.out.println((char) read);
//                read=in.read();
//            }
            String resp = in.readLine();
            System.out.println("Now is : " + resp);
        } catch (Exception e) {
            System.out.println("socket handle error");
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("socket disconnect is error");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("buffered reader is error");
                }
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
