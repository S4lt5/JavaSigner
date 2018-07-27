package javaapplet;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.*;
import java.util.Collections;
import sun.security.pkcs11.SunPKCS11;

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
            
            
                String libraryPath = "library = \"/usr/local/lib/pkcs11/cackey.dylib\"\n";
                //check for opensc
                File f = new File("C:\\Program Files\\OpenSC Project\\OpenSC\\pkcs11\\opensc-pkcs11.dll");
                if(f.exists())
                {
                    libraryPath = "library = \"C:\\\\Program Files\\\\OpenSC Project\\\\OpenSC\\\\pkcs11\\\\opensc-pkcs11.dll\"";
                }
                
            
                String myConfig = "name = FirefoxKeyStore\n" +
                                    libraryPath +
                                    "attributes = compatibility\n";
                                    //"nssArgs = \"configdir='/Users/helloworld/Library/Application Support/Firefox/Profiles/wasdwasd.default-1453211557245' certPrefix='' keyPrefix='' secmod='secmod.db' flags='readOnly' \"\n" +
                                    //"slot = 2";
                
                //look for path 1, if it exists, use it.. (mac)
                
                
                
                
                System.out.println(myConfig);
                InputStream is = new ByteArrayInputStream(myConfig.getBytes());
		Provider provider = new SunPKCS11(is);
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
	 * Prints multiple certficates for multiple alias
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
