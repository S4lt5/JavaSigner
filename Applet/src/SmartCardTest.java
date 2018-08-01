
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.*;
import javax.swing.JOptionPane;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.bc.BcKEKRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import sun.misc.BASE64Encoder;
import sun.security.pkcs11.SunPKCS11;
import sun.security.pkcs11.wrapper.*;
import sun.security.pkcs11.wrapper.PKCS11;


public class SmartCardTest {
	
	private static SunPKCS11 provider;
	private static KeyStore keyStore;
        private static PrivateKey key;
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
        keyStore = createKeyStore();
        
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
                System.out.println("Issuer: " + issuerName + "Alias:" + alias);
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
            try {
                key = (PrivateKey)keyStore.getKey(keyStore.getCertificateAlias(map.get(input)), null);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(SmartCardTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnrecoverableKeyException ex) {
                Logger.getLogger(SmartCardTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(key.getAlgorithm());
                System.out.println(map.get(input).toString());
                return map.get(input);
        }
    }
    
    
    public static String DoSomeStuff(String text, X509Certificate cert)
    {
            try {
                byte[] data = text.getBytes("UTF8"); //transform text to bytes
                //javax.smartcardio.Card card = new 
                //long session = provider.getClass().getin
                
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SmartCardTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "";
    }
    
    
    
    /*
     * 
     * 
     * Sign input string text and return signed text
     * https://github.com/dnascimento/AISSProject/blob/master/PT_citzen_card/src/aiss/CCConnection.java
     * 
     */
    public static String SignText(String text, X509Certificate cert) 
    {
    	String readable = "Not Technically Null";
    	//text = "I don't care about the economy"; 
    	try {
            
    		BASE64Encoder encoder = new BASE64Encoder();
    		byte[] data = text.getBytes("UTF8"); //transform text to bytes
                
                        
    		Signature sig = Signature.getInstance("SHA512withRSA",provider); //use sha256withRSA for signature                
                
                sig.initSign(key);
   		//sig.initSign(keyPair.getPrivate()); //initliaze signature
    		sig.update(data); // update signature buffer for signing
                
    		byte[] sigBytes = sig.sign(); // sign
    		System.out.println("Signature: " + encoder.encode(sigBytes)); //print signature
                System.out.println(sig.getAlgorithm());                
    		
                PublicKey pkey = cert.getPublicKey();
                Signature sig2 = Signature.getInstance("SHA512withRSA");                                
                //System.out.println(new BASE64Encoder().encode(key.getEncoded()));
                System.out.println(encoder.encode(pkey.getEncoded()));
                sig2.initVerify(pkey);
    		sig2.update(data); //update buffer to verify
    		
    		System.out.println(sig2.verify(sigBytes)); //verify and print either true or false
                
                
                
                //do bouncycastle signing
                Security.addProvider(new BouncyCastleProvider());
                //      https://stackoverflow.com/questions/13212186/encryption-and-decryption-with-bouncycastle-pkcs7-cms-in-java
                
                // Pretty sure this is exactly what I need to do https://github.com/itext/i5js-tutorial/blob/master/signatures/src/main/java/signatures/chapter4/C4_01_SignWithPKCS11HSM.java --MG
                
                CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
                CMSTypedData msg = new CMSProcessableByteArray(data);                
                
                
                List certList = new ArrayList();
                certList.add(cert);
                Store certs = new JcaCertStore(certList);
                
                ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA512withRSA").setProvider(provider).build(key);
                gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(sha1Signer, cert));
                gen.addCertificates(certs);
                //gen.addAttributeCertificate(null);
                CMSSignedData sigData = gen.generate(msg,false);
                String foo = new String((byte[])sigData.getSignedContent().getContent(),"UTF-8");
                System.out.println("-----");
                System.out.println(foo);                
                System.out.println(encoder.encode(sigData.getEncoded()));
                System.out.println(sigData.isDetachedSignature());                
    		
    	}
        catch(IOException | java.security.cert.CertificateEncodingException |  NoSuchAlgorithmException | InvalidKeyException | SignatureException | org.bouncycastle.cms.CMSException | OperatorCreationException ex)
        {
            ex.printStackTrace();
        }    	
    	return readable;
    }
    
    
    public static boolean isWindows() {
            return(System.getProperty("os.name").toLowerCase().contains("win"));        
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

        if(isWindows())
        {
         //   provider = 
        }
        else            
        {
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
        }
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
