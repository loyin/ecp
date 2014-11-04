package org.hibernate.engine.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;

public abstract interface LobCreator
{
  public abstract Blob wrap(Blob paramBlob);

  public abstract Clob wrap(Clob paramClob);

  public abstract Blob createBlob(byte[] paramArrayOfByte);

  public abstract Blob createBlob(InputStream paramInputStream, long paramLong);

  public abstract Clob createClob(String paramString);

  public abstract Clob createClob(Reader paramReader, long paramLong);

  public abstract Clob createNClob(String paramString);

  public abstract Clob createNClob(Reader paramReader, long paramLong);
}