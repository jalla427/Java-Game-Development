package tmp;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	
	private static Clip play;

	 public static void playMusicLoop(String link, Float volumeValue) {
		 try {
		   AudioInputStream menuSound = AudioSystem.getAudioInputStream(new File(link));
		   
		   play = AudioSystem.getClip();
		   play.open(menuSound);
		   FloatControl volume = (FloatControl) play.getControl(FloatControl.Type.MASTER_GAIN);
		   volume.setValue(volumeValue);
		   
		   play.loop(Clip.LOOP_CONTINUOUSLY);

	  }catch (LineUnavailableException | IOException | UnsupportedAudioFileException e){
		  e.printStackTrace();
	  }
	 }
	 
	 public static void playSound(String link, Float volumeValue) {
		 try {
		   AudioInputStream gameSound = AudioSystem.getAudioInputStream(new File(link));
		   Clip click = AudioSystem.getClip();
		   click.open(gameSound);
		   FloatControl volume = (FloatControl) click.getControl(FloatControl.Type.MASTER_GAIN);
		   volume.setValue(volumeValue);
		   click.loop(0);
	  }catch (LineUnavailableException | IOException | UnsupportedAudioFileException e){
		  e.printStackTrace();
	  }
	 }

	 public static void stopMusic() {
		 play.close();
	 }
}
