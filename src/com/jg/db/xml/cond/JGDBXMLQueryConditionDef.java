package com.jg.db.xml.cond;

import org.jdom.Element;

import com.jg.vo.JGDataset;

/**
 * XML 해석 시 조건절을 정의할 수 있는 추상화 객체입니다.
 * 
 * @author Hyeong yeon. Kim. kimbobv22@gmail.com
 */
public abstract class JGDBXMLQueryConditionDef{
	abstract public boolean acceptConditionStatement(Element conditionElement_, JGDataset dataset_, int rowIndex_) throws Exception;
	public String getStatement(Element conditionElement_, JGDataset dataset_, int rowIndex_){
		return conditionElement_.getValue();
	}
	
}
