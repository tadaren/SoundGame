package soundGame;

import javax.swing.SwingUtilities;

public class RemoveThread extends Thread {

	private final GamePanel panel;
	private final int index;

	RemoveThread(GamePanel gp, int index) {
		panel = gp;
		this.index = index;
	}
	public void run() {
		doLongTask();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel.setLaneColor(index);
			}
		});
	}
	private void doLongTask() {
		try {
			Thread.sleep(70);
		} catch (InterruptedException ex) {
		}
	}

}
