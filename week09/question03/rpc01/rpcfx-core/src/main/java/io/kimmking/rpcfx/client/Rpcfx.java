package io.kimmking.rpcfx.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import io.kimmking.rpcfx.api.*;
import io.kimmking.rpcfx.client.nettyhttpclient.NettyHttpClient;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class Rpcfx {

    static {
        ParserConfig.getGlobalInstance().addAccept("io.kimmking");
    }

    public static <T, filters> T createFromRegistry(final Class<T> serviceClass, final String zkUrl, Router router, LoadBalancer loadBalance, Filter filter) {

        // 加filte之一

        // curator Provider list from zk
        List<String> invokers = new ArrayList<>();
        // 1. 简单：从zk拿到服务提供的列表
        // 2. 挑战：监听zk的临时节点，根据事件更新这个list（注意，需要做个全局map保持每个服务的提供者List）

        List<String> urls = router.route(invokers);

        String url = loadBalance.select(urls); // router, loadbalance

        return (T) create(serviceClass, url, filter);

    }

    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {

        // 0. 替换动态代理 -> 字节码生成
        // return (T) Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClass}, new RpcfxInvocationHandler(serviceClass, url, filters));

        // CGLIB实现
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(new RpcfxMethodInterceptor(serviceClass, url, filters));
        return (T) enhancer.create();
    }

    public abstract static class RpcfxClientHandler {
        public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

        protected final Class<?> serviceClass;
        protected final String url;
        protected final Filter[] filters;

        protected <T> RpcfxClientHandler(Class<T> serviceClass, String url, Filter[] filters) {
            this.serviceClass = serviceClass;
            this.url = url;
            this.filters = filters;
        }

        protected RpcfxResponse post(RpcfxRequest req, String url) throws Throwable {
            if (null!=filters) {
                for (Filter filter : filters) {
                    if (!filter.filter(req)) {
                        return null;
                    }
                }
            }

            String reqJson = JSON.toJSONString(req);
            log.debug("req json: {}", reqJson);

            // 1.可以复用client
            // 2.尝试使用httpclient或者netty client
//            OkHttpClient client1 = new OkHttpClient();
//            final Request request1 = new Request.Builder()
//                    .url(url)
//                    .post(RequestBody.create(JSONTYPE, reqJson))
//                    .build();
//            String respJson1 = client1.newCall(request1).execute().body().string();
//            log.debug("resp json: {}", respJson1);

            // 使用netty发送http请求
            NettyHttpClient client = new NettyHttpClient();
            URI uri = URI.create(url);
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                    uri.getRawPath());
            request.content().writeBytes(reqJson.getBytes(StandardCharsets.UTF_8));
            request.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            request.headers().add(HttpHeaders.Names.CONTENT_TYPE, JSONTYPE);
            request.headers().add(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
            request.headers().add(HttpHeaders.Names.HOST, uri.getHost());
            String respJson = client.execute(url, request).content().toString(StandardCharsets.UTF_8);
            client.close();
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }

        protected Object getResult(RpcfxResponse response) {
            return JSON.parse(response.getResult().toString());
        }
    }

    // JDK Proxy实现
    public static class RpcfxInvocationHandler extends RpcfxClientHandler implements InvocationHandler {

        public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url, Filter... filters) {
           super(serviceClass, url, filters);
        }

        // 可以尝试，自己去写对象序列化，二进制还是文本的，，，rpcfx是xml自定义序列化、反序列化，json: code.google.com/p/rpcfx
        // int byte char float double long bool
        // [], data class

        @Override
        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {

            // 加filter地方之二
            // mock == true, new Student("hubao");

            RpcfxRequest request = new RpcfxRequest();
            request.setServiceClass(this.serviceClass.getName());
            request.setMethod(method.getName());
            request.setParams(params);

            RpcfxResponse response = post(request, url);

            // 加filter地方之三
            // Student.setTeacher("cuijing");

            // 这里判断response.status，处理异常
            // 考虑封装一个全局的RpcfxException

            return getResult(response);
        }

    }

    // CGLIB实现
    public static class RpcfxMethodInterceptor extends RpcfxClientHandler implements MethodInterceptor {

        public RpcfxMethodInterceptor(Class<?> serviceClass, String url, Filter[] filters) {
            super(serviceClass, url, filters);
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            RpcfxRequest request = new RpcfxRequest();
            request.setServiceClass(this.serviceClass.getName());
            request.setMethod(method.getName());
            request.setParams(objects);

            RpcfxResponse response = post(request, url);

            return getResult(response);
        }


    }
}
