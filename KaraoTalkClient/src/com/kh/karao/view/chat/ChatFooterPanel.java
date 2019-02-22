package com.kh.karao.view.chat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.kh.karao.common.Utils;
import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.view.MainFrame;

public class ChatFooterPanel extends JPanel {
	public Socket socket;
	
	private String chatId;
	private String userId = UserClientController.LOGIN_USER.getUserId();
	
	JPanel chatBodyPanel;
	JTextArea textArea;
	
	public volatile boolean sendMessage;//쓰레드간 캐시를 사용하지 않기 위해 volatile키워드 사용
	
	public ChatFooterPanel(String chatId, JPanel chatBodyPanel) {
		this.chatId = chatId;
		this.chatBodyPanel = chatBodyPanel;
		
		connect();
		
		setSize(new Dimension(MainFrame.WIDTH, 100));
		setBackground(Color.WHITE);
		setLayout(null);
		
		int width1 = 30, width2 = 370, width3 = 80;
		
		//1.파일추가버튼
		JButton addFileBtn = new JButton(new ImageIcon("images/plus-icon.png"));
		addFileBtn.setBounds(0,0,width1,100);
		addFileBtn.setBackground(Color.WHITE);
		addFileBtn.setBorder(new EmptyBorder(5,5,5,5));//LineBorder를 대체함.
		
		//2.메세지입력부
//		textArea = new JTextArea(5, 30);//rows, columns => 지정한 rows를 초과하면 스크롤바가 생긴다.
		textArea = new NoTabTextArea(5, 30);//탭키사용을 위한 사용자JTextArea클래스
		
		textArea.setText("내용을 입력하세요.");
		textArea.setBounds(width1,0,width2,100);
		textArea.setBorder(BorderFactory.createCompoundBorder(textArea.getBorder(), BorderFactory.createEmptyBorder(6, 6, 6, 6)));
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setSize(new Dimension(width2, 100));
		scrollPane.setLocation(width1, 0);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder()));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
	    //3.전송버튼
		JButton btn = new JButton("전송");
		btn.setBounds(width1+width2+10,10,width3,50);
		btn.setBackground(Color.YELLOW);
		btn.setBorder(new EmptyBorder(5,5,5,5));//LineBorder를 대체함.
		
		//margin효과로 border를 사용할 수 없다.
//		btn.setBounds(400,0,100,50); 
//		btn.setBorder(BorderFactory.createEmptyBorder(10,10,10,0));//JButton컴포넌트에 emptyborder는 버튼내부에서 작동함. 
		
		textArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textArea.selectAll();
			}
		});
		
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = textArea.getText();
				//Sender쓰레드에서 메세지를 읽도록 하기위한 flag를 true로 세팅
				if(message.trim().length() != 0) sendMessage = true;
			}
		});
		
		add(addFileBtn);
		add(scrollPane);
		add(btn);
		
		if (!textArea.hasFocus()) {
			textArea.requestFocusInWindow();
		}
	}
	
	private void connect(){
		
		try {
			KaraoClient client = new KaraoClient(MainFrame.serverHost, MainFrame.chatServerPort);
			this.socket = client.socket;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * tab press 이벤트때 다음 컴포넌트로 포커스를 넘기기위해서 
	 * processComponentKeyEvent를 오버라이딩한다.
	 * 
	 * @author shqkel1863
	 *
	 */
	class NoTabTextArea extends JTextArea {
        public NoTabTextArea() {}
         
        public NoTabTextArea(int rows, int columns) {
        	super(rows,columns);
        }

		protected void processComponentKeyEvent(KeyEvent e) {
			//KeyEvent.PRESSED시에만 실행(KeyEvent.Typed) 무시
			if (e.getID() == KeyEvent.KEY_PRESSED 
			&& e.getKeyCode() == KeyEvent.VK_TAB ) {
	            e.consume();//textarea에서 tab키의 defaultAction을 진행하지 않도록 방지. 그래서 consume
	            if (e.isShiftDown()) {
	                transferFocusBackward();
	            } else {
	                transferFocus();
	            }  
			}
            else {
                super.processComponentKeyEvent( e );
            }
        }
    }
	
	class KaraoClient {
		Socket socket;
		
		public KaraoClient(String serverIp, int serverPort) throws UnknownHostException, IOException{
			this.socket = new Socket(serverIp, serverPort);
			System.out.println("[chatId="+chatId+", userId="+userId+"] "+serverIp+":"+serverPort+"서버에 연결되었습니다. ");
			
			//클라이언트의 입출력스트림을 독립된 쓰레드로 분리함.
			//만들어진 socket을 전달.
			Thread sender = new Thread(new ClientSender(socket));
			Thread receiver = new Thread(new ClientReceiver(socket));
			sender.start();
			receiver.start();
		}
		
		
	}
	
	class ClientSender implements Runnable{
		private Socket socket;
		private DataOutputStream dos;
		
		/**
		 * 소켓과 대화명을 매개변수로 받는다.
		 * @param socket
		 * @throws IOException
		 */
		private ClientSender(Socket socket) throws IOException{
			this.socket = socket;
			dos = new DataOutputStream(socket.getOutputStream());
			
			//사용할 이름을 최초전송함. 
			dos.writeUTF(chatId+"§"+userId+"§"+new Date().getTime());
		}
		
		@Override
		public void run() {
			
			while(true){
				
				if(sendMessage){
					String msg = textArea.getText();
					
					try {
						msg = chatId+"§"+userId+"§"+new Date().getTime()+"§"+Utils.convertToMsg(msg);
						dos.writeUTF(msg);
						((ChatBodyPanel)chatBodyPanel).addMessageFromMe(msg);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					textArea.setText("");
					sendMessage = false;//다시 flag를 false로 세팅하고 대기함.
				}
			}
		}
		
	}
	
	class ClientReceiver implements Runnable {
		private Socket socket;
		DataInputStream dis;
		
		private ClientReceiver(Socket socket) throws IOException{
			this.socket = socket;
			dis = new DataInputStream(socket.getInputStream());
		}
		
		@Override
		public void run() {
			//소켓이 닫히기 전까지만 실행한다.
			while(!socket.isClosed()){
				try {
					String msg = dis.readUTF();
					System.out.println(msg);
					((ChatBodyPanel)chatBodyPanel).addMessageFromOther(msg);
					
				} catch(SocketException | EOFException e){
					//ignore
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
