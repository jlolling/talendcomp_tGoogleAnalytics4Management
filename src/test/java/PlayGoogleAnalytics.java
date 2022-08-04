import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import de.jlo.talendcomp.google.analytics.ga4.DimensionValue;
import de.jlo.talendcomp.google.analytics.ga4.MetricValue;
import de.jlo.talendcomp.google.analytics.ga4.v1.GoogleAnalyticsInput;



public class PlayGoogleAnalytics {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Configurator.setRootLevel(Level.DEBUG);
			testGAData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testGAData() throws Exception {
		GoogleAnalyticsInput gi = new GoogleAnalyticsInput();
		gi.setJsonCredentialFile("/Users/jan/development/testdata/ga/config/ga4_test_talendcomp-jlo-6a6adfd8a6b6.json");
		gi.setFetchSize(0);
		gi.setTimeoutInSeconds(240);
		try {
			System.out.println("initialize client....");
			gi.initializeAnalyticsClient();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		// System.out.println("############################# " + i + " ########################");
		gi.setPropertyId("310976591");
		gi.setStartDate("2022-06-01");
		gi.setEndDate("2022-07-01");
		gi.setDimensions("country");
		gi.setMetrics("my_value = activeUsers/2");
		gi.setDimensionFilters("country!=United States");
		gi.setMetricFilters("my_value>1");
		try {
			System.out.println("Start fetching first data...");
			gi.executeQuery();
			fetchPlainData(gi);
//			fetchNormalizedData(gi);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	private static void fetchNormalizedData(GoogleAnalyticsInput gi) throws Exception {
		System.out.println("total results:" + gi.getExpectedTotalPlainRowCount());
//		System.out.println("contains sampled data:" + gi.containsSampledData());
		System.out.println("results:");
		boolean firstLoop = true;
		int index = 0;
		int indexDimensionValue = 0;
		int indexMetricValue = 0; 
		while (true) {
			try {
				// in hasNext we execute query if needed
				if (gi.nextNormalizedRecord() == false) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			index++;
			DimensionValue dv = gi.getCurrentDimensionValue();
			if (dv != null) {
				indexDimensionValue++;
				System.out.println("DM: rowNum=" + dv.rowNum + " dimension=" + dv.name + " value=" + dv.value);
			}
			MetricValue mv = gi.getCurrentMetricValue();
			if (mv != null) {
				indexMetricValue++;
				System.out.println("MV: rowNum=" + mv.rowNum + " metric=" + mv.name + " value=" + mv.value);
			}
		}
	}

	private static void fetchPlainData(GoogleAnalyticsInput gi) throws Exception {
		System.out.println("Start fetching first data...");
		gi.executeQuery();
		System.out.println("total results:" + gi.getExpectedTotalPlainRowCount());
		System.out.println("results:");
		boolean firstLoop = true;
		int index = 0;
		while (true) {
			try {
				// in hasNext we execute query if needed
				if (gi.hasNextPlainRecord() == false) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			index = gi.getCurrentIndexOverAll();
			List<String> list = gi.getNextPlainRecord();
			System.out.print("#" + index);
			System.out.print("\t");
			firstLoop = true;
			for (String s : list) {
				if (firstLoop) {
					firstLoop = false;
				} else {
					System.out.print("\t|\t");
				}
				System.out.print(s);
			}
			System.out.println();
		}
		System.out.println("getOverallCount:" + gi.getOverAllCountRows()
				+ " index:" + index);
		System.out
				.println("#####################################################");
	}
		
}
