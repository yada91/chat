package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

	private static final int SERVER_PORT = 9090;

	public static void main(String[] args) {
		String SERVER_IP;
		// TODO Auto-generated method stub
		Scanner sc = null;
		Socket socket = null;
		try {
			SERVER_IP = args[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			SERVER_IP = "192.168.1.10";
		}

		try {
			// 1. 키보드 연결
			sc = new Scanner(System.in);

			// 2. socket 생성
			socket = new Socket();
			// 3. 연결
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

			// 4. reader/writer 생성
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

			// 5. join 프로토콜
			System.out.print("닉네임>>");
			String nickname = sc.nextLine();
			pw.println("join:" + nickname);
			pw.flush();

			// 6. ChatClientReceiveThread 시작
			Thread thread = new ChatClientReceiveThread(br);
			thread.start();

			// 7. 키보드 입력 처리
			while (true) {
				String input = sc.nextLine();

				if ("quit".equals(input)) {
					// 8. quit 프로토콜 처리
					pw.print("quit:" + input + "\r\n");
					pw.flush();
					thread.stop();
					break;
				} else {
					pw.print("message:" + input + "\r\n");
					pw.flush();
				}
			}

		} catch (IOException ex) {
			ChatServer.consolelog("error:" + ex);
		} finally {
			try {
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
