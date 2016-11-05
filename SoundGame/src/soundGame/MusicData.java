package soundGame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MusicData {

	private int tempo;
	private int speed;
	private int time;

	private byte[] lane1;
	private byte[] lane2;
	private byte[] lane3;
	private byte[] lane4;

	public MusicData(String fileName){
		try {
			FileRead(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MusicData(File f){
		try {
			FileRead(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getTempo() {
		return this.tempo;
	}

	public float getSpeed(){
		return this.speed;
	}

	public int getTime(){
		return this.time;
	}

	public byte[] getLaneData(int index){
		switch(index){
		case 1: return lane1;
		case 2: return lane2;
		case 3: return lane3;
		case 4: return lane4;
		}
		return new byte[1];
	}

	private void FileRead(File f) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(f));
		tempo = Integer.parseInt(br.readLine());
		speed = Integer.parseInt(br.readLine());
		time = Integer.parseInt(br.readLine());
		ArrayList<Byte> lane1 = new ArrayList<Byte>();
		ArrayList<Byte> lane2 = new ArrayList<Byte>();
		ArrayList<Byte> lane3 = new ArrayList<Byte>();
		ArrayList<Byte> lane4 = new ArrayList<Byte>();
		String str;
		while((str = br.readLine()) != null){
			lane1.add(Byte.parseByte(str.substring(0, 1)));
			lane2.add(Byte.parseByte(str.substring(1, 2)));
			lane3.add(Byte.parseByte(str.substring(2, 3)));
			lane4.add(Byte.parseByte(str.substring(3, 4)));
		}
		br.close();
		this.lane1 = toByteFromObject(lane1.toArray());
		this.lane2 = toByteFromObject(lane2.toArray());
		this.lane3 = toByteFromObject(lane3.toArray());
		this.lane4 = toByteFromObject(lane4.toArray());
	}

	private byte[] toByteFromObject(Object[] o){
		byte[] bytes = new byte[o.length];
		for(int i = 0; i < o.length; i++){
			bytes[i] = Byte.parseByte(o[i].toString());
		}
		return bytes;
	}
}
