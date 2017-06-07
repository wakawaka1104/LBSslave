package tcpIp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import asset.Classifier;
import asset.IndoorLocation;
import asset.Property;
import asset._Property;

public class SlaveClient extends SocketComm implements Runnable {

	// member
	private static final int BUF_SIZE = 1024;
	private Selector selector;
	private byte[] sendData;
	private boolean sendFlag = false;

	private SocketChannel channel = null;
	ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

	// constructor
	public SlaveClient(InetAddress addr, int port) {
		try {
			System.out.println("SlaveClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString() + ":" + port
					+ "]にバインドしました。");
		} catch (IOException e) {
			System.err.println("SlaveClient:constructor()[error]");
			e.printStackTrace();
		}
	}
	// constructor
	public SlaveClient(String addr, int port) {
		try {
			System.out.println("SlaveClient:channel open");
			channel = SocketChannel.open(new InetSocketAddress(addr, port));
			System.out.println("[client]:" + "[" + channel.socket().getRemoteSocketAddress().toString() + ":" + port
					+ "]にバインドしました。");
		} catch (IOException e) {
			System.err.println("SlaveClient:constructor()[error]");
			e.printStackTrace();
		}
	}

	public void run() {
		open();
		try {
			channel.register(selector, SelectionKey.OP_WRITE|SelectionKey.OP_READ, new IOHandler());
			while (selector.select() > 0) {
				for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					if (sendFlag && key.isWritable()) {
						_asyncSend(key);
						key.interestOps(SelectionKey.OP_READ);
					}
					if (key.isReadable()) {
						doRead((SocketChannel) key.channel());
						key.interestOps(SelectionKey.OP_WRITE);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doRead(SocketChannel channel2) {
		ArrayList<ByteBuffer> bufferList = new ArrayList<ByteBuffer>();
		bufferList.add(ByteBuffer.allocate(BUF_SIZE));
		byte header = 0x00;
		int bufSize = 0;

		Charset charset = Charset.forName("UTF-8");
		String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
		SChannel = channel;
		try {
			for (int index = 0;; index++) {
				int readSize = channel.read(bufferList.get(index));
				if (readSize < 0) {
					return;
				}
				bufferList.get(index).flip();
				// パケット先頭1バイトはClassifier
				if (index == 0) {
					header = bufferList.get(index).get();
				}


				/////////////////////////debug用/////////////////////////////////
				System.out
						.println("[server]:" + remoteAddress + ":" + charset.decode(bufferList.get(index)).toString());
				bufferList.get(index).flip();
				if(index==0){
					bufferList.get(index).position(1);
				}
				//////////////////////////////////////////////////////////////////


				if (readSize == BUF_SIZE) {
					// BUF_SIZEを超えるデータが現れたとき、格納するByteBufferを追加
					bufferList.add(ByteBuffer.allocate(BUF_SIZE));
					continue;
				}
				break;
			}

			for (Iterator<ByteBuffer> it = bufferList.iterator(); it.hasNext();) {
				bufSize += it.next().limit();
			}
			// -1はheader分のバイトを加味しない、という意味
			bufSize -= 1;
			System.out.println("bufSize:" + bufSize + "\n");

			ByteBuffer contents = ByteBuffer.allocate(bufSize);
			for(Iterator<ByteBuffer> it = bufferList.iterator(); it.hasNext();){
				contents.put(it.next());
			}
			contents.flip();

			/////////////////////////debug用//////////////////////
			System.out
			.println("[server]:" + remoteAddress + ":" + charset.decode(contents).toString());
			contents.flip();
			/////////////////////////////////////////////////////

			Classifier cl = (Classifier) deserialize(contents);
			cl.readFunc(header,this);

		} catch (IOException e) {
			System.err.println("TestServer:doRead()[error]");
			e.printStackTrace();
		} finally {
			System.out.println("[server]:" + remoteAddress + ":[disconnect]");
			try {
				channel.close();
			} catch (IOException e) {
				System.err.println("TestServer:doRead():close()[error]");
				e.printStackTrace();
			}
		}

	}

	public void open() {
		// channel open
		try {
			channel.socket().setReuseAddress(true);
			// non blocking mode
			channel.configureBlocking(false);
			selector = Selector.open();
		} catch (IOException e) {
			System.err.println("SlaveClient:open()[error]");
			e.printStackTrace();
		}
	}

	synchronized public void asyncSend(byte[] data) {
		this.sendData = data;
		sendFlag = true;
	}

	// private function
	synchronized private void _asyncSend(SelectionKey key) {

		try {
			IOHandler handler = (IOHandler) key.attachment();
			handler.byteToBuf(sendData);
			handler.handle(key);
			return;
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			sendFlag = false;
		}

	}

	// static function

	public static byte[] serialize(Classifier cl) {
		try {
			byte[] tmp;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutput oo = new ObjectOutputStream(baos);
			oo.writeObject(cl);
			tmp = baos.toByteArray();
			return tmp;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;

	}

	public Object deserialize(ByteBuffer buf){
		try {
			byte[] bufArray;
			bufArray = buf.array();
			ByteArrayInputStream bais = new ByteArrayInputStream(bufArray);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object tmp = ois.readObject();
			bais.close();
			ois.close();
			return tmp;
		} catch (IOException | ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}

	}

	public static byte[] addHeader(byte[] data) {
		byte[] tmp = new byte[data.length + 1];
		tmp[0] = (byte) 0;
		for (int i = 0; i < data.length; i++) {
			tmp[i + 1] = data[i];
		}
		return tmp;
	}

	// デバグ用main
	public static void main(String[] args) {
		_Property prop = _Property.getInstance();
		prop.setName("name");
		prop.setLocation(new IndoorLocation(1, 1, 1));

		Property p = new Property();
		byte[] tmp = serialize(p);

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(tmp);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object tmpObject = ois.readObject();
			bais.close();
			ois.close();

			System.out.println(tmpObject.toString());
		} catch (IOException | ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

}
