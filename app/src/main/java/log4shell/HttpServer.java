package log4shell;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpServer implements Closeable {
  private static final String CLASS_MIMETYPE = "application/java-vm";
  public static final String PLAIN_TEXT_MIMETYPE = "text/plain; charset=UTF-8";

  private final Channel channel;

  public HttpServer(Config config) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    ServerBootstrap b = new ServerBootstrap();
    b.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(
            new ChannelInitializer<SocketChannel>() {
              @Override
              public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpRequestDecoder());
                p.addLast(new HttpResponseEncoder());
                p.addLast(new ClassServingServerHandler(classLoader));
              }
            })
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true);

    channel = b.bind(config.getHttpPort()).syncUninterruptibly().channel();
    channel
        .closeFuture()
        .addListener(
            future -> {
              workerGroup.shutdownGracefully();
              bossGroup.shutdownGracefully();
            });

    System.out.printf(
        "Listening for HTTP on %s:%d%n", config.getListenAddress(), config.getHttpPort());
  }

  @Override
  public void close() {
    channel.close();
  }

  public static class ClassServingServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private final ClassLoader classLoader;

    public ClassServingServerHandler(ClassLoader classLoader) {
      this.classLoader = classLoader;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) {
      System.out.println("HTTP Request");
      FullHttpResponse response;
      if (HttpMethod.GET.equals(request.method())) {
        try {
          URI uri = new URI(request.uri());
          System.out.printf("HTTP Request GET %s%n", uri.toASCIIString());

          String path = uri.getPath();
          if (path == null || path.isEmpty() || "/".equals(path)) {
            response = text(HttpResponseStatus.NOT_FOUND, "Not Found " + path);
          } else {
            if (path.charAt(0) == '/') path = path.substring(1);
            try (InputStream in = classLoader.getResourceAsStream(path)) {
              if (in != null) {
                byte[] bytes = IO.readAll(in);
                ByteBuf content = Unpooled.copiedBuffer(bytes);
                response = classfile(content);
                System.out.printf("HTTP send %s - %d bytes%n", uri.toASCIIString(), bytes.length);
              } else {
                response = text(HttpResponseStatus.NOT_FOUND, "Not Found " + path);
                System.out.printf("HTTP Not Found %s%n", uri.toASCIIString());
              }
            } catch (IOException e) {
              response = text(HttpResponseStatus.INTERNAL_SERVER_ERROR, "Error " + e.getMessage());
            }
          }
        } catch (URISyntaxException e) {
          response = text(HttpResponseStatus.BAD_REQUEST, "Invalid URI " + e.getMessage());
        }
      } else {
        response =
            text(
                HttpResponseStatus.METHOD_NOT_ALLOWED,
                "Method not allowed " + request.method().name());
      }
      ctx.write(response);
      ctx.flush();
    }

    private FullHttpResponse text(HttpResponseStatus status, String message) {
      ByteBuf content = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
      int contentLength = content.readableBytes();
      FullHttpResponse response =
          new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
      HttpHeaders headers = response.headers();
      headers.set(HttpHeaderNames.CONTENT_TYPE, PLAIN_TEXT_MIMETYPE);
      headers.set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
      return response;
    }

    private FullHttpResponse classfile(ByteBuf classContent) {
      int contentLength = classContent.readableBytes();
      FullHttpResponse response =
          new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, classContent);
      HttpHeaders headers = response.headers();
      headers.set(HttpHeaderNames.CONTENT_TYPE, CLASS_MIMETYPE);
      headers.set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
      return response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
      ctx.close();
    }
  }
}
