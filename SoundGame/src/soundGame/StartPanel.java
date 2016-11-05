package soundGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class StartPanel extends JPanel{

	private JFrame frame;
	private final String musicdir;
	private JList<String> musicList;
	private GamePanel panel;

	public StartPanel(){
		frame = new JFrame("音ゲー");
		frame.setBounds(10, 10, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		Properties configuration = new Properties();
		try {
			InputStream inputStream = new FileInputStream(new File("game.properties"));
			configuration.load(inputStream);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Propertiesファイルが存在しません。", "Error", JOptionPane.ERROR_MESSAGE);
		}
		musicdir = "./"+configuration.getProperty("Directory");

		File f = new File(musicdir);
		String[] lis = f.list();
		Vector<String> list = new Vector<String>();
		for(int i = 0; i < lis.length; i++){
			File fi = new File(musicdir +"/"+ lis[i]);
			if(fi.isDirectory()){
				File file = new File(musicdir +"/"+ lis[i] + "/note.txt");
				if(file.exists()){
					list.add(lis[i]);
				}
			}
		}
		musicList = new JList<String>(list);
		musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );

		JScrollPane sp = new JScrollPane();
		sp.getViewport().setView(musicList);
		sp.setBounds(150,160,300,100);
		musicList.setSelectedIndex(0);

		this.setLayout(null);
		this.add(sp);

		musicList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					if(!getMusicDir(musicList.getSelectedValue()).equals(null)){
//						frame.setEnabled(false);
						try {
							panel.exitPanel();
							panel = new GamePanel(new MusicData(musicdir+"/"+musicList.getSelectedValue()+"/note.txt"),getMusicDir(musicList.getSelectedValue()));
						} catch (FileNotFoundException e1) {
							// TODO 自動生成された catch ブロック
							e1.printStackTrace();
						}catch(java.lang.NullPointerException e2){
							try {
								panel = new GamePanel(new MusicData(musicdir+"/"+musicList.getSelectedValue()+"/note.txt"),getMusicDir(musicList.getSelectedValue()));
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
			}
		});

		JLabel title = new JLabel("音ゲー");
		title.setFont(new Font("MSゴシック",Font.BOLD | Font.ITALIC, 70));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setForeground(Color.ORANGE);
		title.setOpaque(false);
		title.setBounds(150, 30, 300, 100);
		this.add(title);

		JButton button = new JButton("START");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!getMusicDir(musicList.getSelectedValue()).equals(null)){
//					frame.setEnabled(false);
					try {
						panel.exitPanel();
						panel = new GamePanel(new MusicData(musicdir+"/"+musicList.getSelectedValue()+"/note.txt"),getMusicDir(musicList.getSelectedValue()));
					} catch (FileNotFoundException e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
					}catch(java.lang.NullPointerException e2){
						try {
							panel = new GamePanel(new MusicData(musicdir+"/"+musicList.getSelectedValue()+"/note.txt"),getMusicDir(musicList.getSelectedValue()));
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
		button.setBounds(250, 280, 100, 50);
		this.add(button);

		this.setBackground(Color.CYAN);
		frame.add(this);
		frame.setVisible(true);
	}

	public String getMusicDir(String dirname){
		File f = new File(musicdir +"/"+ dirname);
		String[] fs = f.list();
		for(int i = 0; i < fs.length; i++){
			f = new File(musicdir +"/"+ dirname+"/"+fs[i]);
			if(f.isFile() && f.canRead() && f.getPath().endsWith(".mp3")){
				return f.getPath();
			}
		}
		JOptionPane.showMessageDialog(this, "音楽ファイルがありません。", "Error", JOptionPane.ERROR_MESSAGE);
		return null;
	}

	public static void main(String[] args) {
		new StartPanel();
	}

}
