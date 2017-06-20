package tcpIp;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import asset.Classifier;

public class SocketClient extends SocketComm implements Runnable{

	//**********private member
	private final static int BUF_SIZE = 1024;

	private Selector selector;
	private byte[] sendData;
	private boolean sendFlag = false;
	private ByteBuffer writeBuffer = ByteBuffer.allocate(BUF_SIZE);

	//**************************

	//************constructor
	public SocketClient(InetAddress addr, int port) {
		try {
			System.out.println("SocketClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString() + ":" + port
					+ "]にバインドしました。");
		} catch (Exception e) {
			System.err.println("SocketClient:constructor()[error]");
			e.printStackTrace();
		}
	}

	public SocketClient(String addr, int port) {
		try {
			System.out.println("SocketClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString() + ":" + port
					+ "]にバインドしました。");
		} catch (Exception e) {
			System.err.println("SocketClient:constructor()[error]");
			e.printStackTrace();
		}
	}

	//************************

	//run():タスク実行時関数
	//sendFlag == true ならば送信処理
	//それ以外はread待機
	public void run() {
		//channel open処理
		try {
			channel.socket().setReuseAddress(true);
			// non blocking mode
			channel.configureBlocking(false);
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_WRITE|SelectionKey.OP_READ);
		} catch (Exception e) {
			System.err.println("SocketClient:open()[error]");
			e.printStackTrace();
			return;
		}

		//送受信待機ループ
		try {
			while (selector.select() > 0) {
				for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					if (sendFlag && key.isWritable()) {
						doSend(key);
					}
					if (key.isReadable()) {
						doRead((SocketChannel) key.channel());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("SocketClient:run()[error]");
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
			System.err.println("SocketClient:_asyncSend()[error]");
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
			System.err.println("SocketClient:doRead()[error]");
			e.printStackTrace();
		}
	}
}
