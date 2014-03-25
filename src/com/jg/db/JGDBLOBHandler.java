package com.jg.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.jg.db.vo.JGDBQuery;

/**
 * CLOB, BLOB의 제어를 위한 클래스입니다.
 * @author Hyeong yeon. Kim. kimbobv22@gmail.com
 */
public class JGDBLOBHandler{
	protected JGDBConnection _dbConnection;
	
	static protected Object[] _insertToArray(Object[] array1_, Object object_, int index_){
		Object[] result_ = new Object[array1_.length+1];
		
		result_[index_] = object_;
		for(int tIndex_=0;tIndex_<index_;++tIndex_){
			result_[tIndex_] = array1_[tIndex_];
		}
		for(int tIndex_=index_;tIndex_<array1_.length;++tIndex_){
			result_[tIndex_+1] = array1_[tIndex_];
		}
		
		return result_;
	}
	static protected String _createInsertQuery(String tableName_, String columnName_){
		return "INSERT INTO "+tableName_.toUpperCase()+"("+columnName_.toUpperCase()+")VALUES(?)";
	}
	static protected String _createUpdateQuery(String tableName_, String columnName_, String whereStatement_){
		return "UPDATE "+tableName_.toUpperCase()+" SET "+columnName_.toUpperCase()+" = ? "+whereStatement_;
	}
	
	/**
	 * 생성자
	 */
	protected JGDBLOBHandler(){}
	/**
	 * 생성자
	 * @param connection_ DB Connection
	 * @see JGDBConnection
	 */
	protected JGDBLOBHandler(JGDBConnection connection_){
		_dbConnection = connection_;
	}
	
	protected int _byteReadLength = 1024;
	/**
	 * byte 읽기길이를 설정합니다.
	 * 
	 * @param length_ byte 읽기길이
	 */
	public void setByteReadLength(int length_){
		_byteReadLength = length_;
	}
	/**
	 * byte 읽기길이를 반환합니다.
	 * 
	 * @return byte 읽기길이
	 */
	public int getByteReadLength(){
		return _byteReadLength;
	}
	
	protected int _charReadLength = 512;
	/**
	 * 문자 읽기길이를 설정합니다.
	 * 
	 * @param length_ 문자 읽기길이
	 */
	public void setCharReadLength(int length_){
		_charReadLength = length_;
	}
	/**
	 * 문자 읽기길이를 반환합니다.
	 * 
	 * @return 문자 읽기길이
	 */
	public int getCharReadLength(){
		return _charReadLength;
	}
	
	/**
	 * BLOB형식의 열의 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param columnName_ 열명
	 * @param outputStream_ 출력스트림
	 */
	public void selectBLOB(String query_, Object[] parameters_, String columnName_, OutputStream outputStream_) throws Exception{
		PreparedStatement pStatement_ = null;
		ResultSet resultset_ = null;
		
		try{
			JGDBConnection._loggingDef.beforeSelectBLOB(query_, parameters_);
			pStatement_ = _dbConnection.createStatement(query_, parameters_);
			resultset_ = pStatement_.executeQuery();
			
			if(resultset_.first()){
				InputStream inputStream_ = null;
				try{
					inputStream_ = resultset_.getBinaryStream(columnName_);
					byte[] buffer_ = new byte[_byteReadLength];
					int length_;
					while((length_ = inputStream_.read(buffer_)) > 0){
						outputStream_.write(buffer_, 0, length_);
					}
					
				}catch(Exception ex_){
					throw new Exception("failed to read bytes",ex_);
				}finally{
					if(inputStream_ != null) inputStream_.close();
				}
			}
		}catch(Exception ex_){
			throw new Exception("failed to select BLOB value", ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
			if(resultset_ != null) resultset_.close();
			if(outputStream_ != null) outputStream_.close();
		}
	}
	/**
	 * BLOB형식의 열의 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리
	 * @param columnName_ 열명
	 * @param outputStream_ 출력스트림
	 */
	public void selectBLOB(JGDBQuery query_, String columnName_, OutputStream outputStream_) throws Exception{
		selectBLOB(query_.getQuery(), query_.parameterToArray(), columnName_, outputStream_);
	}
	
	/**
	 * BLOB형식의 열값을 삽입합니다.
	 * 
	 * @param tableName_ 테이블명
	 * @param columnName_ 열명
	 * @param inputStream_ 입력스트림
	 * @param length_ 입력길이
	 * @return 수행성공횟수
	 */
	public int insertBLOB(String tableName_, String columnName_, InputStream inputStream_, long length_) throws Exception{
		PreparedStatement pStatement_ = null;
		
		try{
			String query_ = _createInsertQuery(tableName_, columnName_);
			pStatement_ = _dbConnection.createStatement(query_);
			pStatement_.setBinaryStream(1, inputStream_, length_);
			JGDBConnection._loggingDef.beforeInsertBLOB(query_, inputStream_);
			int result_ = pStatement_.executeUpdate();
			return result_;
		}catch(Exception ex_){
			throw new Exception("failed to insert BLOB value", ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
			if(inputStream_ != null) inputStream_.close();
		}
	}
	
	/**
	 * BLOB형식의 열값을 갱신합니다.
	 * 
	 * @param tableName_ 테이블명
	 * @param columnName_ 열명
	 * @param whereStatement_ 조건절 쿼리
	 * @param parameters_ 조건절 매개변수
	 * @param inputStream_ 입력스트림
	 * @param length_ 입력길이
	 * @return 수행성공횟수
	 */
	public int updateBLOB(String tableName_, String columnName_, String whereStatement_, Object[] parameters_, InputStream inputStream_, long length_) throws Exception{
		PreparedStatement pStatement_ = null;
		
		try{
			String query_ = _createUpdateQuery(tableName_, columnName_, whereStatement_);
			pStatement_ = _dbConnection.createStatement(query_);
			pStatement_.setBinaryStream(1, inputStream_, length_);
			for(int index_=0;index_<parameters_.length;++index_)
				pStatement_.setObject(index_+2, parameters_[index_]);
			
			JGDBConnection._loggingDef.beforeUpdateBLOB(query_, inputStream_, _insertToArray(parameters_, inputStream_, 0));
			return pStatement_.executeUpdate();
		}catch(Exception ex_){
			throw new Exception("failed to update BLOB value", ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
			if(inputStream_ != null) inputStream_.close();
		}
	}
	/**
	 * BLOB형식의 열값을 갱신합니다.
	 * 
	 * @param tableName_ 테이블명
	 * @param columnName_ 열명
	 * @param whereQuery_ 조건절 쿼리
	 * @param inputStream_ 입력스트림
	 * @param length_ 입력길이
	 * @return 수행성공횟수
	 */
	public int updateBLOB(String tableName_, String columnName_, JGDBQuery whereQuery_, InputStream inputStream_, long length_) throws Exception{
		return updateBLOB(tableName_, columnName_, whereQuery_.getQuery(), whereQuery_.parameterToArray(), inputStream_, length_);
	}
	
	/**
	 * CLOB형식의 열의 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리 문자열
	 * @param parameters_ 쿼리 매개변수
	 * @param columnName_ 열명
	 * @param writer_ 작성자
	 */
	public void selectCLOB(String query_, Object[] parameters_, String columnName_, Writer writer_) throws Exception{
		PreparedStatement pStatement_ = null;
		ResultSet resultset_ = null;
		
		try{
			pStatement_ = _dbConnection.createStatement(query_, parameters_);
			resultset_ = pStatement_.executeQuery();
			
			if(resultset_.first()){
				Reader reader_ = null;
				try{
					reader_ = resultset_.getCharacterStream(columnName_);
					char[] buffer_ = new char[_charReadLength];
					int length_;
					while((length_ = reader_.read(buffer_)) > 0){
						writer_.write(buffer_, 0, length_);
					}
					
				}catch(Exception ex_){
					throw new Exception("failed to read character",ex_);
				}finally{
					if(reader_ != null) reader_.close();
				}
			}
		}catch(Exception ex_){
			throw new Exception("failed to select CLOB value", ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
			if(resultset_ != null) resultset_.close();
		}
	}
	
	/**
	 * CLOB형식의 열의 열값을 반환합니다.
	 * 
	 * @param query_ 쿼리
	 * @param columnName_ 열명
	 * @param writer_ 작성자
	 */
	public void selectCLOB(JGDBQuery query_, String columnName_, Writer writer_) throws Exception{
		selectCLOB(query_.getQuery(), query_.parameterToArray(), columnName_, writer_);
	}
	/**
	 * CLOB형식의 열값을 삽입합니다.
	 * 
	 * @param tableName_ 테이블명
	 * @param columnName_ 열명
	 * @param reader_ 리더
	 * @param length_ 입력길이
	 * @return 수행성공횟수
	 */
	public int insertCLOB(String tableName_, String columnName_, Reader reader_, long length_) throws Exception{
		PreparedStatement pStatement_ = null;
		
		try{
			String query_ = _createInsertQuery(tableName_, columnName_);
			pStatement_ = _dbConnection.createStatement(query_);
			pStatement_.setCharacterStream(1, reader_, length_);
			JGDBConnection._loggingDef.beforeInsertCLOB(query_, reader_);
			return pStatement_.executeUpdate();
		}catch(Exception ex_){
			throw new Exception("failed to insert CLOB value", ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
			if(reader_ != null) reader_.close();
		}
	}
	
	/**
	 * CLOB형식의 열값을 갱신합니다.
	 * 
	 * @param tableName_ 테이블명
	 * @param columnName_ 열명
	 * @param whereStatement_ 조건절 쿼리 문자열
	 * @param parameters_ 조건절 쿼리 매개변수
	 * @param reader_ 리더
	 * @param length_ 입력길이
	 * @return 수행성공횟수
	 */
	public int updateCLOB(String tableName_, String columnName_, String whereStatement_, Object[] parameters_, Reader reader_, long length_) throws Exception{
		PreparedStatement pStatement_ = null;
		
		try{
			String query_ = "UPDATE "+tableName_.toUpperCase()+" SET "+columnName_.toUpperCase()+" = ? "+whereStatement_;
			pStatement_ = _dbConnection.createStatement(query_);
			pStatement_.setCharacterStream(1, reader_, length_);
			for(int index_=0;index_<parameters_.length;++index_)
				pStatement_.setObject(index_+2, parameters_[index_]);
			
			JGDBConnection._loggingDef.beforeUpdateCLOB(query_, reader_, _insertToArray(parameters_, reader_, 0));
			return pStatement_.executeUpdate();
		}catch(Exception ex_){
			throw new Exception("failed to update CLOB value", ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
			if(reader_ != null) reader_.close();
		}
	}
	/**
	 * CLOB형식의 열값을 갱신합니다.
	 * 
	 * @param tableName_ 테이블명
	 * @param columnName_ 열명
	 * @param whereQuery_ 조건절 쿼리
	 * @param reader_ 리더
	 * @param length_ 입력길이
	 * @return 수행성공횟수
	 */
	public int updateCLOB(String tableName_, String columnName_, JGDBQuery whereQuery_, Reader reader_, long length_) throws Exception{
		return updateCLOB(tableName_, columnName_, whereQuery_.getQuery(), whereQuery_.parameterToArray(), reader_, length_);
	}
}
