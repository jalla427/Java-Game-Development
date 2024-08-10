package tmp;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import static tmp.Game.gameVolume;

public class AudioPlayer {
	
	private static Clip play;

	 public static void playMusicLoop(String link, double volumeValue) {
		 try {
			 AudioInputStream menuSound = AudioSystem.getAudioInputStream(new File(link));

			 play = AudioSystem.getClip();
			 play.open(menuSound);

			 FloatControl volume = (FloatControl) play.getControl(FloatControl.Type.MASTER_GAIN);
			 volume.setValue((float)(volumeValue * (((double) Game.gameVolume) / 100.0)));

			 play.loop(Clip.LOOP_CONTINUOUSLY);

		 } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e){
			 e.printStackTrace();
		 }
	 }
	 
	 public static void playSound(String link) {
		 try {
			 //Retrieve sound from provided link
			 AudioInputStream gameSound = AudioSystem.getAudioInputStream(new File(link));
			 Clip player = AudioSystem.getClip();
			 player.open(gameSound);

			 //Play sound and account for game volume
			 FloatControl volume = (FloatControl) player.getControl(FloatControl.Type.MASTER_GAIN);
			 volume.setValue(20f * (float) Math.log10(Game.gameVolume / 100.0));
			 player.loop(0);
		 } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e){
			 e.printStackTrace();
		 }
	 }

	 public static void stopMusic() {
		 play.close();
	 }
}
