package javaapplet;

import java.security.*;
import java.util.Collections;
import sun.security.pkcs11.*;

public class SmartCardTest {
	public static void main(String... args) throws KeyStoreException {
		registerProvider();
		KeyStore keyStore = createKeyStore();
		Collections.list(keyStore.aliases()).forEach(alias -> printCertificate(keyStore, alias));
		}
	/*
	 Registers the PKCS#11 provider
	 */
	@SuppressWarnings("restriction")
	public static void registerProvider() {
		Provider provider = new SunPKCS11(/*SmartCardTest.class.getResourceAsStream("/pkcs11.cfg")*/ "ehllo");
		Security.addProvider(provider);
	}
	
	/*
	 Creates a new PKCS#11 based KeyStore.
	 return The created KeyStore.
	 throws KeyStoreException thrown if an error occurred (e.g. invalid pin).
	 */
	public static KeyStore createKeyStore() throws KeyStoreException {
		KeyStore.CallbackHandlerProtection callbackHandler = new KeyStore.CallbackHandlerProtection(new ConsoleCallbackHandler());
		KeyStore.Builder builder = KeyStore.Builder.newInstance("PKCS11", null, callbackHandler);
		return builder.getKeyStore();
	}
	/*
	 * prints multiple certficates for multiple alias
	 */

	public static void printMultipleCertificates(KeyStore keyStore) throws KeyStoreException {
		for(String alias : Collections.list(keyStore.aliases())) {
			System.out.println(keyStore.getCertificate(alias));
		}
	}
	/*
	 Prints the certificate with the given alias to the console.
	 @param keyStore The keyStore the certificate belongs to.
	 @param alias The alias of the certificate.
	 */
	public static void printCertificate(KeyStore keyStore, String alias) {
		try {
			System.out.println(keyStore.getCertificate(alias));
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
	
}
