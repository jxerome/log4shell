package log4shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

public class HttpClient {
  public static HttpURLConnection connectToEndpoint(
      URI endpoint, Map<String, String> headers, String method) throws IOException {
    HttpURLConnection connection =
        (HttpURLConnection) endpoint.toURL().openConnection(Proxy.NO_PROXY);
    connection.setConnectTimeout(1000);
    connection.setReadTimeout(1000);
    connection.setRequestMethod(method);
    connection.setDoOutput(true);
    headers.forEach(connection::addRequestProperty);
    connection.setInstanceFollowRedirects(false);
    connection.connect();

    return connection;
  }

  public static void post(URI server, Class<? extends Runnable> target, Object content) {
    try {
      URL url = server.resolve(target.getName().replace('.', '/')).toURL();
      byte[] body = Json.mapper.writeValueAsBytes(content);

      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Content-Length", String.valueOf(body.length));
      conn.setDoOutput(true);
      conn.getOutputStream().write(body);
      discard(conn.getInputStream());
    } catch (IOException ignore) {
    }
  }

  public static byte[] readAll(InputStream is) throws IOException {
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      byte[] b = new byte[8192];
      int n = 0;
      while ((n = is.read(b)) >= 0) {
        output.write(b, 0, n);
      }
      return output.toByteArray();
    }
  }

  public static String readAll(InputStream is, Charset charset) throws IOException {
    return new String(readAll(is), charset);
  }

  public static byte[] readAll(InputStream is, int length) throws IOException {
    byte[] b = new byte[length];
    int i = 0;
    int n;
    while (i < length && (n = is.read(b, i, length - i)) >= 0) {
      i += n;
    }
    return i == length ? b : Arrays.copyOf(b, i);
  }

  public static String readAll(InputStream is, int length, Charset charset) throws IOException {
    return new String(readAll(is, length), charset);
  }

  private static void discard(InputStream in) throws IOException {
    byte[] buffer = new byte[8192];
    while (in.read(buffer) >= 0) {}
  }
}
