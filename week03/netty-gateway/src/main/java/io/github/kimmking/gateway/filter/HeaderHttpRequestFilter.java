package io.github.kimmking.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HeaderHttpRequestFilter implements HttpRequestFilter {
    private String hostInfo;

    public HeaderHttpRequestFilter() {
        this.hostInfo = "UNKNOWN_HOST";
        try {
            InetAddress ia = InetAddress.getLocalHost();
            this.hostInfo = ia.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        // 添加网关标识
        fullRequest.headers().set("X-NETTY-GATEWAY", hostInfo);
    }
}
