package org.hibernate.engine.jdbc;

import java.sql.Clob;

public abstract interface WrappedClob
{
  public abstract Clob getWrappedClob();
}