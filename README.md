# Talend User-Component tGoogleAnalytics4Management
This component is dedicated to work with the Google Analytics 4 (GA4) - successor of the former Google Analytics now called Universal Analytics.
The component has currently limited capabilities compared to the component tGoogleAnalyticsInput (dedicated to UA) because of the Beta-state of the Google API. 

The component provides the ability to fetch metadata like accounts, properties, dimensions and metrics in the same way as we know it from the tGoogleAnalyticsManagement. This helps to migrate smoothly from the now out-dated Google Universal Analytics (live ended at 2023-06-01) to the new Google Analytics 4 (which API is unfortunately still beta).

Differences to the former component (tGoogleAnalyticsInput):
* Use for authorization only the json key file. This allows only by changing this file to switch from personal accounts to service accounts without changing the component configuration
* We do not have the same function set as in the former component because Google has not fully developed the API for GA4 - it is still beta and by far not finished.
