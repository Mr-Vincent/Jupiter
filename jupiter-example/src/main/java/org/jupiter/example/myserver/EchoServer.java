package org.jupiter.example.myserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/08
 * Time: 16:41
 */
public class EchoServer {
    private int port;
    private EchoServerHandler handler = new EchoServerHandler();

    private TestHandler testHandler = new TestHandler();

    private OutHandler outHandler = new OutHandler();

    public EchoServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(outHandler,testHandler,handler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.AUTO_READ,true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 9999;
        }
        new EchoServer(port).run();
    }

    @ChannelHandler.Sharable
    class OutHandler extends ChannelOutboundHandlerAdapter{
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("OutHandler write");
            String response = "I am ok!";
            ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
            encoded.writeBytes(response.getBytes());
            System.out.println(msg);
            super.write(ctx, encoded, promise);
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            System.out.println("OutHandler flush");
            super.flush(ctx);
        }
    }
    @ChannelHandler.Sharable
    class TestHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
            System.out.println("TestHandler");
            ctx.fireChannelRead(msg);
            System.out.println(new Date());
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            System.out.println("test 读完了");
            ctx.fireChannelReadComplete();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            System.err.println("testHandler error");
            cause.printStackTrace();
            ctx.close();
        }
    }

    @ChannelHandler.Sharable
    class EchoServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
            System.out.println("EchoServerHandler--->" + msg);
            System.out.println(new Date());
            // 这是最后一个inbound handler 因此这里write到out bound handler中
            ctx.write(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            System.out.println("echo 读完了");
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
            ctx.close();
        }
    }
}
