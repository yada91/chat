package chat;

import java.io.BufferedReader;
import java.io.IOException;

public class ChatClientReceiveThread extends Thread {

	private BufferedReader br;

	public ChatClientReceiveThread(BufferedReader br) {
		this.br = br;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String str;
		try {
			while ((str = br.readLine()) != null) {
				System.out.println(str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}

}
