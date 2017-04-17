package tcpIp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SlaveClient implements Runnable{

	// member
	private static final int BUF_SIZE = 1024;
	private static final int WAIT_TIME = 500;
	private Selector selector;
	private String sendData;
	private boolean sendFlag = false;

	private SocketChannel channel = null;
	ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

	// constructor
	public SlaveClient(InetAddress addr, int port) {
		try {
			System.out.println("SlaveClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString() + ":" + port + "]にバインドしました。");
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
			//non blocking mode
			channel.configureBlocking(false);
			selector = Selector.open();
		} catch (IOException e) {
			System.err.println("SlaveClient:open()[error]");
			e.printStackTrace();
		}
	}

	synchronized public void asyncSend(String data){
		sendData = data;
		sendFlag = true;
	}

	//private function
	synchronized private void _asyncSend(){
		try {
			channel.register(selector,SelectionKey.OP_WRITE ,new IOHandler());
			while(selector.select() > 0){

				Set<SelectionKey> keys = selector.selectedKeys();
				for(Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); ){
					SelectionKey key = it.next();
					it.remove();

					IOHandler handler = (IOHandler)key.attachment();
					handler.stringToBuf(sendData);
					handler.handle(key);
					return;
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
