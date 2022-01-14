package log4shell.aws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstanceSession {
  @JsonProperty("Code")
  private String code;

  @JsonProperty("Type")
  private String type;

  @JsonProperty("LastUpdated")
  private Instant lastUpdated;

  @JsonProperty("AccessKeyId")
  private String accessKeyId;

  @JsonProperty("SecretAccessKey")
  private String secretAccessKey;

  @JsonProperty("Token")
  private String sessionToken;

  @JsonProperty("Expiration")
  private Instant sessionExpiration;

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner(System.lineSeparator());
    if (code != null) joiner.add("Code=" + code);
    if (type != null) joiner.add("Type=" + type);
    if (lastUpdated != null) joiner.add("LastUpdate=" + lastUpdated);
    if (accessKeyId != null) joiner.add("export AWS_ACCESS_KEY_ID='" + accessKeyId + '\'');
    if (secretAccessKey != null)
      joiner.add("export AWS_SECRET_ACCESS_KEY='" + secretAccessKey + '\'');
    if (sessionToken != null) joiner.add("export AWS_SESSION_TOKEN='" + sessionToken + '\'');
    if (sessionExpiration != null)
      joiner.add("export AWS_SESSION_EXPIRATION='" + sessionExpiration + '\'');
    return joiner.toString();
  }
}
