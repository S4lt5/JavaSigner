
import java.security.*;
public class Driver {

	public static void main(String[] args) {
		try {
			new SmartCardTest();
		}
		catch(KeyStoreException ex) {
			ex.printStackTrace();
		}
	}

}
