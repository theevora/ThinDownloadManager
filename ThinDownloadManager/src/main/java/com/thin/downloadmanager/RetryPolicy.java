package com.thin.downloadmanager;

/**
 * Created by maniselvaraj on 15/4/15.
 */
public interface RetryPolicy {

	/**
	 * Return back off multiplier
	 */
	float getBackOffMultiplier();

	/**
	 * Returns the current retry count (used for logging).
	 */
	int getCurrentRetryCount();

	/**
	 * Returns the current timeout (used for logging).
	 */
	int getCurrentTimeout();

	void retry() throws RetryError;


}
