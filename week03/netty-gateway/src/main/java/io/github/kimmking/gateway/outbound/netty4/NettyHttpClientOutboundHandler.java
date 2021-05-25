package io.github.kimmking.gateway.outbound.netty4;

import io.github.kimmking.gateway.filter.HttpRequestFilter;
import io.github.kimmking.gateway.filter.HttpResponseFilter;
import io.github.kimmking.gateway.outbound.OutBoundHandler;
import io.github.kimmking.gateway.router.HttpEndpointRouter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpClientOutboundHandler implements OutBoundHandler {
    private final NettyHttpAsyncClient forwardClient;
    private final HttpEndpointRouter router;
    private final List<String> backendUrls;
    private final HttpResponseFilter filter;

    public NettyHttpClientOutboundHandler(HttpEndpointRouter router, List<String> backendUrls, HttpResponseFilter filter) {
        this.router = router;
        this.backendUrls = backendUrls;
        this.forwardClient = new NettyHttpAsyncClient();
        this.filter = filter;
    }

    @Override
    public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx, HttpRequestFilter requestFilter) throws InterruptedException {
        String backendUrl = router.route(this.backendUrls);
        final String url = backendUrl + fullRequest.uri();
        FullHttpRequest request = fullRequest.copy();
        request.setUri(url);
        forwardClient.execute(url, fullRequest, new Callback() {
            @Override
            public void completed(FullHttpResponse response) {
                filter.filter(response);
                ctx.writeAndFlush(response);
            }

            @Override
            public void failed(Throwable e) {
                e.printStackTrace();
                FullHttpResponse errorResp = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
                ctx.writeAndFlush(errorResp);
            }
        });


    }
}