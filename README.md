#JGDBConnection for JAVA
###사용하기 전, 반드시 라이센스를 확인하세요

#색인

###Documents

[라이브러리 개요](#summary)<br>
[라이브러리 사용을 위한 환경](#environment)<br>
[JGDBXMLQuery를 위한 XML작성](#howToWriteXML)<Br>

###[Classes](#classes)

[com.jg.db.JGDBConfig](#com.jg.db.JGDBConfig)<br>
[com.jg.db.JGDBConnection](#com.jg.db.JGDBConnection)<br>
[com.jg.db.JGDBKeyword](#com.jg.db.JGDBKeyword)<Br>

[com.jg.db.vo.JGDBParameter](#com.jg.db.vo.JGDBParameter)<Br>
[com.jg.db.vo.JGDBQuery](#com.jg.db.vo.JGDBQuery)<Br>

[com.jg.db.xml.JGDBXMLQuery](#com.jg.db.xml.JGDBXMLQuery)<Br>
[com.jg.db.xml.JGDBXMLQueryManager](#com.jg.db.xml.JGDBXMLQueryManager)<Br>
[com.jg.db.xml.JGDBXMLQuerySet](#com.jg.db.xml.JGDBXMLQuerySet)<Br>

[com.jg.db.xml.cond.JGDBXMLQueryConditionDef](#com.jg.db.xml.cond.JGDBXMLQueryConditionDef)<Br>
[com.jg.db.xml.cond.JGDBXMLQueryConditionIsEquals](#com.jg.db.xml.cond.JGDBXMLQueryConditionIsEquals)<Br>
[com.jg.db.xml.cond.JGDBXMLQueryConditionIsNotNull](#com.jg.db.xml.cond.JGDBXMLQueryConditionIsEquals)<Br>

#Documents

<a name="summary"></a>
##라이브러리 개요

JGDBConnection 라이브러리는 JAVA환경에서 보다 쉽게 DB작업을 수행하기 위한 라이브러리입니다.<br>또한 보다 쉽게 질의문을 관리, 사용할 수 있도록 [JGDBXMLQueryManager](#com.jg.db.xml.JGDBXMLQueryManager)를 제공하고 있습니다.<br>

###JGDBConnection 라이브러리 대략적 관계구조

	JGDBConnection <-(참조)- JGDBConfig
		- JGDBQuery <-(해석)- JGDBParameter
		
	JGDBXMLQueryManager <-(추출 및 해석)- 질의문이 정의된 XML파일
		- JGDBXMLQuerySet
			- JGDBXMLQuery -(생성)-> JGDBQuery		

<a name="environment"></a>
##라이브러리 사용을 위한 환경

###JGDBConnection 라이브러리는<br>
[JDOM 라이브러리](http://www.jdom.org/)가 필요합니다.<br>
[JGDataset 라이브러리](https://github.com/kimbobv22/JGDataset)가 필요합니다.<br><br>

###[JGDBXMLQueryManager](#com.jg.db.xml.JGDBXMLQueryManager)이 질의문이 정의된 XML파일을 불러오기 위한 기본설정이 필요합니다.


	JGDBXMLQueryManager.setXMLDirectoryPath(XML폴더뿌리경로);

만약 위에 과정이 이루어지지 않으면 [JGDBXMLQueryManager](#com.jg.db.xml.JGDBXMLQueryManager)에 대한 라이브러리가 원활하게 구동하지 않을 수 있습니다.

<a name="howToWriteXML"></a>
##[JGDBXMLQuery](#com.jg.db.xml.JGDBXMLQuery)를 위한 XML작성

[JGDBXMLQueryManager](#com.jg.db.xml.JGDBXMLQueryManager) 구동 시, 기본설정된 뿌리경로를 통하여 질의문이 정의된 XML파일을 자동 추출,해석하여 JGDBXMLQuery를 생성합니다.<br>XML을 해석하기 위해서는 규격에 맞는 XML을 작성해야 합니다.<br>

###특징
* 뿌리경로에 생성된 자식경로는 마지막 수준까지 자동으로 추출함으로 필요에 따라 폴더를 나누어 관리할 수 있습니다.
* 하나의 XML파일에 복수의 질의문을 정의할 수 있습니다.

###작성법

기본적인 작성형식은 아래와 같습니다.

	<?xml version="1.0" encoding="UTF-8"?>
	<queryset keyName="querySet키값">
		
		<query keyName="query키값1">
		SELECT * FROM 테이블
		WHERE 1=1
		</query>
		
		<query keyName="query키값2">
		UPDATE 테이블
		WHERE 1=1
		AND   COL1 = 'KEY'
		</query>
		
		...(복수의 query 노드 작성가능)...
		
	</queryset>
	
뿌리노드(이하 queryset)에 반드시 keyName 특성값이 정의되어야 합니다.<br>queryset의 keyName 특성값은 [JGDBXMLQuerySet](#com.jg.db.xml.JGDBXMLQuerySet)의 키값이 됩니다.<br>__*키값이 중복되면 해석순서에 따라 병합됩니다.__<br>

	//queryset 호출방법
	JGDBXMLQueryManager.sharedManager().getQuerySet(String queryset키값);

quackuery로 작성이 가능하며 각 query 또한 keyName 특성값이 정의되어야 합니다.<br>query의 keyName 특성값은 [JGDBXMLQuery](#com.jg.db.xml.JGDBXMLQuery)의 키값이 됩니다.<br>
__*키값이 중복되면 해석순서에 따라 병합됩니다.__


	//query 호출방법
	JGDBXMLQueryManager.sharedManager().getQuerySet(String queryset키값).getQuery(query키값);
	JGDBXMLQueryManager.sharedManager().getQuery(String queryset키값, String query키값);
필요에 따라 매개변수나 [JGDataset](https://github.com/kimbobv22/JGDataset)의 열값을 매핑할 수 있습니다.<br>
매핑형식은 <code>#{매개변수명 or [JGDataset](https://github.com/kimbobv22/JGDataset)의 열값,[true | false]}</code>이며 두번째 인자는 생략이 가능합니다.<br>
질의문 생성 시 매핑형식의 두번째 인자에 따라 true 일 경우 PreparedStatement 형식에 따라 매핑되고, false 일 경우는 단순 변환매핑됩니다.

	<query keyName="test">
	SELECT * FROM 테이블
	WHERE 1=1
	AND   COL1 = #{testValue}
	AND   COL2 = #{testValue,false}
	</query>
	
<a name="howToWriteXMLCondition"></a>
또한, 질의조절문을 사용하여 매개변수나 [JGDataset](https://github.com/kimbobv22/JGDataset)의 열값에 따라 질의문 노출을 조절할 수 있습니다.<br>라이브러리에서 기본으로 제공하는 isnotnull, isequals 질의조절문이 있으며, 필요에 따라 사용자가 정의하여 사용할 수 있습니다.<br>질의조절문 정의방법은 [JGDBXMLQueryConditionDef](#com.jg.db.xml.cond.JGDBXMLQueryConditionDef)를 참조하세요.

	<query keyName="test">
	SELECT * FROM 테이블
	WHERE 1=1

		쿼리생성 시, 해당 매개변수나 열값이 null이 아닐 경우에 아래 구문이 포함됩니다.
		isReverse는 생략가능하며 true 일 경우 조건이 반전됩니다.
		<isnotnull columnName="매개변수명 or 열명" [isReverse="[true | false]"] >
		AND   COL1 = #{testValue}
		AND   COL2 = #{testValue,false}
		</isnotnull>
	
		쿼리생성 시, 해당 매개변수나 열값이 columnValue와 같을 경우 아래 구문이 포함됩니다.
		isReverse는 생략가능하며 true 일 경우 조건이 반전됩니다.
		<isequals columnName="매개변수명 or 열명" columnValue="값" [isReverse="[true | false]"] >
		AND   COL1 = #{testValue}
		AND   COL2 = #{testValue,false}
		</isequals>
	
	</query>
	
필요에 따라 특정 XML질의문을 분기하여 구문으로 포함시킬 수 있습니다.

#### 
	//전체 queryset을 대상으로 검색
	#imp{queryset키값,query키값}

	//현재 queryset 대상으로만 검색
	#imp{query키값}

#### 
	
	<queryset keyName="test">
		
		<query keyName="query1">
		SELECT * FROM (#imp{query2})
		</query>

		<query keyName="query2">
		SELECT COL1, COL2 FROM 테이블
		</query>
		
		<query keyName="query3">
		#imp{test,query2}
		</query>
		
	</queryset>
	
	1. query1으로 생성 시 결과값 
	SELECT * FROM (SELECT COL1, COL2 FROM 테이블)
	
	2. query2으로 생성 시 결과값 
	SELECT COL1, COL2 FROM 테이블
	
	3. query3으로 생성 시 결과값 
	SELECT COL1, COL2 FROM 테이블

<a name="classes"></a>
#Classes

<a name="com.jg.db.JGDBConfig"></a>
##com.jg.db.JGDBConfig

[JGDBConnection](#com.jg.db.JGDBConnection)을 통해 DB 접속을 하기 위한 환경설정 클래스입니다.

###생성자

	public JGDBConfig(String jdbc클래스명, String DB주소, String 사용자명, String 사용자암호, String 케릭터셋);

<a name="com.jg.db.JGDBConnection"></a>
##com.jg.db.JGDBConnection

DB에 접속하여 실제 질의수행 및 결과처리를 담당하는 클래스입니다.

###생성자

	public JGDBConnection(JGDBConfig DB설정) throws Exception;

###주요함수

DB 작업을 위한 기본적인 함수를 제공합니다.


	public Connection getConnection();
	public void commit() throws Exception;
	public void rollback() throws Exception;

모든 질의문은 PreparedStatement로 구성됩니다.<br>
질의수행을 통하여 결과값을 [JGDataset](https://github.com/kimbobv22/JGDataset)형식으로 얻을 수 있습니다.


	//일반 질의수행
	public JGDataset executeQuery(String 질의문, Object[] 매개변수, int ResultSet형식, int resultSet동시실행형식) throws Exception;
	public  executeQuery(JGDBQuery 질의문, int ResultSet형식, int resultSet동시실행형식) throws Exception;
	
	//일반 질의수행 후, 첫번째 행,열에 대한 값만 가져오기
	public Object executeQueryAndGetFirst(String 질의문, Object[] 매개변수, int ResultSet형식, int resultSet동시실행형식) throws Exception;
	public Object executeQueryAndGetFirst(JGDBQuery 질의문, int ResultSet형식, int resultSet동시실행형식) throws Exception;
	
질의을 통하여 특정 작업을 수행합니다.


	//일반 작업수행
	public int executeUpdate(String 질의문, Object[] 매개변수) throws Exception;
	public int executeUpdate(JGDBQuery 질의문) throws Exception;
	
	//프로시져 호출
	public boolean callProcedure(String 질의문, Object[] 매개변수, int resultSet형식, int resultSet동시실행형식) throws Exception;
	public boolean callProcedure(JGDBQuery 질의문, int resultSet형식, int resultSet동시실행형식) throws Exception;
	
또한, [JGDataset](https://github.com/kimbobv22/JGDataset)을 이용하여 다중행에 대한 INSERT, UPDATE, DELETE 작업을 수행할 수 있습니다.<br>
[JGDataset](https://github.com/kimbobv22/JGDataset)의 행상태에 따라서 자동으로 질의문을 생성합니다.

	public int executeUpdate(JGDataset 데이터셋, String 테이블명, boolean 모든행포함여부) throws Exception;

<a name="com.jg.db.JGDBKeyword"></a>
##com.jg.db.JGDBKeyword

JGDBConnection 라이브러리에서 이용하는 키워드가 정의되어 있는 클래스입니다.

<a name="com.jg.db.vo.JGDBParameter"></a>
##com.jg.db.vo.JGDBParameter

[JGDBQuery](#com.jg.db.vo.JGDBQuery)에서 질의문 자동생성 시, 매개변수를 매핑하기 위한 클래스입니다. 값매개변수나 키매개변수를 정의할 수 있습니다.<br>

###생성자


	public JGDBParameter(String 대상명(테이블명));
	
###주요함수

기본적으로 값매개변수를 추가,삭제,가져오기 할 수 있습니다.

	//추가
	public void addValue(JGDBParameterValue 매개변수, int 색인);
	public void addValue(JGDBParameterValue 매개변수, int 색인);
	public JGDBParameterValue addValue(String 열명, Object 값, int 색인);
	
	//삭제
	public void removeValueAtIndex(int 색인);
	public void removeValue(JGDBParameterValue 매개변수);
	public void removeValueAtColumn(String 열명);
	
	//가져오기
	public JGDBParameterValue getValueAtIndex(int 색인);
	public JGDBParameterValue getValueAtColumn(String 열명);
	
	//색인검색
	public int indexOfValue(JGDBParameterValue 매개변수);
	public int indexOfValueWithColumn(String 열명);
	
[JGDBQuery](#com.jg.db.vo.JGDBQuery)활용을 위한 키매개변수를 추가,삭제,가져오기 할 수 있습니다.

	//추가
	public void addKey(JGDBParameterKey 매개변수, int 색인);
	public void addKey(JGDBParameterKey 매개변수, int 색인);
	public JGDBParameterKey addKey(String 열명, Object 값, int 색인);
	
	//삭제
	public void removeKeyAtIndex(int 색인);
	public void removeKey(JGDBParameterKey 매개변수);
	public void removeKeyAtColumn(String 열명);
	
	//가져오기
	public JGDBParameterKey getKeyAtIndex(int 색인);
	public JGDBParameterKey getKeyAtColumn(String 열명);
	
	//색인검색
	public int indexOfKey(JGDBParameterKey 매개변수);
	public int indexOfKeyWithColumn(String 열명);
	
<a name="com.jg.db.vo.JGDBQuery"></a>
##com.jg.db.vo.JGDBQuery

질의수행을 위한 클래스입니다. <br>

###생성자 

	public JGDBQuery();

###주요함수

기본적으로 쿼리를 정의할 수 있습니다.
	
	public void setQuery(String 질의문);
	public void addParameter(Object 매개변수);

필요에 따라 [JGDBParameter](#com.jg.db.vo.JGDBParameter)를 이용하여 질의를 자동정의 할 수 있습니다.

	//SELECT
	public void fillQueryForSELECT(JGDBParameter 매개변수);
	
	//UPDATE
	public void fillQueryForUPDATE(JGDBParameter 매개변수);
	
	//INSERT
	public void fillQueryForINSERT(JGDBParameter 매개변수);
	
	//DELETE
	public void fillQueryForDELETE(JGDBParameter parameter_);
	
	//PROCEDURE
	public void fillQueryForPROCEDURE(JGDBParameter parameter_);

<a name="com.jg.db.xml.JGDBXMLQueryManager"></a>
##com.jg.db.xml.JGDBXMLQueryManager

질의문을 형식이 규격화된 XML로부터 불러와 관리하는 클래스입니다. 이 클래스를 이용하여 보다 편하게 질의문을 접근,사용할 수 있습니다.<br>


###생성자

복수로 생성할 수 없으며 하나의 공유된 인스턴스로 사용이 가능합니다.

	static public JGDBXMLQueryManager sharedManager();


###주요함수

기본적으로 XML질의집합이나 XML질의를 가져올 수 있습니다.


	//XML질의집합 가져오기
	public JGDBXMLQuery getQuerySet(String queryset키값);
	
	//XML질의 가져오기
	public JGDBXMLQuery getQuery(String queryset키값, String query키값);

###전용(Redirect)함수

자세한 내용은 [JGDBQuery](com.jg.db.xml.JGDBXMLQuery)을 참조하세요.

	public JGDBQuery createQuery(String xml질의집합키, String xml질의키, JGDataset 데이타셋, int 행색인) throws Exception;
	public JGDBQuery createQuery(String xml질의집합키, String xml질의키, JGDataset 데이타셋) throws Exception;
	public JGDBQuery createQuery(String xml질의집합키, String xml질의키, Object[] 열명과 열값, String[] 키열명) throws Exception;
	public JGDBQuery createQuery(String xml질의집합키, String xml질의키, Object[] 열명과 열값) throws Exception;
	public JGDBQuery createQuery(String xml질의집합키, String xml질의키) throws Exception;

<a name="com.jg.db.xml.JGDBXMLQuerySet"></a>
##com.jg.db.xml.JGDBXMLQuerySet

[JGDBXMLQuery](#com.jg.db.xml.JGDBXMLQuerySet)를 담고 있는 집합입니다.<br>
보다 편리한 XML질의 관리를 위해 설계된 클래스입니다.

###생성자

XML파일을 추출, 해석으로 자동생성되며 외부에서 추가할 수 없습니다.

###주요함수


	public JGDBXMLQuery getQuery(int 색인);
	public JGDBXMLQuery getQuery(String 키값);

	//색인검색
	public int indexOfQuery(String 키값);

<a name="com.jg.db.xml.JGDBXMLQuery"></a>
##com.jg.db.xml.JGDBXMLQuery

질의문을 형식이 규격화된 XML로부터 불러와 관리하는 클래스입니다. 이 클래스를 이용하여 보다 편하게 질의문을 접근,사용할 수 있습니다. XML작성을 위한 규격 및 방법은 [여기](#howToWriteXML)를 참조하세요.

###생성자

XML파일 추출을, 해석으로 자동생성되며 외부에서 추가할 수 없습니다.

###주요함수

JGDataset을 이용하여 XML질의를 질의로 생성할 수 있습니다.

	public JGDBQuery createQuery(JGDataset 데이터셋, int 행색인) throws Exception;
	public JGDBQuery createQuery(JGDataset 데이터셋) throws Exception;
	public JGDBQuery createQuery(JGDataset 데이터셋) throws Exception;
	public JGDBQuery createQuery(String xml질의집합키, String xml질의키, Object[] 열명과 열값, String[] 키열명) throws Exception;
	public JGDBQuery createQuery(String xml질의집합키, String xml질의키, Object[] 열명과 열값) throws Exception;

생성예제입니다.

	//XML부분
	<queryset keyName="test">
		<query keyName="query1">
		SELECT * FROM 테이블
		WHERE 1=1
		AND   COL1 = #{col1}
		AND   COL1 = '#{col1,false}'
		
		<isnotnull columnName="col1">
		AND   COL1 = #{col1}
		</isnotnull>
		
		<isnotnull columnName="col2" isReverse="true">
		AND   COL2 = #{col2}
		</isnotnull>
		
		<isequals columnName="col1" columnValue="testValue1">
		AND   COL1 = #{col1}
		</isequals>
		
		<isequals columnName="col2" columnValue="testValue2" isReverse="true">
		AND   COL2 = #{col2}
		</isequals>
		
		</query>
	</queryset>

	//함수부분
	public void testMethod() throws Exception{
		JGDataset dataset_ = new JGDataset();
		dataset_.setColumnValues(dataset_.addRow(),new Object[](){"col1", "testValue1","col2","testValue2"},true);

		JGDBXMLQuery xmlQuery_ = JGDBXMLQueryManager.sharedManager().getQuery("test", "query1");
		JGDBQuery query_ = xmlQuery_.createQuery(dataset_,0);
	
		System.out.println(query_.toString());
	}

	//결과
	[query]
	SELECT * FROM 테이블
	WHERE 1=1
	AND   COL1 = ?
	AND   COL1 = 'testValue1'
	AND   COL1 = ?
	AND   COL1 = ?
	
	[parameters]
	testValue1, testValue1, testValue1


<a name="com.jg.db.xml.cond.JGDBXMLQueryConditionDef"></a>
##com.jg.db.xml.cond.JGDBXMLQueryConditionDef

[JGDBXMLQuery](#com.jg.db.xml.JGDBXMLQuery)가 질의를 생성 시, 매개변수나 열값에 따라 구문생성을 조정할 수 있도록 설계된 조건여과추상클래스입니다.<br>사용자가 필요에 따라 상속받아 분기을 구현,등록 하여 사용할 수 있습니다.

###등록하기


	JGDBXMLQueryManager.sharedManager().putConditionDef(키값(XML조건절노드명), new JGDBXMLQueryConditionDef(){
	
		public boolean acceptConditionStatement(Element 해당XML조건절노드, JGDataset 데이타셋, int 행색인) throws Exception{
			return false;
		}
	
	});


<a name="com.jg.db.xml.cond.JGDBXMLQueryConditionIsEquals"></a>
##com.jg.db.xml.cond.JGDBXMLQueryConditionIsEquals<br>com.jg.db.xml.cond.JGDBXMLQueryConditionIsNotNull

라이브러리에서 제공하는 기본적인 조건여과클래스입니다.<br>
XML조건절 작성에 대한 자세한 내용은 [여기](#howToWriteXMLCondition)를 참조하세요.
