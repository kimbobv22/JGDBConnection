package com.jg.db.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.jg.db.JGDBKeyword;

public class JGDBXMLQuerySet {
	
	protected String _keyName = null;
	public String getKeyName(){
		return _keyName;
	}
	
	protected ArrayList<JGDBXMLQuery> _queryList = new ArrayList<JGDBXMLQuery>();
	public int countOfQuery(){
		return _queryList.size();
	}
	
	protected JGDBXMLQuerySet(){}
	protected JGDBXMLQuerySet(String targetPath_) throws JDOMException, IOException{
		Document rootDocument_ = new SAXBuilder().build(new File(targetPath_));
		Element querySetElement_ = rootDocument_.getRootElement();
		_keyName = querySetElement_.getAttributeValue(JGDBKeyword.STR_ATTR_KEYNAME);
		List<?> queryList_ = querySetElement_.getChildren(JGDBKeyword.STR_ELEMENT_QUERY);
		int queryCount_ = queryList_.size();
		for(int index_=0;index_<queryCount_;++index_){
			Element queryElement_ = (Element)queryList_.get(index_);
			addQuery(new JGDBXMLQuery(queryElement_));
		}
	}
	
	protected void addQuery(JGDBXMLQuery query_){
		query_.setParent(this);
		_queryList.add(query_);
	}
	protected void removeQuery(int index_){
		JGDBXMLQuery query_ = getQuery(index_);
		if(query_ != null){
			query_.setParent(null);
			_queryList.remove(index_);
		}
	}
	protected void removeQuery(String keyName_){
		removeQuery(indexOfQuery(keyName_));
	}
	
	public JGDBXMLQuery getQuery(int index_){
		return _queryList.get(index_);
	}
	public JGDBXMLQuery getQuery(String keyName_){
		return _queryList.get(indexOfQuery(keyName_));
	}
	
	public int indexOfQuery(String keyName_){
		int queryCount_ = _queryList.size();
		for(int index_=0;index_<queryCount_;++index_){
			if(getQuery(index_).getKeyName().equals(keyName_)){
				return index_;
			}
		}
		return -1;
	}
}
