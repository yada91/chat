package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServerTread extends Thread {
	private Socket socket;
	private String nickName;
	private List<PrintWriter> listWriters;

	public ChatServerTread(Socket socket, List<PrintWriter> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
	}

	@Override
	public void run() {
		try {
			// logging Remote Host IP Address & Port
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
			ChatServer.consolelog(inetSocketAddress.getHostString());

			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

			while (true) {
				String request = br.readLine(); // 브라우저가 연결을 끊으면
				if (request == null) {
					doQuit(pw);
					break;
				}

				String[] tokens = request.split(":");
				if ("join".equals(tokens[0])) {

					doJoin(tokens[1], pw);

				} else if ("message".equals(tokens[0])) {

					doMessage(tokens[1]);

				} else if ("quit".equals(tokens[0])) {

					doQuit(pw);

				} else {

					ChatServer.consolelog("에러:알수 없는 요청(" + tokens[0] + ")");
				}
			}

		} catch (Exception ex) {
			ChatServer.consolelog("error: " + ex);
		} finally {
			// clean-up
			try {
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}

			} catch (IOException ex) {
				ChatServer.consolelog("error: " + ex);
			}
		}
	}

	private void doJoin(String nickName, PrintWriter pw) {
		this.nickName = nickName;

		String data = nickName + "님이 참여하였습니다.";
		pw.println("어서오세요 매너채팅");
		pw.flush();
		broadcast(data);

		/* writer pool에 저장 */
		addWriter(pw);

		// ack

	}

	private void addWriter(PrintWriter writer) {
		synchronized (listWriters) {
			listWriters.add(writer);
		}
	}

	private void deleteWriter(PrintWriter writer) {
		synchronized (listWriters) {
			listWriters.remove(writer);
		}
	}

	private void broadcast(String data) {

		synchronized (listWriters) {
			for (PrintWriter pw : listWriters) {
				pw.println(data);
				pw.flush();
			}

		}

	}

	private void doMessage(String message) {

		String data = nickName + ":" + message;
		broadcast(data);

	}

	private void doQuit(PrintWriter writer) {
		removeWriter(writer);
		try {
			socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data = nickName + "님이 퇴장 하였습니다.";
		broadcast(data);
		stop();
	}

	private void removeWriter(PrintWriter writer) {
		deleteWriter(writer);
	}

}
