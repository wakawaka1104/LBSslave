package asset;

import java.io.Serializable;

import tcpIp.SocketComm;

public class ByteFile implements Serializable, Classifier {

	private byte[] file;
	private String extension = "";
	private static final long serialVersionUID = 3L;

	public ByteFile(byte[] file, String extension){
		this.file = file;
		this.extension = extension;
	}

	@Override
	public void readFunc(byte header, SocketComm sc) {
		System.out.println("[ByteFile]file received");
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public String getClassName() {
		return "ByteFile";
	}




}
