package io.github.kimmking.gateway.outbound.netty4;

import com.sun.deploy.net.proxy.ProxyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

public class NettyHttpAsyncClient implements Closeable {
    private final Bootstrap bootstrap;
    private final EventLoopGroup workerGroup;

    public NettyHttpAsyncClient() {
        this.bootstrap = new Bootstrap();
        this.workerGroup = new NioEventLoopGroup(1);
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public void close() throws IOException {
        workerGroup.shutdownGracefully();
    }

    class ClientInboundHandler extends ChannelInboundHandlerAdapter {
        private final Callback callback;

        public ClientInboundHandler(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            callback.completed((FullHttpResponse) msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            callback.failed(cause);
        }
    }

    public void execute(String url, FullHttpRequest request, Callback callback) throws InterruptedException {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpClientCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
                ch.pipeline().addLast(new ClientInboundHandler(callback));
            }
        });

        URI uri = URI.create(url);
        // Start the client.
        bootstrap.connect(uri.getHost(), uri.getPort())
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(request);
                        return;
                    }
                    System.out.println("connect failed.");
                });
    }

}