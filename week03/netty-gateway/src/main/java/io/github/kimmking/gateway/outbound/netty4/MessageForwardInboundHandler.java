package io.github.kimmking.gateway.outbound.netty4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MessageForwardInboundHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext requestCtx;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        requestCtx.writeAndFlush(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }
}
