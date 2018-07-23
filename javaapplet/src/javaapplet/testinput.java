package javaapplet;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import netscape.javascript.*;


public class testinput extends JApplet {
	
	private JButton button = new JButton("Submit");
	public void init() {
		getRootPane().getContentPane().setLayout(new FlowLayout());
		getRootPane().getContentPane().add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				buttonActionPerformed(evt);
				}
			});
		}
	public void buttonActionPerformed(ActionEvent evt) throws JSException {
		try {
			JSObject jsObj = JSObject.getWindow(this);
			jsObj.call("submitForm", null);
			} catch (JSException ex) {
				ex.printStackTrace();
				}
		}
	}