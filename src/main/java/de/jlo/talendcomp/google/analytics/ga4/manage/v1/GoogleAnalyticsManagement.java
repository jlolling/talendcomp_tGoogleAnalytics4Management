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

import com.google.analytics.admin.v1beta.Account;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListAccountsPage;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListAccountsPagedResponse;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListCustomDimensionsPage;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListCustomDimensionsPagedResponse;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListCustomMetricsPage;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListCustomMetricsPagedResponse;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListPropertiesPage;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceClient.ListPropertiesPagedResponse;
import com.google.analytics.admin.v1beta.AnalyticsAdminServiceSettings;
import com.google.analytics.admin.v1beta.CustomDimension;
import com.google.analytics.admin.v1beta.CustomMetric;
import com.google.analytics.admin.v1beta.ListAccountsRequest;
import com.google.analytics.admin.v1beta.ListCustomDimensionsRequest;
import com.google.analytics.admin.v1beta.ListCustomMetricsRequest;
import com.google.analytics.admin.v1beta.ListPropertiesRequest;
import com.google.analytics.admin.v1beta.Property;
import com.google.analytics.data.v1beta.DimensionMetadata;
import com.google.analytics.data.v1beta.GetMetadataRequest;
import com.google.analytics.data.v1beta.Metadata;
import com.google.analytics.data.v1beta.MetricMetadata;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;

import de.jlo.talendcomp.google.analytics.ga4.manage.AccountWrapper;
import de.jlo.talendcomp.google.analytics.ga4.manage.CustomDimensionWrapper;
import de.jlo.talendcomp.google.analytics.ga4.manage.CustomMetricWrapper;
import de.jlo.talendcomp.google.analytics.ga4.manage.DimensionWrapper;
import de.jlo.talendcomp.google.analytics.ga4.manage.GoogleAnalyticsBase;
import de.jlo.talendcomp.google.analytics.ga4.manage.MetricWrapper;
import de.jlo.talendcomp.google.analytics.ga4.manage.PropertyWrapper;


public class GoogleAnalyticsManagement extends GoogleAnalyticsBase {

	private static final Map<String, GoogleAnalyticsManagement> clientCache = new HashMap<String, GoogleAnalyticsManagement>();
	private int expectedCountDimensions = 0;
	private int expectedCountMetrics = 0;
	private AnalyticsAdminServiceClient analyticsAdmin = null;
	private List<DimensionMetadata> listDimensionsMetadata;
	private List<MetricMetadata> listMetricMetadata;
	private List<AccountWrapper> listAccounts;
	private List<PropertyWrapper> listProperties;
	private List<CustomDimensionWrapper> listCustomDimensions;
	private List<CustomMetricWrapper> listCustomMetrics;
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

	public void collectCustomDimensions() throws Exception {
		info("Collect GA4 custom dimensions");
		if (analyticsAdmin == null) {
			throw new IllegalStateException("Analytics Admin Client not initialized");
		}
		if (listProperties == null) {
			throw new IllegalStateException("No properties collected before. We need properties to filter the custom dimensions");
		}
		listCustomDimensions = new ArrayList<>();
		for (PropertyWrapper property : listProperties) {
			info("Collect custom dimensions for property: " + property.getName() + " (" + property.getDisplayName() + ")");
			ListCustomDimensionsRequest.Builder builder = ListCustomDimensionsRequest.newBuilder();
			builder.setParent(property.getName());
			ListCustomDimensionsPagedResponse response = analyticsAdmin.listCustomDimensions(builder.build());
			for (ListCustomDimensionsPage page : response.iteratePages()) {
				for (CustomDimension item : page.iterateAll()) {
					CustomDimensionWrapper w = new CustomDimensionWrapper(item);
					w.setPropertyId(property.getId());
					listCustomDimensions.add(w);
					info("* custom dimension: " + item.getName() + " (" + item.getDisplayName() + ")");
				}
			}
			Thread.sleep(waitMillisBetweenRequests);
		}
		info(listCustomDimensions.size() + " custom dimensions received");
		setMaxRows(listCustomDimensions.size());
	}

	public void collectCustomMetrics() throws Exception {
		info("Collect GA4 custom metrics");
		if (analyticsAdmin == null) {
			throw new IllegalStateException("Analytics Admin Client not initialized");
		}
		if (listProperties == null) {
			throw new IllegalStateException("No properties collected before. We need properties to filter the custom metrics");
		}
		listCustomMetrics = new ArrayList<>();
		for (PropertyWrapper property : listProperties) {
			info("Collect custom metrics for property: " + property.getName() + " (" + property.getDisplayName() + ")");
			ListCustomMetricsRequest.Builder builder = ListCustomMetricsRequest.newBuilder();
			builder.setParent(property.getName());
			ListCustomMetricsPagedResponse response = analyticsAdmin.listCustomMetrics(builder.build());
			for (ListCustomMetricsPage page : response.iteratePages()) {
				for (CustomMetric item : page.iterateAll()) {
					CustomMetricWrapper w = new CustomMetricWrapper(item);
					w.setPropertyId(property.getId());
					listCustomMetrics.add(w);
					info("* custom metric: " + item.getName() + " (" + item.getDisplayName() + ")");
				}
			}
			Thread.sleep(waitMillisBetweenRequests);
		}
		info(listCustomMetrics.size() + " custom metrics received");
		setMaxRows(listCustomMetrics.size());
	}

	private int maxRetriesInCaseOfErrors = 5;
	private int currentAttempt = 0;
	
	public void collectDimensionAndMetrics() throws Exception {
		info("Collect GA4 dimensions and metrics");
		GetMetadataRequest.Builder requestBuilder = GetMetadataRequest.newBuilder();
		requestBuilder.setName("properties/0/metadata"); // propertyId=0 means only common dimensions and metrics
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
		info(expectedCountDimensions + " dimensions and " + expectedCountMetrics + " metrics received");
		listDimensionsMetadata = responseMetadata.getDimensionsList();
		listMetricMetadata = responseMetadata.getMetricsList();
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
	
	public boolean hasCurrentCommonDimension() {
		if (listDimensionsMetadata != null) {
			return currentIndex <= listDimensionsMetadata.size();
		} else {
			return false;
		}
	}

	public boolean hasCurrentCommonMetric() {
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

	public boolean hasCurrentCustomDimension() {
		if (listCustomDimensions != null) {
			return currentIndex <= listCustomDimensions.size();
		} else {
			return false;
		}
	}

	public boolean hasCurrentCustomMetric() {
		if (listCustomMetrics != null) {
			return currentIndex <= listCustomMetrics.size();
		} else {
			return false;
		}
	}

	public DimensionWrapper getCurrentCommonDimension() {
		if (currentIndex == 0) {
			throw new IllegalStateException("Call collectDimensionAndMetrics() before!");
		}
		if (currentIndex <= listDimensionsMetadata.size()) {
			return new DimensionWrapper(listDimensionsMetadata.get(currentIndex - 1));
		} else {
			return null;
		}
	}
	
	public MetricWrapper getCurrentCommonMetric() {
		if (currentIndex == 0) {
			throw new IllegalStateException("Call collectDimensionAndMetrics() before!");
		}
		if (currentIndex <= listMetricMetadata.size()) {
			return new MetricWrapper(listMetricMetadata.get(currentIndex - 1));
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

	public CustomDimensionWrapper getCurrentCustomDimension() {
		if (currentIndex == 0) {
			throw new IllegalStateException("Call collectCustomDimensions() before!");
		}
		if (currentIndex <= listCustomDimensions.size()) {
			return listCustomDimensions.get(currentIndex - 1);
		} else {
			return null;
		}
	}

	public CustomMetricWrapper getCurrentCustomMetric() {
		if (currentIndex == 0) {
			throw new IllegalStateException("Call collectCustomMetrics() before!");
		}
		if (currentIndex <= listCustomMetrics.size()) {
			return listCustomMetrics.get(currentIndex - 1);
		} else {
			return null;
		}
	}

	@Override
	public void close() {
		if (analyticsAdmin != null) {
			try {
				analyticsAdmin.shutdown();
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
