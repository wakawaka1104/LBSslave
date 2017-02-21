package tcpIp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class TestServer implements Runnable{

	/*
	 * 送信されたデータをそのままコールバックするエコーサーバ
	 */


	private static final int BUF_SIZE = 1024;

	private int port;
	private InetAddress addr;
	private Selector selector;

	public TestServer(InetAddress addr, int port){
		this.addr = addr;
		this.port = port;
	}
	
	public void run(){
		
	}

	public void open(){
		ServerSocketChannel serverChannel = null;
		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(addr,port));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("TestServerが起動しました(port=" + serverChannel.socket().getLocalPort() +":[connect]");

			while(selector.select() > 0){
				for(Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();){
					SelectionKey key = (SelectionKey)it.next();
					it.remove();
					if(key.isAcceptable()){
						doAccept((ServerSocketChannel)key.channel());
					}else if(key.isReadable()){
						doRead((SocketChannel)key.channel());
					}
				}
			}
		} catch (IOException e) {
			System.err.println("TestServer:open()[error]");
			e.printStackTrace();
		}
	}

	private void doAccept(ServerSocketChannel serverChannel){

			try {
				SocketChannel channel = serverChannel.accept();
				String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
				System.out.println("[server]:" + remoteAddress + ":[connect]");
				channel.register(selector, SelectionKey.OP_READ);
			} catch (IOException e) {
System.err.println("TestServer:doAccept()[error]");
				e.printStackTrace();
			}
	}

	private void doRead(SocketChannel channel){
		ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
		Charset charset = Charset.forName("UTF-8");
		String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
		try {
			if(channel.read(buf) < 0 ){
				return;
			}
			buf.flip();
			System.out.println("[server]:" + remoteAddress + ":" + charset.decode(buf).toString());
			buf.flip();
			channel.write(buf);
		} catch (IOException e) {
			System.err.println("TestServer:doRead()[error]");
			e.printStackTrace();
		}finally {
			System.out.println("[server]:" + remoteAddress + ":[disconnect]");
			try {
				channel.close();
			} catch (IOException e) {
				System.err.println("TestServer:doRead():close()[error]");
				e.printStackTrace();
			}
		}
	}

}
