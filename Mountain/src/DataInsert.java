//MySQL의 mountain 스키마에 user, famousmountain, conquer 테이블을 생성하고 데이터를 삽입하는 클래스


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DataInsert {

	public static void main(String[] args) {
		DataInsert di = new DataInsert();		//DataInsert 객체 생성
		di.createUserTable();
		di.insertUserData();
		di.createMountainTable();
		di.insertMountainData();
		di.createConquerTable();
		
		try {
			//JDBC 연결 종료
			if(DBconnect.stmt != null) DBconnect.stmt.close();
			if(DBconnect.con != null) DBconnect.con.close();		
		}catch(Exception e) {
			System.out.println(e.toString());
		}

	}
	
	DataInsert(){
		DBconnect.makeConnection();		//DBconnect의 makeConnection() 메소드
	}

	//user테이블 생성을 위한 createUserTable 메소드
	void createUserTable() {
		String createString = "CREATE TABLE user "+"(id varchar(20) not null PRIMARY KEY," + 		//id와 password 속성을 가지는 user 테이블 생성
													"password varchar(20) not null)";
		try {
			DBconnect.stmt = DBconnect.con.createStatement();
			DBconnect.stmt.executeUpdate(createString);		//createString 실행
			System.out.println("good");
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	//users.txt의 데이터를 삽입하기 위한 insertUserData 메소드
	void insertUserData() {
		String insertString = "INSERT INTO user VALUES ";
		File f = new File("users.txt");			//users.txt를 가리키는 File객체 생성
		
		StringTokenizer st;
		String str;
		String insertID;
		String insertPassword;
		
		try {
			BufferedReader bf = new BufferedReader(new FileReader(f));
			int i=0;
			DBconnect.stmt = DBconnect.con.createStatement();
			while((str = bf.readLine())!= null) {		//users.txt 한줄 씩 읽어들임
				st = new StringTokenizer(str, "//");		// 읽어들인 문자열을 "//"를 기준으로 나눔
				insertID = st.nextToken();
				insertPassword = st.nextToken();
				DBconnect.stmt.executeUpdate(insertString + "('"+insertID+"','"+insertPassword+"')" );		//분류한 id와 password를 user테이블에 삽입
				i++;	
			}
			System.out.print(i+"개의 고객 정보가 추가되었습니다");
			
		}catch(Exception e){
			System.out.println(e.toString());
			
		}
	}
	//famousmountain테이블 생성을 위한 createMountainTable 메소드
	void createMountainTable() {
		String createString = "CREATE TABLE famousMountain (num varchar(20) not null PRIMARY KEY,"  + "name varchar(20) UNIQUE not null," + 
	"address varchar(30) not null," + "village varchar(10) not null," + "high double not null," + "detail varchar(2000) not null," +"top varchar(2000) not null)"; 		//famousMountain 테이블 생성

		try {
			DBconnect.stmt = DBconnect.con.createStatement();
			DBconnect.stmt.executeUpdate(createString);		//createString 실행
			System.out.println("good");
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}

	//famousMountain 테이블에 famousMountain.txt의 데이터를 삽입하기 위한 insertMountainData 메소드 
	void insertMountainData() {
		String insertString = "INSERT INTO famousMountain VALUES ";
		File f = new File("famousMountain.txt");			//famousMountain.txt를 가리키는 File객체 생성

		String str;
		int i = 0;
		
		System.out.println("명산 정보 추가 중");
		try {
			BufferedReader bf = new BufferedReader(new FileReader(f));
			String mtName;
			DBconnect.stmt = DBconnect.con.createStatement();
			
			//공공데이터포털의 산 정보 서비스 api 활용
			while((mtName = bf.readLine()) != null) {
				StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1400000/service/cultureInfoService/mntInfoOpenAPI"); /*URL*/
				urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=CHAzRgQeztLycJzta0gUitxk5GfLHFDUkt1tsQmDwfzHnRMH%2FOlHXH99wvapI7E%2ByGFb%2F%2FNtBH1tXXSY3Bz0tQ%3D%3D"); /*Service Key*/
				urlBuilder.append("&" + URLEncoder.encode("searchWrd","UTF-8") + "=" + URLEncoder.encode(mtName, "UTF-8")); /*예:1) searchWrd = “북한산”일때 산명안에 “북한산”을 포함하는 “북한산”이 검색됨*/
				URL url = new URL(urlBuilder.toString());		//urlBuilder에 대한 url객체 생성 
				HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 	//http 통신
				connection.setRequestMethod("GET");			//get으로 조회
				connection.setRequestProperty("Content-type", "application/xml");		//xml 형식
				BufferedReader rd;
				if(connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300) {		//통신이 성공할 경우
					rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				} else {
					rd = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				}
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {		//xml의 모든 줄 sb에 추가
					sb.append(line);		
				}
				InputSource is = new InputSource(new StringReader(sb.toString()));
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
				//xpath 생성        
				XPath xpath = XPathFactory.newInstance().newXPath();
				// xpath를 컴파일
				XPathExpression expression = xpath.compile("//response/body/items/item");		
				NodeList nodelist = (NodeList)expression.evaluate(document, XPathConstants.NODESET);	// NodeList 가져오기 /item 아래에 있는 모든 태그들
				
				//famousMountain테이블의 속성을 담을 변수 선언
				String name;			//산 이름
				String address;			//산 주소
				String high;			//산 높이
				String detail;			//산 설명
				String top;				//명산 선정 이유
				String village = null;	//동읍면리, OPENWEATHER API를 사용하기 위함
				String num;				//산 번호
				
				StringTokenizer st;
				
				
				for( int idx=0; idx<nodelist.getLength(); idx++ ){
					// item 밑의 태그
					expression = xpath.compile("mntiname");		//산 이름
					Node node = (Node) expression.evaluate(nodelist.item(idx), XPathConstants.NODE);
					name = node.getTextContent();
					expression = xpath.compile("mntiadd");		//산 주소
					node = (Node) expression.evaluate(nodelist.item(idx), XPathConstants.NODE);
					address = node.getTextContent();
					st = new StringTokenizer(address, " ");
					while(st.hasMoreTokens() == true) {
			        	village = st.nextToken();
			        }
					expression = xpath.compile("mntihigh");		//산 높이
					node = (Node) expression.evaluate(nodelist.item(idx), XPathConstants.NODE);
					high = node.getTextContent();
					expression = xpath.compile("mntidetails");	//산 설명
					node = (Node) expression.evaluate(nodelist.item(idx), XPathConstants.NODE);
					detail = node.getTextContent();
					detail = detail.replace("'", "");
					expression = xpath.compile("mntitop");		//명산 선정 이유
					node = (Node) expression.evaluate(nodelist.item(idx), XPathConstants.NODE);
					top = node.getTextContent();
					top = top.replace("'", "");
					expression = xpath.compile("mntilistno");	//산 번호
					node = (Node) expression.evaluate(nodelist.item(idx), XPathConstants.NODE);
					num = node.getTextContent();
					try {
						if(top.equals(" ") == false) {		//명산인 산만 가져온다, 명산이 아니면  mntitop태그의 text가 " "
							DBconnect.stmt.executeUpdate(insertString + "('"+num+"','"+name+"','"+address+"','"+village+"','"+high+"','"+detail+"','"+top+"')" );
							i++;
						}
						
					}catch(java.sql.SQLIntegrityConstraintViolationException e){
						System.out.println("중복데이터 제거");
					}
					
				}
				
				rd.close();
				connection.disconnect();	
			}
			System.out.println(i+"개의 명산 정보가 추가되었습니다");
			
		}catch(Exception e) {
			System.out.println(e.toString());
		}
		


	}
	//conquer테이블 생성을 위한 createConquerTable 메소드
	void createConquerTable() {
		//user 테이블의 id와 famousMountain테이블의 name을 외래키로 받는 conquer 테이블 생성
		String createString = "CREATE TABLE conquer "+"(id varchar(20) not null," +
														"name varchar(20) not null,"+
														"PRIMARY KEY (id,name),"+
														"FOREIGN KEY (id) REFERENCES user (id),"+
														"FOREIGN KEY (name) REFERENCES famousmountain (name))";
		try {
			DBconnect.stmt = DBconnect.con.createStatement();
			DBconnect.stmt.executeUpdate(createString);		//createString 실행
			System.out.println("good");
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}

}

