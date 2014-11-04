package org.hibernate.engine.jdbc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Blob;

public class SerializableBlobProxy
  implements InvocationHandler, Serializable
{
  private static final Class[] PROXY_INTERFACES = { Blob.class, WrappedBlob.class, Serializable.class };
  private final transient Blob blob;

  private SerializableBlobProxy(Blob blob)
  {
    this.blob = blob;
  }

  public Blob getWrappedBlob() {
    if (this.blob == null) {
      throw new IllegalStateException("Blobs may not be accessed after serialization");
    }

    return this.blob;
  }

  public Object invoke(Object proxy, Method method, Object[] args)
    throws Throwable
  {
    if ("getWrappedBlob".equals(method.getName()))
      return getWrappedBlob();
    try
    {
      return method.invoke(getWrappedBlob(), args);
    }
    catch (AbstractMethodError e) {
      throw new Exception("The JDBC driver does not implement the method: " + method, e);
    }
    catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  public static Blob generateProxy(Blob blob)
  {
    return (Blob)Proxy.newProxyInstance(getProxyClassLoader(), PROXY_INTERFACES, new SerializableBlobProxy(blob));
  }

  public static ClassLoader getProxyClassLoader()
  {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = WrappedBlob.class.getClassLoader();
    }
    return cl;
  }
}