package com.mygdx.game;


import java.io.File;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkSslClientContext;
import io.netty.handler.ssl.OpenSslClientContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Simple SSL chat client modified from
 */
public class SSLClientThread extends Thread{

    private String server;
    private int port;

    public SSLClientThread(String server, int port){
        this.server = server;
        this.port = port;
    }

    @Override
    public void run(){
        // Configure SSL.
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            //final SslContext sslCtx = SslContextBuilder.forClient()
            //        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

            //build without SslContextBuilder
            final SslContext sslCtx;
            TrustManagerFactory trustManagerFactory = InsecureTrustManagerFactory.INSTANCE;
            SslProvider provider = SslContext.defaultClientProvider();
            //for resolving only
            File trustCertChainFile = null;
            File keyCertChainFile = null;
            File keyFile = null;
            String keyPassword = null;
            KeyManagerFactory keyManagerFactory = null;
            Iterable<String> ciphers = null;
            ApplicationProtocolConfig apn = null;
            switch (provider) {
                case JDK:
                    sslCtx = new JdkSslClientContext(
                            trustCertChainFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword,
                            keyManagerFactory, ciphers, IdentityCipherSuiteFilter.INSTANCE, apn, 0, 0);
                    break;
                case OPENSSL:
                default:
                    //sslCtx = new OpenSslClientContext(
                    //        trustCertChainFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword,
                    //        keyManagerFactory, ciphers, IdentityCipherSuiteFilter.INSTANCE, apn, 0, 0);
                    sslCtx = new OpenSslClientContext(
                            trustCertChainFile, trustManagerFactory,
                            ciphers, apn, 0, 0);
                    break;
            }

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new SSLClientInitializer(sslCtx,server,port));

            // Start the connection attempt.
            Channel ch = b.connect(server, port).sync().channel();

            // Wait until the connection is closed.
            ch.closeFuture().sync();
            System.out.println("SSL closed   ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }
}
