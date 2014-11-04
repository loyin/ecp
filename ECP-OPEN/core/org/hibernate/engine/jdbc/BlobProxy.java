package org.hibernate.engine.jdbc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Blob;
import java.sql.SQLException;

public class BlobProxy
  implements InvocationHandler
{
  private static final Class[] PROXY_INTERFACES = { Blob.class, BlobImplementer.class };
  private InputStream stream;
  private long length;
  private boolean needsReset = false;

  private BlobProxy(byte[] bytes)
  {
    this.stream = new ByteArrayInputStream(bytes);
    this.length = bytes.length;
  }

  private BlobProxy(InputStream stream, long length)
  {
    this.stream = stream;
    this.length = length;
  }

  private long getLength() {
    return this.length;
  }

  private InputStream getStream() throws SQLException {
    try {
      if (this.needsReset)
        this.stream.reset();
    }
    catch (IOException ioe)
    {
      throw new SQLException("could not reset reader");
    }
    this.needsReset = true;
    return this.stream;
  }

  public Object invoke(Object proxy, Method method, Object[] args)
    throws Throwable
  {
    if ("length".equals(method.getName())) {
      return new Long(getLength());
    }
    if (("getBinaryStream".equals(method.getName())) && (method.getParameterTypes().length == 0)) {
      return getStream();
    }
    if ("free".equals(method.getName())) {
      this.stream.close();
      return null;
    }
    if ("toString".equals(method.getName())) {
      return super.toString();
    }
    if ("equals".equals(method.getName())) {
      return Boolean.valueOf(proxy == args[0]);
    }
    if ("hashCode".equals(method.getName())) {
      return new Integer(super.hashCode());
    }
    throw new UnsupportedOperationException("Blob may not be manipulated from creating session");
  }

  public static Blob generateProxy(byte[] bytes)
  {
    return (Blob)Proxy.newProxyInstance(getProxyClassLoader(), PROXY_INTERFACES, new BlobProxy(bytes));
  }

  public static Blob generateProxy(InputStream stream, long length)
  {
    return (Blob)Proxy.newProxyInstance(getProxyClassLoader(), PROXY_INTERFACES, new BlobProxy(stream, length));
  }

  private static ClassLoader getProxyClassLoader()
  {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = BlobImplementer.class.getClassLoader();
    }
    return cl;
  }
}