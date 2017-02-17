package tcpIp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SlaveClient {

	// member
//	private String server = "";
	private int port = -1;
	private static final int BUF_SIZE = 1024;
	private InetAddress addr = null;
	private Socket s;
	private Selector selector;

	private SocketChannel channel = null;
	ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

	// constructor
	public SlaveClient(String server, int port) {
//		this.server = server;

		try {
			s = new Socket(server, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SlaveClient(InetAddress addr, int port) {
		this.addr = addr;
		this.port = port;
	}

	public void open(){
		//channel open
		try {
			channel.socket().setReuseAddress(true);
			channel.socket().bind(new InetSocketAddress(addr,port));
			//non blocking mode
			channel.configureBlocking(false);
			selector = Selector.open();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//public function
	public void send(String data){
		try {
			//send
			OutputStream os = s.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeUTF(data);
			s.close();

//			//callback receive
//			InputStream is = s.getInputStream();
//			DataInputStream dis = new DataInputStream(is);
//			String s = dis.readUTF();
//			System.out.println( "コールバック：["+ s + "]を受信しました");

			//close
//			dis.close();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void asyncSend(String data){
		try {
			channel.register(selector,SelectionKey.OP_WRITE ,new IOHandler());
			while(selector.select() > 0){

				Set<SelectionKey> keys = selector.selectedKeys();
				for(Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); ){
					SelectionKey key = it.next();
					it.remove();

					IOHandler handler = (IOHandler)key.attachment();
					handler.stringToBuf(data);
					handler.handle(key);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
