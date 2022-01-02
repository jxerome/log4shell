package log4shell;

import java.io.*;

public class IO {

  public static final byte[] EMPTY_STRING;

  static {
    byte[] payload;
    try {
      payload = IO.serialize("");
    } catch (UncheckedIOException e) {
      e.printStackTrace();
      payload = null;
    }
    EMPTY_STRING = payload;
  }

  public static byte[] serialize(Object value) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream serial = new ObjectOutputStream(out);
      serial.writeObject(value);
      serial.flush();
      return out.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static byte[] readAll(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[8192];
    int n;
    while ((n = in.read(buffer)) >= 0) {
      out.write(buffer, 0, n);
    }
    return out.toByteArray();
  }
}
