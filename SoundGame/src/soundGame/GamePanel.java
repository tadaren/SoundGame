package soundGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener{

	private final char KEY1;
	private final char KEY2;
	private final char KEY3;
	private final char KEY4;

	private final int FRAME_SIZE_X = 600;
	private final int FRAME_SIZE_Y = 800;
	private final int BARPOSITION = 650;

	private final JFrame GAME_FRAME;
	private final GamePanel gp;

	private byte lane1color = 3;
	private byte lane2color = 3;
	private byte lane3color = 3;
	private byte lane4color = 3;

	private final float DISTANCE;
	private final double INTERVAL;
	private final int WAIT_COUNT;
	private final int COUNT_MAX;
	private final int SCORE_POINT;
	private final float SCORE_MAGNIFICATION;
	private final float COMBO_MAGNIFICATION;
	private final int ERROR;
	private final int EVENT;

	private final byte[][] notes;
	private int noteIndex = 0;

	@SuppressWarnings("unchecked")
	private Vector<float[]>[] noteState = new Vector[4];

	private final Timer timer;
	private final MP3Player music;
	private final File keyMusic;
	private final boolean keySound;
	private boolean notPlay = true;
	private double count = 0.0;
	private long eventcount = 0;

	private int score = 0;
	private int combo = 0;

	public GamePanel(MusicData md, String musicdir) throws FileNotFoundException{
		GAME_FRAME = new JFrame("音ゲー");
		gp = this;
		notes = new byte[4][];
		for(int i = 0; i < notes.length; i++){
			notes[i] = md.getLaneData(i+1);
		}
		Properties configuration = new Properties();
		try {
			InputStream inputStream = new FileInputStream(new File("game.properties"));
			configuration.load(inputStream);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Propertiesファイルが存在しません。", "Error", JOptionPane.ERROR_MESSAGE);
		}
		KEY1 = configuration.get("KEY1").toString().charAt(0);
		KEY2 = configuration.get("KEY2").toString().charAt(0);
		KEY3 = configuration.get("KEY3").toString().charAt(0);
		KEY4 = configuration.get("KEY4").toString().charAt(0);
		this.keyMusic = new File(configuration.getProperty("KeyDirectory"));
		this.ERROR = Integer.parseInt(configuration.getProperty("Error"));
		this.EVENT = Integer.parseInt(configuration.getProperty("Event"));
		this.SCORE_POINT = Integer.parseInt(configuration.getProperty("ScorePoint"));
		this.SCORE_MAGNIFICATION = Float.parseFloat(configuration.getProperty("Magnification"));
		this.COMBO_MAGNIFICATION = Float.parseFloat(configuration.getProperty("Combo"));
		keySound = Boolean.parseBoolean(configuration.getProperty("keySound"));
		configuration = null;
		this.DISTANCE = md.getSpeed()/EVENT*100;
		this.INTERVAL = EVENT*60.0/md.getTempo()/100;
		this.COUNT_MAX = md.getTime()*EVENT;
		this.WAIT_COUNT = (int)(650/DISTANCE);
		for(int i = 0; i < noteState.length; i++){
			noteState[i] = new Vector<float[]>();
		}
		GAME_FRAME.setBounds(10, 10, FRAME_SIZE_X, FRAME_SIZE_Y);
		GAME_FRAME.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e){
				if(e.getKeyChar() == KEY1){
					changeColor(1);
					if(keySound){
						try {
							new MP3Player(keyMusic).play();
						} catch (FileNotFoundException e1) {
						};
					}
					Thread removeThread = new RemoveThread(gp,1);
					removeThread.start();
				}else if(e.getKeyChar() == KEY2){
					changeColor(2);
					if(keySound){
						try {
							new MP3Player(keyMusic).play();
						} catch (FileNotFoundException e1) {
						};
					}
					Thread removeThread = new RemoveThread(gp,2);
					removeThread.start();
				}else if(e.getKeyChar() == KEY3){
					changeColor(3);
					if(keySound){
						try {
							new MP3Player(keyMusic).play();
						} catch (FileNotFoundException e1) {
						};
					}
					Thread removeThread = new RemoveThread(gp,3);
					removeThread.start();
				}else if(e.getKeyChar() == KEY4){
					changeColor(4);
					if(keySound){
						try {
							new MP3Player(keyMusic).play();
						} catch (FileNotFoundException e1) {
						};
					}
					Thread removeThread = new RemoveThread(gp,4);
					removeThread.start();
				}else if(e.getKeyChar() == KeyEvent.VK_SPACE && notPlay){
					notPlay = false;
					timer.start();
				}
			}
		});
		GAME_FRAME.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				music.stop();
				timer.stop();
			}
		});

		timer = new Timer(1000/EVENT, this);
		GAME_FRAME.setResizable(false);
		GAME_FRAME.add(this);
		GAME_FRAME.setVisible(true);

		music = new MP3Player(new File(musicdir));
	}

	private void changeColor(int index){
		switch(index){
		case 1: lane1color = checkNote(index);break;
		case 2: lane2color = checkNote(index);break;
		case 3: lane3color = checkNote(index);break;
		case 4: lane4color = checkNote(index);break;
		}
	}
	public void setLaneColor(int index){
		switch(index){
		case 1: lane1color = 3;break;
		case 2: lane2color = 3;break;
		case 3: lane3color = 3;break;
		case 4: lane4color = 3;break;
		}
	}
	private void moveNote(){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < noteState[i].size(); j++){
				noteState[i].get(j)[0] += DISTANCE;
			}
			if(noteState[i].size()>0){
				if(noteState[i].get(0)[0] > 800){
					noteState[i].remove(0);
					combo = 0;
				}
			}
		}

	}
	private void addNote(){
		if(count < 0.023){
			if(noteIndex < notes[0].length){
				for(int i = 0; i < 4; i++){
					switch(notes[i][noteIndex]){
					case 1: noteState[i].add(new float[]{0,1}); break;
					}
				}
				noteIndex++;
				count = INTERVAL;
			}
		}else{
			count-=0.01;
		}
	}
	private byte checkNote(int index){
		index -= 1;
		for(int i = 0; i < noteState[index].size(); i++){
			if(BARPOSITION-ERROR <= noteState[index].get(i)[0] && noteState[index].get(i)[0] <= BARPOSITION+ERROR+4){
				noteState[index].remove(i);
				score += SCORE_POINT*(COMBO_MAGNIFICATION+1);
				combo+=1;
				return 0;
			}else if(BARPOSITION-ERROR*4 < noteState[index].get(i)[0] && noteState[index].get(i)[0] < BARPOSITION-ERROR
					|| BARPOSITION+ERROR < noteState[index].get(i)[0] && noteState[index].get(i)[0] < BARPOSITION+ERROR*4){
				noteState[index].remove(i);
				score += SCORE_POINT*SCORE_MAGNIFICATION*(COMBO_MAGNIFICATION*combo+1);
				combo+=1;
				return 1;
			}else if(BARPOSITION-ERROR*6 < noteState[index].get(i)[0] && noteState[index].get(i)[0] < BARPOSITION-ERROR*4){
				combo = 2;
			}
		}
		return 2;
	}
	public void exitPanel(){
		GAME_FRAME.dispose();
	}

	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		//レーンの色の描画
		switch(lane1color){
		case 0:g2.setColor(Color.ORANGE);break;
		case 1:g2.setColor(Color.GREEN);break;
		case 2:g2.setColor(new Color(190, 255, 255));break;
		case 3:g2.setColor(Color.CYAN);break;
		}
		g2.fill(new Rectangle(0, 0, 120, FRAME_SIZE_Y));
		switch(lane2color){
		case 0:g2.setColor(Color.ORANGE);break;
		case 1:g2.setColor(Color.GREEN);break;
		case 2:g2.setColor(new Color(190, 255, 255));break;
		case 3:g2.setColor(Color.CYAN);break;
		}
		g2.fill(new Rectangle(120, 0, 120, FRAME_SIZE_Y));
		switch(lane3color){
		case 0:g2.setColor(Color.ORANGE);break;
		case 1:g2.setColor(Color.GREEN);break;
		case 2:g2.setColor(new Color(190, 255, 255));break;
		case 3:g2.setColor(Color.CYAN);break;
		}
		g2.fill(new Rectangle(240, 0, 120, FRAME_SIZE_Y));
		switch(lane4color){
		case 0:g2.setColor(Color.ORANGE);break;
		case 1:g2.setColor(Color.GREEN);break;
		case 2:g2.setColor(new Color(190, 255, 255));break;
		case 3:g2.setColor(Color.CYAN);break;
		}

		//右端の部分の描画
		g2.fill(new Rectangle(360, 0, 120, FRAME_SIZE_Y));
		g2.setColor(Color.LIGHT_GRAY);
		g2.fill(new Rectangle(480, 0, 120, FRAME_SIZE_Y));
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(2));
		g2.setFont(new Font("MSゴシック", Font.PLAIN, 20));
		g2.drawString("score", FRAME_SIZE_X-90, 100);
		g2.drawString(""+score, FRAME_SIZE_X-90, 120);
		g2.drawString("combo", FRAME_SIZE_X-90, 160);
		g2.drawString(""+combo, FRAME_SIZE_X-90, 180);

		//レーンの枠線とバーの描画
		g2.drawLine(0, 0, 0, FRAME_SIZE_Y);
		g2.drawLine(120, 0, 120, FRAME_SIZE_Y);
		g2.drawLine(240, 0, 240, FRAME_SIZE_Y);
		g2.drawLine(360, 0, 360, FRAME_SIZE_Y);
		g2.drawLine(480, 0, 480, FRAME_SIZE_Y);
		g2.setStroke(new BasicStroke(3));
		g2.drawLine(0, BARPOSITION, 480, BARPOSITION);

		//ノーツの描画
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(7));
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < noteState[i].size(); j++){
				g2.draw(new Line2D.Double(120*i+3,noteState[i].get(j)[0],120*(i+1)-4,noteState[i].get(j)[0]));
			}
		}
		if(notPlay){
			g2.setFont(new Font("MSゴシック", Font.PLAIN, 40));
			g2.drawString("スペースキーを押してね", 20, 150);
		}
		//repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(eventcount == WAIT_COUNT){
			music.play();
		}
		if(eventcount < COUNT_MAX+WAIT_COUNT){
			moveNote();
			addNote();
		}else{
			timer.stop();
			music.stop();
		}
		eventcount++;
		repaint();
	}
}
