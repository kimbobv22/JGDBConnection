package com.jg.db;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import com.jg.db.vo.JGDBParameter;
import com.jg.db.vo.JGDBQuery;
import com.jg.vo.JGDataset;
import com.jg.vo.JGDatasetColumn;
import com.jg.vo.JGDatasetRow;

public class JGDBConnection{
	protected Connection _connection;
	
	protected JGDBConfig _DBConfig = null;
	protected int _defaultResultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
	protected int _defaultResultSetConcurrency = ResultSet.CONCUR_READ_ONLY;
	
	public JGDBConfig getDBConfig(){
		return _DBConfig;
	}
	public int getDefaultResultSetType(){
		return _defaultResultSetType;
	}
	
	static protected JGDBLoggingDef _loggingDef = new JGDBLoggingDef() {
		@Override
		protected void beforeExecuteUpdate(String query_, Object[] parameters_){}
		
		@Override
		protected void beforeExecuteQuery(String query_, Object[] parameters_){}
		
		@Override
		protected void beforeCallProcedure(String query_, Object[] parameters_){}
	};
	static public void setLoggingDef(JGDBLoggingDef loggingDef_){
		_loggingDef = loggingDef_;
	}
	static public JGDBLoggingDef getLoggingDef(){
		return _loggingDef;
	}
	
	/**
	 * set default {@link ResultSet} type <br>
	 * 
	 * @param resultSetType_ type of {@link ResultSet} type
	 */
	public void setDefaultResultSetType(int resultSetType_){
		_defaultResultSetType = resultSetType_;
	}
	
	public int getDefaultResultSetConcurrency(){
		return _defaultResultSetConcurrency;
	}
	
	/**
	 * set default {@link ResultSet} concurrency<br>
	 * 
	 * @param resultSetType_ type of {@link ResultSet} concurrency
	 */
	public void setDefaultResultSetConcurrency(int resultSetConcurrency_){
		_defaultResultSetConcurrency = resultSetConcurrency_;
	}
	
	protected JGDBBLOBHandler _blobHandler = null;
	public JGDBBLOBHandler blobHandler(){
		if(_blobHandler == null){
			_blobHandler = new JGDBBLOBHandler(this);
		}
		
		return _blobHandler;
	}
	
	/**
	 * database connection instance<br>
	 * *refer {@link JGDBConfig} <br>
	 * 
	 * @param dBConfig_
	 * @throws Exception
	 */
	public JGDBConnection(JGDBConfig dBConfig_) throws Exception{
		_DBConfig = dBConfig_;
		
		try{
			Class.forName(_DBConfig._JDBCClassName);
		}catch(ClassNotFoundException ex_) {
			throw new Exception("can't load JDBC Driver", ex_);
		}
		
		try{
			DriverManager.setLoginTimeout(20);
			_connection = DriverManager.getConnection(_DBConfig._URL
					,_DBConfig._userName
					,_DBConfig._password);
			_connection.setAutoCommit(false);
		}catch(SQLException ex_){
			throw new Exception("failed to initialize JDBC Driver", ex_);
		}
	}
	
	public Connection getConnection(){
		return _connection;
	}
	
	public void commit() throws Exception{
		try{
			_connection.commit();
		}catch(Exception ex_){
			throw new Exception("error with commit command to Database",ex_);
		}
	}
	public void rollback() throws Exception{
		try{
			_connection.rollback();
		}catch(Exception ex_){
			throw new Exception("error with rollback command to Database",ex_);
		}
		
	}
	
	public void release(){
		try{
			_connection.rollback();
			_connection.close();
		}catch(SQLException ex_){
			_connection = null;
		}
	}
	
	protected void fillStatement(PreparedStatement statement_, Object[] parameters_) throws Exception{
		try{
			if(parameters_ != null){
				int count_ = parameters_.length;
				for(int index_=0;index_<count_;++index_){
					Object object_ = parameters_[index_];
					statement_.setObject(index_+1, object_);
				}
			}
		}catch(SQLException ex_){
			throw new Exception("can't fill statament with objects", ex_);
		}
	}
	
	public PreparedStatement createStatement(String query_, Object[] parameters_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		PreparedStatement statement_ = null;
		try{
			statement_ = _connection.prepareStatement(query_, resultSetType_, resultSetConcurrency_);
		}catch (SQLException ex_){
			throw new Exception("can't make statament with query", ex_);
		}

		fillStatement(statement_, parameters_);
		return statement_;
	}
	public PreparedStatement createStatement(String query_, Object[] parameters_, int resultSetType_) throws Exception{
		return createStatement(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	public PreparedStatement createStatement(String query_, Object[] parameters_) throws Exception{
		return createStatement(query_, parameters_, _defaultResultSetType);
	}
	public PreparedStatement createStatement(String query_) throws Exception{
		return createStatement(query_, (Object[])null);
	}
	
	public CallableStatement createCallableStatement(String query_, Object[] parameters_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		CallableStatement statement_ = null;
		
		try{
			statement_ = _connection.prepareCall(query_, resultSetType_, resultSetConcurrency_);
		}catch (SQLException ex_){
			throw new Exception("can't make callable statament", ex_);
		}
		
		fillStatement(statement_, parameters_);
		return statement_;
		
	}
	public CallableStatement createCallableStatement(String query_, Object[] parameters_, int resultSetType_) throws Exception{
		return createCallableStatement(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	public CallableStatement createCallableStatement(String query_, Object[] parameters_) throws Exception{
		return createCallableStatement(query_, parameters_, _defaultResultSetType);
	}
	public CallableStatement createCallableStatement(String query_) throws Exception{
		return createCallableStatement(query_, (Object[])null);
	}
	
	public JGDataset executeQuery(String query_, Object[] objects_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		PreparedStatement pStatement_ = null;
		ResultSet resultSet_ = null;
		
		try{
			try{
				_loggingDef.beforeExecuteQuery(query_, objects_);
				pStatement_ = createStatement(query_,objects_, resultSetType_, resultSetConcurrency_);
				resultSet_ = pStatement_.executeQuery();
			}catch(SQLException ex_){
				throw new Exception("failed to execute query", ex_);
			}
			
			JGDataset dataSet_ = new JGDataset();
			ResultSetMetaData resultSetMetaData_ = null;
			int columnCount_ = 0;
			try{
				//for search columns
				resultSetMetaData_ = resultSet_.getMetaData();
				columnCount_ = resultSetMetaData_.getColumnCount();
				for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
					int rColumnIndex_ = columnIndex_+1;
					String columnLabel_ = resultSetMetaData_.getColumnLabel(rColumnIndex_);
					if(columnLabel_ == null || columnLabel_.length() == 0)
						columnLabel_ = resultSetMetaData_.getColumnName(rColumnIndex_);
					dataSet_.addColumn(columnLabel_);
				}
			}catch(SQLException ex_){
				throw new Exception("failed to add column to dataset from resultSet metadata", ex_);
			}
			
			try{
				
				String characterEncoding_ = _DBConfig._characterEncoding;
				while(resultSet_.next()){
					int rowIndex_ = dataSet_.addRow();
					
					for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
						int rColumnIndex_ = columnIndex_+1;
						int dbColumnType_ = resultSetMetaData_.getColumnType(rColumnIndex_);
						
						switch(dbColumnType_){
							case Types.NUMERIC:
							case Types.INTEGER:
							case Types.BIGINT:
							case Types.FLOAT:
							case Types.DOUBLE:
							case Types.DECIMAL:
							case Types.CHAR:
							case Types.VARCHAR:
							case Types.BOOLEAN:
								dataSet_.setColumnValue(columnIndex_, rowIndex_, resultSet_.getObject(rColumnIndex_));
								break;
							case Types.DATE:
							case Types.TIMESTAMP:
								java.sql.Date dateValue_ = resultSet_.getDate(rColumnIndex_);
								if(dateValue_ != null)
									dataSet_.setColumnValue(columnIndex_, rowIndex_, dateValue_.toString());
							case Types.TIME:
								break;
							case Types.LONGNVARCHAR:
							case Types.LONGVARBINARY:
							case Types.LONGVARCHAR:
							case Types.CLOB:
							case Types.BLOB:
							case Types.BINARY:
							case Types.BIT:
								try{
									dataSet_.setColumnValue(columnIndex_, rowIndex_, new String(resultSet_.getBytes(rColumnIndex_),characterEncoding_));
								}catch(UnsupportedEncodingException ex_){
									throw new Exception("failed to convert byte to string", ex_);
								}
								
								break;
							default : break;
						}
					}
				}
			}catch(SQLException ex_){
				throw new Exception("failed to add row to dataset from resultSet row data", ex_);
			}
			
			dataSet_.apply();
			
			try{
				resultSet_.close();
				pStatement_.close();
			}catch(SQLException ex_){
				throw new Exception("failed to close row statment,resultSet", ex_);
			}
			
			return dataSet_;
		}catch(Exception ex_){
			throw ex_;
		}finally{
			if(pStatement_ != null) pStatement_.close();
			if(resultSet_ != null) resultSet_.close();
		}
	}
	public JGDataset executeQuery(String query_, Object[] objects_, int resultSetType_) throws Exception{
		return executeQuery(query_,objects_, resultSetType_, _defaultResultSetConcurrency);
	}
	public JGDataset executeQuery(String query_, Object[] objects_) throws Exception{
		return executeQuery(query_,objects_, _defaultResultSetType);
	}
	public JGDataset executeQuery(String query_) throws Exception{
		return executeQuery(query_,(Object[])null);
	}
	
	public JGDataset executeQuery(JGDBQuery query_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		return executeQuery(query_.getQuery(), query_.parameterToArray(), resultSetType_, resultSetConcurrency_);
	}
	public JGDataset executeQuery(JGDBQuery query_, int resultSetType_) throws Exception{
		return executeQuery(query_, resultSetType_, _defaultResultSetConcurrency);
	}
	public JGDataset executeQuery(JGDBQuery query_) throws Exception{
		return executeQuery(query_, _defaultResultSetType);
	}
	
	public Object executeQueryAndGetFirst(String query_, Object[] objects_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		JGDataset result_ = executeQuery(query_, objects_, resultSetType_, resultSetConcurrency_);
		return result_.getColumnValue(0, 0);
	}
	public Object executeQueryAndGetFirst(String query_, Object[] objects_, int resultSetType_) throws Exception{
		return executeQueryAndGetFirst(query_, objects_, resultSetType_, _defaultResultSetConcurrency);
	}
	public Object executeQueryAndGetFirst(String query_, Object[] objects_) throws Exception{
		return executeQueryAndGetFirst(query_, objects_, _defaultResultSetType);
	}
	public Object executeQueryAndGetFirst(String query_) throws Exception{
		return executeQueryAndGetFirst(query_, (Object[])null);
	}
	
	public Object executeQueryAndGetFirst(JGDBQuery query_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		return executeQueryAndGetFirst(query_.getQuery(), query_.parameterToArray(), resultSetType_, resultSetConcurrency_);
	}
	public Object executeQueryAndGetFirst(JGDBQuery query_, int resultSetType_) throws Exception{
		return executeQueryAndGetFirst(query_, resultSetType_, _defaultResultSetConcurrency);
	}
	public Object executeQueryAndGetFirst(JGDBQuery query_) throws Exception{
		return executeQueryAndGetFirst(query_, _defaultResultSetType);
	}
	
	public int executeUpdate(String query_, Object[] objects_) throws Exception{
		PreparedStatement pStatement_ = null;
		int result_ = 0;
		try{
			_loggingDef.beforeExecuteUpdate(query_, objects_);
			pStatement_ = createStatement(query_, objects_);
			result_ = pStatement_.executeUpdate();
		}catch(SQLException ex_){
			throw new Exception("failed to executeUpdate query" ,ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
		}
		
		return result_;
	}
	public int executeUpdate(String query_) throws Exception{
		return executeUpdate(query_, (Object[]) null);
	}
	
	public int executeUpdate(JGDBQuery query_) throws Exception{
		return executeUpdate(query_.getQuery(), query_.parameterToArray());
	}
	
	public int executeUpdate(JGDataset dataSet_, String tableName_, boolean executeAllColumns_) throws Exception{
		JGDBQuery query_ = new JGDBQuery();
		
		// execute insert & update
		int columnCount_ = dataSet_.getColumnCount();
		int rowCount_ = dataSet_.getRowCount();
		int result_ = 0;
		for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
			JGDatasetRow rowItem_ = dataSet_.getRow(rowIndex_);
			JGDBParameter parameter_ = new JGDBParameter(tableName_);
			parameter_.setTargetName(tableName_);
			
			int rowStatus_ = rowItem_.getRowStatus();
			boolean doExecute_ = false;
			switch(rowStatus_){
				case JGDatasetRow.ROWSTATUS_INSERT:{
					doExecute_ = true;
					for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
						JGDatasetColumn columnItem_ = dataSet_.getColumn(columnIndex_);
						String columnName_ = columnItem_.getName();
						parameter_.addValue(columnName_, rowItem_.getColumnValue(columnName_));
					}
					query_.fillQueryForINSERT(parameter_);
					break;
				}
				case JGDatasetRow.ROWSTATUS_UPDATE:{
					for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
						JGDatasetColumn columnItem_ = dataSet_.getColumn(columnIndex_);
						String columnName_ = columnItem_.getName();
						if(columnItem_.isKey()){
							parameter_.addKey(columnName_,rowItem_.getColumnValue(columnName_));
						}
						
						if(executeAllColumns_ || rowItem_.isColumnModified(columnName_)){
							doExecute_ = true;
							parameter_.addValue(columnName_, rowItem_.getColumnValue(columnName_));
						}
					}
					
					query_.fillQueryForUPDATE(parameter_);
					break;
				}
				default: break;
			}
			
			if(doExecute_)
				result_ = result_ + executeUpdate(query_);
		}
		
		// execute delete
		ArrayList<JGDatasetColumn> keyColumnList_ = dataSet_.getKeyColumnList();
		int keyCount_ = keyColumnList_.size();
		
		ArrayList<JGDatasetRow> deletedRowList_ = dataSet_.getDeletedRowData();
		rowCount_ = deletedRowList_.size();
		for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
			JGDatasetRow rowItem_ = dataSet_.getDeletedRow(rowIndex_);
			
			JGDBParameter parameter_ = new JGDBParameter(tableName_);
			parameter_.setTargetName(tableName_);
			for(int columnIndex_=0;columnIndex_<keyCount_;++columnIndex_){
				JGDatasetColumn columnItem_ = keyColumnList_.get(columnIndex_);
				String columnName_ = columnItem_.getName();				
				parameter_.addKey(columnName_,rowItem_.getColumnValue(columnName_));
			}
			
			query_.fillQueryForDELETE(parameter_);
			
			result_ = result_ + executeUpdate(query_);
		}
		
		return result_;
	}
	public int executeUpdate(JGDataset dataSet_, String tableName_) throws Exception{
		return executeUpdate(dataSet_, tableName_, false);
	}
	
	public boolean callProcedure(String query_, Object[] parameters_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		CallableStatement statement_ = createCallableStatement(query_, parameters_, resultSetType_, resultSetConcurrency_);
		boolean result_ = false;
		try{
			_loggingDef.beforeCallProcedure(query_, parameters_);
			result_ = statement_.execute();
		}catch(SQLException ex_){
			throw new Exception("failed to execute procedure", ex_);
		}
		
		try{
			statement_.close();
		}catch(SQLException ex_){
			throw new Exception("failed to close statement", ex_);
		}
		
		return result_ ;
	}
	public boolean callProcedure(String query_, Object[] parameters_, int resultSetType_) throws Exception{
		return callProcedure(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	public boolean callProcedure(String query_, Object[] parameters_) throws Exception{
		return callProcedure(query_, parameters_, _defaultResultSetType);
	}
	public boolean callProcedure(String query_) throws Exception{
		return callProcedure(query_, (Object[])null);
	}
	
	public boolean callProcedure(JGDBQuery query_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		return callProcedure(query_.getQuery(), query_.parameterToArray(), resultSetType_, resultSetConcurrency_);
	}
	public boolean callProcedure(JGDBQuery query_, int resultSetType_) throws Exception{
		return callProcedure(query_, resultSetType_, _defaultResultSetConcurrency);
	}
	public boolean callProcedure(JGDBQuery query_) throws Exception{
		return callProcedure(query_, _defaultResultSetType);
	}
	
	@Override
	protected void finalize() throws Throwable{
		try{
			release();
		}catch(Exception ex_){}finally{
			super.finalize();
		}
	}
}
