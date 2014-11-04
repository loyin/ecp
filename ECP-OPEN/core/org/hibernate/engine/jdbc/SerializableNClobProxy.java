package org.hibernate.engine.jdbc;

import java.lang.reflect.Proxy;
import java.sql.Clob;

public class SerializableNClobProxy extends SerializableClobProxy
{
  private static final Class NCLOB_CLASS = loadNClobClassIfAvailable();

  private static final Class[] PROXY_INTERFACES = { determineNClobInterface(), WrappedClob.class };

  private static Class loadNClobClassIfAvailable()
  {
    try
    {
      return getProxyClassLoader().loadClass("java.sql.NClob");
    } catch (ClassNotFoundException e) {
    }
    return null;
  }

  private static Class determineNClobInterface()
  {
    return (NCLOB_CLASS == null) ? Clob.class : NCLOB_CLASS;
  }

  public static boolean isNClob(Clob clob) {
    return (NCLOB_CLASS != null) && (NCLOB_CLASS.isInstance(clob));
  }

  protected SerializableNClobProxy(Clob clob)
  {
    super(clob);
  }

  public static Clob generateProxy(Clob clob)
  {
    return (Clob)Proxy.newProxyInstance(getProxyClassLoader(), PROXY_INTERFACES, new SerializableNClobProxy(clob));
  }

  public static ClassLoader getProxyClassLoader()
  {
    return SerializableClobProxy.getProxyClassLoader();
  }
}