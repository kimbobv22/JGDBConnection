package com.jg.db.xml.cond;

import org.jdom.Element;

import com.jg.vo.JGDataset;


public abstract class JGDBXMLQueryConditionDef{
	abstract public boolean acceptConditionStatement(Element conditionElement_, JGDataset dataset_, int rowIndex_) throws Exception;
	public String getStatement(Element conditionElement_, JGDataset dataset_, int rowIndex_){
		return conditionElement_.getValue();
	}
	
}
