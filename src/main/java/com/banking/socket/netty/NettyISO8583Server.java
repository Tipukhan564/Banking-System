package com.banking.socket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Netty-based ISO8583 Server for ATM/POS Communication
 * High-performance async I/O server
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "netty.server.enabled", havingValue = "true")
public class NettyISO8583Server {

    @Value("${netty.server.port:8584}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    @PostConstruct
    public void start() throws Exception {
        log.info("Starting Netty ISO8583 Server on port {}", port);

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ISO8583FrameDecoder());
                            pipeline.addLast(new ISO8583MessageHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture future = bootstrap.bind(port).sync();
            serverChannel = future.channel();

            log.info("Netty ISO8583 Server started successfully on port {}", port);

            // Don't wait here - let Spring continue startup
            // future.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("Failed to start Netty ISO8583 Server", e);
            shutdown();
            throw e;
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down Netty ISO8583 Server");
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        log.info("Netty ISO8583 Server stopped");
    }
}
