package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class FilePresentationWindow extends JFrame {

	private JPanel contentPane;
	private JLabel label = new JLabel("");
	private Rectangle nowPosition = new Rectangle(0,0,0,0);
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FilePresentationWindow frame = new FilePresentationWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FilePresentationWindow() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(nowPosition);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setAlwaysOnTop(true);
		contentPane.add(label, BorderLayout.CENTER);

	}
	public void setImageIcon(ImageIcon ii){
		label.setIcon(ii);
		nowPosition = new Rectangle(2000,0,0,0);
		setBounds(nowPosition);
		setSize(ii.getIconWidth(), ii.getIconHeight());
	}
}
