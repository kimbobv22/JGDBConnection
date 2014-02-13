package com.jg.db.xml.cond;

import org.jdom.Attribute;
import org.jdom.Element;

import com.jg.db.JGDBKeyword;
import com.jg.vo.JGDataset;

public class JGDBXMLQueryConditionIsColumnEquals extends JGDBXMLQueryConditionDef{
	
	@Override
	public boolean acceptConditionStatement(Element conditionElement_, JGDataset dataset_, int rowIndex_) throws Exception{
		Attribute attrColumnName_ = conditionElement_.getAttribute(JGDBKeyword.STR_ATTR_COLUMNNAME);
		Attribute attrOtherColumnName_ = conditionElement_.getAttribute(JGDBKeyword.STR_ATTR_OTHERCOLUMNNAME);
		Attribute attrIsReverse_ = conditionElement_.getAttribute(JGDBKeyword.STR_ATTR_ISREVERSE);
		
		boolean isReverse_ = false;
		try{
			isReverse_ = Boolean.valueOf(attrIsReverse_.getValue()).booleanValue();
		}catch(Exception ex_){}
		
		try{
			Object columnValue_ = dataset_.getColumnValue(attrColumnName_.getValue(), rowIndex_);
			Object otherColumnValue_ = dataset_.getColumnValue(attrOtherColumnName_.getValue(), rowIndex_);
			
			return (String.valueOf(columnValue_).equals(String.valueOf(otherColumnValue_)) != isReverse_);
		}catch(Exception ex_){
			return false;
		}
	}
}
