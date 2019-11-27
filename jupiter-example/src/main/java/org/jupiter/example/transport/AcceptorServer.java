package org.jupiter.example.transport;

import org.jupiter.transport.JAcceptor;
import org.jupiter.transport.netty.JNettyTcpAcceptor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * 服务器 这个和业务逻辑处理无关 但是稍微耦合一点就是codec是强制写进去的 没法通过插拔的方式去定制
 *
 * @author dongwei
 * @date 2018/08/01
 * Time: 11:41
 */
public class AcceptorServer {

    public static void main(String[] args) throws InterruptedException {
        JAcceptor acceptor = new JNettyTcpAcceptor(9999);
        acceptor.start();
    }
}
