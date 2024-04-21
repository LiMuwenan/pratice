package cn.ligen.practice.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @author: ligen
 * @date: 2024/4/18 21:46
 * @description: 文件服务器，访问文件则下载；访问目录则列出所有文件
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess()) {
            System.out.println(HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (request.getMethod() != HttpMethod.GET) {
            System.out.println(HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        final String url = request.getUri();
        final String path = sanitizeUri(url);
        if (path == null) {
            System.out.println(HttpResponseStatus.FORBIDDEN);
            return;
        }
        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            System.out.println(HttpResponseStatus.NOT_FOUND);
            return;
        }
        if (file.isFile()) {
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(file, "r"); // 只读
            } catch (FileNotFoundException exception) {
                System.out.println(HttpResponseStatus.NOT_FOUND);
                return;
            }

            long fileLength = randomAccessFile.length();
            DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, fileLength);
//            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/octet-stream"); // 下载
//            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, file + ";charset=UTF-8"); // 按文件格式展示文件内容
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json"); // 按文件格式展示文件内容（不乱吗）
            if (HttpHeaders.isKeepAlive(request)) {
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);
            ChannelFuture sendFileFuture;
            sendFileFuture = ctx.writeAndFlush(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long l, long l1) throws Exception {
                    System.out.println("发送中");
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
                    System.out.println("发送完成");
                }
            });
        } else {
            sendListing(ctx, file, request);
        }

    }

    private String sanitizeUri(String uri) {
        uri = URLDecoder.decode(uri, StandardCharsets.UTF_8);
        uri = uri.replace('/', File.separatorChar);
        if (uri.contains(File.separator + ".") || uri.contains("." + File.separator)
                || uri.startsWith(".") || uri.endsWith(".")) {
            System.out.println("非法uri");
            return null;
        }
        return System.getProperty("user.dir") + uri;
    }

    private static void sendListing(ChannelHandlerContext ctx, File dir, HttpRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");
        StringBuilder buf = new StringBuilder();
        buf.append("<html><body><ul>");
        buf.append("<li><a href=\"")
                .append("..")
                .append("\">链接:")
                .append("..")
                .append("</a></li>\r\n");
        for (File file : dir.listFiles()) {
            buf.append("<li><a href=\"")
//                    .append(dir.getName())
                    .append(request.getUri())
                    .append("/")
                    .append(file.getName())
                    .append("\">链接:")
                    .append(file.getName())
                    .append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
