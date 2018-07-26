

import java.io.BufferedReader;
import java.io.Console;

import java.io.IOException;
import java.io.InputStreamReader;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class ConsoleCallbackHandler implements CallbackHandler {
/**
 * Prompts for user's password using console.
 */
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for(Callback callback : callbacks) {
			if(!(callback instanceof PasswordCallback)) {
		        throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
			}
			PasswordCallback pc = (PasswordCallback)callback;
                        JPasswordField pass = new JPasswordField(10);
                        
                        JPanel panel = new JPanel();
                        JLabel label = new JLabel("Please enter your PIN:");
                        panel.add(label);
                        panel.add(pass);
                        String[] options = new String[]{"OK","Cancel"};
                        
                        pass.requestFocusInWindow();
                        int option = JOptionPane.showOptionDialog(null,panel,"PIN Entry",JOptionPane.NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[1]);
                        
                        if(option == 0)
                        {
                        char[] pswd = pass.getPassword();
                        
			pc.setPassword(pswd);
                        }
                        
		}
	}
}
