
import java.awt.*;
import java.awt.event.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Collections;
import javax.swing.*;
import netscape.javascript.*;

public class testinput extends JApplet {

    private JButton button = new JButton("Submit");
    public void init() { //creates button layout
        getRootPane().getContentPane().setLayout(new FlowLayout());
        getRootPane().getContentPane().add(button);
        button.addActionListener(new ActionListener() { //Create action listener and establish what action is performed upon 
            public void actionPerformed(ActionEvent evt) {
                try {
                    String input = GetInputText(); 	//Receives input text from html text field
                    X509Certificate cert = SmartCardTest.GetCert(); //retrieve cert from card
                    String signature = SmartCardTest.SignText(input, cert); //sign input text with cert
                    SetSignature(signature); //printout signed text into html text field
                } catch (KeyStoreException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    
    // Calls Get String which takes html input and returns it.
    public String GetInputText() throws JSException {
        try {
            JSObject jsObj = JSObject.getWindow(this);
            //get the thing to sign
            String input = (String) jsObj.call("getString", (Object) null);
            return input;            
        } catch (JSException ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    
// Calls setSignature which prints the signed string
    public void SetSignature(String signature) throws JSException {
        try {
            JSObject jsObj = JSObject.getWindow(this);                                                
            //set it back out                                                 
            jsObj.call("setSignature", signature);
        } catch (JSException ex) {
            ex.printStackTrace();
        }
    }
    
}
