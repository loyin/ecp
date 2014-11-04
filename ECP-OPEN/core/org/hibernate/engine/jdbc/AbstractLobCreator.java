package org.hibernate.engine.jdbc;

import java.sql.Blob;
import java.sql.Clob;

public abstract class AbstractLobCreator
  implements LobCreator
{
  public Blob wrap(Blob blob)
  {
    return SerializableBlobProxy.generateProxy(blob);
  }

  public Clob wrap(Clob clob)
  {
    if (SerializableNClobProxy.isNClob(clob)) {
      return SerializableNClobProxy.generateProxy(clob);
    }

    return SerializableClobProxy.generateProxy(clob);
  }
}