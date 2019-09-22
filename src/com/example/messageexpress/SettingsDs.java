package com.example.messageexpress;

public class SettingsDs {
	private boolean enablelogging;
	private boolean enablesentnotifications;
	private String signature;

	public boolean isEnablelogging() {
		return enablelogging;
	}

	public void setEnablelogging(boolean enablelogging) {
		this.enablelogging = enablelogging;
	}

	public boolean isEnablesentnotifications() {
		return enablesentnotifications;
	}

	public void setEnablesentnotifications(boolean enablesentnotifications) {
		this.enablesentnotifications = enablesentnotifications;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
