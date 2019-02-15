package com.echinacoop.utils;

import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class TestMusic extends Thread {
	private AudioFormat audioFormat = null;
	private SourceDataLine sourceDataLine = null;
	private DataLine.Info dataLine_info = null;
	private AudioInputStream audioInputStream = null;

	public TestMusic(File file) {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
			audioFormat = audioInputStream.getFormat();
			dataLine_info = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLine_info);
		} catch (Exception e) {
		}
	}

	public TestMusic(URL url) {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(url);
			audioFormat = audioInputStream.getFormat();
			dataLine_info = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLine_info);
		} catch (Exception e) {
		}
	}

	public void run() {
		try {
			byte[] b = new byte[1024];
			int len = 0;
			sourceDataLine.open(audioFormat, 1024);
			sourceDataLine.start();
			while ((len = audioInputStream.read(b)) > 0) {
				sourceDataLine.write(b, 0, len);
			}
			audioInputStream.close();
			sourceDataLine.drain();
			sourceDataLine.close();
		} catch (Exception e) {
		}
	}
}
