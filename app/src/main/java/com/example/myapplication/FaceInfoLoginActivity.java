package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.engine.FaceEngine;
import com.example.myapplication.utils.CameraUtil;
import com.example.myapplication.utils.ConvertUtil;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class FaceInfoLoginActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private CameraUtil mCameraUtil;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private Camera mCamera;
    //    private int mCameraId = 0;
    private int mCameraId = 1;
    private int mWidth = 1920;
    private int mHeight = 1080;
    private SurfaceHolder mHolder;
    private ImageView image1, image2;
    Bitmap bit = null;
    Bitmap change = null;
    //    private DetecteSeeta mDetecteSeeta;
    private ProgressDialog pd;
    private static final int MSG_COPE = 0;
    private static final int MSG_COPE_FAIL = 2;
    private SurfaceView mViewSurface;
    private ImageView iv_iviviv;
    private Button bt_change;
    private RelativeLayout pageBg;
    //    private CountDownView cdv;
    private boolean isLoginVerify = true;
    private boolean isErrorDialog = true;
    private boolean isLoginDialog = true;
    private TextView mGoPsd, mGoNext, mGoHome;
    private LinearLayout anquanBg;
    private LinearLayout anquanTitleBg;
    private Bitmap qq1;
    private ImageView iv;
    private LinearLayout anquanguanbi;
    private ImageView guanbi;
    private Dialog dialog;
    private TextView tv_face_num;

    private boolean isScanning = false;
    private int failedCount = 0;//失败次数
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    failedCount++;
                    if (failedCount >= 5) {
                        Toast.makeText(FaceInfoLoginActivity.this, "人脸不匹配，登录失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    isScanning = false;
                    break;
                case 1:
                    Toast.makeText(FaceInfoLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_info_login);
        FileInputStream fs = null;
//        try {
////            fs = new FileInputStream("/sdcard/eBook/facelogin/qq1.jpg");
//            fs = new FileInputStream(DataComfig.SD_PATH_EXTERNAL + "facelogin/face.jpg");
//            qq1 = BitmapFactory.decodeStream(fs);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        mDetecteSeeta = new DetecteSeeta();

//        cdv = (CountDownView) findViewById(R.id.countDownView);
//        cdv.setAddCountDownListener(new CountDownView.OnCountDownFinishListener() {
//            @Override
//            public void countDownFinished() {
//                isLoginVerify = false;
//                loginVerify(Integer.parseInt(FileUtil.loadFromSDFile(DataComfig.SAFETY_LOGIN_PATH, DataComfig.LOGIN_ERROR_NUMBERS)));
//                Toast.makeText(FaceInfoLoginActivity.this,"请重试",Toast.LENGTH_SHORT).show();
//            }
//        });
//        cdv.startCountDown();

        initPermission();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mHolder != null) {
            mHolder.addCallback(holderCallback);
        }
//        mHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder surfaceHolder) {
//                mCamera = Camera.open(mCameraId);
//                mCameraUtil = new CameraUtil(mCamera, mCameraId);
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//                mCameraUtil
//                        .initCamera(mWidth,
//                                mHeight,
//                                FaceInfoLoginActivity.this);
//                try {
//                    mCamera.setPreviewDisplay(surfaceHolder);
////                    if (isLoginVerify) {
//                    getPreViewImage();
////                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//                if (mCamera != null) {
//                    surfaceHolder.removeCallback(this);
//                    try {
//                        mCamera.setPreviewCallback(null);
//                        mCamera.stopPreview();
//                        mCamera.lock();
//                        mCamera.release();
//                        mCamera = null;
//                        mCameraUtil.stopPreview();
//                    } catch (Exception e) {
//                    }
//                }
//            }
//        });
//        cdv.startCountDown();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            initView();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            } else {
                initView();
            }
        }
    }


    private void initView() {
        mViewSurface = (SurfaceView) findViewById(R.id.view_surface);
        iv_iviviv = findViewById(R.id.iv_iviviv);
        bt_change = findViewById(R.id.bt_change);
        tv_face_num = findViewById(R.id.tv_face_num);
        mViewSurface.setWillNotDraw(false);
//        mViewSurface.setOutlineProvider(new ViewOutlineProvider() {
//            @Override
//            public void getOutline(View view, Outline outline) {
//                Rect rect = new Rect();
//                view.getGlobalVisibleRect(rect);
//                int leftMargin = 0;
//                int topMargin = 0;
////                Rect selfRect = new Rect(leftMargin,topMargin,rect.right - rect.left - leftMargin,rect.bottom - rect.top - topMargin);
////                Rect selfRect = new Rect(leftMargin + 40, topMargin, rect.right - rect.left - leftMargin - 40, rect.bottom - rect.top - topMargin);
//                Rect selfRect = new Rect(leftMargin, topMargin, rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
//                outline.setRoundRect(selfRect, 280);
//            }
//        });
//        mViewSurface.setClipToOutline(true);
        mHolder = mViewSurface.getHolder();
        mHolder.addCallback(holderCallback);

        bt_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginVerify = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCameraId == 0) {
                            mCameraId = 1;
                        } else {
                            mCameraId = 0;
                        }

//                    mCamera.setPreviewCallback(null);
//                    mCamera.stopPreview();
//                    mCamera.lock();
//                    mCamera.release();
//                    mCamera = null;
//                    mCamera = Camera.open(mCameraId);
//
//                    mCamera.setPreviewDisplay(mHolder);
//                    mCameraUtil.initCamera(mWidth,
//                            mHeight,
//                            FaceInfoLoginActivity.this);

                        mHolder.removeCallback(holderCallback);
                        mCameraUtil.changeCamera(mHolder);
                        mCameraUtil.setPreviewCallback(new CameraUtil.PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {
                                if (FaceEngine.FACEDETECTOR != null && FaceEngine.FACERECOGNIZER != null && FaceEngine.POINTDETECTOR != null) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //识别中不处理其他帧数据
                                            if (!isScanning) {
                                                isScanning = true;
                                                try {
                                                    //获取Camera预览尺寸
                                                    Camera.Size size = camera.getParameters().getPreviewSize();
                                                    //将帧数据转为bitmap
                                                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                                                    if (image != null) {
                                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                                                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                                                        //纠正图像的旋转角度问题
                                                        Matrix m = new Matrix();
                                                        m.setRotate(-90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                                                        Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
                                                        SeetaImageData loginSeetaImageData = ConvertUtil.ConvertToSeetaImageData(bm);
                                                        SeetaRect[] faceRects = FaceEngine.FACEDETECTOR.Detect(loginSeetaImageData);
                                                        if (faceRects.length > 0) {
                                                            //获取人脸区域（这里只有一个所以取0）
                                                            SeetaRect faceRect = faceRects[0];
                                                            SeetaPointF[] seetaPoints = FaceEngine.POINTDETECTOR.Detect(loginSeetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                                                            float[] similarity = new float[1];//用来存储人脸相似度值
                                                            FaceEngine.FACERECOGNIZER.Recognize(loginSeetaImageData, seetaPoints, similarity);//匹配
                                                            if (similarity[0] > 0.7) {
                                                                handler.sendEmptyMessage(1);
                                                            } else {
                                                                handler.sendEmptyMessage(0);
                                                            }
                                                        } else {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(FaceInfoLoginActivity.this, "请保持手机不要晃动", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            isScanning = false;
                                                        }
                                                    }
                                                } catch (Exception ex) {
                                                    isScanning = false;
                                                }
                                            }
                                        }
                                    }
                                    ).start();
                                }
                            }
                        });

                    }
                });

            }
        });
    }

    private SurfaceHolder.Callback holderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mCamera = Camera.open(mCameraId);
            mCameraUtil = new CameraUtil(mCamera, mCameraId);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            mCameraUtil
                    .initCamera(mWidth,
                            mHeight,
                            FaceInfoLoginActivity.this);
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
//                    if (isLoginVerify) {
                getPreViewImage();
//                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (mCamera != null) {
                surfaceHolder.removeCallback(this);
                try {
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    mCamera.lock();
                    mCamera.release();
                    mCamera = null;
                    mCameraUtil.stopPreview();
                } catch (Exception e) {
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }


    private void getPreViewImage() {
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

//                Camera.Size size = camera.getParameters().getPreviewSize();
//                try {
//                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
//                    if (image != null) {
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
////                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(),0,stream.size());
//                        //                        bit = BitmapFactory.decodeResource(getResources(),R.mipmap.qq1);
//
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(90); /*翻转180度*/
//                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
//                        bmp = Bitmap.createBitmap(bmp, 0, 0, size.width, size.height, matrix, true);
//                        iv_iviviv.setImageBitmap(bmp);
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(FaceInfoLoginActivity.this, "image抛异常", Toast.LENGTH_SHORT).show();
//                }

                if (FaceEngine.FACEDETECTOR != null && FaceEngine.FACERECOGNIZER != null && FaceEngine.POINTDETECTOR != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //识别中不处理其他帧数据
                            if (!isScanning) {
                                isScanning = true;
                                try {
                                    //获取Camera预览尺寸
                                    Camera.Size size = camera.getParameters().getPreviewSize();
                                    //将帧数据转为bitmap
                                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                                    if (image != null) {
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                                        //纠正图像的旋转角度问题
                                        Matrix m = new Matrix();
                                        m.setRotate(-90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                                        Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
                                        SeetaImageData loginSeetaImageData = ConvertUtil.ConvertToSeetaImageData(bm);
                                        SeetaRect[] faceRects = FaceEngine.FACEDETECTOR.Detect(loginSeetaImageData);
                                        if (faceRects.length > 0) {
                                            //获取人脸区域（这里只有一个所以取0）
                                            SeetaRect faceRect = faceRects[0];
                                            SeetaPointF[] seetaPoints = FaceEngine.POINTDETECTOR.Detect(loginSeetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                                            float[] similarity = new float[1];//用来存储人脸相似度值
                                            FaceEngine.FACERECOGNIZER.Recognize(loginSeetaImageData, seetaPoints, similarity);//匹配
                                            if (similarity[0] > 0.7) {
                                                handler.sendEmptyMessage(1);
                                            } else {
                                                handler.sendEmptyMessage(0);
                                            }
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(FaceInfoLoginActivity.this, "请保持手机不要晃动", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            isScanning = false;
                                        }
                                    }
                                } catch (Exception ex) {
                                    isScanning = false;
                                }
                            }
                        }
                    }
                    ).start();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mCamera != null) {
//            mCamera.stopPreview();
//            mCamera.setPreviewCallback(null);
//            mCamera.release();
//            mCamera = null;
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isLoginVerify) {
            if (mCameraUtil != null) {
                mCameraUtil.stopPreview();
            }
        }
    }

}
