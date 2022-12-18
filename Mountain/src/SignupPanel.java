import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class SignupPanel extends JPanel {
	private MainFrame f;

	private JTextField signupID;
	private JTextField signupPassword;
	JLabel signupFailed;
	
	
	public SignupPanel(MainFrame f) {
		//AbsoluteLayout 사용
		setBounds(0, 0, 984, 637);
		setLayout(null);
		
		
		//컴포넌트 생성 및 추가
		JLabel signupText = new JLabel("회원가입");
		signupText.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 40));
		signupText.setBounds(207, 149, 151, 86);
		this.add(signupText);
		
		JLabel idText = new JLabel("ID");
		idText.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		idText.setBounds(207, 264, 122, 48);
		this.add(idText);
		
		JLabel passwordText = new JLabel("PASSWORD");
		passwordText.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		passwordText.setBounds(207, 322, 144, 48);
		this.add(passwordText);
		
		//회원가입 텍스트 필드
		signupID = new JTextField();
		signupID.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		signupID.setColumns(10);
		signupID.setBounds(355, 273, 307, 39);
		this.add(signupID);
		
		signupPassword = new JTextField();
		signupPassword.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		signupPassword.setColumns(10);
		signupPassword.setBounds(355, 322, 307, 39);
		this.add(signupPassword);
		
		//회원가입 확인 버튼
		JButton comfirm = new JButton("확인");
		comfirm.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		comfirm.setBounds(355, 402, 100, 40);
		this.add(comfirm);
		
		//회원가입 확인 이벤트 리스너
		comfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(insertUser()) {
					f.getCardLayout().show(f.contentPane, "Login");		//정상적으로 회원가입이 되었을 경우 LoginPanel로 이동
				}
				
			}
		});
		
		//회원가입 취소 버튼
		JButton cancel = new JButton("취소");
		cancel.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		cancel.setBounds(561, 402, 100, 40);
		this.add(cancel);
		
		//회원가입 취소 이벤트 리스너
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.getCardLayout().show(f.contentPane, "Login");		//LoginPanel로 이동
			}
		});
		
		
		//중복된 가입
		signupFailed = new JLabel("이미 가입된 회원입니다");
		signupFailed.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 15));
		signupFailed.setForeground(new Color(255, 0, 0));
		signupFailed.setBounds(526, 372, 151, 30);
		this.add(signupFailed);
		signupFailed.setVisible(false);
	}
	
	//데이터베이스에 회원가입한 유저정보를 삽입하기 위한 insertUser 메소드
		boolean insertUser() {
			String insertString = "INSERT INTO user VALUES ";
			boolean judge = true;		//회원가입이 정상적으로 완료되었는지 판단
			
			try {
				DBconnect.stmt = DBconnect.con.createStatement();
				DBconnect.stmt.executeUpdate(insertString + "('"+signupID.getText()+"','"+signupPassword.getText()+"')" );		//분류한 id와 password를 user테이블에 삽입	
				System.out.println("회원정보가 추가되었습니다.");
			}catch(java.sql.SQLIntegrityConstraintViolationException ex){		//id와 password가 중복될 경우 개체 무결정 제약 조건에 위배되어 예외 발생
				signupFailed.setVisible(true);		//중복 가입 텍스트 출력
				judge = false;
			}catch(Exception ex){
				System.out.println(ex.toString());
				
			}
			
			return judge;
		}
}
