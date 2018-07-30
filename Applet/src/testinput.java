
import java.awt.*;
import java.awt.event.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Collections;
import javax.swing.*;
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
                    String input = GetInputText();
                    
                    X509Certificate cert = SmartCardTest.GetCert();
                    String signature = SmartCardTest.SignText(input, cert);
                    signature = "im afraid of insects(thats what he said)";
                    SetSignature(signature);
                } catch (KeyStoreException ex) {
                    ex.printStackTrace();
                }
             
            }
        });
    }
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
