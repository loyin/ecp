package org.hibernate.engine.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;

public class NonContextualLobCreator extends AbstractLobCreator
  implements LobCreator
{
  public static final NonContextualLobCreator INSTANCE = new NonContextualLobCreator();

  public Blob createBlob(byte[] bytes)
  {
    return BlobProxy.generateProxy(bytes);
  }

  public Blob createBlob(InputStream stream, long length)
  {
    return BlobProxy.generateProxy(stream, length);
  }

  public Clob createClob(String string)
  {
    return ClobProxy.generateProxy(string);
  }

  public Clob createClob(Reader reader, long length)
  {
    return ClobProxy.generateProxy(reader, length);
  }

  public Clob createNClob(String string)
  {
    return NClobProxy.generateProxy(string);
  }

  public Clob createNClob(Reader reader, long length)
  {
    return NClobProxy.generateProxy(reader, length);
  }
}