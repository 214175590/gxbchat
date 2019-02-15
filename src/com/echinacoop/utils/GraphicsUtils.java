package com.echinacoop.utils;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JTextPane;

public class GraphicsUtils {
	public static Clipboard clipbd = Toolkit.getDefaultToolkit().getSystemClipboard();
	public static URL url = GraphicsUtils.class.getClassLoader().getResource("");
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	public static int numIndex = 0;

	public static String getPath() {
		String str = null;
		try {
			str = URLDecoder.decode(url.getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return exPath(str);
	}

	public static String exPath(String path) {
		if (null != path && path.length() > 2) {
			path = path.substring(1);
		}
		return path;
	}

	/**
	 * 截图屏幕中制定区域的图片
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return 被截部分的BufferedImage对象
	 * @throws AWTException
	 * @throws InterruptedException
	 */
	public static BufferedImage getScreenImage(int x, int y, int w, int h) throws AWTException, InterruptedException {
		Robot robot = new Robot();
		w = w > 0 ? w : 1;
		h = h > 0 ? h : 1;
		BufferedImage screen = robot.createScreenCapture(new Rectangle(x, y, w, h));
		return screen;
	}

	/**
	 * 给图片添加文字水印
	 * 
	 * @param targetImage
	 *            需要加上水印的图片
	 * @param text
	 *            用做水印的文字
	 * @param font
	 *            水印文字的字体
	 * @param color
	 *            水印文字的颜色
	 * @param x
	 * @param y
	 * @return 加上水印后的BufferedImage对象
	 */
	public static BufferedImage addImageWaterMark(Image targetImage, String text, Font font, Color color, int x, int y) {
		int width = targetImage.getWidth(null);
		int height = targetImage.getHeight(null);

		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.drawImage(targetImage, 0, 0, null);
		g.setFont(font);
		g.setColor(color);
		g.drawString(text, x, y);
		g.dispose();

		return bi;
	}

	/**
	 * 给图片添加图片水印
	 * 
	 * @param markImage
	 *            用做水印的图片
	 * @param targetImage
	 *            需要加上水印的图片
	 * @param x
	 * @param y
	 * @return 加上水印后的BufferedImage对象
	 */
	public static BufferedImage addImageWaterMark(Image targetImage, Image markImage, int x, int y) {
		int wideth = targetImage.getWidth(null);
		int height = targetImage.getHeight(null);

		BufferedImage bi = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		g.drawImage(targetImage, 0, 0, null);
		g.drawImage(markImage, x, y, null);
		g.dispose();

		return bi;
	}

	/**
	 * 将指定图片写入系统剪贴板
	 * 
	 * @param image
	 */
	public static void setClipboardImage(final Image image) {
		Transferable trans = new Transferable() {
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor))
					return image;
				throw new UnsupportedFlavorException(flavor);
			}

		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}

	/**
	 * 返回当前剪切板中文本
	 * 
	 * @return
	 */
	public static String getClipboardText() {
		// 获得当前系统剪切板内容到Transferable(这是一个Java提供的剪切板数据包装对象)
		Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		// 当剪切板数据存在，并且类型为图片时
		if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			// 转换数据为Image并返回
			try {
				return (String) transferable.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 复制文本
	 * 
	 * @param text
	 */
	public static void copy(JTextPane text) {
		String selection = text.getSelectedText();
		if (selection == null)
			return;
		StringSelection clipString = new StringSelection(selection);
		clipbd.setContents(clipString, null);
	}

	/**
	 * 剪切
	 * 
	 * @param text
	 */
	public static void cut(JTextPane text) {
		try {
			copy(text);
			text.replaceSelection("");
		} catch (Exception ex) {
			System.err.println("Not String flavor");
		}

	}
	
	public static Image getImg(String fileName) {
		URL url = GraphicsUtils.class.getResource(fileName);
		if (url != null)
			try {
				return ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}

	public static Icon getImageIcon(String fileName) {
		URL url = GraphicsUtils.class.getResource(fileName);
		if (url != null) {
			try {
				return (Icon) ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static BufferedImage getGrayPicture(BufferedImage originalPic) {
		int imageWidth = originalPic.getWidth();
		int imageHeight = originalPic.getHeight();

		BufferedImage newPic = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_3BYTE_BGR);

		ColorConvertOp cco = new ColorConvertOp(ColorSpace
				.getInstance(ColorSpace.CS_GRAY), null);
		cco.filter(originalPic, newPic);
		return newPic;
	}
	
}