package com.jg.db.vo;

import java.util.ArrayList;

public class JGDBParameter{

	public class JGDBParameterValue{
		public String column = null;
		public Object value = null;
		
		public JGDBParameterValue(String column_, Object value_){
			column = column_;
			value = value_;
		}
		
		public String toString(){
			return "column : "+column+"\nvalue : "+value;
		}
	}
	
	protected String _targetName = null;
	public void setTargetName(String targetName_){
		_targetName = targetName_;
	}
	public String getTargetName(){
		return _targetName;
	}
	
	public ArrayList<JGDBParameterValue> values = new ArrayList<JGDBParameterValue>();
	public ArrayList<JGDBParameterValue> keyList = new ArrayList<JGDBParameterValue>();
	
	public JGDBParameter(String targetName_){
		_targetName = targetName_;
	}
	
	public void addValue(JGDBParameterValue value_, int index_){
		values.add(index_,value_);
	}
	public JGDBParameterValue addValue(String column_, Object value_, int index_){
		JGDBParameterValue pValue_ = new JGDBParameterValue(column_, value_);
		addValue(pValue_,index_);
		return pValue_;
	}
	public JGDBParameterValue addValue(String column_, Object value_){
		return addValue(column_, value_, values.size());
	}
	
	public void removeValueAtIndex(int index_){
		values.remove(index_);
	}
	public void removeValue(JGDBParameterValue value_){
		removeValueAtIndex(indexOfValue(value_));
	}
	public void removeValueAtColumn(String column_){
		removeValueAtIndex(indexOfValueWithColumn(column_));
	}
	public JGDBParameterValue getValueAtIndex(int index_){
		return values.get(index_);
	}
	public JGDBParameterValue getValueAtColumn(String column_){
		return getValueAtIndex(indexOfValueWithColumn(column_));
	}
	
	public int indexOfValue(JGDBParameterValue value_){
		return values.indexOf(value_);
	}
	public int indexOfValueWithColumn(String column_){
		JGDBParameterValue[] values_ = (JGDBParameterValue[]) values.toArray();
		int count_ = values_.length;
		for(int index_=0;index_<count_;++index_){
			if(values_[index_].column.equals(column_)){
				return index_;
			}
		}
		
		return -1;
	}
	
	public int countOfValue(){
		return values.size();
	}
	
	public void addKey(JGDBParameterValue value_, int index_){
		keyList.add(index_, value_);
	}
	public JGDBParameterValue addKey(String column_, Object value_, int index_){
		JGDBParameterValue pValue_ = new JGDBParameterValue(column_, value_);
		addKey(pValue_,index_);
		return pValue_;
	}
	public JGDBParameterValue addKey(String column_, Object value_){
		return addKey(column_, value_, keyList.size());
	}
	public void removeKeyAtIndex(int index_){
		keyList.remove(index_);
	}
	public void removeKey(JGDBParameterValue value_){
		removeKeyAtIndex(indexOfKey(value_));
	}
	public void removeKeyAtColumn(String column_){
		removeKeyAtIndex(indexOfKeyWithColumn(column_));
	}
	public JGDBParameterValue getKeyAtIndex(int index_){
		return keyList.get(index_);
	}
	public JGDBParameterValue getKeyAtColumn(String column_){
		return getKeyAtIndex(indexOfKeyWithColumn(column_));
	}
	
	public int indexOfKey(JGDBParameterValue value_){
		return keyList.indexOf(value_);
	}
	public int indexOfKeyWithColumn(String column_){
		JGDBParameterValue[] keyList_ = (JGDBParameterValue[]) keyList.toArray();
		int count_ = keyList_.length;
		for(int index_=0;index_<count_;++index_){
			if(keyList_[index_].column.equals(column_)){
				return index_;
			}
		}
		
		return -1;
	}
	
	public int countOfKey(){
		return keyList.size();
	}
	
	public void reset(){
		_targetName = null;
		values.clear();
		keyList.clear();
	}
}
