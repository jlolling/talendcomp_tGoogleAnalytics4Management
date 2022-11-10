package de.jlo.talendcomp.google.analytics.ga4.manage;

import java.util.Date;

import com.google.analytics.admin.v1beta.Property;

public class PropertyWrapper {
	
	private Property property = null;
	
	public PropertyWrapper(Property p) {
		if (p == null) {
			throw new IllegalArgumentException("Property cannot be null");
		}
		this.property = p;
	}
	
	public Property getProperty() {
		return property;
	}
	
	public long getId() {
		return Util.getId(property);
	}
	
	public long getAccountId() {
		return Util.getId(property.getAccount());
	}
	
	public String getName() {
		return property.getName();
	}
	
	public String getDisplayName() {
		return property.getDisplayName();
	}
	
	public Date getCreated() {
		return Util.getDate(property.getCreateTime());
	}
	
	public Date getUpdated() {
		return Util.getDate(property.getUpdateTime());
	}

	public Date getExpire() {
		return Util.getDate(property.getExpireTime());
	}
	
	public String getCurrencyCode() {
		return property.getCurrencyCode();
	}
	
	public String getServiceLevel() {
		switch (property.getServiceLevelValue()) {
		case 0: return "SERVICE_LEVEL_UNSPECIFIED";
		case 1: return "GOOGLE_ANALYTICS_STANDARD";
		case 2: return "GOOGLE_ANALYTICS_360";
		default: return "UNRECOGNIZED";
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PropertyWrapper) {
			return ((PropertyWrapper) o).getId() == getId();
		}
		return false;
	}

}
