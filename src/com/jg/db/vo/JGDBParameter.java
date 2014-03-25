package com.jg.db.vo;

import java.util.ArrayList;

/**
 * {@link JGDBQuery}의 자동 쿼리 생성을 위한 매개변수 객체입니다.
 * 
 * @author Hyeong yeon. Kim. kimbobv22@gmail.com
 * 
 * @see JGDBQuery#fillQueryForSELECT(JGDBParameter)
 * @see JGDBQuery#fillQueryForINSERT(JGDBParameter)
 * @see JGDBQuery#fillQueryForUPDATE(JGDBParameter)
 * @see JGDBQuery#fillQueryForDELETE(JGDBParameter)
 * @see JGDBQuery#fillQueryForPROCEDURE(JGDBParameter)
 */
public class JGDBParameter{
	
	protected String _targetName = null;
	/**
	 * 대상명을 설정합니다.
	 * @param targetName_ 대상명
	 */
	public void setTargetName(String targetName_){
		_targetName = targetName_;
	}
	/**
	 * 대상명을 반환합니다.
	 * @return 대상명
	 */
	public String getTargetName(){
		return _targetName;
	}
	
	protected ArrayList<JGDBParameterValue> _values = new ArrayList<JGDBParameterValue>();
	protected ArrayList<JGDBParameterValue> _keyList = new ArrayList<JGDBParameterValue>();
	
	/**
	 * 생성자
	 * @param targetName_ 대상명(테이블, 객체 등)
	 */
	public JGDBParameter(String targetName_){
		_targetName = targetName_;
	}
	/**
	 * 특정 색인에 값을 삽입합니다. 
	 * @param value_ 값
	 * @param index_ 색인
	 */
	public void addValue(JGDBParameterValue value_, int index_){
		_values.add(index_,value_);
	}
	/**
	 * 특정 색인에 값을 삽입합니다. 
	 * @param column_ 열명
	 * @param value_ 열값
	 * @param index_ 색인
	 * @return 매개변수값
	 */
	public JGDBParameterValue addValue(String column_, Object value_, int index_){
		JGDBParameterValue pValue_ = new JGDBParameterValue(column_, value_);
		addValue(pValue_,index_);
		return pValue_;
	}
	/**
	 * 마지막 색인에 값을 추가합니다. 
	 * @param column_ 열명
	 * @param value_ 열값
	 * @return 매개변수값
	 */
	public JGDBParameterValue addValue(String column_, Object value_){
		return addValue(column_, value_, _values.size());
	}
	
	/**
	 * 특정 색인에 값을 삭제합니다.
	 * @param index_ 색인
	 */
	public void removeValueAtIndex(int index_){
		_values.remove(index_);
	}
	/**
	 * 값을 삭제합니다.
	 * @param value_ 값
	 */
	public void removeValue(JGDBParameterValue value_){
		removeValueAtIndex(indexOfValue(value_));
	}
	/**
	 * 특정 열명을 가진 값을 삭제합니다.
	 * @param column_ 열명
	 */
	public void removeValueAtColumn(String column_){
		removeValueAtIndex(indexOfValueWithColumn(column_));
	}
	/**
	 * 특정 색인의 값을 반환합니다.
	 * @param index_ 색인
	 * @return 값
	 */
	public JGDBParameterValue getValueAtIndex(int index_){
		return _values.get(index_);
	}
	/**
	 * 특정 열명의 값을 반환합니다.
	 * @param column_ 열명
	 * @return 값
	 */
	public JGDBParameterValue getValueAtColumn(String column_){
		return getValueAtIndex(indexOfValueWithColumn(column_));
	}
	
	/**
	 * 값의 색인을 반환합니다.
	 * @param value_ 값
	 * @return 색인
	 */
	public int indexOfValue(JGDBParameterValue value_){
		return _values.indexOf(value_);
	}
	/**
	 * 특정 열명을 가진 값의 색인을 반환합니다.
	 * @param column_ 열명
	 * @return 색인
	 */
	public int indexOfValueWithColumn(String column_){
		JGDBParameterValue[] values_ = (JGDBParameterValue[]) _values.toArray();
		int count_ = values_.length;
		for(int index_=0;index_<count_;++index_){
			if(values_[index_].getColumn().equals(column_)){
				return index_;
			}
		}
		
		return -1;
	}
	/**
	 * 값의 갯수를 반환합니다.
	 * @return 값의 갯수
	 */
	public int countOfValue(){
		return _values.size();
	}
	
	/**
	 * 특정 색인에 키를 삽입합니다. 
	 * @param value_ 값
	 * @param index_ 색인
	 */
	public void addKey(JGDBParameterValue value_, int index_){
		_keyList.add(index_, value_);
	}
	/**
	 * 특정 색인에 키를 삽입합니다.
	 * @param column_ 열명
	 * @param value_ 열값
	 * @param index_ 색인
	 * @return 키
	 */
	public JGDBParameterValue addKey(String column_, Object value_, int index_){
		JGDBParameterValue pValue_ = new JGDBParameterValue(column_, value_);
		addKey(pValue_,index_);
		return pValue_;
	}
	/**
	 * 마지막 색인에 키를 삽입합니다.
	 * @param column_ 열명
	 * @param value_ 열값
	 * @return 키
	 */
	public JGDBParameterValue addKey(String column_, Object value_){
		return addKey(column_, value_, _keyList.size());
	}
	/**
	 * 특정 색인에 키를 삭제합니다.
	 * @param index_ 색인
	 */
	public void removeKeyAtIndex(int index_){
		_keyList.remove(index_);
	}
	/**
	 * 키를 삭제합니다.
	 * @param value_ 키
	 */
	public void removeKey(JGDBParameterValue value_){
		removeKeyAtIndex(indexOfKey(value_));
	}
	/**
	 * 특정 열명에 키를 삭제합니다.
	 * @param column_ 열명
	 */
	public void removeKeyAtColumn(String column_){
		removeKeyAtIndex(indexOfKeyWithColumn(column_));
	}
	/**
	 * 특정 색인의 키를 반환합니다.
	 * @param index_ 색인
	 * @return 키
	 */
	public JGDBParameterValue getKeyAtIndex(int index_){
		return _keyList.get(index_);
	}
	/**
	 * 특정 열명에 키를 반환합니다.
	 * @param column_ 열명
	 * @return 키
	 */
	public JGDBParameterValue getKeyAtColumn(String column_){
		return getKeyAtIndex(indexOfKeyWithColumn(column_));
	}
	
	/**
	 * 키의 색인을 반환합니다.
	 * @param value_ 키
	 * @return 색인
	 */
	public int indexOfKey(JGDBParameterValue value_){
		return _keyList.indexOf(value_);
	}
	/**
	 * 특정 열에 키의 색인을 반환합니다.
	 * @param column_ 열
	 * @return 색인
	 */
	public int indexOfKeyWithColumn(String column_){
		JGDBParameterValue[] keyList_ = (JGDBParameterValue[]) _keyList.toArray();
		int count_ = keyList_.length;
		for(int index_=0;index_<count_;++index_){
			if(keyList_[index_].getColumn().equals(column_)){
				return index_;
			}
		}
		
		return -1;
	}
	/**
	 * 키의 갯수를 반환합니다.
	 * @return 키의 갯수
	 */
	public int countOfKey(){
		return _keyList.size();
	}
	
	/**
	 * 대상, 값, 키를 비웁니다.
	 */
	public void clear(){
		_targetName = null;
		_values.clear();
		_keyList.clear();
	}
}
