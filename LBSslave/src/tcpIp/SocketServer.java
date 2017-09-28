package tcpIp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import asset.Classifier;

public class SocketServer extends SocketComm implements Runnable{

	//**********private member
	private final static int BUF_SIZE = 1024;

	private String addr;
	private int port;
	private Selector selector;
	private byte[] sendData;
	private boolean sendFlag = false;

	//***************************

	//************constructor
	public SocketServer(String addr, int port) {
		this.addr = addr;
		this.port = port;
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
					}else
					if (sendFlag && key.isWritable()) {
						doSend((SocketChannel)key.channel());
					}else
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
	private void doAccept(ServerSocketChannel serverChannel) {
		try {
			SocketChannel channel = serverChannel.accept();
			String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
			System.out.println("[server]:" + remoteAddress + ":[connect]");
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} catch (Exception e) {
			System.err.println("SocketServer:doAccept()[error]");
			e.printStackTrace();
		}
	}

	synchronized private void doSend(SocketChannel channel) {
		try {

			ByteBuffer writeBuffer = ByteBuffer.allocate(sendData.length);

			//byte[]をwriteBufferに書き込み
			writeBuffer.clear();
			writeBuffer.put(sendData);
			writeBuffer.flip();

			//send処理
			System.out.println( "ServerSend:[" + new Timestamp(System.currentTimeMillis()).toString() + "]");
			channel.write(writeBuffer);
			writeBuffer.clear();
			return;
		} catch (Exception e) {
			System.err.println("SocketServer:doSend()[error]");
			e.printStackTrace();
		} finally {
			sendFlag = false;
		}
	}

	private void doRead(SocketChannel channel) {
		//読み取ったバイト列を格納するList
		ArrayList<Byte> contentsList = new ArrayList<Byte>();
		//channelから読み取るバイトを一時保持する変数
		ByteBuffer tmp = ByteBuffer.allocate(BUF_SIZE);
		System.out.println( "ServerRead:[" + new Timestamp(System.currentTimeMillis()).toString() + "]");
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
			System.err.println("SocketServer:doRead()[error]");
			System.err.println("読み込んだデータがClassifierクラスでないか、不要な読み込みが行われました．");
		}
		catch (IOException e) {
			System.err.println("SocketServer:doRead()[error]");
			e.printStackTrace();
			System.out.println("[client]:" + channel.socket().getRemoteSocketAddress().toString() + ":[disconnect]");
			try {
				channel.close();
			} catch (Exception _e) {
				System.err.println("SocketServer:doRead():close()[error]");
				_e.printStackTrace();
			}
		}
	}
}
