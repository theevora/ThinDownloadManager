package com.thin.downloadmanager;

import android.content.Context;
import android.os.Handler;

import java.security.InvalidParameterException;

public class ThinDownloadManager implements DownloadManager {

	private Context context;
	/**
	 * Download request queue takes care of handling the request based on priority.
	 */
	private DownloadRequestQueue mRequestQueue;

	/**
	 * Default constructor
	 */
	public ThinDownloadManager(Context context) {
		this(null, context);
	}

	/**
	 * Construct with provided callback handler
	 *
	 * @param callbackHandler
	 */
	public ThinDownloadManager(Handler callbackHandler, Context context) throws InvalidParameterException {
		mRequestQueue = callbackHandler == null ? new DownloadRequestQueue(context) : new DownloadRequestQueue(callbackHandler, context);
		mRequestQueue.start();
	}

	/**
	 * Add a new download.  The download will start automatically once the download manager is
	 * ready to execute it and connectivity is available.
	 *
	 * @param request the parameters specifying this download
	 * @return an ID for the download, unique across the application.  This ID is used to make future
	 * calls related to this download.
	 * @throws IllegalArgumentException
	 */
	@Override
	public int add(DownloadRequest request) throws IllegalArgumentException {
		if (request == null) {
			throw new IllegalArgumentException("DownloadRequest cannot be null");
		}

		return mRequestQueue.add(request);
	}

	@Override
	public int cancel(int downloadId) {
		return mRequestQueue.cancel(downloadId);
	}

	@Override
	public void cancelAll() {
		mRequestQueue.cancelAll();
	}

	@Override
	public int query(int downloadId) {
		return mRequestQueue.query(downloadId);
	}

	@Override
	public void release() {
		if (mRequestQueue != null) {
			mRequestQueue.release();
			mRequestQueue = null;
		}
	}
}


