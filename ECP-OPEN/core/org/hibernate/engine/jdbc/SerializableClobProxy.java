package org.hibernate.engine.jdbc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Clob;

public class SerializableClobProxy
  implements InvocationHandler, Serializable
{
  private static final Class[] PROXY_INTERFACES = { Clob.class, WrappedClob.class, Serializable.class };
  private final transient Clob clob;

  protected SerializableClobProxy(Clob clob)
  {
    this.clob = clob;
  }

  public Clob getWrappedClob() {
    if (this.clob == null) {
      throw new IllegalStateException("Clobs may not be accessed after serialization");
    }

    return this.clob;
  }

  public Object invoke(Object proxy, Method method, Object[] args)
    throws Throwable
  {
    if ("getWrappedClob".equals(method.getName()))
      return getWrappedClob();
    try
    {
      return method.invoke(getWrappedClob(), args);
    }
    catch (AbstractMethodError e) {
      throw new Exception("The JDBC driver does not implement the method: " + method, e);
    }
    catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  public static Clob generateProxy(Clob clob)
  {
    return (Clob)Proxy.newProxyInstance(getProxyClassLoader(), PROXY_INTERFACES, new SerializableClobProxy(clob));
  }

  public static ClassLoader getProxyClassLoader()
  {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = WrappedClob.class.getClassLoader();
    }
    return cl;
  }
}