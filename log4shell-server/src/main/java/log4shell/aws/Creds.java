package log4shell.aws;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Creds {
  private String profile;
  private String roleArn;
  private String accessKeyId;
  private String secretAccessKey;
  private String sessionToken;
  private String region;
  private String webIdentityTokenFile;
  private String configFile;
  private String credentialsFile;
  private InstanceSession instanceSession;

  public String getRoleArn() {
    return roleArn;
  }

  public void setRoleArn(String roleArn) {
    this.roleArn = roleArn;
  }

  public String getAccessKeyId() {
    return accessKeyId;
  }

  public void setAccessKeyId(String accessKeyId) {
    this.accessKeyId = accessKeyId;
  }

  public String getSecretAccessKey() {
    return secretAccessKey;
  }

  public void setSecretAccessKey(String secretAccessKey) {
    this.secretAccessKey = secretAccessKey;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public void setSessionToken(String sessionToken) {
    this.sessionToken = sessionToken;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public String getWebIdentityTokenFile() {
    return webIdentityTokenFile;
  }

  public void setWebIdentityTokenFile(String webIdentityTokenFile) {
    this.webIdentityTokenFile = webIdentityTokenFile;
  }

  public String getConfigFile() {
    return configFile;
  }

  public void setConfigFile(String configFile) {
    this.configFile = configFile;
  }

  public String getCredentialsFile() {
    return credentialsFile;
  }

  public void setCredentialsFile(String credentialsFile) {
    this.credentialsFile = credentialsFile;
  }

  public InstanceSession getInstanceSession() {
    return instanceSession;
  }

  public void setInstanceSession(InstanceSession instanceSession) {
    this.instanceSession = instanceSession;
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner("    ", "    ", "\n");
    if (profile != null) joiner.add("AWS_PROFILE=" + profile);
    if (accessKeyId != null) joiner.add("AWS_ACCESS_KEY_ID=" + accessKeyId);
    if (secretAccessKey != null) joiner.add("AWS_SECRET_ACCESS_KEY=" + secretAccessKey);
    if (sessionToken != null) joiner.add("AWS_SESSION_TOKEN=" + sessionToken);
    if (roleArn != null) joiner.add("AWS_ROLE_ARN=" + roleArn);
    if (region != null) joiner.add("AWS_REGION=" + region);
    if (webIdentityTokenFile != null)
      joiner.add("AWS_WEB_IDENTITY_TOKEN_FILE=" + webIdentityTokenFile);

    StringBuilder sb = new StringBuilder();
    sb.append("======== AWS Credentials ========\n");
    sb.append("---- Environment ----\n").append(joiner);
    sb.append('\n');
    if (configFile != null) {
      sb.append("---- .aws/config ----\n");
      sb.append(configFile);
      sb.append("\n");
    }
    if (credentialsFile != null) {
      sb.append("---- .aws/credentials ----\n");
      sb.append(credentialsFile);
      sb.append("\n");
    }
    if (instanceSession != null) {
      sb.append("---- Instance Session ----\n");
      sb.append(credentialsFile);
      sb.append("\n");
    }
    return sb.toString();
  }
}
