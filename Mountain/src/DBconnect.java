//MySQL과 java를 연결하기 위한 클래스

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBconnect {
	static String URL = "jdbc:mysql://localhost:3306/mountain?serverTimezone=Asia/Seoul";		//mountain 스키마에 연결
	static String id = "mountain";
	static String password = "mountain";
	static Connection con = null;
	static Statement stmt = null;
	
	//데이터베이스 연결을 위한 makeConnection 메소드
	static void makeConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");		//com.mysql.cj.jdbc.Driver를 사용하겠다고 지정
		}catch(Exception e) {
			System.out.println(e.toString());
		}
		
		try {
			con = DriverManager.getConnection(URL, id, password);		//URL로 id와 password로 연결
			System.out.println("ok");
			
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	

}
