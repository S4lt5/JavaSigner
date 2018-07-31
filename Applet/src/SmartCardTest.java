
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JOptionPane;
import sun.security.pkcs11.SunPKCS11;

public class SmartCardTest {
	
	private static Provider provider;
	
	/*
	 * 
	 * 
	 * Get's X509 cert from keystore, stores readable name, 
	 * prints available certs in dialog box and prints selected cert in console log
	 * 
	 * 
	 * 
	 */

    public static X509Certificate GetCert() throws KeyStoreException {
        registerProvider();
        KeyStore keyStore = createKeyStore();
        ArrayList<String> list = Collections.list(keyStore.aliases());
        ArrayList<String> issuers = new ArrayList<String>();
        HashMap<String,X509Certificate> map = new HashMap<>();
        int i = 0;
        String name = "null";
        String commonname = "null";
        for (String alias : list)
        {
            Certificate cert = keyStore.getCertificate(alias);            
            
            X509Certificate x509cert = (X509Certificate) cert;
            Principal principal = x509cert.getSubjectDN();
            
            // Prints readable user name for cert
            
            int start = principal.getName().indexOf("CN");
            String tmpName;
            if (start >= 0) { 
              tmpName = principal.getName().substring(start+3);
              int end = tmpName.indexOf(",");
              if (end > 0) {
                commonname = tmpName.substring(0, end);
              }
              else {
                commonname = tmpName; 
              }
            }
            
           //Finds and prints full cert name, and issuername
            name = principal.getName();
            
            principal = x509cert.getIssuerDN();
            String issuerName = principal.getName();
            
            if(issuerName.contains("CA-"))
            {
                issuers.add(issuerName);
                map.put(issuerName, x509cert);
            }
            i++;
        }
        
        // Input Dialog for selecting cert with which to sign
        String input = (String) JOptionPane.showInputDialog(null, "Please select a certificate for " + commonname + ":", 
        													"DIALOG TITLE", JOptionPane.QUESTION_MESSAGE, null,
											        		issuers.toArray(),		// array of certs
											        		issuers.get(0) 	// selected element of array
											        		);     
        
        // need some behavior for null input (clicked the X or cancel)
        if(input == null) {
        	System.out.println("You done messed up A A ron");
                return null;
        } else {
        	System.out.println("We selected: " + input);
                System.out.println(map.get(input).toString());
                return map.get(input);
        }
    }
    
    /*
     * 
     * 
     * Sign input string text and return signed text
     * 
     * 
     */
    public static String SignText(String text, X509Certificate cert)            
    {
    	String readable = "Not Technically Null";
    	text = "I don't care about the economy";
    	String prov = provider.getName();
    	
    	try {
    		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", prov); //Generate keyPair with RSA from sun provider
    		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
    		keyGen.initialize(2048, random);
    		KeyPair pair = keyGen.generateKeyPair();
    		PrivateKey priv = pair.getPrivate(); //private key
    		PublicKey pub = pair.getPublic();    //public key
    		Signature sig = Signature.getInstance("SHA256withRSA", prov); //creates signature object
    		sig.initSign(priv); //sets private key to sign data
    		byte[] sigbytes = text.getBytes(); //convert text to byte array
    		sig.update(sigbytes); //update signature buffer with text byte array
    		byte[] realSig = sig.sign(); //THE SIGNING
    		readable = realSig.toString(); //convert back to string
    		System.out.println("The following is the sig");
    		System.out.println(readable);
    	}
    	catch(NoSuchProviderException ex) {
    		ex.printStackTrace();
    	}
    	catch(NoSuchAlgorithmException ex) {
    		ex.printStackTrace();
    	}
    	catch(InvalidKeyException ex) {
    		ex.printStackTrace();
    	}
    	catch(SignatureException ex) {
    		ex.printStackTrace();
    	}
    	return readable;
    }
    
    
    
   
    
    /* 
     * 
     * 
     * Registers the PKCS#11 provider
     *
     *
     *
     *
     */

    @SuppressWarnings("restriction")
    public static void registerProvider() {

        String libraryPath = "library = \"/usr/local/lib/pkcs11/cackey.dylib\"\n";
        //check for opensc
        File f = new File("C:\\Program Files\\OpenSC Project\\OpenSC\\pkcs11\\opensc-pkcs11.dll");
        File f2 = new File("C:\\Program Files\\HID Global\\ActivClient\\acpkcs211.dll");
        if (f.exists()) {
            libraryPath = "library = \"C:\\\\Program Files\\\\OpenSC Project\\\\OpenSC\\\\pkcs11\\\\opensc-pkcs11.dll\"\n";
        } else if (f2.exists()) {
            libraryPath = "library = \"C:\\\\Program Files\\\\HID Global\\\\ActivClient\\\\acpkcs211.dll\"\n"
                    + "showInfo = true\n";
        }

        String myConfig = "name = FirefoxKeyStore\n"
                + libraryPath
                + "attributes = compatibility\n";
                                    //"nssArgs = \"configdir='/Users/helloworld/Library/Application Support/Firefox/Profiles/wasdwasd.default-1453211557245' certPrefix='' keyPrefix='' secmod='secmod.db' flags='readOnly' \"\n" +
        //"slot = 2";

                //look for path 1, if it exists, use it.. (mac)
        System.out.println(myConfig);
        InputStream is = new ByteArrayInputStream(myConfig.getBytes());
        provider = new SunPKCS11(is);
        Security.addProvider(provider);
    }
    
    
    
    /*
     * 
     * 
     *Creates a new PKCS#11 based KeyStore.
     *return The created KeyStore.
     *throws KeyStoreException thrown if an error occurred (e.g. invalid pin).
     *
     *
     *
     */

    public static KeyStore createKeyStore() throws KeyStoreException {
        KeyStore.CallbackHandlerProtection callbackHandler = new KeyStore.CallbackHandlerProtection(new ConsoleCallbackHandler());
        KeyStore.Builder builder = KeyStore.Builder.newInstance("PKCS11", null, callbackHandler);
        return builder.getKeyStore();
    }
    
    
    
    
    /*
     * 
     * 
     *Prints the certificate with the given alias to the console.
     *@param keyStore The keyStore the certificate belongs to.
     *@param alias The alias of the certificate.
     *
     *
     *
     */

    public static void printCertificate(KeyStore keyStore, String alias) {
        try {
            Certificate cert = keyStore.getCertificate(alias);
            System.out.println(cert);
            // Would like to access cert.info but cannot (info is private?)
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

}
