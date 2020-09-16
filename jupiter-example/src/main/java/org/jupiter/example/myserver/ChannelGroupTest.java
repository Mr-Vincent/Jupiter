package org.jupiter.example.myserver;

import com.sun.xml.internal.ws.api.handler.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;

/**
 * @author dongwei
 * @since 2020/09/12
 * Time: 15:22
 */
public class ChannelGroupTest {

    private int port;

    private ChannelGroup clients = new DefaultChannelGroup("clients", GlobalEventExecutor.INSTANCE);

    private MessageHandler handler = new MessageHandler();

    public ChannelGroupTest(int port) {
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
                            ch.pipeline().addLast(handler);
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

    @ChannelHandler.Sharable
    class MessageHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("来客人了！");
            clients.add(ctx.channel());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("当前客户端数："+clients.size());
            SocketAddress socketAddress = ctx.channel().remoteAddress();
            System.out.println(socketAddress);
            System.out.println(msg);
            String message = "hello guest!" + socketAddress;
            ByteBuf encoded = ctx.alloc().buffer(message.getBytes().length);
            encoded.writeBytes(message.getBytes());
            clients.write(encoded);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 9999;
        }
        new ChannelGroupTest(port).run();
    }

}
