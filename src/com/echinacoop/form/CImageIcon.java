package com.echinacoop.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;

import sun.awt.AppContext;

import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.ImageType;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.Utils;

public class CImageIcon extends JLabel implements Icon {
	private CImageIcon self;
	private ImageType type;
	private String string;
	private int intValue;
	private Object object;

	transient private String filename;
	transient private URL location;

	transient Image image;
	transient int loadStatus = 0;
	ImageObserver imageObserver;
	String description = null;

	private static int mediaTrackerID;

	private final static Object TRACKER_KEY = new StringBuilder("TRACKER_KEY");

	int width = -1;
	int height = -1;

	public CImageIcon() {
		initCompoments();
	}

	public CImageIcon(Image image) {
		this.image = image;
		Object o = image.getProperty("comment", imageObserver);
		if (o instanceof String) {
			description = (String) o;
		}
		loadImage(image);
		initCompoments();
	}

	public CImageIcon(String filename) {
		image = Toolkit.getDefaultToolkit().getImage(filename);
		if (image == null) {
			return;
		}
		this.filename = filename;
		this.description = filename;
		loadImage(image);
		initCompoments();
	}

	public CImageIcon(URL location) {
		image = Toolkit.getDefaultToolkit().getImage(location);
		if (image == null) {
			return;
		}
		this.location = location;
		this.description = location.toExternalForm();
		loadImage(image);
		initCompoments();
	}

	public void initCompoments() {
		self = this;
		self.setIcon(new ImageIcon(image));
		self.setBorder(defBorder);
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(self.type == ImageType.CHAT_PIC && e.getButton() == MouseEvent.BUTTON1){
					try {
						String fileName = (String) object;
						String[] files = fileName.split("[.]");
						fileName = SocketService.imgServerUrl + files[0] + "_large." + files[1];
						File file = Utils.getNetFile(fileName);
						new PicViewer(file).showImage();
						
						//Runtime.getRuntime().exec("rundll32 c:\\Windows\\System32\\shimgvw.dll,ImageView_Fullscreen " + file.getAbsolutePath());
					} catch (Exception ex) {
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				self.setBorder(roverBorder);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				self.setBorder(defBorder);
			}
			
		});
	}

	protected void loadImage(Image image) {
		MediaTracker mTracker = getTracker();
		synchronized (mTracker) {
			int id = getNextID();

			mTracker.addImage(image, id);
			try {
				mTracker.waitForID(id, 0);
			} catch (InterruptedException e) {
				System.out.println("INTERRUPTED while loading Image");
			}
			loadStatus = mTracker.statusID(id, false);
			mTracker.removeImage(image, id);

			width = image.getWidth(imageObserver);
			height = image.getHeight(imageObserver);
		}
	}

	private int getNextID() {
		synchronized (getTracker()) {
			return ++mediaTrackerID;
		}
	}

	private MediaTracker getTracker() {
		Object trackerObj;
		AppContext ac = AppContext.getAppContext();
		synchronized (ac) {
			trackerObj = ac.get(TRACKER_KEY);
			if (trackerObj == null) {
				Component comp = new Component() {
				};
				trackerObj = new MediaTracker(comp);
				ac.put(TRACKER_KEY, trackerObj);
			}
		}
		return (MediaTracker) trackerObj;
	}

	public ImageType getType() {
		return type;
	}

	public void setType(ImageType type) {
		this.type = type;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
		if (imageObserver == null) {
			g.drawImage(image, x, y, c);
		} else {
			g.drawImage(image, x, y, imageObserver);
		}
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}
	
	@Transient
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        loadImage(image);
    }

	public void setImageObserver(ImageObserver observer) {
		imageObserver = observer;
	}

	@Transient
	public ImageObserver getImageObserver() {
		return imageObserver;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		if (description != null) {
			return description;
		}
		return super.toString();
	}
	
	public static Border roverBorder = new Border() {
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(new Color(176, 210, 241));
			g.drawRect(x, y, width - 1, height - 1);
		}
		public Insets getBorderInsets(Component c) {
			return new Insets(1, 1, 1, 1);
		}
		public boolean isBorderOpaque() {
			return true;
		}
	};
	
	public static Border defBorder = new Border() {
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(Color.WHITE);
			g.drawRect(x, y, width - 1, height - 1);
		}
		public Insets getBorderInsets(Component c) {
			return new Insets(1, 1, 1, 1);
		}
		public boolean isBorderOpaque() {
			return true;
		}
	};

}
