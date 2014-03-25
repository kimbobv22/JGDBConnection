package com.jg.db.vo;

import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * {@link PreparedStatement}의 유연한 생성을 위한 객체입니다.
 * 
 * @author Hyeong yeon. Kim. kimbobv22@gmail.com
 */
public class JGDBQuery {
	protected String _query = null;
	/**
	 * 쿼리 문자열을 반환합니다. 
	 * @return 쿼리 문자열
	 */
	public String getQuery(){
		return _query;
	}
	/**
	 * 쿼리 문자열을 설정합니다. 
	 * @param query_ 쿼리 문자열
	 */
	public void setQuery(String query_){
		_query = query_;
	}
	
	protected ArrayList<Object> _parameter = new ArrayList<Object>();
	/**
	 * 쿼리 매개변수를 반환합니다.
	 * @return 쿼리 매개변수
	 */
	public ArrayList<Object> getParameter(){
		return _parameter;
	}
	/**
	 * 쿼리 매개변수를 추가합니다.
	 * @param object_ 쿼리 매개변수
	 */
	public void addParameter(Object object_){
		_parameter.add(object_);
	}
	/**
	 * 쿼리 매개변수를 배열로 반환합니다.
	 * 
	 * @return 쿼리 매개변수
	 */
	public Object[] parameterToArray(){
		return _parameter.toArray();
	}
	
	/**
	 * 쿼리 문자열과 쿼리 매개변수를 비웁니다.
	 */
	public void clear(){
		_query = null;
		_parameter.clear();
	}
	
	static protected void fillWHEREStatement(JGDBQuery query_,JGDBParameter parameter_){
		StringBuffer queryStr_ = new StringBuffer(query_.getQuery()+"WHERE 1=1 \n");
		
		ArrayList<JGDBParameterValue> keyList_ = parameter_._keyList;
		int count_ = keyList_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue key_ = keyList_.get(index_);
			if(key_.getValue() != null){
				queryStr_.append("AND "+key_.getColumn()+" = ? \n");
				query_.addParameter(key_.getValue());
			}else{
				queryStr_.append("AND "+key_.getColumn()+" IS NULL \n");
			}
		}
		
		query_.setQuery(queryStr_.toString());
	}
	
	/**
	 * 외부 매개변수를 이용해 쿼리 문자열과 쿼리 매개변수를 통해 SELECT 쿼리를 생성합니다. 
	 * @param parameter_ 외부 매개변수
	 */
	public void fillQueryForSELECT(JGDBParameter parameter_){
		clear();
		String tableName_ = parameter_._targetName;
		if(tableName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("SELECT \n");
		
		ArrayList<JGDBParameterValue> values_ = parameter_._values;
		int count_ = values_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue value_ = values_.get(index_);
			queryStr_.append(value_.getColumn());
			if(index_ < count_-1){
				queryStr_.append(",");
			}
			queryStr_.append(" \n");
		}
		
		queryStr_.append("FROM "+tableName_+" \n");
		_query = queryStr_.toString();
		fillWHEREStatement(this, parameter_);
	}
	/**
	 * 외부 매개변수를 이용해 쿼리 문자열과 쿼리 매개변수를 통해 INSERT 쿼리를 생성합니다. 
	 * @param parameter_ 외부 매개변수
	 */
	public void fillQueryForINSERT(JGDBParameter parameter_){
		clear();
		String tableName_ = parameter_._targetName;
		if(tableName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("INSERT INTO "+tableName_+" (\n");
		
		ArrayList<JGDBParameterValue> values_ = parameter_._values;
		int count_ = values_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue value_ = values_.get(index_);
			queryStr_.append(value_.getColumn());
			if(index_ < count_-1){
				queryStr_.append(",");
			}
			queryStr_.append(" \n");
		}
		queryStr_.append(")VALUES( \n");
		
		for(int index_=0;index_<count_;++index_){
			queryStr_.append("?");
			JGDBParameterValue value_ = values_.get(index_);
			addParameter(value_.getValue());
			if(index_ < count_-1){
				queryStr_.append(",");
			}
			queryStr_.append(" \n");
		}
		
		queryStr_.append(")\n");
		_query = queryStr_.toString();
	}
	/**
	 * 외부 매개변수를 이용해 쿼리 문자열과 쿼리 매개변수를 통해 UPDATE 쿼리를 생성합니다. 
	 * @param parameter_ 외부 매개변수
	 */
	public void fillQueryForUPDATE(JGDBParameter parameter_){
		clear();
		String tableName_ = parameter_._targetName;
		if(tableName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("UPDATE "+tableName_+" SET \n");
		ArrayList<JGDBParameterValue> values_ = parameter_._values;
		int count_ = values_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue value_ = values_.get(index_);
			
			queryStr_.append(value_.getColumn()+" = ?");
			addParameter(value_.getValue());
			if(index_ < count_-1){
				queryStr_.append(",");
			}
			queryStr_.append(" \n");
		}
		_query = queryStr_.toString();
		fillWHEREStatement(this, parameter_);
	}
	/**
	 * 외부 매개변수를 이용해 쿼리 문자열과 쿼리 매개변수를 통해 DELETE 쿼리를 생성합니다. 
	 * @param parameter_ 외부 매개변수
	 */
	public void fillQueryForDELETE(JGDBParameter parameter_){
		clear();
		String tableName_ = parameter_._targetName;
		if(tableName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("DELETE FROM "+tableName_+" \n");
		_query = queryStr_.toString();
		fillWHEREStatement(this, parameter_);
	}
	/**
	 * 외부 매개변수를 이용해 쿼리 문자열과 쿼리 매개변수를 통해 프로시져를 생성합니다. 
	 * @param parameter_ 외부 매개변수
	 */
	public void fillQueryForPROCEDURE(JGDBParameter parameter_){
		clear();
		String targetName_ = parameter_._targetName;
		if(targetName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("CALL "+targetName_+"(");
		
		ArrayList<JGDBParameterValue> values_ = parameter_._values;
		int count_ = values_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue value_ = values_.get(index_);
			queryStr_.append("?");
			addParameter(value_.getValue());
			if(index_ < count_-1){
				queryStr_.append(", ");
			}
		}
		
		queryStr_.append(")\n");
		_query = queryStr_.toString();
	}
	public String toString(){		
		StringBuffer result_ = new StringBuffer();
		result_.append("[query]\n");
		result_.append(_query+"\n\n");
		result_.append("[parameters]\n");
		result_.append(_parameter.toString()+"\n");
		return result_.toString();
	}
}
