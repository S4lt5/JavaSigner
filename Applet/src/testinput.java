

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.security.*;
import java.util.Collections;
import netscape.javascript.*;


public class testinput extends JApplet {
	
	private JButton button = new JButton("Submit");
	public void init() {
		getRootPane().getContentPane().setLayout(new FlowLayout());
		getRootPane().getContentPane().add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//Call SmartCardTest pop-up Dialog
				try {
					new SmartCardTest();
				}
				catch(KeyStoreException ex) {
					ex.printStackTrace();
				}
				buttonActionPerformed(evt);
				}
			});
		}
	public void buttonActionPerformed(ActionEvent evt) throws JSException {
		try {
			JSObject jsObj = JSObject.getWindow(this);
			String input = (String)jsObj.call("getString", null);
			jsObj.call("getSignature", input);
			} catch (JSException ex) {
				ex.printStackTrace();
				}
		}
	}