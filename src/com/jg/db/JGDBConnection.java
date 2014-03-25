package com.jg.db;

import java.io.InputStream;
import java.io.Reader;
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

/**
 * DB연결을 위한 지원 라이브러리입니다.
 * @author Hyeong yeon. Kim. kimbobv22@gmail.com
 * @version 1.0.1
 */
public class JGDBConnection{
	protected Connection _connection;
	
	protected JGDBConfig _DBConfig = null;
	protected int _defaultResultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
	protected int _defaultResultSetConcurrency = ResultSet.CONCUR_READ_ONLY;
	/**
	 * DB 접속설정을 반환합니다.
	 * @return DB 접속설정
	 */
	public JGDBConfig getDBConfig(){
		return _DBConfig;
	}
	
	static protected JGDBLoggingDef _loggingDef = new JGDBLoggingDef() {
		@Override
		protected void beforeExecuteUpdate(String query_, Object[] parameters_){}
		
		@Override
		protected void beforeExecuteQuery(String query_, Object[] parameters_){}
		
		@Override
		protected void beforeCallProcedure(String query_, Object[] parameters_){}
		
		@Override
		protected void beforeInsertBLOB(String query_, InputStream inputStream_){}
		@Override
		protected void beforeSelectBLOB(String query_, Object[] parameters_){}
		@Override
		protected void beforeUpdateBLOB(String query_, InputStream inputStream_, Object[] parameters_){}
		
		@Override
		protected void beforeInsertCLOB(String query_, Reader reader_){}
		@Override
		protected void beforeSelectCLOB(String query_, Object[] parameters_){}
		@Override
		protected void beforeUpdateCLOB(String query_, Reader reader_, Object[] parameters_){}
	};
	/**
	 * Logger를 설정합니다.
	 * @param loggingDef_ logger
	 */
	static public void setLoggingDef(JGDBLoggingDef loggingDef_){
		_loggingDef = loggingDef_;
	}
	/**
	 * Logger를 반환합니다.
	 */
	static public JGDBLoggingDef getLoggingDef(){
		return _loggingDef;
	}
	/**
	 * {@link ResultSet}유형 기본값을 반환합니다. <br>
	 * 
	 * @return {@link ResultSet}유형
	 */
	public int getDefaultResultSetType(){
		return _defaultResultSetType;
	}
	
	/**
	 * {@link ResultSet}유형 기본값을 설정합니다. <br>
	 * 
	 * @param resultSetType_ {@link ResultSet}유형
	 */
	public void setDefaultResultSetType(int resultSetType_){
		_defaultResultSetType = resultSetType_;
	}
	
	/**
	 * {@link ResultSet}동시성 기본값을 반환합니다. <br>
	 * 
	 * @return {@link ResultSet}동시성
	 */
	public int getDefaultResultSetConcurrency(){
		return _defaultResultSetConcurrency;
	}
	
	/**
	 * {@link ResultSet}동시성 기본값을 설정합니다.<br>
	 * 
	 * @param resultSetConcurrency_ {@link ResultSet}동시성
	 */
	public void setDefaultResultSetConcurrency(int resultSetConcurrency_){
		_defaultResultSetConcurrency = resultSetConcurrency_;
	}
	
	protected JGDBLOBHandler _lobHandler = null;
	/**
	 * LOB핸들러를 반환합니다.
	 * 
	 * @return LOB핸들러
	 */
	public JGDBLOBHandler lobHandler(){
		if(_lobHandler == null){
			_lobHandler = new JGDBLOBHandler(this);
		}
		
		return _lobHandler;
	}
	
	/**
	 * DB Connection을 생성합니다.
	 * 
	 * @param dBConfig_ DB설정
	 * @see JGDBConfig
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
	
	/**
	 * 원본 DB Connection을 반환합니다.
	 * 
	 * @return 원본 DB Connection
	 */
	public Connection getConnection(){
		return _connection;
	}
	/**
	 * 작업을 커밋합니다.
	 */
	public void commit() throws Exception{
		try{
			_connection.commit();
		}catch(Exception ex_){
			throw new Exception("error with commit command to Database",ex_);
		}
	}
	/**
	 * 작업을 롤백합니다.
	 */
	public void rollback() throws Exception{
		try{
			_connection.rollback();
		}catch(Exception ex_){
			throw new Exception("error with rollback command to Database",ex_);
		}
		
	}
	
	/**
	 * 작업을 롤백하고 Connection을 닫습니다.
	 */
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
	
	/**
	 * 쿼리 문자열과 쿼리 매개변수를 가지고 {@link PreparedStatement}를 생성합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 * @param resultSetConcurrency_ {@link ResultSet}동시성
	 */
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
	/**
	 * 쿼리 문자열과 쿼리 매개변수를 가지고 {@link PreparedStatement}를 생성합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 */
	public PreparedStatement createStatement(String query_, Object[] parameters_, int resultSetType_) throws Exception{
		return createStatement(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	/**
	 * 쿼리 문자열과 쿼리 매개변수를 가지고 {@link PreparedStatement}를 생성합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 */
	public PreparedStatement createStatement(String query_, Object[] parameters_) throws Exception{
		return createStatement(query_, parameters_, _defaultResultSetType);
	}
	/**
	 * 쿼리 문자열과 쿼리 매개변수를 가지고 {@link PreparedStatement}를 생성합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 */
	public PreparedStatement createStatement(String query_) throws Exception{
		return createStatement(query_, (Object[])null);
	}
	
	/**
	 * 쿼리 문자열과 쿼리 매개변수를 가지고 {@link CallableStatement}를 생성합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 * @param resultSetConcurrency_ {@link ResultSet}동시성
	 */
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
	/**
	 * 쿼리 문자열과 쿼리 매개변수를 가지고 {@link CallableStatement}를 생성합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 */
	public CallableStatement createCallableStatement(String query_, Object[] parameters_, int resultSetType_) throws Exception{
		return createCallableStatement(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	/**
	 * 쿼리 문자열과 쿼리 매개변수를 가지고 {@link CallableStatement}를 생성합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 */
	public CallableStatement createCallableStatement(String query_, Object[] parameters_) throws Exception{
		return createCallableStatement(query_, parameters_, _defaultResultSetType);
	}
	/**
	 * 쿼리 문자열과 쿼리 매개변수를 가지고 {@link CallableStatement}를 생성합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 */
	public CallableStatement createCallableStatement(String query_) throws Exception{
		return createCallableStatement(query_, (Object[])null);
	}
	
	/**
	 * 쿼리를 수행하고 결과값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 * @param resultSetConcurrency_ {@link ResultSet}동시성
	 */
	public JGDataset executeQuery(String query_, Object[] parameters_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		PreparedStatement pStatement_ = null;
		ResultSet resultSet_ = null;
		
		try{
			try{
				_loggingDef.beforeExecuteQuery(query_, parameters_);
				pStatement_ = createStatement(query_,parameters_, resultSetType_, resultSetConcurrency_);
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
	/**
	 * 쿼리를 수행하고 결과값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 */
	public JGDataset executeQuery(String query_, Object[] parameters_, int resultSetType_) throws Exception{
		return executeQuery(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	/**
	 * 쿼리를 수행하고 결과값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 */
	public JGDataset executeQuery(String query_, Object[] parameters_) throws Exception{
		return executeQuery(query_, parameters_, _defaultResultSetType);
	}
	/**
	 * 쿼리를 수행하고 결과값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 */
	public JGDataset executeQuery(String query_) throws Exception{
		return executeQuery(query_,(Object[])null);
	}
	/**
	 * 쿼리를 수행하고 결과값을 반환합니다.
	 * 
	 * @param query_ 쿼리
	 * @param resultSetType_ {@link ResultSet}유형
	 * @param resultSetConcurrency_ {@link ResultSet}동시성
	 */
	public JGDataset executeQuery(JGDBQuery query_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		return executeQuery(query_.getQuery(), query_.parameterToArray(), resultSetType_, resultSetConcurrency_);
	}
	/**
	 * 쿼리를 수행하고 결과값을 반환합니다.
	 * 
	 * @param query_ 쿼리
	 * @param resultSetType_ {@link ResultSet}유형
	 */
	public JGDataset executeQuery(JGDBQuery query_, int resultSetType_) throws Exception{
		return executeQuery(query_, resultSetType_, _defaultResultSetConcurrency);
	}
	/**
	 * 쿼리를 수행하고 결과값을 반환합니다.
	 * 
	 * @param query_ 쿼리
	 */
	public JGDataset executeQuery(JGDBQuery query_) throws Exception{
		return executeQuery(query_, _defaultResultSetType);
	}
	
	/**
	 * 쿼리를 수행하고 첫번째 행의 첫번째 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 * @param resultSetConcurrency_ {@link ResultSet}동시성
	 */
	public Object executeQueryAndGetFirst(String query_, Object[] parameters_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		JGDataset result_ = executeQuery(query_, parameters_, resultSetType_, resultSetConcurrency_);
		return result_.getColumnValue(0, 0);
	}
	/**
	 * 쿼리를 수행하고 첫번째 행의 첫번째 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 */
	public Object executeQueryAndGetFirst(String query_, Object[] parameters_, int resultSetType_) throws Exception{
		return executeQueryAndGetFirst(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	/**
	 * 쿼리를 수행하고 첫번째 행의 첫번째 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 */
	public Object executeQueryAndGetFirst(String query_, Object[] parameters_) throws Exception{
		return executeQueryAndGetFirst(query_, parameters_, _defaultResultSetType);
	}
	/**
	 * 쿼리를 수행하고 첫번째 행의 첫번째 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 */
	public Object executeQueryAndGetFirst(String query_) throws Exception{
		return executeQueryAndGetFirst(query_, (Object[])null);
	}
	
	/**
	 * 쿼리를 수행하고 첫번째 행의 첫번째 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리
	 * @param resultSetType_ {@link ResultSet}유형
	 * @param resultSetConcurrency_ {@link ResultSet}동시성
	 */
	public Object executeQueryAndGetFirst(JGDBQuery query_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		return executeQueryAndGetFirst(query_.getQuery(), query_.parameterToArray(), resultSetType_, resultSetConcurrency_);
	}
	/**
	 * 쿼리를 수행하고 첫번째 행의 첫번째 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리
	 * @param resultSetType_ {@link ResultSet}유형
	 */
	public Object executeQueryAndGetFirst(JGDBQuery query_, int resultSetType_) throws Exception{
		return executeQueryAndGetFirst(query_, resultSetType_, _defaultResultSetConcurrency);
	}
	/**
	 * 쿼리를 수행하고 첫번째 행의 첫번째 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리
	 */
	public Object executeQueryAndGetFirst(JGDBQuery query_) throws Exception{
		return executeQueryAndGetFirst(query_, _defaultResultSetType);
	}
	
	/**
	 * 갱신을 수행합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @return 갱신갯수
	 */
	public int executeUpdate(String query_, Object[] parameters_) throws Exception{
		PreparedStatement pStatement_ = null;
		int result_ = 0;
		try{
			_loggingDef.beforeExecuteUpdate(query_, parameters_);
			pStatement_ = createStatement(query_, parameters_);
			result_ = pStatement_.executeUpdate();
		}catch(SQLException ex_){
			throw new Exception("failed to executeUpdate query" ,ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
		}
		
		return result_;
	}
	/**
	 * 갱신을 수행합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @return 갱신갯수
	 */
	public int executeUpdate(String query_) throws Exception{
		return executeUpdate(query_, (Object[]) null);
	}
	
	/**
	 * 갱신을 수행합니다.
	 * 
	 * @param query_ 쿼리
	 * @return 갱신갯수
	 */
	public int executeUpdate(JGDBQuery query_) throws Exception{
		return executeUpdate(query_.getQuery(), query_.parameterToArray());
	}
	
	/**
	 * 데이타셋을 가지고 갱신을 수행합니다.<br>
	 * 데이타셋의 행상태가 insert일 경우 삽입갱신을, update일 경우에 수정갱신을 수행하며,<br>
	 * 삭제된 행이 있을 경우, 삭제갱신을 수행합니다.
	 * 
	 * @param dataSet_ 데이타셋
	 * @param tableName_ 대상 테이블명
	 * @param executeAllColumns_ 전체열수행여부
	 * @return 갱신갯수
	 * @see JGDataset
	 */
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
	/**
	 * 데이타셋을 가지고 갱신을 수행합니다.
	 * 
	 * @param dataSet_ 데이타셋
	 * @param tableName_ 대상 테이블명
	 * @return 갱신갯수
	 * @see JGDataset
	 */
	public int executeUpdate(JGDataset dataSet_, String tableName_) throws Exception{
		return executeUpdate(dataSet_, tableName_, false);
	}
	/**
	 * 프로시져를 호출합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 * @param resultSetConcurrency_ {@link ResultSet}동시성
	 * @return 성공여부
	 */
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
	/**
	 * 프로시져를 호출합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param resultSetType_ {@link ResultSet}유형
	 * @return 성공여부
	 */
	public boolean callProcedure(String query_, Object[] parameters_, int resultSetType_) throws Exception{
		return callProcedure(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	/**
	 * 프로시져를 호출합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @return 성공여부
	 */
	public boolean callProcedure(String query_, Object[] parameters_) throws Exception{
		return callProcedure(query_, parameters_, _defaultResultSetType);
	}
	/**
	 * 프로시져를 호출합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @return 성공여부
	 */
	public boolean callProcedure(String query_) throws Exception{
		return callProcedure(query_, (Object[])null);
	}
	
	/**
	 * 프로시져를 호출합니다.
	 * 
	 * @param query_ 쿼리
	 * @param resultSetType_ {@link ResultSet}유형
	 * @param resultSetConcurrency_ {@link ResultSet}동시성
	 * @return 성공여부
	 */
	public boolean callProcedure(JGDBQuery query_, int resultSetType_, int resultSetConcurrency_) throws Exception{
		return callProcedure(query_.getQuery(), query_.parameterToArray(), resultSetType_, resultSetConcurrency_);
	}
	/**
	 * 프로시져를 호출합니다.
	 * 
	 * @param query_ 쿼리
	 * @param resultSetType_ {@link ResultSet}유형
	 * @return 성공여부
	 */
	public boolean callProcedure(JGDBQuery query_, int resultSetType_) throws Exception{
		return callProcedure(query_, resultSetType_, _defaultResultSetConcurrency);
	}
	/**
	 * 프로시져를 호출합니다.
	 * 
	 * @param query_ 쿼리
	 * @return 성공여부
	 */
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
