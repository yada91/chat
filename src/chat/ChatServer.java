package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ServerSocket serverSocket = null;
		List<PrintWriter> listWriters = new ArrayList<PrintWriter>();
		try {
			// 1
			serverSocket = new ServerSocket();

			// 2
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostAddress = inetAddress.getHostAddress();
			serverSocket.bind(new InetSocketAddress(hostAddress, 9090), 5);
			consolelog("연결기다림");
			while (true) {
				// 3
				Socket socket = serverSocket.accept();
				InetSocketAddress inetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
				String remoteHostAddress = inetSocketAddress.getAddress().getHostAddress();

				consolelog("연결됨 from " + remoteHostAddress);

				Thread thread = new ChatServerTread(socket, listWriters);
				thread.start();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			consolelog("error:" + e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			consolelog("error:" + e);
		} finally {
			try {
				if (serverSocket != null && serverSocket.isClosed() == false) {
					// 7
					serverSocket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				consolelog("error:" + e);
			}
		}

	}

	public static void consolelog(String message) {
		System.out.println("[chat server]" + message);
	}
}
