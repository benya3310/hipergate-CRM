package com.knowgate.storage;

import java.sql.SQLException;

import java.util.Date;
import java.util.LinkedList;

import com.knowgate.storage.Column;
import com.knowgate.storage.Record;
import com.knowgate.storage.AbstractRecord;
import com.knowgate.storage.RecordSet;

import com.knowgate.misc.NameValuePair;

public interface Table {

  public String getName();

  public LinkedList<Column> columns();

  public DataSource getDataSource();
  
  public void close() throws SQLException,StorageException;

  public boolean exists(String sKey) throws StorageException;

  public Record load(String sKey) throws StorageException;

  public void store(AbstractRecord oRec) throws StorageException;

  public void delete(AbstractRecord oRec) throws StorageException;

  public void delete(final String sIndexColumn, final String sIndexValue) throws StorageException;

  public void dropIndex(final String sIndexColumn) throws StorageException;

  public RecordSet fetch() throws StorageException;

  public RecordSet fetch(final int iMaxRows, final int iOffset) throws StorageException;

  public RecordSet fetch(final String sIndexColumn, String sIndexValue) throws StorageException;

  public RecordSet fetch(final String sIndexColumn, String sIndexValueMin, String sIndexValueMax) throws StorageException;

  public RecordSet fetch(final String sIndexColumn, Date dtIndexValueMin, Date dtIndexValueMax) throws StorageException;

  public RecordSet fetch(final String sIndexColumn, String sIndexValue, final int iMaxRows) throws StorageException;

  public RecordSet fetch(NameValuePair[] aPairs, final int iMaxRows) throws StorageException;

  public RecordSet last(final String sOrderByColumn, final int iMaxRows, final int iOffset) throws StorageException;

  public void truncate() throws StorageException;

}
