package tmp;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main extends Canvas{

	public Main(String title, int width, int height, Game gamePlay) {
		//Import icon image
		ImageIcon iconImg = new ImageIcon("./res/icon.png");
		
		//Instantiate Outer Container
		JFrame jfrGameCore = new JFrame(title);
		
		setJFrameAttributes(jfrGameCore, iconImg);
		
		//Prepare inner game frame
		gamePlay.setSize(width, height);
		jfrGameCore.add(gamePlay);
		jfrGameCore.pack();
		gamePlay.start();
		
		//Handles changing the window size while game is running.
		//Does nothing while gamePlay.setResizeable = false
		gamePlay.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent componentEvent) {
		        gamePlay.setSize(jfrGameCore.getBounds().width, jfrGameCore.getBounds().height);
		        Game.sWidth = jfrGameCore.getBounds().width;
		        Game.sHeight = jfrGameCore.getBounds().height;
		    }
		});
	}
	
	private void setJFrameAttributes(JFrame frameMain, ImageIcon iconImg) {
		//Sets attributes for main container
		frameMain.setLocation(200, 50);
		frameMain.setResizable(false);
		frameMain.setVisible(true);
		frameMain.setIconImage(iconImg.getImage());
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
