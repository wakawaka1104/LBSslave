package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.net.InetAddress;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import tcpIp.SlaveClient;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private final Action action = new SwingAction();
	private JLabel stateLabel = new JLabel("none");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
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
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JButton testButton = new JButton("test button");
		testButton.setAction(action);


		contentPane.add(testButton, BorderLayout.SOUTH);


		contentPane.add(stateLabel, BorderLayout.CENTER);
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "testButtonAction");
			putValue(SHORT_DESCRIPTION, "testButtonAction");
		}
		public void actionPerformed(ActionEvent e) {
			stateLabel.setText("sending");
			try{
				InetAddress addr = InetAddress.getLocalHost();
				SlaveClient sc = new SlaveClient(addr,10000);
				sc.Send("test message");
			}catch (Exception error) {
				System.err.println(error);
			}

		}
	}
}
