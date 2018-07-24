package javaapplet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

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
			System.out.println("Please enter your password:");
			String password = new BufferedReader(new InputStreamReader(System.in)).readLine();
			pc.setPassword(password.toCharArray());
		}
	}
}
