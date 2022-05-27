package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.engine.FaceEngine;

import static com.example.myapplication.engine.FaceEngine.FACERECOGNIZER;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'myapplication' library on application startup.
    static {
        System.loadLibrary("myapplication");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        //初始化引擎
//        FaceEngine.init();
        initView();
    }

    /**
     * A native method that is implemented by the 'myapplication' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void initView() {
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        Button openWebView = binding.openWebView;
        Button btOpenFaceCheck = binding.btOpenFaceCheck;
        Button btOpenFaceDetector = binding.btOpenFaceDetector;
        Button btRegisterFace = binding.btRegisterFace;
        Button btInfoLogin = binding.btInfoLogin;
        openWebView.setOnClickListener(this);
        btOpenFaceCheck.setOnClickListener(this);
        btOpenFaceDetector.setOnClickListener(this);
        btRegisterFace.setOnClickListener(this);
        btInfoLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
//        if (!FaceEngine.INIT_SUCCESS) {
//            Toast.makeText(this, "引擎尚未加载完毕请稍等", Toast.LENGTH_SHORT).show();
//            return;
//        }
        switch (view.getId()) {
            case R.id.openWebView:
                Intent intent = new Intent(MainActivity.this, NewWebView.class);
                startActivity(intent);
                break;
            case R.id.btOpenFaceCheck:
                Intent intent2 = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent2);
                break;
            case R.id.btOpenFaceDetector:
                Intent intent3 = new Intent(MainActivity.this, FaceContrast.class);
                startActivity(intent3);
                break;
                case R.id.btRegisterFace:
                Intent intent4 = new Intent(MainActivity.this, RegisterFaceInfoActivity.class);
                startActivity(intent4);
                break;
                case R.id.btInfoLogin:
                Intent intent5 = new Intent(MainActivity.this, FaceInfoLoginActivity.class);
                startActivity(intent5);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(FACERECOGNIZER!=null){
            FACERECOGNIZER.Clear();
        }
    }
}