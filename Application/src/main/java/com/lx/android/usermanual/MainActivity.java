/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lx.android.usermanual;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.Locale;

public class MainActivity extends Activity {
    public static final String ASSIGN_PATH = "/storage/emulated/0/VideoBook";
    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";
    private static final String TAG = "MainActivity";
    private String mFileName="UserManual_default.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_real);

        if (savedInstanceState == null) {
            mFileName = "UserManual_"+getCurrentLanguage()+".pdf";
            new AsyncWorkTask().execute(new Object[] {mFileName});
        }
    }


    /**
     * 异步任务
     * @author hellogv
     */
    class AsyncWorkTask extends AsyncTask<Object,Object,Void> {
        @Override
        protected Void doInBackground(Object... params) {
            String fileName=(String) params[0];
            String path="null";
            Uri fileUri=searchFile(ASSIGN_PATH,fileName);
            if(fileUri!=null){
                path = fileUri.getPath();
            }
            publishProgress(new Object[] {path});

            return null;
        }

        protected void onProgressUpdate(Object... progress) {
            String filePath=(String) progress[0];
            openPdf(filePath);
//            finish();
//            System.exit(0);
        }
    }
    public void openPdf(String filePath)
    {
        Log.d(TAG,"openPdf="+filePath);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, new PdfRendererBasicFragment(),filePath)
                .commit();

    }

    private String getCurrentLanguage() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
      //  Log.d(TAG,"language="+language);
        return language;
    }

    private Uri searchFile(String dir, String fileName) {
        Uri result=null;
        boolean findOK=false;
   //     Log.d(TAG,"searchFile fileName="+fileName);
        File[] files = new File(dir).listFiles();
        for (File f : files) {
   //         Log.d(TAG,"list: "+f.toString());
            if(f.getName().indexOf(fileName)>=0){
                result = Uri.fromFile(f);
                findOK=true;
            }
        }
        //查找默认
        if(findOK==false) {
            for (File f : files) {
        //        Log.d(TAG, "list: " + f.toString());
                if (f.getName().indexOf("UserManual_default.pdf") >= 0) {
                    result = Uri.fromFile(f);
                    findOK = true;
                }
            }
        }

//        if(result==null){
//            Looper.prepare();
//            showErroDialog("Can't find the file, find the position: "+dir+"/"+fileName);
//            Looper.loop();
//        }
        return result;
    }

    protected void showErroDialog(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Erro!!!");

        builder.setMessage(msg);

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                finish();
                System.exit(0);

            }
        });

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                new AlertDialog.Builder(this)
                        .setMessage("The location of the user manual is "+ASSIGN_PATH+"/"+mFileName)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
