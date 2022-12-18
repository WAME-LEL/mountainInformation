import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class InformationPanel extends JPanel {
	Image image = null;
	
	JLabel mountainHigh;		
	JLabel mountainAddress;	
	JTextArea mountainDetail;
	JTextArea mountainTop;
	JLabel detailLabel;
	JLabel topLabel;
	JLabel weatherLabel;
	
	String village = null;
	JLabel temperature;			//기온
	JLabel humidity;			//습도
	JLabel weather;				//날씨
	

	public InformationPanel(MainFrame f) {
		//AbsoluteLayout 사용
		setBounds(0, 0, 984, 637);
		setLayout(null);
		
		//아이콘 설정
		JLabel mountainImg = new JLabel();
		ImageIcon newImage = new ImageIcon(InformationPanel.class.getResource("/image/"+MountainPanel.mountainName+".jpg"));
		image = newImage.getImage().getScaledInstance(500, 300, image.SCALE_SMOOTH);
		mountainImg.setIcon(new ImageIcon(image));
		mountainImg.setBounds(25, 58, 450, 265);
		add(mountainImg);
		
		//컴포넌트 생성 및 설정, 추가
		JLabel mountainName = new JLabel(MountainPanel.mountainName);
		mountainName.setFont(new Font("KoPub돋움체 Bold", Font.PLAIN, 30));
		mountainName.setBounds(25, 10, 450, 33);
		add(mountainName);
		
		
		mountainHigh = new JLabel();
		mountainHigh.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		mountainHigh.setBounds(503, 10, 160, 33);
		add(mountainHigh);
		
		mountainAddress = new JLabel();
		mountainAddress.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		mountainAddress.setBounds(690, 10, 255, 33);
		add(mountainAddress);
		
		mountainDetail = new JTextArea();
		mountainDetail.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 15));
		mountainDetail.setBounds(503, 89, 442, 244);
		mountainDetail.setBackground(getBackground());
		mountainDetail.setLineWrap(true);		//자동 줄 바꾸기
		mountainDetail.setEditable(false);		//편집 불가
		
		//산 설명 스크롤바
		JScrollPane detailScroll= new JScrollPane(mountainDetail);
		detailScroll.setBounds(503, 89, 442, 244);
		add(detailScroll);
		
		mountainTop = new JTextArea();
		mountainTop.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 15));
		mountainTop.setBackground(getBackground());
		mountainTop.setLineWrap(true);
		mountainTop.setEditable(false);
		
		JScrollPane topScroll= new JScrollPane(mountainTop);
		topScroll.setBounds(503, 368, 442, 244);
		add(topScroll);
		

		//텍스트 생성 및 추가
		detailLabel = new JLabel("설명");
		detailLabel.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 14));
		detailLabel.setBounds(503, 66, 57, 15);
		add(detailLabel);
		
		topLabel = new JLabel("명산 선정 이유");
		topLabel.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 14));
		topLabel.setBounds(503, 343, 86, 15);
		add(topLabel);
		
		weatherLabel = new JLabel("기상 정보");
		weatherLabel.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 25));
		weatherLabel.setBounds(25, 340, 126, 73);
		add(weatherLabel);
		
		weather = new JLabel();
		weather.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		weather.setBounds(25, 405, 345, 27);
		add(weather);
		
		temperature = new JLabel();
		temperature.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		temperature.setBounds(25, 442, 345, 27);
		add(temperature);
		
		humidity = new JLabel();
		humidity.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		humidity.setBounds(25, 479, 345, 27);
		add(humidity);
		
		//정복 버튼
		JButton conquerButton = new JButton();
		if(searchConquer() == true) {		
			conquerButton.setText("정복 취소");		//선택한 선이 정복한 산이면 정복 취소 활성화
		}
		else {
			conquerButton.setText("정복 등록");		//선택한 선이 정복한 산이 아니면 정복 등록 활성화
		}
		
		conquerButton.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		conquerButton.setBounds(120, 549, 100, 40);
		add(conquerButton);
		
		//정복 버튼 이벤트 리스너
		conquerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(searchConquer() == true) {
					deleteConquer();		//정복 등록 삭제
				}
				else {
					insertConquer();		//정복 등록
				}
				f.getContentPane().add("Mountain", new MountainPanel(f));		//바꾼 내용 갱신

			}
		});

		
		JButton beforeButton = new JButton("뒤로");
		beforeButton.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		beforeButton.setBounds(283, 549, 100, 40);
		add(beforeButton);
		
		//뒤로 버튼 이벤트 리스너
		beforeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.getCardLayout().show(f.contentPane, "Mountain");
			}
		});
		
		
		
		searchMountain();
		searchWeather();
	}
	//산 정보를 찾아주는 searchMountain 메소드
	void searchMountain() {
		try {
			DBconnect.stmt =  DBconnect.con.createStatement();
			ResultSet result = DBconnect.stmt.executeQuery("SELECT * FROM famousmountain");
			
			while(result.next()) {
				//선택한 산과 같은 산 정보 가져오기
				if(MountainPanel.mountainName.equals(result.getString("name"))) {
					mountainHigh.setText("높이 : " + result.getString("high") + "m");
					mountainAddress.setText(result.getString("address"));
					mountainDetail.setText(result.getString("detail"));
					mountainTop.setText(result.getString("top"));
					village = result.getString("village");
				}
			}


		}catch(Exception ex) {
			System.out.println(ex.toString());
		}
	}
	
	//현재 날씨 정보를 찾아주는 searchWeather 메소드
	void searchWeather() {
		//JSON SIMPLE 라이브러리 활용
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		try {
			//geocodin api 활용, 지역이름으로 위도, 경도 찾기
			URL url = new URL("http://api.openweathermap.org/geo/1.0/direct?q="+village+"&limit=5&appid=5057fec77493e1c3c21a9bf0bbe6ae23");
			URL url2;
			
			HttpURLConnection connect = (HttpURLConnection) url.openConnection();
			connect.setRequestMethod("GET");
			//json형식으로 get
			connect.setRequestProperty("Content-type", "application/json");
			BufferedReader rd;
			if (connect.getResponseCode() >= 200 && connect.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(connect.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			sb.append(rd.readLine());
			String str = sb.toString();
			
			//json형태의 문자열 양쪽의 [] 제거
			str = str.replace("[", "");
			str = str.replace("]", "");
			
			//해당 지역 정보가 여러개 있을 경우 맨 앞의 하나만 사용하기위해 문자열 자르기
			int findIndex = str.indexOf("},{");		//정보가 여러개임을 증명하는 부분의 index 확인 
			if(findIndex != -1) {		//},{가 str에 존재한다면 실행
				String splitResult = str.substring(0, findIndex+1);		//지역정보가 하나만 남도록 문자열 자르기
				str = splitResult;		//str에 자른 문자열 대입
			}
		
			obj = (JSONObject)parser.parse(str);
			
			//json을 파싱하여 valueof로 String 타입으로 변환
			String lat = String.valueOf(obj.get("lat"));	//위도 파싱	
			String lon = String.valueOf(obj.get("lon"));	//경도 파싱

			//openweather api
			url2 = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + lat +"&lon=" + lon +"&appid=5057fec77493e1c3c21a9bf0bbe6ae23&lang=kr");
			connect = (HttpURLConnection) url2.openConnection();
			connect.setRequestMethod("GET");
			connect.setRequestProperty("Content-type", "application/json");
			BufferedReader rd2;
			if (connect.getResponseCode() >= 200 && connect.getResponseCode() <= 300) {
				rd2 = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			} else {
				rd2 = new BufferedReader(new InputStreamReader(connect.getErrorStream()));
			}
			
			//날씨 파싱
			StringBuilder sb2 = new StringBuilder();
			sb2.append(rd2.readLine());
			String str2 = sb2.toString();

			JSONObject job = (JSONObject)parser.parse(str2);
			JSONArray array = (JSONArray) job.get("weather"); 		//날씨 정보
			
			JSONObject result = (JSONObject) array.get(0);
			
			
			
			//기온 파싱
			JSONObject injob = (JSONObject) job.get("main");
			
			Double temp = Double.parseDouble(injob.get("temp").toString());		//바로 double형으로 바꾸면 오류나기 때문에 String으로 바꾼 뒤 double로 변환
			temp=temp-273.15;				//temp의 단위는 켈빈
			temp = (double) (Math.round(temp * 10)/10);
			
			Long hum = (Long) injob.get("humidity");		//습도 정보
			
			weather.setText("날씨 : " + (String) result.get("description"));
			temperature.setText("기온 : "+ temp.toString() + "°C");
			humidity.setText("습도 : " + hum.toString()+ "%");
			
		}catch(Exception e) {
			System.out.println(e.toString());
		}
		
	}
	
	//conquer 테이블에 정복 등록을 추가 해주는 insertConquer 메소드
	void insertConquer() {
		String insertString = "INSERT INTO conquer VALUES ";
		try {
			DBconnect.stmt = DBconnect.con.createStatement();
			DBconnect.stmt.executeUpdate(insertString + "('"+LoginPanel.loggedInID+"','"+MountainPanel.mountainName+"')" );
			System.out.println("정복 등록 완료되었습니다");
		}catch(java.sql.SQLIntegrityConstraintViolationException ex) {		//이미 정복 등록한 정보면 등록 취소
			System.out.println("이미 정복한 정보입니다");
		}catch(Exception ex){
			System.out.println(ex.toString());
			
		}
	}
	//conquer 테이블에 정복 등록을 삭제해주는 deleteConquer 메소드	
	void deleteConquer() {
		String deleteString = "DELETE FROM conquer WHERE ";
		try {
			DBconnect.stmt = DBconnect.con.createStatement();
			DBconnect.stmt.executeUpdate(deleteString + "id LIKE '"+LoginPanel.loggedInID+"' AND name LIKE '"+MountainPanel.mountainName+"'" );
			System.out.println("정복 정보가 삭제되었습니다");
		}catch(Exception ex){
			System.out.println(ex.toString());
			
		}
	}
	//conquer 테이블에서 사용자가 정복한 산인지 확인해주는 searchConquer 메소드
	boolean searchConquer() {
		String selectString = "SELECT name FROM conquer WHERE id LIKE '" + LoginPanel.loggedInID + "' AND name LIKE '" + MountainPanel.mountainName + "'";
		boolean judge = false;
		try {
			DBconnect.stmt = DBconnect.con.createStatement();
			ResultSet result = DBconnect.stmt.executeQuery(selectString);
			
			if(result.next() == true) {			//conquer 테이블에 정복 정보가 있으면 judge = true 
				judge = true;
			}
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
		return judge;
	}
}
