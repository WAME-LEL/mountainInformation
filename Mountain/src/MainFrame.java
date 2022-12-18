//개발자 : 2019243065 박석주
//개발목적 : MySQL과 GUI, REST API를 활용하여 등산애호가를 위한 산 정보 프로그램 개발
//개발기한 2022-11-25 ~ 2022-12-18

import java.awt.*;
import java.sql.ResultSet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
	JPanel contentPane;
	CardLayout cards = new CardLayout();		//다양한 화면을 사용하기 위해 CardLayout 사용

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainFrame() { 
		DBconnect.makeConnection();		//데이터베이스 연결
		
		
		//프레임 설정
		setTitle("산 정보 프로그램 -박석주-");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 700);
		
		
		//contentPane 설정
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(cards);
		
		
		//패널 추가
		contentPane.add("Login", new LoginPanel(this));		//로그인 패널 추가
		contentPane.add("Signup", new SignupPanel(this)); 	//회원가입 패널 추가
	}
	//다른 클래스에서 CardLayout을 사용하기위한 getCardLayout 메소드
	public CardLayout getCardLayout() {
		return cards;
	}

}
