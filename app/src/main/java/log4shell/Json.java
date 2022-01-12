package log4shell;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Json {
  public static final ObjectMapper mapper =
      new ObjectMapper()
          .configure(JsonParser.Feature.IGNORE_UNDEFINED, true)
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
}
