package com.jg.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.jg.db.vo.JGDBQuery;


public class JGDBBLOBHandler{
	protected JGDBConnection _dbConnection;
	
	protected JGDBBLOBHandler(){}
	protected JGDBBLOBHandler(JGDBConnection connection_){
		_dbConnection = connection_;
	}
	
	protected int _byteReadLength = 8192;
	public void setByteReadLength(int length_){
		_byteReadLength = length_;
	}
	public int getByteReadLength(){
		return _byteReadLength;
	}
	
	public void select(String query_, Object[] objects_, String columnName_, OutputStream outputStream_) throws Exception{
		PreparedStatement pStatement_ = null;
		ResultSet resultset_ = null;
		
		try{
			pStatement_ = _dbConnection.createStatement(query_, objects_);
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
			throw new Exception("failed to select blob value", ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
			if(resultset_ != null) resultset_.close();
			if(outputStream_ != null) outputStream_.close();
		}
	}
	public void select(String query_, String columnName_, OutputStream outputStream_) throws Exception{
		select(query_, (Object[])null, columnName_, outputStream_);
	}
	
	public void executeQuery(JGDBQuery query_, String columnName_, OutputStream outputStream_) throws Exception{
		select(query_.getQuery(), query_.parameterToArray(), columnName_, outputStream_);
	}
	
	public int insert(String tableName_, String columnName_, InputStream inputStream_, long length_) throws Exception{
		PreparedStatement pStatement_ = null;
		
		try{
			pStatement_ = _dbConnection.createStatement("INSERT INTO "+tableName_.toUpperCase()+"("+columnName_.toUpperCase()+")VALUES(?)");
			pStatement_.setBinaryStream(1, inputStream_, length_);
			int result_ = pStatement_.executeUpdate();
			return result_;
		}catch(Exception ex_){
			throw new Exception("failed to insert blob value", ex_);
		}finally{
			if(pStatement_ != null) pStatement_.close();
			if(inputStream_ != null) inputStream_.close();
		}
	}
}
