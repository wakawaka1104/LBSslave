package asset;

public interface Classifier {
	
	//受信時の挙動を表す関数
	public void readFunc(byte header);
	public String toString();
}
