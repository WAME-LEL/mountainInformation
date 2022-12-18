import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Image;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;

public class MountainPanel extends JPanel {
	Image image = null;
	static String mountainName;			//선택한 산 이름
	String recommendMountainName;		//추천 산 이름
	ArrayList<String> conquerList = new ArrayList<String>();		//해당 사용자가 정복한 산 목록

	
	public MountainPanel(MainFrame f) {
		String name;
		
		int mountainCount = checkConquer();		//정복한 산 체크
		checkRecommend();		//추천할 산 체크
		
		setBounds(0, 0, 984, 637);
		setLayout(new BorderLayout());
	
		
		
		
		JPanel[] panels = new JPanel[91];		//산 이미지와 산 이름을 담을 패널
		JLabel[] images = new JLabel[91];		//산 이미지
		JLabel[] names = new JLabel[91];		//산 이름
		String[] em = new String[91];			//툴팁 텍스트
		
		
		//centerPanel 설정
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(23, 4, 2, 2));
		
		
		try {
			
			DBconnect.stmt =  DBconnect.con.createStatement();
			ResultSet result = DBconnect.stmt.executeQuery("SELECT * FROM famousmountain");
			int i=0; 
			while(result.next()) {
				panels[i] = new JPanel();
				panels[i].setLayout(new BorderLayout());
				panels[i].setPreferredSize(new Dimension(100, 110));;
				
				em[i] = result.getString("name");
				
				images[i] = new JLabel();
				ImageIcon newImage = new ImageIcon(InformationPanel.class.getResource("/image/"+em[i]+".jpg"));		//이미지 불러오기
				image = newImage.getImage().getScaledInstance(100, 100, image.SCALE_SMOOTH);		//이미지 크기 설정
				images[i].setIcon(new ImageIcon(image));		//images[i]의 아이콘으로 설정
				
				names[i] = new JLabel(em[i], SwingConstants.LEFT);
				names[i].setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 15));
				
				for(String conquerMountain : conquerList) {		//정복리스트에 들어있는 산이면 산 이름을 빨간색으로 설정
					if(em[i].equals(conquerMountain)) {
						names[i].setForeground(Color.red);
					}
				}
				
				panels[i].setToolTipText(em[i]);		//툴팁 테스트 설정
				panels[i].add(images[i], BorderLayout.CENTER);
				panels[i].add(names[i], BorderLayout.SOUTH);
				
				centerPanel.add(panels[i]);
				mountainName = em[i];
				
				//패널 마우스 이벤트 리스너
				panels[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {		//패널을 클릭하면 card에 ㅑnformationPanel을 추가하고 이동
						JPanel gd = (JPanel)e.getSource();
						mountainName = gd.getToolTipText();			//InformationPanel로 선택한 산 이름을 넘겨줌 
						f.getContentPane().add("Information", new InformationPanel(f));
						f.getCardLayout().show(f.contentPane, "Information");
					}
				});
				
				i++;
			}
			
			
		}catch(Exception ex) {
			System.out.println(ex.toString());
		}
		
		//스크롤바 생성 및 추가
		JScrollPane centerScroll= new JScrollPane(centerPanel);
		centerScroll.setBounds(503, 89, 442, 244);
		centerScroll.getVerticalScrollBar().setUnitIncrement(20);
		add(centerScroll, BorderLayout.CENTER);
		
		
		//westPanel 설정
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel welcomLabel = new JLabel(LoginPanel.loggedInID + "님 환영합니다!");
		welcomLabel.setOpaque(true);		//배경 투명도 설정, 배경 색을 바꾸기 위함
		welcomLabel.setBackground(Color.white);
		welcomLabel.setHorizontalAlignment(SwingConstants.CENTER);
		welcomLabel.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 25));
		westPanel.add(welcomLabel);
		
		
		JLabel progressLabel = new JLabel("<html>91개의 명산 중<br>" + mountainCount + "개의 산을 등반하였습니다</html>");		//개행을 위해 태그 사용
		progressLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
		progressLabel.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 30));
		westPanel.add(progressLabel);
		
		//추천 산 패널
		JPanel recommendPanel = new JPanel();
		recommendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		recommendPanel.setToolTipText(recommendMountainName);
		recommendPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JPanel gd = (JPanel)e.getSource();
				mountainName = gd.getToolTipText();
				f.getContentPane().add("Information", new InformationPanel(f));
				f.getCardLayout().show(f.contentPane, "Information");
			}
		});
		westPanel.add(recommendPanel);
		recommendPanel.setLayout(null);
		
		
		JLabel recommendText = new JLabel("다음 산 추천");
		recommendText.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 20));
		recommendText.setBounds(107, 10, 113, 29);
		recommendPanel.add(recommendText);
		
		//추천 산 이미지, 아이콘 설정
		JLabel recommendImage = new JLabel();
		recommendImage.setBounds(62, 37, 200, 130);
		ImageIcon newImage = new ImageIcon(InformationPanel.class.getResource("/image/"+recommendMountainName+".jpg"));
		image = newImage.getImage().getScaledInstance(200, 130, image.SCALE_SMOOTH);
		recommendImage.setIcon(new ImageIcon(image));
		recommendPanel.add(recommendImage);
		
		//추천 산 이름
		JLabel recommendName = new JLabel(recommendMountainName);
		recommendName.setHorizontalAlignment(SwingConstants.CENTER);
		recommendName.setFont(new Font("KoPub돋움체 Light", Font.PLAIN, 17));
		recommendName.setBounds(62, 172, 200, 30);
		recommendPanel.add(recommendName);
		
		add(westPanel, BorderLayout.WEST);
	}
	
	//사용자가 정복한 산을 세크하는 checkConquer 메소드
	int checkConquer() {
		int count = 0;
		try {
			DBconnect.stmt = DBconnect.con.createStatement();
			ResultSet result = DBconnect.stmt.executeQuery("SELECT * FROM conquer WHERE id LIKE '"+LoginPanel.loggedInID +"'");	//인수로는 물어볼 내용
			
			while(result.next()) {
				conquerList.add(result.getString("name"));
				count++;
			}
			
		}catch(Exception e) {
			System.out.println(e.toString());
		}
		
		return count;		//지금까지 등반한 산 개수 반환
	}
	//사용자가 아직 정복하지 않은 산 추천
	void checkRecommend() {
		try {
			DBconnect.stmt = DBconnect.con.createStatement();
			//유저가 정복하지 않은 산 중에서 가장 높이가 낮은 산
			ResultSet result = DBconnect.stmt.executeQuery("SELECT name FROM famousmountain WHERE high = (SELECT MIN(high) FROM famousmountain WHERE name NOT IN(SELECT name FROM conquer WHERE id LIKE '"+LoginPanel.loggedInID +"'))");	//인수로는 물어볼 내용
			
			while(result.next()) {
				recommendMountainName = result.getString("name");
			}
			
		}catch(Exception e) {
			System.out.println(e.toString());
		}
		
	}
}
