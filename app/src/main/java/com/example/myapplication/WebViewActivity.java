package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class WebViewActivity extends AppCompatActivity implements OnClickListener {

    private WebView wv_main;
    private WebSettings websetting;
    private ProgressDialog dialog;
    //        private String loadUrl = "https://www.baidu.com/";
    private String loadUrl = "http://114.67.227.59/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        findViewById();
        initData();
    }

    private void findViewById() {
        wv_main = (WebView) findViewById(R.id.wv_main);
    }


    @SuppressLint("JavascriptInterface")
    private void initData() {


        wv_main.setInitialScale(100);
        websetting = wv_main.getSettings();
        websetting.setJavaScriptEnabled(true);
        websetting.setSupportZoom(false);
        websetting.setBuiltInZoomControls(true);
        websetting.setUseWideViewPort(true);
        websetting.setDomStorageEnabled(true);


        websetting.setBlockNetworkImage(false);
        websetting.setBlockNetworkLoads(false);



        websetting.setLoadsImagesAutomatically(true); // 加载图片
        websetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        wv_main.setWebChromeClient(new GeoWebChromeClient());
        websetting.setGeolocationEnabled(true);
//        websetting.setAppCacheEnabled(true);// 开启缓存
//        websetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 缓存优先模式
//        websetting.setAppCacheMaxSize(8 * 1024 * 1024);// 设置最大缓存为8M

        wv_main.addJavascriptInterface(this, "share");
//        dialog = new ProgressDialog(this);
//        dialog.setMessage("正在加载，请耐心等待!");
        wv_main.loadUrl(loadUrl);
        wv_main.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        wv_main.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.d("WebView----------", "url = " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                dialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                dialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(WebViewActivity.this, "网络连接失败，请连接网络！",
                        Toast.LENGTH_SHORT).show();

                wv_main.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            }

        });

    }

    public class GeoWebChromeClient extends WebChromeClient {

        @Override

        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {

            callback.invoke(origin, true, false);

        }
    }

    public void onResume() {
        super.onResume();
        wv_main.onResume();
    }

    public void onPause() {
        super.onPause();
        wv_main.onPause();
        wv_main.clearCache(true);
        wv_main.clearHistory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wv_main != null) {
            wv_main.removeAllViews();

            wv_main.stopLoading();
            wv_main.setWebChromeClient(null);
            wv_main.setWebViewClient(null);

            wv_main.clearCache(true);
            wv_main.clearHistory();
            try {
                wv_main.destroy();
            } catch (Throwable t) {
            }
            wv_main = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }

}
