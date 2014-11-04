package org.hibernate.engine.jdbc;

import java.sql.Blob;

public abstract interface WrappedBlob
{
  public abstract Blob getWrappedBlob();
}