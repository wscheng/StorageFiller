
package com.asus.StorageFiller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Filler extends Activity {

    EditText mInternalFillSizeText;
    EditText mExternalFillSizeText;
    TextView mInternalLeftSizeText;
    TextView mExternalLeftSizeText;
    Button mFillExternalButton;
    Button mFillInternalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filler);
        mInternalFillSizeText = (EditText) findViewById(R.id.internal_fill_size_text);
        mExternalFillSizeText = (EditText) findViewById(R.id.external_fill_size_text);
        mInternalLeftSizeText = (TextView) findViewById(R.id.internal_left_size_text);
        mExternalLeftSizeText = (TextView) findViewById(R.id.external_left_size_text);
        mFillExternalButton = (Button) findViewById(R.id.fill_external);
        mFillInternalButton = (Button) findViewById(R.id.fill_internal);
        mFillExternalButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        mFillInternalButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    fillInternal(Long.parseLong(mInternalFillSizeText.getText().toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(Filler.this, mInternalFillSizeText.getText().toString(), Toast.LENGTH_SHORT).show();
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mInternalLeftSizeText.setText(getInternalStorageAvailableMB()+"MB");
            }
        });
    }
    private long getInternalStorageAvailableMB(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getAvailableBlocks() * stat.getBlockSize();// / 1024;
    }
    private long getExternalStorageAvailableMB(){
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        StatFs stat = new StatFs(path.getPath());
        return stat.getAvailableBlocks() * stat.getBlockSize() / 1024;
    }

    /*private void fillInternal(long sizeMB) throws IOException {
        String fileName = "blank";
        FileOutputStream writer = openFileOutput(fileName, Context.MODE_PRIVATE);
        String content="";
        writer.write(content.getBytes());
        writer.close();
    }*/

    private void fillInternal(long sizeMB) throws IOException {
        long sizeByte = sizeMB;// * 1024* 1024;
        //Toast.makeText(this, getFilesDir().getAbsolutePath(), Toast.LENGTH_SHORT).show();
        for (int i = 0; sizeByte!=0; i++) {
            String content = "x";
            String fileName = i + ".txt";
            if(new File(getFilesDir().getAbsolutePath()+"/"+fileName).exists()) continue;
            FileOutputStream writer = openFileOutput(fileName, Context.MODE_PRIVATE);
            //Long xxx2= new Long( 1024 * 1024 * 4);
            for (Long j = new Long(0); j < Long.valueOf("4294967296"); j++) {
                if(sizeByte==0)break;
                writer.write(content.getBytes());
                sizeByte--;
            }
            writer.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_filler, menu);
        return true;
    }

}

