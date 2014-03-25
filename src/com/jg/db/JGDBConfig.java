package com.jg.db;

/**
 * {@link JGDBConnection}을 생성하기 위한 설정 객체입니다.
 * 
 * @author Hyeong yeon. Kim. kimbobv22@gmail.com
 */
public class JGDBConfig {
	protected String _JDBCClassName = null;
	protected String _URL = null;
	protected String _userName = null;
	protected String _password = null;
	protected String _characterEncoding = null;
	
	/**
	 * JDBC 클래스명을 반환합니다.
	 * 
	 * @return JDBC 클래스명
	 */
	public String getJDBCClassName(){
		return _JDBCClassName;
	}
	/**
	 * DB URL을 반환합니다.
	 * 
	 * @return URL
	 */
	public String getURL(){
		return _URL;
	}
	/**
	 * 사용자명을 반환합니다.
	 * 
	 * @return 사용자명
	 */
	public String getUserName(){
		return _userName;
	}
	/**
	 * 암호를 반환합니다.
	 * 
	 * @return 암호
	 */
	public String getPassword(){
		return _password;
	}
	/**
	 * 문자 인코딩를 반환합니다.
	 * 
	 * @return 문자 인코딩
	 */
	public String getCharacterEncoding(){
		return _characterEncoding;
	}

	/**
	 * 생성자
	 * 
	 * @param jdbcClassName_ JDBC 클래스명
	 * @param url_ 데이타베이스 URL
	 * @param userName_ 사용자명
	 * @param password_ 암호
	 * @param characterEncoding_ 문자 인코딩
	 */
	public JGDBConfig(String jdbcClassName_, String url_, String userName_, String password_, String characterEncoding_){
		_JDBCClassName = jdbcClassName_;
		_URL = url_;
		_userName = userName_;
		_password = password_;
		_characterEncoding = characterEncoding_;
	}

}
