import java.security.KeyStoreException;
import java.awt.*;
import java.awt.event.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Collections;
import javax.swing.*;
import netscape.javascript.*;

public class Driver {

	public static void main(String[] args) {
		
	
		try {
			X509Certificate cert = SmartCardTest.GetCert();
			String signature = SmartCardTest.SignText("please signthis", cert); 
		}
		catch(KeyStoreException ex) {
			ex.printStackTrace();
		}

	}

}
