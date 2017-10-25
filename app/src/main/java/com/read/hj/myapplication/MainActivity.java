package com.read.hj.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class MainActivity extends Activity {

    private WebView webView;
    private ImageView imgView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView) findViewById(R.id.imgView);
        //String url = "http://42.243.109.158:7001/et/msys/index.html" ;//139
        //String url = "http://192.168.0.123:8081/recjc_ym/m_sys/_start.html" ;//me
        //String url = "http://192.168.2.113:8081/recjc_ym/m_sys/_start.html" ;//me
        String url =  "http://139.129.202.71/recjc/m_sys/_start.html" ;
        //String url = "http://192.168.0.111:8081/recjc_ym/m_sys/_start.html" ;//郭
        //String url = "http://192.168.0.110:8080/recjc_ym/m_sys/_start.html" ;//蔡
        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(url);
        webView.setVisibility(View.INVISIBLE);
        //http://192.168.0.123:8081/recjc/m_sys/ios.html
        //http://192.168.0.123:8081/recjc/m_sys/iframe.html
        //http://www.sandy.iego.cn/recjc/m_sys/_login.html
        webView.getSettings().setJavaScriptEnabled(true);
        webView.requestFocus();
//        webView.setWebViewClient(new WebViewClient());
       // webView.setWebChromeClient(new MyWebChromeClient());//让WebView支持弹出框
        webView.setWebChromeClient(mWebChromeClient);//让WebView支持弹出框

       // webView.setWebChromeClient(new WebChromeClient(){@Override public boolean onJsAlert(WebView view,String url,String message,JsResult result){return super.onJsAlert(view,url,message,result);}});

        //webView.addJavascriptInterface(new AndroidToastForJs(mContext), "JavaScriptInterface");
       // webView.addJavascriptInterface(new Contact(), "contact");

        webView.addJavascriptInterface(MainActivity.this,"android");

        //webView.addJavascriptInterface(new TestScanActivity(),"scan");


//使用localStorage
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDatabaseEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabasePath(dir);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });







    }




    /**
     * 监听Back键按下事件,方法1:
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确认退出吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        MainActivity.this.finish();

                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();
    }

    /**
     * 与js交互时用到的方法，在js里直接调用的
     */
    @JavascriptInterface
    public void showToast() {
        Intent intent = new Intent(MainActivity.this,TestScanActivity.class);
        startActivityForResult(intent,100);
        // Toast.makeText(context, ssss, Toast.LENGTH_LONG).show();
    }
    //mWebView.loadUrl("javascript:funFromjs()");
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 100) {
                String da = data.getStringExtra("result") ;
                webView.loadUrl("javascript:recall('"+da+"')");
        }
    }
    */


    /**
     * 与js交互时用到的方法，在js里直接调用的
     */
    @JavascriptInterface
    public void hideImgView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgView.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
            }
        });

    }
    /***
     * 支持打开选择文件
     */
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 2;
    private ValueCallback<Uri[]> mFilePathCallback;

    private String mCameraPhotoPath;

    //在sdcard卡创建缩略图
    //createImageFileInSdcard
    @SuppressLint("SdCardPath")
    private File createImageFile() {
        //mCameraPhotoPath="/mnt/sdcard/tmp.png";
        File file=new File(Environment.getExternalStorageDirectory()+"/","tmp.png");
        mCameraPhotoPath=file.getAbsolutePath();
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {


        // android 5.0 这里需要使用android5.0 sdk
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                WebChromeClient.FileChooserParams fileChooserParams) {

            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
                //********  mFilePathCallback.();
            }


            mFilePathCallback = filePathCallback;

            /**
             Open Declaration   String android.provider.MediaStore.ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE"
             Standard Intent action that can be sent to have the camera application capture an image and return it.
             The caller may pass an extra EXTRA_OUTPUT to control where this image will be written. If the EXTRA_OUTPUT is not present, then a small sized image is returned as a Bitmap object in the extra field. This is useful for applications that only need a small image. If the EXTRA_OUTPUT is present, then the full-sized image will be written to the Uri value of EXTRA_OUTPUT. As of android.os.Build.VERSION_CODES.LOLLIPOP, this uri can also be supplied through android.content.Intent.setClipData(ClipData). If using this approach, you still must supply the uri through the EXTRA_OUTPUT field for compatibility with old applications. If you don't set a ClipData, it will be copied there for you when calling Context.startActivity(Intent).
             See Also:EXTRA_OUTPUT
             标准意图，被发送到相机应用程序捕获一个图像，并返回它。通过一个额外的extra_output控制这个图像将被写入。如果extra_output是不存在的，
             那么一个小尺寸的图像作为位图对象返回。如果extra_output是存在的，那么全尺寸的图像将被写入extra_output URI值。
             */
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    //设置MediaStore.EXTRA_OUTPUT路径,相机拍照写入的全路径
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (Exception ex) {
                    // Error occurred while creating the File
                    Log.e("WebViewSetting", "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    System.out.println(mCameraPhotoPath);
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
                System.out.println(takePictureIntent);
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

            return true;
        }



        //The undocumented magic method override
        //Eclipse will swear at you if you try to put @Override here
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {

            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            MainActivity.this.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILECHOOSER_RESULTCODE);

        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {

            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            MainActivity.this.startActivityForResult(
                    Intent.createChooser(i, "Image Chooser"),
                    FILECHOOSER_RESULTCODE);
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {


            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            MainActivity.this.startActivityForResult(Intent.createChooser(i, "Image Chooser"), MainActivity.FILECHOOSER_RESULTCODE);

        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == 100) {
            String da = data.getStringExtra("result") ;
            webView.loadUrl("javascript:recall('"+da+"')");
        }

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = data == null || resultCode != RESULT_OK ? null
                    : data.getData();
            if (result != null) {
                String imagePath = ImageFilePath.getPath(this, result);
                if (!TextUtils.isEmpty(imagePath)) {
                    result = Uri.parse("file:///" + imagePath);
                }
            }
            //mUploadMessage.(result);
            //****** onReceiveValue
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == INPUT_FILE_REQUEST_CODE && mFilePathCallback != null) {
            // 5.0的回调
            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {

                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();

                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            //***  mFilePathCallback.(results);
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

}
