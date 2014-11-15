/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.landa.rempi.client.ssh;

import static org.jboss.netty.channel.Channels.pipeline;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.Timer;
import org.landa.rempi.client.RempiClientHandler;
import org.landa.rempi.client.coding.Encoder;
import org.landa.rempi.comm.Command;
import org.landa.rempi.comm.ssh.SecureSslContextFactory;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class SecureClientPipelineFactory implements ChannelPipelineFactory {

    private final Timer timer;

    private final ChannelHandler clientHandler;

    public SecureClientPipelineFactory(final ClientBootstrap bootstrap, final Timer timer, final String id) {
        this.timer = timer;
        clientHandler = new RempiClientHandler(bootstrap, timer, id);
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        final ChannelPipeline pipeline = pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.

        final SSLEngine engine = SecureSslContextFactory.getClientContext().createSSLEngine();
        engine.setUseClientMode(true);

        pipeline.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("frame encoder", new LengthFieldPrepender(4, false));

        // On top of the SSL handler, add the text line codec.
        //        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.softCachingResolver(Command.class.getClassLoader())));
        pipeline.addLast("encoder", new Encoder());

        // and then business logic.
        pipeline.addLast("handler", clientHandler);

        return pipeline;
    }
}
