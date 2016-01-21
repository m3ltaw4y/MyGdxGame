package com.mygdx.game;

import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class SSLClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private String server;
    private int port;

    public SSLClientInitializer(SslContext sslCtx, String server, int port) {
        this.sslCtx = sslCtx;
        this.server = server;
        this.port = port;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        SSLEngine sslEngine = sslCtx.newEngine(ch.alloc(), server, port);
        //sslEngine.setEnabledProtocols(new String[]{"TLSv1"});
        //sslEngine.setEnabledProtocols(new String[]{"SSLv3"});
        //sslEngine.setEnabledProtocols(new String[]{"TLSv1.2"});
        SslHandler sslHandler = new SslHandler(sslEngine);
        //SslHandler sslHandler = sslCtx.newHandler(ch.alloc(), server, port);

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.
        pipeline.addLast("sslHandler",sslHandler);

        // On top of the SSL handler, add the text line codec.
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        // and then business logic.
        pipeline.addLast(new SSLClientHandler());
    }
}
