package soundGame;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

//import sainte.game.soundgame.AudioPlayer;

import javazoom.jl.decoder.JavaLayerException;

/**
 *
 * JavaLayerを使用したmp3専用プレイヤーです。再生をスレッドで行えるように改造してあります。
 *
 */
public class MP3Player //implements AudioPlayer
{
	private MyJLPlayer player;
	private Thread playingThread;
	private String path;

	public MP3Player(File mp3) throws FileNotFoundException
	{
		try
		{
			player = new MyJLPlayer(new BufferedInputStream((new FileInputStream(mp3))));
			path = mp3.getAbsolutePath();
			playingThread = new Thread(player, "MP3-Player");
			playingThread.setPriority(8);
		}
		catch(JavaLayerException e)
		{
			throw new RuntimeException(e);
		}
	}

	public MP3Player(File mp3, int threadPriority) throws FileNotFoundException
	{
		new MP3Player(mp3);
		playingThread.setPriority(threadPriority);
	}

	public void play()
	{
		playingThread.start();
	}

	public void stop()
	{
		player.stopFlag = true;
	}

	public void replay()
	{
		player.stopFlag = false;
		player.restart();
	}

	public int getPlayedTimeMillis()
	{
		return player.getPosition();
	}

	public void close()
	{
		player.isFinished = true;
		player.close();
	}

	public boolean isFinished()
	{
		return player.isFinished;
	}

	private class MyJLPlayer extends javazoom.jl.player.Player implements Runnable
	{
		private boolean stopFlag = false;
		private boolean isFinished = false;

		// javazoom.jl.player.Player#play() と同様の処理です。
		// スレッドの停止、再開処理が可能です。
		public void run()
		{
			boolean ret = true;

			while(ret)
			{
				synchronized(this)
				{
					while(stopFlag)
					{
						try
						{
							wait();
						}
						catch(InterruptedException e1) {}
					}
				}

				try
				{
					ret = decodeFrame();
				}
				catch(JavaLayerException e)
				{
					throw new RuntimeException(e);
				}
			}

			isFinished = true;
		}

		public synchronized void restart()
		{
			notify();
		}

		public MyJLPlayer(InputStream stream) throws JavaLayerException
		{
			super(stream);
		}

		protected boolean decodeFrame() throws JavaLayerException
		{
			if(!isFinished)
				return super.decodeFrame();
			else
				return false;
		}
	}

	public String getMusicPath()
	{
		return path;
	}
}
