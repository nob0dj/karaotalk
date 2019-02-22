package com.kh.karao.common;

import java.awt.Container;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jsoup.Jsoup;


public class Utils {
	private static Properties prop = new Properties();
	
	static {
		try {
			prop.load(new FileInputStream("karao.properties"));
//			prop.list(System.out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String key){
		return prop.getProperty(key);
	}
	
	
	public static void changePanel(Container pane, JPanel currentPanel, JPanel nextPanel){
		pane.remove(currentPanel);
		pane.add(nextPanel);
		pane.repaint();
		
	}
	
	public static void changeComponent(Container container, JComponent current, JComponent next){
		container.remove(current);
		container.add(next);
		container.revalidate();//LayoutManager객체 호출을 통해 container의 하위컴포넌트들을 다시 재배치함.
		container.repaint();//revalidate()이후 다시 repaint()
	}
	
	/**
	 * 정렬된 사용자이름을 가지고 문자열hashCode값을 chatId로 사용함
	 * @param clientSet
	 * @return
	 */
	public static String getEncodedChatId(Set<String> clientSet){
		String tokens = "";
		Object[] a = clientSet.toArray();
		Arrays.sort(a);
		
		for(Object o : a){
			tokens += (String)o;
		}
		
		tokens = String.valueOf(tokens.hashCode());
		
//		//해시함수 적용
//		MessageDigest md = null;
//        try {
//            md = MessageDigest.getInstance("SHA-256");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        
//        //바이트배열로 리턴함.
//        byte[] bytes = tokens.getBytes(Charset.forName("UTF-8"));
//
//        //md객체에 바이트배열을 전달해서 갱신.
//        md.update(bytes);
//        
//        //java.util.Base64인코더를 이용해서 암호화된 바이트배열을 인코딩해서 문자열로 출력.
//        tokens = Base64.getEncoder().encodeToString(md.digest());
		
        return tokens;
	}
	
	/**
	 * Server로깅용 메소드
	 */
	public static String convertToTime(String t) {
		long time = Long.parseLong(t);
		Date d = new Date(time);
		SimpleDateFormat f = new SimpleDateFormat("[yyyy/MM/dd hh:mm:ss] ");
		return f.format(d);
	}

	public static String convertToTime() {
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("[yyyy/MM/dd hh:mm:ss] ");
		return f.format(d);
	}
	
	/**
	 * 전달받은 메세지에서 시간정보 추출
	 * 
	 * MM/dd H:mm
	 * 
	 * @param msg
	 * @return
	 */
	public static String convertToChatTime(String t) {
		long time = Long.parseLong(t);
		Date d = new Date(time);
		SimpleDateFormat f = new SimpleDateFormat("MM/dd H:mm:ss");
		return f.format(d);//10/9 20:16
	}
	
	/**
	 * 전달받은 메세지에 개행문자가 있다면, html코드로 변환하는 메소드
	 * 
	 * @param msg
	 * @return
	 */
	public static String convertToMsg(String msg) {
		String result = "<html>";
		if(msg.indexOf("\n")==-1){
			result += msg;
		}
		else {
			String[] temp = msg.split("\n");
			for(String s : temp){
				result += "<span>"+s+"</span><br>";
			}
		}
		result += "</html>";
		return result;
	}
	
	/**
	 * 전달받은 객체를 해당타입으로 형변환 하는 메소드
	 * 예외처리함.
	 * 
	 * @param socket
	 * @return
	 */
	public static <T> T getObject(ObjectInputStream in){
		T obj = null;
		try {
			obj = (T)in.readObject();
		} catch (EOFException e){
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}


	public static String html2text(String html) {
		html = html.split("<br>")[0];
		return Jsoup.parse(html).text();
	}
	
	
}
