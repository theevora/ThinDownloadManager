package com.mani.thindownloadmanager.app;

import android.widget.ProgressBar;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;

/**
 * Listener.
 * Created by theewara_v on 28/3/2559.
 */
class MyDownloadStatusListener implements DownloadStatusListenerV1 {

    private int downloadId;
    private ProgressBar progressBar;

    void listen(ProgressBar progress, int downloadId) {
        this.progressBar = progress;
        this.downloadId = downloadId;
    }

    @Override
    public void onDownloadComplete(DownloadRequest request) {
        final int id = request.getDownloadId();
        if (id == downloadId) {
            progressBar.setProgress(0);
        }
    }

    @Override
    public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
        final int id = request.getDownloadId();
        if (id == downloadId) {
            progressBar.setProgress(0);
        }
    }

    @Override
    public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
        int id = request.getDownloadId();

        System.out.println("######## onProgress ###### " + id + " : " + totalBytes + " : " + downloadedBytes + " : " + progress);
        if (id == downloadId) {
            this.progressBar.setProgress(progress);
        }
    }

}

