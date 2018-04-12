package org.jupiter.example.nettyinpractice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created with IntelliJ IDEA.
 * Description: 最简单的server
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 11:21
 */
public class SimpleServer extends AbstractServer{


    public static void main(String[] args) throws InterruptedException {
        new SimpleServer().run();
    }

    @Override
    protected ChannelHandler[] addHandlers() {
        return new ChannelHandler[]{
                new FirstInboundEventHandler(),
                new SecondInboundEventHandler()
        };
    }

    /***
     * 继承自最简单的handler 无需手动释放资源
     * 会根据泛型参数去判断能否读取 不指定编码器的话 类型都为ByteBuf -> PooledUnsafeDirectByteBuf
     *
     * 仅仅处理in事件
     */
    private static class SimpleHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            System.out.println("received:" + msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            cause.printStackTrace();
            ctx.close();
        }
    }

    /**
     * 第一个inbound处理器
     */
    private static class FirstInboundEventHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("FirstInboundEventHandler");
            // 继续调用下个handler
            ctx.fireChannelActive();
        }
    }

    /**
     * 第二个inbound处理器
     */
    private static class SecondInboundEventHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("SecondInboundEventHandler");
        }
    }

}
