package com.banking.socket.netty;

import com.banking.iso8583.ISO8583Message;
import com.banking.iso8583.ISO8583Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * ISO8583 Message Handler for Netty
 * Processes incoming ISO8583 messages
 */
@Slf4j
public class ISO8583MessageHandler extends ChannelInboundHandlerAdapter {

    private final ISO8583Parser parser = new ISO8583Parser();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            byte[] data = (byte[]) msg;
            log.info("Received ISO8583 message: {} bytes", data.length);

            // Parse ISO8583 message
            ISO8583Message isoMessage = parser.parse(data);
            log.info("MTI: {}, STAN: {}, PAN: {}",
                    isoMessage.getMti(),
                    isoMessage.getField(ISO8583Message.Fields.STAN),
                    maskPan(isoMessage.getField(ISO8583Message.Fields.PAN)));

            // Process message based on MTI
            ISO8583Message response = processMessage(isoMessage);

            // Send response
            String responseStr = parser.format(response);
            byte[] responseBytes = responseStr.getBytes(StandardCharsets.UTF_8);

            // Add length header
            ByteBuf buffer = Unpooled.buffer(2 + responseBytes.length);
            buffer.writeShort(responseBytes.length);
            buffer.writeBytes(responseBytes);

            ctx.writeAndFlush(buffer);

            log.info("Sent response: MTI {}, Response Code: {}",
                    response.getMti(),
                    response.getField(ISO8583Message.Fields.RESPONSE_CODE));

        } catch (Exception e) {
            log.error("Error processing ISO8583 message", e);
            ctx.close();
        }
    }

    private ISO8583Message processMessage(ISO8583Message request) {
        ISO8583Message response = new ISO8583Message();

        String mti = request.getMti();
        String responseMti = switch (mti) {
            case "0100" -> "0110"; // Authorization Response
            case "0200" -> "0210"; // Financial Response
            case "0400" -> "0410"; // Reversal Response
            case "0800" -> "0810"; // Network Management Response
            default -> "0000";
        };

        response.setMti(responseMti);

        // Copy common fields
        if (request.hasField(ISO8583Message.Fields.PAN)) {
            response.setField(ISO8583Message.Fields.PAN, request.getField(ISO8583Message.Fields.PAN));
        }
        if (request.hasField(ISO8583Message.Fields.PROCESSING_CODE)) {
            response.setField(ISO8583Message.Fields.PROCESSING_CODE,
                    request.getField(ISO8583Message.Fields.PROCESSING_CODE));
        }
        if (request.hasField(ISO8583Message.Fields.AMOUNT)) {
            response.setField(ISO8583Message.Fields.AMOUNT, request.getField(ISO8583Message.Fields.AMOUNT));
        }
        if (request.hasField(ISO8583Message.Fields.STAN)) {
            response.setField(ISO8583Message.Fields.STAN, request.getField(ISO8583Message.Fields.STAN));
        }
        if (request.hasField(ISO8583Message.Fields.RRN)) {
            response.setField(ISO8583Message.Fields.RRN, request.getField(ISO8583Message.Fields.RRN));
        }

        // Set response code (simplified - in production, validate transaction)
        response.setField(ISO8583Message.Fields.RESPONSE_CODE, ISO8583Message.ResponseCode.APPROVED);

        // Set authorization code
        response.setField(ISO8583Message.Fields.AUTH_CODE, generateAuthCode());

        return response;
    }

    private String maskPan(String pan) {
        if (pan == null || pan.length() < 8) {
            return "****";
        }
        return pan.substring(0, 6) + "******" + pan.substring(pan.length() - 4);
    }

    private String generateAuthCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception in channel", cause);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("New connection from: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Connection closed: {}", ctx.channel().remoteAddress());
    }
}
