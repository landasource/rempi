package org.landa.rempi.server.io.livestream;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.inject.Inject;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.landa.rempi.comm.livestream.handler.frame.FrameDecoder;
import org.landa.rempi.server.io.RempiServerHandler;

import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.ConverterFactory.Type;
import com.xuggle.xuggler.video.IConverter;

public class Decoder extends ObjectDecoder {

    @Inject
    private org.apache.log4j.Logger logger;

    protected final IStreamCoder iStreamCoder = IStreamCoder.make(Direction.DECODING, ICodec.ID.CODEC_ID_H264);
    protected final Type type = ConverterFactory.findRegisteredConverter(ConverterFactory.XUGGLER_BGR_24);

    protected final Dimension dimension = new Dimension(320, 240);

    protected final FrameDecoder frameDecoder;

    @Inject
    protected ObserverStreamFrameListener streamFrameListener;

    @Inject
    private RempiServerHandler rempiServerHandler;

    /**
     * Cause there may be one or more image in the frame,so we need an Stream
     * listener here to get all the image
     */

    public Decoder() {
        super();

        //        if (internalFrameDecoder) {
        frameDecoder = new FrameDecoder(4);
        //        } else {
        //            frameDecoder = null;
        //        }
        //        if (decodeInOtherThread) {

        //        } else {
        //            decodeWorker = null;
        //        }

        initialize();
    }

    private void initialize() {
        //iStreamCoder.setNumPicturesInGroupOfPictures(20);
        //iStreamCoder.setBitRate(250000);
        //iStreamCoder.setBitRateTolerance(9000);
        //iStreamCoder.setPixelType(IPixelFormat.Type.YUV420P);
        //iStreamCoder.setHeight(dimension.height);
        //iStreamCoder.setWidth(dimension.width);
        //iStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
        //iStreamCoder.setGlobalQuality(0);
        //rate
        //IRational rate = IRational.make(25, 1);
        //iStreamCoder.setFrameRate(rate);
        //time base
        //iStreamCoder.setAutomaticallyStampPacketsForStream(true);
        //iStreamCoder.setTimeBase(IRational.make(rate.getDenominator(),rate.getNumerator()));
        iStreamCoder.open(null, null);
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {

        return super.decode(ctx, channel, buffer);
    }

    public void decode(final String clientId, final Object msg) throws Exception {

        if (msg == null) {
            throw new NullPointerException("you cannot pass into an null to the decode");
        }
        ChannelBuffer frameBuffer;
        if (frameDecoder != null) {
            frameBuffer = frameDecoder.decode((ChannelBuffer) msg);
            if (frameBuffer == null) {
                return;
            }

        } else {
            frameBuffer = (ChannelBuffer) msg;
        }

        final int size = frameBuffer.readableBytes();
        logger.info("decode the frame size :" + size);
        //start to decode
        final IBuffer iBuffer = IBuffer.make(null, size);
        final IPacket iPacket = IPacket.make(iBuffer);
        iPacket.getByteBuffer().put(frameBuffer.toByteBuffer());
        //decode the packet
        if (!iPacket.isComplete()) {
            return;
        }

        final IVideoPicture picture = IVideoPicture.make(IPixelFormat.Type.YUV420P, dimension.width, dimension.height);
        try {
            // decode the packet into the video picture
            int postion = 0;
            final int packageSize = iPacket.getSize();
            while (postion < packageSize) {
                postion += iStreamCoder.decodeVideo(picture, iPacket, postion);
                if (postion < 0) {
                    throw new RuntimeException("error " + " decoding video");
                }
                // if this is a complete picture, dispatch the picture
                if (picture.isComplete()) {
                    final IConverter converter = ConverterFactory.createConverter(type.getDescriptor(), picture);
                    final BufferedImage image = converter.toImage(picture);
                    //BufferedImage convertedImage = ImageUtils.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
                    //here ,put out the image
                    if (streamFrameListener != null) {
                        streamFrameListener.onFrameReceived(clientId, image);
                    }
                    converter.delete();
                } else {
                    picture.delete();
                    iPacket.delete();
                    return;
                }
                //clean the picture and reuse it
                picture.getByteBuffer().clear();
            }
        } finally {
            if (picture != null) {
                picture.delete();
            }
            iPacket.delete();
            // ByteBufferUtil.destroy(data);
        }

    }

}
