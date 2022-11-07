/**
 * Copyright 2022 Jan Lolling jan.lolling@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jlo.talendcomp.google.analytics.ga4.manage;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

public class GoogleAnalyticsBase {

	private Logger logger = LogManager.getLogger(GoogleAnalyticsBase.class);
	private static final Map<String, GoogleAnalyticsBase> clientCache = new HashMap<String, GoogleAnalyticsBase>();
	private Date currentDate;
	private File jsonAuthFile = null;
	private int errorCode = 0;
	private boolean success = true;
	private BetaAnalyticsDataClient analyticsDataClient = null;
	private GoogleCredentials credentials = null;
	
	protected BetaAnalyticsDataClient getAnalyticsClient() {
		return analyticsDataClient;
	}
	
	public void setAnalyticsClient(BetaAnalyticsDataClient analyticsDataClient) {
		this.analyticsDataClient = analyticsDataClient;
	}

	public static void putIntoCache(String key, GoogleAnalyticsBase gai) {
		clientCache.put(key, gai);
	}
	
	public static GoogleAnalyticsBase getFromCache(String key) {
		return clientCache.get(key);
	}
	
	public void setJsonCredentialFile(String filePath) throws Exception {
		Util.assertNotEmpty(filePath, "File path to json file cannot be null or empty!");
		jsonAuthFile = new File(filePath);
		if (jsonAuthFile.canRead() == false) {
			throw new Exception("Json credential file: " + jsonAuthFile.getAbsolutePath() + " cannot be read!");
		}
	}
	
	protected GoogleCredentials getGoogleCredentials() throws Exception {
		if (credentials == null) {
			if (jsonAuthFile == null) {
				throw new Exception("json credential file is not set");
			}
			credentials = GoogleCredentials.fromStream(new FileInputStream(jsonAuthFile));
		}
		return credentials;
	}
	
	public void initializeAnalyticsClient() throws Exception {
		BetaAnalyticsDataSettings betaAnalyticsDataSettings = BetaAnalyticsDataSettings.newBuilder()
				.setCredentialsProvider(FixedCredentialsProvider.create(getGoogleCredentials()))
				.build();
		analyticsDataClient = BetaAnalyticsDataClient.create(betaAnalyticsDataSettings);
	}
	
	public Date getCurrentDate() throws ParseException {
		return currentDate;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public boolean isSuccess() {
		return success;
	}
	
	public void info(String message) {
		if (logger != null) {
			logger.info(message);
		} else {
			System.out.println("INFO:" + message);
		}
	}
	
	public void debug(String message) {
		if (logger != null) {
			logger.debug(message);
		} else {
			System.out.println("DEBUG:" + message);
		}
	}

	public void warn(String message) {
		if (logger != null) {
			logger.warn(message);
		} else {
			System.err.println("WARN:" + message);
		}
	}

	public void error(String message, Exception e) {
		if (logger != null) {
			if (e != null) {
				logger.error(message, e);
			} else {
				logger.error(message);
			}
		} else {
			System.err.println("ERROR:" + message);
		}
	}

<<<<<<< HEAD:src/main/java/de/jlo/talendcomp/google/analytics/ga4/manage/GoogleAnalyticsBase.java
	public void close() {
		if (analyticsDataClient != null) {
			try {
				analyticsDataClient.shutdownNow();
			} catch (Throwable t) {}
		}
=======
	public int getTimeoutInSeconds() {
		return timeoutInSeconds;
>>>>>>> master:src/main/java/de/jlo/talendcomp/google/analytics/ga4/GoogleAnalyticsBase.java
	}

}
