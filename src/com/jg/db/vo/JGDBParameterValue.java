package com.jg.db.vo;


public class JGDBParameterValue{
	protected String _column = null;
	public String getColumn(){
		return _column;
	}
	protected Object _value = null;
	public Object getValue(){
		return _value;
	}
	
	public JGDBParameterValue(String column_, Object value_){
		_column = column_;
		_value = value_;
	}
	
	public String toString(){
		return "column : "+_column+"\nvalue : "+String.valueOf(_value);
	}
}