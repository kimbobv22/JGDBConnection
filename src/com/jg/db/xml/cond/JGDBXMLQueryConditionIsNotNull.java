package com.jg.db.xml.cond;

import org.jdom.Attribute;
import org.jdom.Element;

import com.jg.db.JGDBKeyword;
import com.jg.vo.JGDataset;


public class JGDBXMLQueryConditionIsNotNull extends JGDBXMLQueryConditionDef{

	@Override
	public boolean acceptConditionStatement(Element conditionElement_, JGDataset dataset_, int rowIndex_) throws Exception{
		Attribute attrColumnName_ = conditionElement_.getAttribute(JGDBKeyword.STR_ATTR_COLUMNNAME);
		Attribute attrIsReverse_ = conditionElement_.getAttribute(JGDBKeyword.STR_ATTR_ISREVERSE);
		
		Object columnValue_ = dataset_.getColumnValue(attrColumnName_.getValue(), rowIndex_);
		boolean isReverse_ = false;
		try{
			isReverse_ = Boolean.valueOf(attrIsReverse_.getValue()).booleanValue();
		}catch(Exception ex_){}
		
		try{
			return ((columnValue_ != null) != isReverse_);
		}catch(Exception ex_){
			return false;
		}
	}
}
