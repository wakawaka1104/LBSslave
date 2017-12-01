package camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

public class CameraAppTest {

	  public void takePicture() throws IOException {
		    Webcam webcam = null;
		    webcam = Webcam.getDefault();
		    if (webcam != null) {
		      System.out.println("Webcam : " + webcam.getName());
		      webcam.open();
		      BufferedImage image = webcam.getImage();
		      File f = new File("pictures" + File.separator + "webcam-capture.png");
		      ImageIO.write(image, "PNG", f);
		    } else {
		      System.out.println("Failed: Webcam Not Found Error");
		    }
		  }

	public static void main(String[] args) {

		try {
			new CameraAppTest().takePicture();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
}
