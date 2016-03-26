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

	private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
	private static final String FILE1 = "https://dl.dropboxusercontent.com/u/25887355/test_photo1.JPG";
	private static final String FILE2 = "https://dl.dropboxusercontent.com/u/25887355/test_photo2.jpg";
	private static final String FILE3 = "https://dl.dropboxusercontent.com/u/25887355/test_song.mp3";
	private static final String FILE4 = "https://dl.dropboxusercontent.com/u/25887355/test_video.mp4";
	private static final String FILE5 = "http://httpbin.org/headers";
	private static final String FILE6 = "https://dl.dropboxusercontent.com/u/25887355/ThinDownloadManager.tar.gz";

	private static final String LOG_TAG = "ThinDownloadManager";
	private int downloadId1;
	private int downloadId2;
	private int downloadId3;
	private int downloadId4;
	private int downloadId5;
	private int downloadId6;
	private ThinDownloadManager downloadManager;

	private ProgressBar mProgress1;
	private ProgressBar mProgress2;
	private ProgressBar mProgress3;
	private ProgressBar mProgress4;
	private ProgressBar mProgress5;
	private TextView mProgress5Txt;
	private MyDownloadDownloadStatusListenerV1
			myDownloadStatusListener = new MyDownloadDownloadStatusListenerV1();
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
			DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

			outputFile = pickedDir.createFile("text/plain", "My1.txt");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button mDownload1 = (Button) findViewById(R.id.button1);
		Button mDownload2 = (Button) findViewById(R.id.button2);
		Button mDownload3 = (Button) findViewById(R.id.button3);
		Button mDownload4 = (Button) findViewById(R.id.button4);
		Button mDownload5 = (Button) findViewById(R.id.button_download_headers);

		Button mCancelAll = (Button) findViewById(R.id.button6);
		Button mListFiles = (Button) findViewById(R.id.button7);

		mProgress1 = (ProgressBar) findViewById(R.id.progress1);
		mProgress2 = (ProgressBar) findViewById(R.id.progress2);
		mProgress3 = (ProgressBar) findViewById(R.id.progress3);
		mProgress4 = (ProgressBar) findViewById(R.id.progress4);
		mProgress5 = (ProgressBar) findViewById(R.id.progress5);

		mProgress1.setMax(100);
		mProgress1.setProgress(0);

		mProgress2.setMax(100);
		mProgress2.setProgress(0);

		mProgress3.setMax(100);
		mProgress3.setProgress(0);

		mProgress4.setMax(100);
		mProgress4.setProgress(0);

		mProgress5.setMax(100);
		mProgress5.setProgress(0);

		downloadManager = new ThinDownloadManager(this);
		RetryPolicy retryPolicy = new DefaultRetryPolicy();

		File filesDir = getExternalFilesDir("");

		Uri downloadUri = Uri.parse(FILE1);
		Uri destinationUri = Uri.parse(filesDir + "/test_photo1.JPG");
		final DownloadRequest downloadRequest1 = new DownloadRequest(downloadUri)
				.setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.LOW)
				.setRetryPolicy(retryPolicy)
				.setDownloadContext("Download1")
				.setStatusListener(myDownloadStatusListener);

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

		downloadUri = Uri.parse(FILE4);
		destinationUri = Uri.parse(filesDir + "/mani/test/aaa/test_video.mp4");
		// Define a custom retry policy
		retryPolicy = new DefaultRetryPolicy(5000, 3, 2f);
		final DownloadRequest downloadRequest4 = new DownloadRequest(downloadUri)
				.setRetryPolicy(retryPolicy)
				.setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
				.setDownloadContext("Download4")
				.setStatusListener(myDownloadStatusListener);

		downloadUri = Uri.parse(FILE5);
		destinationUri = Uri.parse(filesDir + "/headers.json");
		final DownloadRequest downloadRequest5 = new DownloadRequest(downloadUri)
				.addCustomHeader("Auth-Token", "myTokenKey")
				.addCustomHeader("User-Agent", "Thin/Android")
				.setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
				.setDownloadContext("Download5")
				.setStatusListener(myDownloadStatusListener);

		downloadUri = Uri.parse(FILE6);
		destinationUri = Uri.parse(filesDir + "/wtfappengine.zip");
		final DownloadRequest downloadRequest6 = new DownloadRequest(downloadUri)
				.setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
				.setDownloadContext("Download6")
				.setStatusListener(myDownloadStatusListener);

		mDownload1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (downloadManager.query(downloadId1) == DownloadManager.STATUS_NOT_FOUND) {
					downloadId1 = downloadManager.add(downloadRequest1);
				}
			}
		});

		mDownload2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (downloadManager.query(downloadId2) == DownloadManager.STATUS_NOT_FOUND) {
					downloadId2 = downloadManager.add(downloadRequest2);
				}
			}
		});

		mDownload3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (downloadManager.query(downloadId3) == DownloadManager.STATUS_NOT_FOUND) {
					downloadId3 = downloadManager.add(downloadRequest3);
				}
			}
		});

		mDownload4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (downloadManager.query(downloadId4) == DownloadManager.STATUS_NOT_FOUND) {
					downloadId4 = downloadManager.add(downloadRequest4);
				}
			}
		});

		mDownload5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//if (downloadManager.query(downloadId5) == DownloadManager.STATUS_NOT_FOUND) {
				//    downloadId5 = downloadManager.add(downloadRequest5);
				//}

				if (downloadManager.query(downloadId6) == DownloadManager.STATUS_NOT_FOUND) {
					downloadId6 = downloadManager.add(downloadRequest6);
				}

			}
		});

		Button mStartAll = (Button) findViewById(R.id.button5);
		mStartAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				downloadManager.cancelAll();
				downloadId1 = downloadManager.add(downloadRequest1);
				downloadId2 = downloadManager.add(downloadRequest2);
				downloadId3 = downloadManager.add(downloadRequest3);
				downloadId4 = downloadManager.add(downloadRequest4);
				downloadId5 = downloadManager.add(downloadRequest5);
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

	private class MyDownloadDownloadStatusListenerV1 implements DownloadStatusListenerV1 {

		@Override
		public void onDownloadComplete(DownloadRequest request) {
			final int id = request.getDownloadId();
			if (id == downloadId1) {
				mProgress1.setProgress(0);
			} else if (id == downloadId2) {
				mProgress2.setProgress(0);

			} else if (id == downloadId3) {
				mProgress3.setProgress(0);
			} else if (id == downloadId4) {
				mProgress4.setProgress(0);
			} else if (id == downloadId5) {
				mProgress5Txt.setText(String.format("%s id: %s Completed", request.getDownloadContext(), Integer.toString(id)));
			}
		}

		@Override
		public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
			final int id = request.getDownloadId();
			if (id == downloadId1) {
				mProgress1.setProgress(0);
			} else if (id == downloadId2) {
				mProgress2.setProgress(0);

			} else if (id == downloadId3) {
				mProgress3.setProgress(0);
			} else if (id == downloadId4) {
				mProgress4.setProgress(0);
			} else if (id == downloadId5) {
				mProgress5.setProgress(0);
			}
		}

		@Override
		public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
			int id = request.getDownloadId();

			System.out.println("######## onProgress ###### " + id + " : " + totalBytes + " : " + downloadedBytes + " : " + progress);
			if (id == downloadId1) {
				mProgress1.setProgress(progress);

			} else if (id == downloadId2) {
				mProgress2.setProgress(progress);

			} else if (id == downloadId3) {
				mProgress3.setProgress(progress);

			} else if (id == downloadId4) {
				mProgress4.setProgress(progress);
			} else if (id == downloadId5) {
				mProgress5Txt.setText("Download5 id: " + id + ", " + progress + "%" + "  " + getBytesDownloaded(progress, totalBytes));
				mProgress5.setProgress(progress);
			} else if (id == downloadId6) {
				mProgress5Txt.setText("Download6 id: " + id + ", " + progress + "%" + "  " + getBytesDownloaded(progress, totalBytes));
				mProgress5.setProgress(progress);
			}
		}
	}
}
