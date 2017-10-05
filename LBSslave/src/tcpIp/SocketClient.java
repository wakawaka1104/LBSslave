package tcpIp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import asset.Classifier;

public class SocketClient extends SocketComm implements Runnable{

	//**********private member
	private final static int BUF_SIZE = 1024;

	private String addr;
	private int port;

	private Selector selector;
	private SocketChannel channel;
	private byte[] sendData;
	private boolean sendFlag = false;

	//**************************

	//************constructor

	public SocketClient(String addr, int port) {
		this.addr = addr;
		this.port = port;
	}

	//************************

	//run():タスク実行時関数
	//sendFlag == true ならば送信処理
	//それ以外はread待機
	public void run() {
		//channel open処理
		try {
			System.out.println("SocketClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString()	+ "]にバインドしました。");
			channel.socket().setReuseAddress(true);
			// non blocking mode
			channel.configureBlocking(false);
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_WRITE|SelectionKey.OP_READ);
			remoteAddress = channel.socket().getRemoteSocketAddress().toString();
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
						doSend((SocketChannel)key.channel());
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
		while(sendFlag == false){
			sendData = data;
			sendFlag = true;
		}
	}

	public void asyncSend(Classifier ob, byte header){
		while(sendFlag == false){
			sendData = Converter.serialize(ob,header);
			System.out.println("asyncSend:" + ob.getClassName());
			sendFlag = true;
		}
	}
	// private function
	synchronized private void doSend(SocketChannel channel) {
		try {

			ByteBuffer writeBuffer = ByteBuffer.allocate(sendData.length);

			//byte[]をwriteBufferに書き込み
			writeBuffer.clear();
			writeBuffer.put(sendData);
			writeBuffer.flip();

			//send処理
			channel.write(writeBuffer);
			System.out.println( "ClientSend:[" + new Timestamp(System.currentTimeMillis()).toString() + "]");
			writeBuffer.clear();
		} catch (Exception e) {
			System.err.println("SocketClient:doSend[error]");
			e.printStackTrace();
			System.out.println("[server]:" + channel.socket().getRemoteSocketAddress().toString() + ":[disconnect]");
		} finally {
			sendFlag = false;
		}
	}

	private void doRead(SocketChannel channel) {
		//読み取ったバイト列を格納するList
		ArrayList<Byte> contentsList = new ArrayList<Byte>();
		//channelから読み取るバイトを一時保持する変数
		ByteBuffer tmp = ByteBuffer.allocate(BUF_SIZE);
		System.out.println( "ClientRead:[" + new Timestamp(System.currentTimeMillis()).toString() + "]");
		try {
			while(true){
				if(channel.read(tmp) <= 0) break;
				tmp.flip();
				while(tmp.hasRemaining()){
					contentsList.add(tmp.get());
				}
				tmp.clear();
			}
			int contentsSize = contentsList.size();
			byte[] contents = new byte[contentsSize];
			//頭悪い配列結合の図
			for(int i = 0 ; i < contentsSize ; i++){
				contents[i] = contentsList.get(i);
			}
			byte header = contents[0];
			Classifier cl = (Classifier) Converter.deserialize(contents);
			System.out.println("Read:" + cl.getClassName());
			cl.readFunc(header,this);
		}catch(ClassifierReadException e){
			System.err.println("SocketClient:doRead()[error]");
			System.err.println("読み込んだデータがClassifierクラスでないか、不要な読み込みが行われました．");
		}
		catch (IOException e) {
			System.err.println("SocketClient:doRead()[error]");
			e.printStackTrace();
			System.out.println("[client]:" + channel.socket().getRemoteSocketAddress().toString() + ":[disconnect]");
			try {
				channel.close();
			} catch (Exception _e) {
				System.err.println("SocketClient:doRead():close()[error]");
				_e.printStackTrace();
			}
		}
	}
}
