package org.landa.rempi.server.io.livestream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.ssl.SslHandler;

@ApplicationScoped
public class LiveStreamHandler extends SimpleChannelUpstreamHandler {

    @Inject
    private Logger logger;

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        // Get the SslHandler in the current pipeline.
        // We added it in SecureChatPipelineFactory.
        final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);

        // Get notified when SSL handshake is done.
        final ChannelFuture handshakeFuture = sslHandler.handshake();

        handshakeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.warn("Client will stream without authentication");
                }
            }
        });
    }

    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        // TODO Auto-generated method stub
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext arg0, final ExceptionEvent arg1) throws Exception {
        // TODO Auto-generated method stub
        super.exceptionCaught(arg0, arg1);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        // TODO Auto-generated method stub
        super.messageReceived(ctx, e);
    }

}
