package com.jg.db;


public class JGDBConfig {
	protected String _JDBCClassName = null;
	protected String _URL = null;
	protected String _userName = null;
	protected String _password = null;
	protected String _characterEncoding = null;
	
	public String getJDBCClassName(){
		return _JDBCClassName;
	}
	public String getURL(){
		return _URL;
	}
	public String getUserName(){
		return _userName;
	}
	public String getPassword(){
		return _password;
	}
	public String getCharacterEncoding(){
		return _characterEncoding;
	}

	/**
	 * configuration for make {@link JGDBConnection}<br>
	 * 
	 * @param jdbcClassName_ JDBC class name
	 * @param url_ Database url
	 * @param userName_
	 * @param password_
	 * @param characterEncoding_
	 */
	public JGDBConfig(String jdbcClassName_, String url_, String userName_, String password_, String characterEncoding_){
		_JDBCClassName = jdbcClassName_;
		_URL = url_;
		_userName = userName_;
		_password = password_;
		_characterEncoding = characterEncoding_;
	}

}
