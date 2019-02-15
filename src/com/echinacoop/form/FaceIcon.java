package com.echinacoop.form;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public class FaceIcon extends ImageIcon {

	private String facePath;
	private String faceName;
	
	public FaceIcon(){
		super();
	}
	
	public FaceIcon(String filename){
		super(filename);
	}
	
	public FaceIcon(Image image){
		super(image);
	}
	
	public FaceIcon(URL location){
		super(location);
	}

	public String getFacePath() {
		return facePath;
	}

	public void setFacePath(String facePath) {
		this.facePath = facePath;
	}

	public String getFaceName() {
		return faceName;
	}

	public void setFaceName(String faceName) {
		this.faceName = faceName;
	}

}
