package de.jlo.talendcomp.google.analytics.ga4.manage;

import java.util.Date;

import com.google.analytics.admin.v1beta.Account;

public class AccountWrapper {
	
	private Account account;
	
	public AccountWrapper(Account a) {
		if (a == null) {
			throw new IllegalArgumentException("Account cannot be null");
		}
		this.account = a;
	}
	
	public long getId() {
		return Util.getId(account);
	}
	
	public String getName() {
		return account.getName();
	}
	
	public String getDisplayName() {
		return account.getDisplayName();
	}
	
	public Date getCreated() {
		return Util.getDate(account.getCreateTime());
	}
	
	public Date getUpdated() {
		return Util.getDate(account.getUpdateTime());
	}
	
	public String getRegionCode() {
		return account.getRegionCode();
	}
	
	public boolean isDeleted() {
		return account.getDeleted();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AccountWrapper) {
			return ((AccountWrapper) o).getId() == getId();
		}
		return false;
	}

}
