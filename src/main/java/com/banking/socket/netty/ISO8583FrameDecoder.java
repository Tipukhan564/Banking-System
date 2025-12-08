package com.banking.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * ISO8583 Frame Decoder for Netty
 * Handles message framing for ISO8583 messages
 */
public class ISO8583FrameDecoder extends ByteToMessageDecoder {

    private static final int MAX_FRAME_LENGTH = 9999;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 2) {
            return; // Wait for length header
        }

        in.markReaderIndex();

        // Read 2-byte length header
        int length = in.readUnsignedShort();

        if (length > MAX_FRAME_LENGTH) {
            in.resetReaderIndex();
            ctx.close();
            return;
        }

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return; // Wait for complete message
        }

        // Read the message
        byte[] message = new byte[length];
        in.readBytes(message);

        out.add(message);
    }
}
