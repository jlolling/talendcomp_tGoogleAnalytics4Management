# Talend User-Component tGoogleAnalytics4Input
This component is dedicated to work with the Google Analytics 4 (GA4) - successor of the former Google Analytics now called Universal Analytics.
The component has currently limited capabilities compared to the component tGoogleAnalyticsInput (dedicated to UA) because of the Beta-state of the Google API. 

The component provides the ability to use the define metrics, dimensions, metric-filters and dimension-filters in the same way as we know it from the tGoogleAnalyticsInput. This helps to migrate smoothly from the now out-dated Google Universal Analytics (live ended at 2023-06-01) to the new Google Analytics 4 (which API is unfortunately still beta).

Differences to the former component (tGoogleAnalyticsInput):
* Use for authorization only the json key file. This allows only by changing this file to switch from personal accounts to service accounts without changing the component configuration
* Instead of a profile or view-ID you need now the new GA4 property-ID
* The API currently does not provide the information about sampling
* Segments works completely different and cannot currently addressed in the reports (perhaps because of the beta state) 
