
package com.asus.StorageFiller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wei-Sheng Cheng(WeiShen_Cheng@asus.com)
 * @version v1.0
 **/

public class Filler extends Activity {

    EditText mInternalFillSizeText;
    EditText mExternalFillSizeText;
    TextView mInternalLeftSizeText;
    TextView mExternalLeftSizeText;
    Button mFillExternalButton;
    Button mFillInternalButton;

    private class StorageInfo {
        public File mFile;
        public EditText mFillSizeText;
        public TextView mLeftSizeText;
        public Button mFillButton;
        private long mFillSizeMB;

        StorageInfo(File file) {
            mFile = file;
        }

        public void fillStorage() {
            try {
                mFillButton.setEnabled(false);
                mFillSizeMB = Long.valueOf(mFillSizeText.getText().toString());
                if (mFillSizeMB <= 0) {
                    return;
                }
                Toast.makeText(Filler.this, "Filling " + mFile.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
                Thread fillStorageThread = new FillStorageThread();
                fillStorageThread.start();
            } catch (NumberFormatException e) {
                Toast.makeText(Filler.this, mFillSizeText.getText().toString(), Toast.LENGTH_SHORT)
                        .show();
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void refreshStorageInfo() {
            DecimalFormat df=new DecimalFormat("#.##");
            if (getStorageAvailableMB() > 0) {
                mLeftSizeText.setText(df.format(getStorageAvailableMB()) + " MB");
            } else {
                mLeftSizeText.setText(df.format(getStorageAvailableKB()) + " KB");
            }
        }

        private double getStorageAvailableMB() {
            return mFile.getUsableSpace() / Math.pow(2, 20);
        }

        private long getStorageAvailableKB() {
            return mFile.getUsableSpace() / 1024;
        }

        private class FillStorageThread extends Thread {
            public void run() {
                try {
                    Thread refreshThread = new RefreshThread();
                    refreshThread.start();
                    fillStorage(mFillSizeMB);
                    refreshThread.interrupt();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                new EnableButtonTask().execute();
            }
        };

        private class EnableButtonTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                return null;
                // TODO Auto-generated method stub
            }

            @Override
            protected void onPostExecute(Void unused) {
                mFillButton.setEnabled(true);
            }
        };

        private void fillStorage(long sizeMB) throws IOException {
            if (mFile.getAbsolutePath()
                    .startsWith(Environment.getDataDirectory().getAbsolutePath())) {
                fillInternalStorage(sizeMB);
            } else {
                fillExternalStorage(sizeMB);
            }
        }

        private void fillInternalStorage(Long sizeMB) throws IOException {
            byte[] size1MBArr = new byte[1024*1024];
            for (int i = 0; i < 1024; i++) {
                size1MBArr[0] = 'x';
            }

            for (int i = 0; sizeMB != 0; i++) {
                String fileName = i + ".txt";
                if (new File(getFilesDir().getAbsolutePath() + "/" + fileName).exists())
                    continue;

                FileOutputStream writer = openFileOutput(fileName, Context.MODE_PRIVATE);
                for (Long j = Long.valueOf(0); j < Long.valueOf((long) Math.pow(2, 12)); j++) {
                    if (sizeMB == 0)
                        break;
                    writer.write(size1MBArr, 0, 1024*1024);// per KB
                    sizeMB--;
                }
                writer.close();
            }
        }

        private void fillExternalStorage(Long sizeMB) throws IOException {
            byte[] size1MBArr = new byte[1024*1024];
            for (int i = 0; i < 1024; i++) {
                size1MBArr[0] = 'x';
            }

            for (int i = 0; sizeMB != 0; i++) {
                File outFile = new File(mFile, i + ".txt");

                if (outFile.exists())
                    continue;

                FileOutputStream writer = new FileOutputStream(outFile);
                for (Long j = Long.valueOf(0); j < Long.valueOf((long) Math.pow(2, 12)); j++) {
                    if (sizeMB == 0)
                        break;
                    writer.write(size1MBArr, 0, 1024*1024);// per MB
                    sizeMB--;
                }
                writer.close();
            }
        }

    }

    private StorageInfo mInternalStorageInfo, mExternalStorageInfo;

    private class RefreshThread extends Thread {
        public void run() {
            while (true) {
                new RefreshTask().execute();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    break;
                }
            }
        }
    };

    private class RefreshTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
            // TODO Auto-generated method stub
        }

        @Override
        protected void onPostExecute(Void unused) {
            mInternalStorageInfo.refreshStorageInfo();
            mExternalStorageInfo.refreshStorageInfo();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filler);
        mInternalStorageInfo = new StorageInfo(Environment.getDataDirectory());
        // ++WeiSheng_Cheng: should add permission, or the Download storage
        // cannot be written
        mExternalStorageInfo = new StorageInfo(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

        mInternalStorageInfo.mFillSizeText = (EditText) findViewById(R.id.internal_fill_size_text);
        mInternalStorageInfo.mLeftSizeText = (TextView) findViewById(R.id.internal_left_size_text);
        mInternalStorageInfo.mFillButton = (Button) findViewById(R.id.fill_internal);
        mInternalStorageInfo.refreshStorageInfo();

        mExternalStorageInfo.mFillSizeText = (EditText) findViewById(R.id.external_fill_size_text);
        mExternalStorageInfo.mLeftSizeText = (TextView) findViewById(R.id.external_left_size_text);
        mExternalStorageInfo.mFillButton = (Button) findViewById(R.id.fill_external);
        mExternalStorageInfo.refreshStorageInfo();

        mExternalStorageInfo.mFillButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mExternalStorageInfo.fillStorage();
            }
        });
        mInternalStorageInfo.mFillButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mInternalStorageInfo.fillStorage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_filler, menu);
        return true;
    }

}
