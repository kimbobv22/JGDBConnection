package com.jg.db;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.jg.vo.JGDataset;

public class JGDBUtils {

	static public JGDataset convertToDataset(ResultSet resultSet_, String characterEncoding_) throws Exception{
		JGDataset dataset_ = new JGDataset();
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
				dataset_.addColumn(columnLabel_);
			}
		}catch(SQLException ex_){
			throw new Exception("failed to add column to dataset from resultSet metadata", ex_);
		}
		
		try{
			while(resultSet_.next()){
				int rowIndex_ = dataset_.addRow();
				
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
						dataset_.setColumnValue(columnIndex_, rowIndex_, resultSet_.getObject(rColumnIndex_));
						break;
					case Types.DATE:
					case Types.TIMESTAMP:
						java.sql.Date dateValue_ = resultSet_.getDate(rColumnIndex_);
						if(dateValue_ != null)
							dataset_.setColumnValue(columnIndex_, rowIndex_, dateValue_.toString());
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
							dataset_.setColumnValue(columnIndex_, rowIndex_, new String(resultSet_.getBytes(rColumnIndex_),characterEncoding_));
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
		
		dataset_.apply();
		return dataset_;
	}
}
