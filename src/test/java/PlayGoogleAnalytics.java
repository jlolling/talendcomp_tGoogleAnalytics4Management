import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import de.jlo.talendcomp.google.analytics.ga4.manage.v1.GoogleAnalyticsManagement;


public class PlayGoogleAnalytics {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Configurator.setRootLevel(Level.INFO);
			testGAData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testGAData() throws Exception {
		GoogleAnalyticsManagement gi = new GoogleAnalyticsManagement();
		gi.setJsonCredentialFile("/var/testdata/ga/config/ga4_test_talendcomp-jlo-6a6adfd8a6b6.json");
		System.out.println("initialize clients....");
		gi.initializeAdminClient();
		System.out.println("collect data...");
		gi.collectAll();
		gi.close();
	}
		
}
