package com.echinacoop.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.echinacoop.Startup;
import com.echinacoop.modal.ChatType;
import com.echinacoop.utils.UserData;

public class MsgTiper extends JFrame implements MouseListener {
	
	JLabel title;
	JLabel link;
	
	JPanel panel;
	
	public MsgTiper(){
		this.setSize(220, 75);
		this.setUndecorated(true);
		this.setLayout(null);
		
		title = new JLabel("新消息(0)");
		title.setBounds(10, 0, 140, 30);
		
		panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 30, 220, 40);
		
		link = new JLabel("全部忽略");
		link.setBounds(160, 0, 60, 30);
		link.setForeground(Color.BLUE);
		
		this.add(title);
		this.add(panel);
		this.add(link);
		
		this.addMouseListener(this);
		
		link.addMouseListener(this);
	}
	
	public int loadMessage(){
		MessageItem item = null;
		int index = 0, count = 0;
		panel.removeAll();
		for (Entry<String, UserItem> entry : UserData.USER_ITEM_MAP.entrySet()) {
			if(entry.getValue().hasNoReadMessage() && index < 10){
				item = new MessageItem(entry.getValue().getUser(), ChatType.SINGLE_CHAT, index++);
				item.setMsgCount(entry.getValue().getNoReadMessageCount());
				panel.add(item);
			}
			count += entry.getValue().getNoReadMessageCount();
		}
		for (Entry<String, GroupItem> entry : UserData.GROUP_ITEM_MAP.entrySet()) {
			if(entry.getValue().hasNoReadMessage() && index < 10){
				item = new MessageItem(entry.getValue().getGroup(), ChatType.GROUP_CHAT, index++);
				item.setMsgCount(entry.getValue().getNoReadMessageCount());
				panel.add(item);
			}
			count += entry.getValue().getNoReadMessageCount();
		}
		title.setText(MessageFormat.format("新消息({0})", count));
		
		this.setSize(220, 35 + index * 40);
		panel.setBounds(0, 30, 220, index * 40);
		this.repaint();
		return index;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object obj = e.getSource();
		if(obj == link){
			Startup.trayStopTwinkle();
			Startup.hideMsgTiper();
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
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Point p = new Point(e.getXOnScreen(), e.getYOnScreen());
		Point wp = this.getLocation();
		Dimension d = this.getSize();
		if(p.x < wp.x || p.x > wp.x + d.width || p.y < wp.y || p.y > wp.y + d.height){
			Startup.hideMsgTiper();
		}
	}
	
}
