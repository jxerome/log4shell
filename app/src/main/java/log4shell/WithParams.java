package log4shell;

import com.unboundid.ldap.sdk.DN;

public interface WithParams {
  void setParams(DN params);
}
