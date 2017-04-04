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

public class SlaveClient implements Runnable{

	// member
//	private String server = "";
	private int port = -1;
	private static final int BUF_SIZE = 1024;
	private static final int WAIT_TIME = 500;
	private InetAddress addr = null;
	private Socket s;
	private Selector selector;
	private String data;
	private boolean sendFlag = false;

	private SocketChannel channel = null;
	ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

	// constructor
	public SlaveClient(String server, int port) {
//		this.server = server;

		try {
			s = new Socket(server, port);
		} catch (UnknownHostException e) {
			System.err.println("SlaveClient:constructor[error]");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("SlaveClient:constructor[error]");
			e.printStackTrace();
		}
	}

	public SlaveClient(InetAddress addr, int port) {
		this.addr = addr;
		this.port = port;
		try {
			System.out.println("SlaveClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
		} catch (IOException e) {
			System.err.println("SlaveClient:constructor()[error]");
			e.printStackTrace();
		}
	}

	public void run(){
		open();
		while(true){

			if(sendFlag){

				_asyncSend();

			}else{
				try {
					Thread.sleep(WAIT_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void open(){
		//channel open
		try {
			channel.socket().setReuseAddress(true);
			channel.socket().bind(new InetSocketAddress(addr,port));
//			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString() + ":" + port + "]にバインドしました。");
			//non blocking mode
			channel.configureBlocking(false);
			selector = Selector.open();

		} catch (IOException e) {
			System.err.println("SlaveClient:open()[error]");
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
		data = this.data;
		sendFlag = true;
	}

	//private function
	private void _asyncSend(){
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
			System.err.println("SlaveClient;asyncSend()[error]");
			e.printStackTrace();
		}finally {
			sendFlag = false;
		}
	}

}
