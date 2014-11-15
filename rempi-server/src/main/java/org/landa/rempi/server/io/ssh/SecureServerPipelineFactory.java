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
package org.landa.rempi.server.io.ssh;

import static org.jboss.netty.channel.Channels.pipeline;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.landa.rempi.comm.Command;
import org.landa.rempi.comm.livestream.handler.StreamFrameListener;
import org.landa.rempi.comm.ssh.SecureSslContextFactory;
import org.landa.rempi.server.io.RempiServerHandler;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class SecureServerPipelineFactory implements ChannelPipelineFactory {

    private final RempiServerHandler rempiServerHandler;

    private final StreamFrameListener streamFrameListener;

    public SecureServerPipelineFactory(final RempiServerHandler rempiServerHandler, final StreamFrameListener streamFrameListener) {
        this.rempiServerHandler = rempiServerHandler;
        this.streamFrameListener = streamFrameListener;

    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        final ChannelPipeline pipeline = pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.
        //
        // Read SecureSslContextFactory
        // if you need client certificate authentication.

        final SSLEngine engine = SecureSslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);

        pipeline.addLast("ssl", new SslHandler(engine));

        // On top of the SSL handler, add the text line codec.
        //        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(Command.class.getClassLoader())));
        pipeline.addLast("encoder", new ObjectEncoder());

        // and then business logic.
        pipeline.addLast("handler", rempiServerHandler);

        return pipeline;
    }
}
