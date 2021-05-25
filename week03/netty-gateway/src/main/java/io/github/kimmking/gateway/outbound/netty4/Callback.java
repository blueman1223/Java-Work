package io.github.kimmking.gateway.outbound.netty4;

import io.netty.handler.codec.http.FullHttpResponse;

public interface Callback {
    void completed(FullHttpResponse response);

    void failed(Throwable e);
}
