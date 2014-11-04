package org.hibernate.engine.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Clob;
import java.sql.SQLException;

import org.apache.commons.io.input.ReaderInputStream;

public class ClobProxy
  implements InvocationHandler
{
  private static final Class[] PROXY_INTERFACES = { Clob.class, ClobImplementer.class };
  private Reader reader;
  private long length;
  private boolean needsReset = false;

  protected ClobProxy(String string)
  {
    this.reader = new StringReader(string);
    this.length = string.length();
  }

  protected ClobProxy(Reader reader, long length)
  {
    this.reader = reader;
    this.length = length;
  }

  protected long getLength() {
    return this.length;
  }

  protected InputStream getAsciiStream() throws SQLException {
    resetIfNeeded();
    return new ReaderInputStream(this.reader);
  }

  protected Reader getCharacterStream() throws SQLException {
    resetIfNeeded();
    return this.reader;
  }

  public Object invoke(Object proxy, Method method, Object[] args)
    throws Throwable
  {
    if ("length".equals(method.getName())) {
      return new Long(getLength());
    }
    if ("getAsciiStream".equals(method.getName())) {
      return getAsciiStream();
    }
    if ("getCharacterStream".equals(method.getName())) {
      return getCharacterStream();
    }
    if ("free".equals(method.getName())) {
      this.reader.close();
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
    throw new UnsupportedOperationException("Clob may not be manipulated from creating session");
  }

  protected void resetIfNeeded() throws SQLException {
    try {
      if (this.needsReset)
        this.reader.reset();
    }
    catch (IOException ioe)
    {
      throw new SQLException("could not reset reader");
    }
    this.needsReset = true;
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
      cl = ClobImplementer.class.getClassLoader();
    }
    return cl;
  }
}