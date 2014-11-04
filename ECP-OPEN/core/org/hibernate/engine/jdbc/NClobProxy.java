package org.hibernate.engine.jdbc;

import java.io.Reader;
import java.lang.reflect.Proxy;
import java.sql.Clob;

public class NClobProxy extends ClobProxy
{
  public static final Class[] PROXY_INTERFACES = { determineNClobInterface(), NClobImplementer.class };

  private static Class determineNClobInterface()
  {
    try
    {
      return getProxyClassLoader().loadClass("java.sql.NClob");
    } catch (ClassNotFoundException e) {
    }
    return Clob.class;
  }

  protected NClobProxy(String string)
  {
    super(string);
  }

  protected NClobProxy(Reader reader, long length) {
    super(reader, length);
  }

  public static Clob generateProxy(String string)
  {
    return (Clob)Proxy.newProxyInstance(getProxyClassLoader(), PROXY_INTERFACES, new ClobProxy(string));
  }

  public static Clob generateProxy(Reader reader, long length)
  {
    return (Clob)Proxy.newProxyInstance(getProxyClassLoader(), PROXY_INTERFACES, new ClobProxy(reader, length));
  }

  protected static ClassLoader getProxyClassLoader()
  {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = NClobImplementer.class.getClassLoader();
    }
    return cl;
  }
}