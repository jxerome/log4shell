package log4shell;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;

public abstract class Command {
  private final DN dn;

  protected Command(DN dn) {
    this.dn = dn;
  }

  public DN getDn() {
    return dn;
  }

  abstract Entry response();
}
