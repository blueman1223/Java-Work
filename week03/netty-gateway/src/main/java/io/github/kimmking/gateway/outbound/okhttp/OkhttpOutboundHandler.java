package io.github.kimmking.gateway.outbound.okhttp;


import io.github.kimmking.gateway.filter.HeaderHttpResponseFilter;
import io.github.kimmking.gateway.filter.HttpRequestFilter;
import io.github.kimmking.gateway.filter.HttpResponseFilter;
import io.github.kimmking.gateway.outbound.OutBoundHandler;
import io.github.kimmking.gateway.router.HttpEndpointRouter;
import io.github.kimmking.gateway.router.RandomHttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkhttpOutboundHandler implements OutBoundHandler {
    private final OkHttpClient okHttpClient;
    private final List<String> backendUrls;

    private final HttpResponseFilter filter = new HeaderHttpResponseFilter();
    private final HttpEndpointRouter router = new RandomHttpEndpointRouter();

    public OkhttpOutboundHandler(List<String> backends) {
        this.backendUrls = backends.stream()
                .map(this::formatUrl)
                .collect(Collectors.toList());
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(8);
        this.okHttpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .build();
    }

    private String formatUrl(String backend) {
        return backend.endsWith("/")?backend.substring(0,backend.length()-1):backend;
    }

    @Override
    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, HttpRequestFilter filter) {
        String backendUrl = router.route(this.backendUrls);
        final String url = backendUrl + fullRequest.uri();
        filter.filter(fullRequest, ctx);

        Map<String, String> headerMap = new HashMap<>();
        fullRequest.headers().forEach(header->
                headerMap.merge(header.getKey(), header.getValue(),
                        (oldValue, newValue) -> oldValue+";"+newValue));

        Headers headers = Headers.of(headerMap);
        RequestBody requestBody = null;
        if (fullRequest.content().array().length > 0) {
            requestBody = RequestBody.create(fullRequest.content().array());
        }
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .method(fullRequest.method().name(), requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                handleException(ctx, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    handleResponse(fullRequest, ctx, response);
                } catch (Exception e) {
                    handleException(ctx, e);
                }
            }
        });
    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final Response endpointResponse) throws Exception {
        byte[] body = null;
        if (endpointResponse.body() != null) {
            body = endpointResponse.body().bytes();
        }
        HttpHeaders headers = new DefaultHttpHeaders();
        HttpResponseStatus status = new HttpResponseStatus(endpointResponse.code(), endpointResponse.message());
        Map<String, List<String>> headerMap = endpointResponse.headers().toMultimap();
        headerMap.forEach(headers::add);
        HttpVersion version = HttpVersion.valueOf(endpointResponse.protocol().toString());
        FullHttpResponse response;
        if (body == null) {
            response = new DefaultFullHttpResponse(version, status);
        } else {
            response = new DefaultFullHttpResponse(version, status, Unpooled.wrappedBuffer(body));
        }
        response.headers().add(headers);
        filter.filter(response);
        ctx.writeAndFlush(response);
    }


    /**
     * 处理转发过程中产生异常
     * 记录日志
     * 返回500
     */
    public void handleException(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        FullHttpResponse errorResp = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
        if (ctx != null) {
            ctx.writeAndFlush(errorResp);
        }
        // ctx.close(); 尽量避免服务端主动关闭连接，会造成TIME_WAIT
    }
}
