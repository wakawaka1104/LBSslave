package tcpIp;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import asset.Classifier;

public class SocketServer extends SocketComm implements Runnable{

	//**********private member
	private final static int BUF_SIZE = 1024;

	private int port;
	private Selector selector;
	private byte[] sendData;
	private boolean sendFlag = false;
	private ByteBuffer writeBuffer = ByteBuffer.allocate(BUF_SIZE);

	//***************************

	//************constructor
	public SocketServer(InetAddress addr, int port) {
		try {
			this.port = port;
			System.out.println("SocketClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString() + ":" + port
					+ "]にバインドしました。");
		} catch (Exception e) {
			System.err.println("SlaveClient:constructor()[error]");
			e.printStackTrace();
		}
	}

	public SocketServer(String addr, int port) {
		try {
			this.port = port;
			System.out.println("SocketClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString() + ":" + port
					+ "]にバインドしました。");
		} catch (Exception e) {
			System.err.println("SlaveClient:constructor()[error]");
			e.printStackTrace();
		}
	}

	//************************

	//run():タスク実行時関数
	//sendFlag == true ならば送信処理
	//それ以外はread待機
	public void run() {
		//channel open処理
		ServerSocketChannel serverChannel = null;
		try{
		selector = Selector.open();
		serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		serverChannel.socket().bind(new InetSocketAddress(port));
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("SocketServerが起動しました(port=" + serverChannel.socket().getLocalPort() + ":[connect]");
		} catch(Exception e){
			System.err.println("SocketServer:run()[error]");
			e.printStackTrace();
		}

		//送受信待機ループ
		try {
			while (selector.select() > 0) {
				for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					if (key.isAcceptable()) {
						doAccept((ServerSocketChannel) key.channel());
						key.interestOps(SelectionKey.OP_ACCEPT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
					}
					if (sendFlag && key.isWritable()) {
						doSend(key);
					}
					if (key.isReadable()) {
						doRead((SocketChannel) key.channel());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("SocketServer:run()[error]");
			e.printStackTrace();
			return;
		}
	}

	public void asyncSend(byte[] data){
		sendData = data;
		sendFlag = true;
	}

	public void asyncSend(Classifier ob, byte header){
		sendData = Converter.serialize(ob,header);
		sendFlag = true;
	}

	// private function
	private void doAccept(ServerSocketChannel serverChannel) {
		try {
			SocketChannel channel = serverChannel.accept();
			String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
			System.out.println("[server]:" + remoteAddress + ":[connect]");
			channel.configureBlocking(false);
		} catch (Exception e) {
			System.err.println("SocketServer:doAccept()[error]");
			e.printStackTrace();
		}
	}

	synchronized private void doSend(SelectionKey key) {
		try {
			//byte[]をwriteBufferに書き込み
			writeBuffer.clear();
			writeBuffer.put(sendData);
			writeBuffer.flip();

			//send処理
			channel.write(writeBuffer);
			writeBuffer.clear();
			return;
		} catch (Exception e) {
			System.err.println("SocketServer:_asyncSend()[error]");
			e.printStackTrace();
			return;
		} finally {
			sendFlag = false;
		}
	}

	private void doRead(SocketChannel _channel) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while(true){
				ByteBuffer tmp = ByteBuffer.allocate(BUF_SIZE);
				int readSize = channel.read(tmp);
				baos.write(tmp.array());
				if(readSize < BUF_SIZE) break;
			}
			byte[] contents = baos.toByteArray();
			byte header = contents[0];

			Classifier cl = (Classifier) Converter.deserialize(contents);
			cl.readFunc(header,this);

		} catch (Exception e) {
			System.err.println("SocketServer:doRead()[error]");
			e.printStackTrace();
		}
	}
}
