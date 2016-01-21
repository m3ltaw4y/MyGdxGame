package com.mygdx.game;

/**
 * Created by M3LTAW4Y on 12/6/2015.
 */


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;

/**
 * Handles a client-side channel.
 */
public class SSLClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("SSL channel active " + ((SslHandler) (ctx.pipeline().get("sslHandler"))).applicationProtocol());
        String [] enprotocols = ((SslHandler)(ctx.pipeline().get("sslHandler"))).engine().getEnabledProtocols();
        String [] protocols = ((SslHandler)(ctx.pipeline().get("sslHandler"))).engine().getSupportedProtocols();
        for(int i =0; i< protocols.length; i++) {
            System.out.println("SSL channel active protocols supported " + protocols[i]);
        }
        for(int i =0; i< enprotocols.length; i++) {
            System.out.println("SSL channel active protocols enabled " + enprotocols[i]);
        }
        super.channelActive(ctx);
        String send = "hi\n";
        System.out.println("SSL channel active, send is:"+send);
        ctx.channel().writeAndFlush(send);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("SSL read0:" + msg);
        ctx.channel().writeAndFlush("client sent this\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
