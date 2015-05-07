package org.github.noahshen.mycodefactory.signproxy
import com.google.common.base.Splitter
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.DefaultHttpRequest
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import org.littleshoot.proxy.HttpFilters
import org.littleshoot.proxy.HttpFiltersAdapter
import org.littleshoot.proxy.HttpFiltersSourceAdapter
import org.littleshoot.proxy.HttpProxyServer
import org.littleshoot.proxy.impl.DefaultHttpProxyServer

public class Main {

    public static void main(String[] args) {
        HttpProxyServer server =
                DefaultHttpProxyServer.bootstrap()
                        .withListenOnAllAddresses(true)
                        .withAddress(new InetSocketAddress("0.0.0.0", 8686))
//                        .withPort(8686)
                        .withFiltersSource(new HttpFiltersSourceAdapter() {
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFiltersAdapter(originalRequest) {
                            @Override
                            public HttpResponse requestPre(HttpObject httpObject) {
                                switch (httpObject) {
                                    case DefaultHttpRequest:
                                        DefaultHttpRequest httpRequest = httpObject
                                        // http://qywx.dper.com/app/checkin/loadSign?latitude=31.21725&longitude=121.4162&uuid=8d7a1fe2-83f0-41a3-a09a-7f088c994c2c
                                        if (httpRequest.uri?.startsWith("""http://qywx.dper.com/app/checkin/loadSign""")) {
                                            URI uri = new URI(httpRequest.uri);
                                            def paramMap = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(uri.getQuery())
                                            def latitude = "31.21725"
                                            def longitude = "121.4162"
                                            httpRequest.uri = "http://qywx.dper.com/app/checkin/loadSign?latitude=${latitude}&longitude=${longitude}&uuid=${paramMap['uuid']}"
                                        }
                                    default:
                                        break
                                }
                                // TODO: implement your filtering here
                                return null;
                            }

                            @Override
                            public HttpResponse requestPost(HttpObject httpObject) {
                                // TODO: implement your filtering here
                                return null;
                            }

                            @Override
                            public HttpObject responsePre(HttpObject httpObject) {
                                // TODO: implement your filtering here
                                return httpObject;
                            }

                            @Override
                            public HttpObject responsePost(HttpObject httpObject) {
                                // TODO: implement your filtering here
                                return httpObject;
                            }
                        };
                    }
                }).start();
    }
}
