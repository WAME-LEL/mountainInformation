import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.ResultSet;

public class LoginPanel extends JPanel {
	MainFrame f;
	JTextField loginID;
	JTextField loginPassword;
	JLabel loginFailed;
	
	static String loggedInID = null;		//로그인한 사용자의 ID
	
	public LoginPanel(MainFrame f) {
		//AbsoluteLayout 사용
		setBounds(0, 0, 984, 637);
		setLayout(null);
		
		//컴포넌트 생성 및 추가
		JLabel loginText = new JLabel("로그인");
		loginText.setBounds(207, 149, 122, 86);
		this.add(loginText);
		loginText.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 40));
		
		JLabel idText = new JLabel("ID");
		idText.setBounds(207, 264, 122, 48);
		this.add(idText);
		idText.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		
		JLabel passwordText = new JLabel("PASSWORD");
		passwordText.setBounds(207, 322, 144, 48);
		this.add(passwordText);
		passwordText.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		
		//입력 필드
		loginPassword = new JTextField();
		loginPassword.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		loginPassword.setBounds(355, 322, 307, 39);
		this.add(loginPassword);
		loginPassword.setColumns(10);
		
		loginID = new JTextField();
		loginID.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		loginID.setBounds(355, 273, 307, 39);
		this.add(loginID);
		loginID.setColumns(10);
		
		//버튼
		JButton loginButton = new JButton("로그인");
		loginButton.setBounds(355, 402, 100, 40);
		this.add(loginButton);
		loginButton.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		
		JButton signupButton = new JButton("회원가입");
		signupButton.setBounds(561, 402, 100, 40);
		this.add(signupButton);
		signupButton.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		
		//로그인 실패 텍스트
		loginFailed = new JLabel("로그인 실패!");
		loginFailed.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 15));
		loginFailed.setForeground(new Color(255, 0, 0));
		loginFailed.setBounds(576, 372, 101, 30);
		loginFailed.setVisible(false);
		this.add(loginFailed);
		
		
		//로그인 이벤트 리스너
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(searchUser()) {
					f.getContentPane().add("Mountain", new MountainPanel(f));		
					f.getCardLayout().show(f.contentPane, "Mountain");			//로그인 버튼 클릭시 정상적으로 로그인 되었을 경우 Mountain 카드 추가 후 MountainPanel로 이동
					
					
				}
				
			}
		});
		
		
		//회원가입 이벤트 리스너
		signupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.getCardLayout().show(f.contentPane, "Signup");		//회원가입 버튼 클릭 시 SignupPanel로 이동
			}
		});
		
		
	
	}
	// 데이터베이스에서 id와 password를 찾아 일치하면 로그인시키는 searchUser 메소드
	boolean searchUser() {
		boolean judge = false;		
		try {
			DBconnect.stmt =  DBconnect.con.createStatement();
			ResultSet result = DBconnect.stmt.executeQuery("SELECT * FROM user");
			
			while(result.next()) {
				try {
					if(loginID.getText().equals(result.getString("id")) && loginPassword.getText().equals(result.getString("password"))) {		//result 행 하나씩 id와 password 비교
						judge = true;
						loggedInID = loginID.getText();
						System.out.println("Logged ID :" + loggedInID);
					}
					
				}catch(Exception ex) {
					System.out.println(ex.toString());
				}
				
			}
			if(!judge) {		//데이터베이스에 일치하는 데이터가 없을 시 로그인 실패 
				System.out.println("로그인 실패!");
				loginFailed.setVisible(true);		//로그인 실패 텍스트 출력
			}

		}catch(Exception ex) {
			System.out.println(ex.toString());
		}
		return judge;
	}

}
