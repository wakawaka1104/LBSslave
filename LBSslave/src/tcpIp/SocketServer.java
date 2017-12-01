package tcpIp;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketServer extends SocketComm implements Runnable{

	//************constructor
	public SocketServer(String addr, int port) {
		super(addr,port);
	}
	//************************

	//run():タスク実行時関数
	//sendDataにデータがあれば送信処理
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
			while (selector.select() > 0 || selector.selectedKeys().size() > 0) {
				for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					if (key.isAcceptable()) {
						doAccept((ServerSocketChannel) key.channel());
					}else
					if (!sendData.isEmpty() && key.isWritable()) {
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

	//接続要求受付時実行関数
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

	public String getClassName(){
		return "SocketServer";
	}





	//test function
	//SocketClientと同時に実行すること
	//localhostでの仮テスト
	public static void main(String[] args) {

		//サーバ起動
		SocketServer ss = new SocketServer("localhost", 11111);
		Thread socketServer = new Thread(ss);
		socketServer.start();

	}
}

