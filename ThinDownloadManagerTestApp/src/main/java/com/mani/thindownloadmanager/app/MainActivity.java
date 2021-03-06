package com.mani.thindownloadmanager.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.thin.downloadmanager.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

	private static final String FILE2 = "https://upload.wikimedia.org/wikipedia/en/3/3f/Case_Closed_Volume_36.png";
	private static final String FILE3 = "https://dl.dropboxusercontent.com/u/25887355/test_song.mp3";

	private static final String LOG_TAG = "ThinDownloadManager";
	private int downloadId2;
	private int downloadId3;
	private ThinDownloadManager downloadManager;

	private ProgressBar mProgress2;
	private ProgressBar mProgress3;
	private MyDownloadStatusListener
			myDownloadStatusListener = new MyDownloadStatusListener();
	private DocumentFile outputFile;

	private void clearLog() {
		try {
			Process process = new ProcessBuilder()
					.command("logcat", "-c")
					.redirectErrorStream(true)
					.start();
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
	}

	public void clearLogButtonOnClick(View v) {
		clearLog();
	}

	private String getBytesDownloaded(int progress, long totalBytes) {
		//Greater than 1 MB
		long bytesCompleted = (progress * totalBytes) / 100;
		if (totalBytes >= 1000000) {
			return ("" + (String.format("%.1f", (float) bytesCompleted / 1000000)) + "/" + (String.format("%.1f", (float) totalBytes / 1000000)) + "MB");
		}
		if (totalBytes >= 1000) {
			return ("" + (String.format("%.1f", (float) bytesCompleted / 1000)) + "/" + (String.format("%.1f", (float) totalBytes / 1000)) + "Kb");

		} else {
			return ("" + bytesCompleted + "/" + totalBytes);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
		if (resultCode == RESULT_OK) {
			Uri treeUri = resultData.getData();
			outputFile = DocumentFile.fromTreeUri(this, treeUri);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button mDownload2 = (Button) findViewById(R.id.button2);
		Button mDownload3 = (Button) findViewById(R.id.button3);

		Button mCancelAll = (Button) findViewById(R.id.button6);
		Button mListFiles = (Button) findViewById(R.id.button7);

		mProgress2 = (ProgressBar) findViewById(R.id.progress2);
		mProgress3 = (ProgressBar) findViewById(R.id.progress3);

		mProgress2.setMax(100);
		mProgress2.setProgress(0);

		mProgress3.setMax(100);
		mProgress3.setProgress(0);

		downloadManager = new ThinDownloadManager(this);
		RetryPolicy retryPolicy = new DefaultRetryPolicy();

		File filesDir = getExternalFilesDir("");

		Uri downloadUri;
		Uri destinationUri;

		downloadUri = Uri.parse(FILE2);
		destinationUri = Uri.parse(filesDir + "/test_photo2.jpg");
		final DownloadRequest downloadRequest2 = new DownloadRequest(downloadUri)
				.setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.LOW)
				.setDownloadContext("Download2")
				.setStatusListener(myDownloadStatusListener);

		downloadUri = Uri.parse(FILE3);
		destinationUri = Uri.parse(filesDir + "/test_song.mp3");
		final DownloadRequest downloadRequest3 = new DownloadRequest(downloadUri)
				.setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
				.setDownloadContext("Download3")
				.setStatusListener(myDownloadStatusListener);

		mDownload2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (downloadManager.query(downloadId2) == DownloadManager.STATUS_NOT_FOUND) {
				DocumentFile outputdf = outputFile.createFile("application/octet-stream", "tt2.jpg");	downloadRequest2.setDestinationURI(outputdf.getUri());
					downloadId2 = downloadManager.add(downloadRequest2);
					myDownloadStatusListener.listen(mProgress2, downloadId2);
				}
			}
		});

		mDownload3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (downloadManager.query(downloadId3) == DownloadManager.STATUS_NOT_FOUND) {
					downloadId3 = downloadManager.add(downloadRequest3);
					myDownloadStatusListener.listen(mProgress3, downloadId3);
				}
			}
		});

		mCancelAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				downloadManager.cancelAll();
			}
		});

		mListFiles.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showInternalFilesDir();
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("######## onDestroy ######## ");
		downloadManager.release();
	}

	public void outputButtonOnClick(View v) {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
		startActivityForResult(intent, 42);
	}

	private void showInternalFilesDir() {
		File internalFile = new File(getExternalFilesDir("").getPath());
		File files[] = internalFile.listFiles();
		String contentText = "";
		if (files.length == 0) {
			contentText = "No Files Found";
		}

		for (File file : files) {
			contentText += file.getName() + " " + file.length() + " \n\n ";
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog internalCacheDialog = builder.create();
		LayoutInflater inflater = internalCacheDialog.getLayoutInflater();
		View dialogLayout = inflater.inflate(R.layout.layout_files, null);
		TextView content = (TextView) dialogLayout.findViewById(R.id.filesList);
		content.setText(contentText);

		builder.setView(dialogLayout);
		builder.show();

	}

	public void showLogButtonOnClick(View v) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("logcat -d");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			StringBuilder log = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				log.append(line);
			}
			TextView tv = (TextView) findViewById(R.id.logText);
			tv.setText(log.toString());
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	}


}