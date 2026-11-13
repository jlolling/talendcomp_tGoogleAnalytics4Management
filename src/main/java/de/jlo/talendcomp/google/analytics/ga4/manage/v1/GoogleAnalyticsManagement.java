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
package de.jlo.talendcomp.google.analytics.ga4.manage.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.analytics.admin.v1beta.Account;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListAccountsPage;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListAccountsPagedResponse;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListPropertiesPage;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListPropertiesPagedResponse;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceSettings;
import com.google.analytics.admin.v1beta.ListAccountsRequest;
import com.google.analytics.admin.v1beta.ListPropertiesRequest;
import com.google.analytics.admin.v1beta.Property;
import com.google.analytics.data.v1beta.DimensionMetadata;
import com.google.analytics.data.v1beta.GetMetadataRequest;
import com.google.analytics.data.v1beta.Metadata;
import com.google.analytics.data.v1beta.MetricMetadata;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;

import de.jlo.talendcomp.google.analytics.ga4.manage.AccountWrapper;
import de.jlo.talendcomp.google.analytics.ga4.manage.DimensionWrapper;
import de.jlo.talendcomp.google.analytics.ga4.manage.GoogleAnalyticsBase;
import de.jlo.talendcomp.google.analytics.ga4.manage.MetricWrapper;
import de.jlo.talendcomp.google.analytics.ga4.manage.PropertyWrapper;


public class GoogleAnalyticsManagement extends GoogleAnalyticsBase {

	private static final Map<String, GoogleAnalyticsManagement> clientCache = new HashMap<String, GoogleAnalyticsManagement>();
	private int expectedCountDimensions = 0;
	private int expectedCountMetrics = 0;
	private AnalyticsAdminServiceClient analyticsAdmin = null;
	private List<DimensionWrapper> listDimensionsMetadata = new ArrayList<>();
	private List<MetricWrapper> listMetricMetadata = new ArrayList<>();
	private List<AccountWrapper> listAccounts;
	private List<PropertyWrapper> listProperties;
	private int currentIndex = 0;
	private int maxRows = 0;
	private long waitMillisBetweenRequests = 200;
	
	public static void putIntoCache(String key, GoogleAnalyticsManagement gai) {
		clientCache.put(key, gai);
	}
	
	public static GoogleAnalyticsManagement getFromCache(String key) {
		return clientCache.get(key);
	}
	
	public void initializeAdminClient() throws Exception {
		initializeAnalyticsClient();
		AnalyticsAdminServiceSettings settings = AnalyticsAdminServiceSettings.newBuilder()
				.setCredentialsProvider(FixedCredentialsProvider.create(getGoogleCredentials()))
				.build();
		analyticsAdmin = AnalyticsAdminServiceClient.create(settings);
		super.initializeAnalyticsClient();
	}
	
	public void collectAccounts() throws Exception {
		info("Collect GA4 accounts");
		if (analyticsAdmin == null) {
			throw new IllegalStateException("Analytics Admin Client not initialized");
		}
		listAccounts = new ArrayList<>();
		ListAccountsRequest.Builder builder = ListAccountsRequest.newBuilder();
		ListAccountsPagedResponse response = analyticsAdmin.listAccounts(builder.build());
		for (ListAccountsPage page : response.iteratePages()) {
			for (Account item : page.iterateAll()) {
				listAccounts.add(new AccountWrapper(item));
				info("* account: " + item.getName() + " (" + item.getDisplayName() + ")");
			}
		}
		info(listAccounts.size() + " accounts received");
		setMaxRows(listAccounts.size());
		Thread.sleep(waitMillisBetweenRequests);
	}
	
	public void collectProperties() throws Exception {
		info("Collect GA4 properties");
		if (analyticsAdmin == null) {
			throw new IllegalStateException("Analytics Admin Client not initialized");
		}
		if (listAccounts == null) {
			throw new IllegalStateException("No accounts collected before. We need accounts to filter the properties!");
		}
		listProperties = new ArrayList<>();
		for (AccountWrapper account : listAccounts) {
			info("Collect properties for account: " + account.getName() + " (" + account.getDisplayName() + ")");
			ListPropertiesRequest.Builder builder = ListPropertiesRequest.newBuilder();
			builder.setFilter("parent:" + account.getName());
			ListPropertiesPagedResponse response = analyticsAdmin.listProperties(builder.build());
			for (ListPropertiesPage page : response.iteratePages()) {
				for (Property item : page.iterateAll()) {
					listProperties.add(new PropertyWrapper(item));
					info("* property: " + item.getName() + " (" + item.getDisplayName() + "), parent: " + item.getParent());
				}
			}
		}
		info(listProperties.size() + " properties received");
		setMaxRows(listProperties.size());
		Thread.sleep(waitMillisBetweenRequests);
	}

	private int maxRetriesInCaseOfErrors = 5;
	private int currentAttempt = 0;
	
	public void collectDimensionAndMetrics() throws Exception {
		collectDimensionAndMetrics(0l);
		if (listProperties == null) {
			throw new IllegalStateException("No properties collected before. We need properties to filter the custom metrics");
		}
		for (PropertyWrapper p : listProperties) {
			collectDimensionAndMetrics(p.getId());
		}
	}
	
	public void collectDimensionAndMetrics(long propertyId) throws Exception {
		if (propertyId == 0) {
			info("Collect GA4 dimensions and metrics for ALL properties");
		} else {
			info("Collect GA4 dimensions and metrics for propertyId=" + propertyId);
		}
		GetMetadataRequest.Builder requestBuilder = GetMetadataRequest.newBuilder();
		requestBuilder.setName("properties/" + propertyId + "/metadata"); // propertyId=0 means only common dimensions and metrics
		GetMetadataRequest request = requestBuilder.build();
		Metadata responseMetadata = null;
		int waitTime = 1000;
		for (currentAttempt = 0; currentAttempt < maxRetriesInCaseOfErrors; currentAttempt++) {
			try {
				responseMetadata = getAnalyticsClient().getMetadata(request);
				break;
			} catch (ApiException apie) {
				warn("Got error:" + apie.getMessage());
				if (apie.isRetryable() == false) {
					throw new Exception("Request is not retryable and failed: " + apie.getMessage(), apie);
				}
				if (currentAttempt == (maxRetriesInCaseOfErrors - 1)) {
					error("All retries (#" + (currentAttempt+1) + " of " + maxRetriesInCaseOfErrors + ") request failed:" + apie.getMessage(), apie);
					throw apie;
				} else {
					// wait
					try {
						info("Retry #" + (currentAttempt+1) + " request in " + waitTime + "ms");
						Thread.sleep(waitTime);
					} catch (InterruptedException ie) {}
					int random = (int) Math.random() * 500;
					waitTime = (waitTime * 2) + random;
				}
			}
		}
		if (responseMetadata == null) {
			throw new Exception("No metadata response received!");
		}
		expectedCountDimensions = responseMetadata.getDimensionsCount();
		setMaxRows(expectedCountDimensions);
		expectedCountMetrics = responseMetadata.getMetricsCount();
		setMaxRows(expectedCountMetrics);
		info("Dimensions for propertyId: " + propertyId);
		List<DimensionMetadata> listD = responseMetadata.getDimensionsList();
		for (DimensionMetadata d : listD) {
			if (propertyId == 0 || (propertyId > 0 && d.getCustomDefinition())) {
				DimensionWrapper w = new DimensionWrapper(d);
				w.setPropertyId(propertyId);
				listDimensionsMetadata.add(w);
				info("* " + w.toString());
			}
		}
		info("Metrics for propertyId: " + propertyId);
		List<MetricMetadata> listM = responseMetadata.getMetricsList();
		for (MetricMetadata m : listM) {
			if (propertyId == 0 || (propertyId > 0 && m.getCustomDefinition())) {
				MetricWrapper w = new MetricWrapper(m);
				w.setPropertyId(propertyId);
				listMetricMetadata.add(w);
				info("* " + w.toString());
			}
		}
		Thread.sleep(waitMillisBetweenRequests);
	}
	
	private void setMaxRows(int rows) {
		if (maxRows < rows) {
			maxRows = rows;
		}
	}

	public boolean next() {
		return ++currentIndex <= maxRows;
	}
	
	public boolean hasCurrentDimension() {
		if (listDimensionsMetadata != null) {
			return currentIndex <= listDimensionsMetadata.size();
		} else {
			return false;
		}
	}

	public boolean hasCurrentMetric() {
		if (listMetricMetadata != null) {
			return currentIndex <= listMetricMetadata.size();
		} else {
			return false;
		}
	}

	public boolean hasCurrentAccount() {
		if (listAccounts != null) {
			return currentIndex <= listAccounts.size();
		} else {
			return false;
		}
	}

	public boolean hasCurrentProperty() {
		if (listProperties != null) {
			return currentIndex <= listProperties.size();
		} else {
			return false;
		}
	}

	public DimensionWrapper getCurrentDimension() {
		if (currentIndex == 0) {
			throw new IllegalStateException("Call collectDimensionAndMetrics() before!");
		}
		if (currentIndex <= listDimensionsMetadata.size()) {
			return listDimensionsMetadata.get(currentIndex - 1);
		} else {
			return null;
		}
	}
	
	public MetricWrapper getCurrentMetric() {
		if (currentIndex == 0) {
			throw new IllegalStateException("Call collectDimensionAndMetrics() before!");
		}
		if (currentIndex <= listMetricMetadata.size()) {
			return listMetricMetadata.get(currentIndex - 1);
		} else {
			return null;
		}
	}
	
	public AccountWrapper getCurrentAccount() {
		if (currentIndex == 0) {
			throw new IllegalStateException("Call collectAccounts() before!");
		}
		if (currentIndex <= listAccounts.size()) {
			return listAccounts.get(currentIndex - 1);
		} else {
			return null;
		}
	}

	public PropertyWrapper getCurrentProperty() {
		if (currentIndex == 0) {
			throw new IllegalStateException("Call collectProperties() before!");
		}
		if (currentIndex <= listProperties.size()) {
			return listProperties.get(currentIndex - 1);
		} else {
			return null;
		}
	}

	@Override
	public void close() {
		info("Close clients");
		if (analyticsAdmin != null) {
			try {
				analyticsAdmin.shutdown();
				while (true) {
					if (analyticsAdmin.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
						break;
					}
				}
			} catch (Throwable t) {}
		}
		super.close();
	}

	public long getWaitMillisBetweenRequests() {
		return waitMillisBetweenRequests;
	}

	public void setWaitMillisBetweenRequests(Long waitMillisBetweenRequests) {
		if (waitMillisBetweenRequests != null && waitMillisBetweenRequests.longValue() > 0l) {
			this.waitMillisBetweenRequests = waitMillisBetweenRequests.longValue();
		}
	}

}
