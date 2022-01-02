package log4shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class IO {

  public static final byte[] EMPTY_STRING;

  static {
    byte[] payload;
    try {
      payload = IO.serialize("");
    } catch (IOException e) {
      e.printStackTrace();
      payload = null;
    }
    EMPTY_STRING = payload;
  }

  public static byte[] serialize(String value) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream serial = new ObjectOutputStream(out);
    serial.writeObject(value);
    serial.flush();
    return out.toByteArray();
  }
}
