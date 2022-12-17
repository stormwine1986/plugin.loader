package plugin.loader;

import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class LoadingBar extends JFrame implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JProgressBar progress;

	private boolean closeable;
	
	public LoadingBar(int size) {
		super("");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int winWidth = 250;
		int winHeight = 20;
		progress = new JProgressBar(1, size);
		progress.setStringPainted(true);
		progress.setBackground(Color.PINK);
		progress.setString("DOWNLOADING ...");
		this.add(progress);
		this.setUndecorated(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setBounds((width-winWidth)/2, (height-winHeight)/2, winWidth, winHeight);
		this.setVisible(true);
	}

	@Override
	public void run() {
		while(!this.closeable) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void up(int size) {
		progress.setValue(progress.getValue() + size);
	}

	public void close() {
		this.closeable = true;
		this.dispose();
	}

}
