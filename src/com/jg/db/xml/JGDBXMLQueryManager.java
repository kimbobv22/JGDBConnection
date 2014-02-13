package com.jg.db.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.jdom.JDOMException;

import com.jg.db.JGDBKeyword;
import com.jg.db.vo.JGDBQuery;
import com.jg.db.xml.cond.JGDBXMLQueryConditionDef;
import com.jg.db.xml.cond.JGDBXMLQueryConditionIsColumnEquals;
import com.jg.db.xml.cond.JGDBXMLQueryConditionIsEquals;
import com.jg.db.xml.cond.JGDBXMLQueryConditionIsNotBlank;
import com.jg.db.xml.cond.JGDBXMLQueryConditionIsNotNull;
import com.jg.vo.JGDataset;


public class JGDBXMLQueryManager {
	static private JGDBXMLQueryManager _sharedQueryManager_ = null;
	
	static protected String _XMLDirectoryPath = null;
	static public String getXMLDirectoryPath(){
		return _XMLDirectoryPath;
	}
	static public void setXMLDirectoryPath(String path_){
		_XMLDirectoryPath = path_;
	}
	
	protected HashMap<String, JGDBXMLQuerySet> _querySetList = new HashMap<String, JGDBXMLQuerySet>();
	public int countOfQuerySet(){
		return _querySetList.size();
	}
	
	protected HashMap<String, JGDBXMLQueryConditionDef> _conditionDefs = new HashMap<String, JGDBXMLQueryConditionDef>();
	public int countOfConditionDef(){
		return _conditionDefs.size();
	}
	public JGDBXMLQueryConditionDef getConditionDef(String keyName_){
		return _conditionDefs.get(keyName_.toLowerCase());
	}
	public void putConditionDef(String keyName_, JGDBXMLQueryConditionDef conditionDef_){
		_conditionDefs.put(keyName_.toLowerCase(), conditionDef_);
	}
	
	static public JGDBXMLQueryManager sharedManager(){
		if(_sharedQueryManager_ == null){
			synchronized(JGDBXMLQueryManager.class){
				try{
					_sharedQueryManager_ = new JGDBXMLQueryManager();
				}catch(Exception ex_){
					_sharedQueryManager_ = null;
					System.out.println("can't not load JGDBXMLQueryManager");
					ex_.printStackTrace();
				}
			}
		}
		
		return _sharedQueryManager_;
	}
		
	private void _searchXMLDirectory(String targetPath_) throws Exception{
		File targetDirectory_ = new File(targetPath_);
		File[] fileList_ = targetDirectory_.listFiles();
		int fileCount_ = fileList_.length;
		for(int index_=0;index_<fileCount_;++index_){
			File targetFile_ = fileList_[index_];
			String childPath_ = null;
			try{
				childPath_ = targetFile_.getCanonicalPath();
			}catch(IOException ex_){
				throw new Exception("can't find child path from ["+targetPath_+"]",ex_);
			}
			if(targetFile_.isFile() && targetFile_.getName().endsWith(".xml")){
				JGDBXMLQuerySet querySet_ = null;
				try{
					querySet_ = new JGDBXMLQuerySet(childPath_);
				}catch(JDOMException ex_){
					throw new Exception("can't parse queryset["+childPath_+"]",ex_);
				}catch(IOException ex_){
					throw new Exception("failed to read queryset["+childPath_+"]",ex_);
				}
				addQuerySet(querySet_);
			}else if(targetFile_.isDirectory()){
				_searchXMLDirectory(childPath_);
			}
		}
	}
	
	protected JGDBXMLQueryManager() throws Exception{
		reload();
		putConditionDef(JGDBKeyword.STR_ELEMENT_ISNOTNULL, new JGDBXMLQueryConditionIsNotNull());
		putConditionDef(JGDBKeyword.STR_ELEMENT_ISNOTBLANK, new JGDBXMLQueryConditionIsNotBlank());
		putConditionDef(JGDBKeyword.STR_ELEMENT_ISEQUALS, new JGDBXMLQueryConditionIsEquals());
		putConditionDef(JGDBKeyword.STR_ELEMENT_ISCOLUMNEQUALS, new JGDBXMLQueryConditionIsColumnEquals());
	}
	
	public void reload() throws Exception{
		_querySetList.clear();
		
		if(_XMLDirectoryPath == null){
			throw new Exception("XML Directory path is null");
		}
		
		_searchXMLDirectory(_XMLDirectoryPath);
	}
	
	protected void addQuerySet(JGDBXMLQuerySet querySet_){
		JGDBXMLQuerySet existQuerySet_ = getQuerySet(querySet_._keyName);
		if(existQuerySet_ != null){
			
			int count_ = querySet_.countOfQuery();
			for(int index_=0;index_<count_;++index_){
				JGDBXMLQuery query_ = querySet_.getQuery(index_);
				int checkIndex_ = existQuerySet_.indexOfQuery(query_._keyName);
				if(checkIndex_ >= 0){
					existQuerySet_.removeQuery(checkIndex_);
				}
				existQuerySet_.addQuery(query_);
			}
			existQuerySet_._queryList.addAll(querySet_._queryList);
		}else{
			_querySetList.put(querySet_._keyName, querySet_);
		}
	}
	
	public JGDBXMLQuerySet getQuerySet(String keyName_){
		return _querySetList.get(keyName_);
	}
	public JGDBXMLQuery getQuery(String querySetKeyName_, String queryKeyName_){
		JGDBXMLQuerySet querySet_ = getQuerySet(querySetKeyName_);
		if(querySet_ == null){
			throw new NullPointerException("Can't find QuerySet ["+querySetKeyName_+"]");
		}
		return querySet_.getQuery(queryKeyName_);
	}
	
	/**
	 * redirect method
	 */
	public JGDBQuery createQuery(String querySetKeyName_, String queryKeyName_, JGDataset dataset_, int rowIndex_) throws Exception{
		return getQuery(querySetKeyName_, queryKeyName_).createQuery(dataset_, rowIndex_);
	}
	public JGDBQuery createQuery(String querySetKeyName_, String queryKeyName_, JGDataset dataset_) throws Exception{
		return getQuery(querySetKeyName_, queryKeyName_).createQuery(dataset_);
	}
	public JGDBQuery createQuery(String querySetKeyName_, String queryKeyName_, Object[] columnNamesAndValues_, String[] keyColumns_) throws Exception{
		return getQuery(querySetKeyName_, queryKeyName_).createQuery(columnNamesAndValues_, keyColumns_);
	}
	public JGDBQuery createQuery(String querySetKeyName_, String queryKeyName_, Object[] columnNamesAndValues_) throws Exception{
		return getQuery(querySetKeyName_, queryKeyName_).createQuery(columnNamesAndValues_);
	}
	public JGDBQuery createQuery(String querySetKeyName_, String queryKeyName_) throws Exception{
		return getQuery(querySetKeyName_, queryKeyName_).createQuery();
	}
}
