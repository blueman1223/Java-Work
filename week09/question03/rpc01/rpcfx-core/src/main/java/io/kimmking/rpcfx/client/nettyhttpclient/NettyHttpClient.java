package io.kimmking.rpcfx.client.nettyhttpclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

@Slf4j
public class NettyHttpClient implements Closeable {
    // FIXME 一次性client 只能execute一次
    // 用同步锁的方式改进InboundHandler实现同步请求
    private final Bootstrap bootstrap;
    private final EventLoopGroup workerGroup;

    public NettyHttpClient() {
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

    @Getter
    static class ClientInboundHandler extends ChannelInboundHandlerAdapter {

        private FullHttpResponse response = null;
        private Throwable exception = null;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            this.response = (FullHttpResponse) msg;
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.close();    // 读取完成关闭channel
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            exception = cause;
            ctx.close();
        }

    }

    public FullHttpResponse execute(String url, FullHttpRequest request) throws Throwable {
        ClientInboundHandler httpHandler = new ClientInboundHandler();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpClientCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
                ch.pipeline().addLast(httpHandler);
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
                    // 连接失败
                    log.error("{}:{} connect failed", uri.getHost(), uri.getPort());
                }).channel().closeFuture().sync();  // 阻塞等待channel关闭
        if (null != httpHandler.getException()) {
            throw httpHandler.getException();
        }
        return httpHandler.getResponse();

    }

}