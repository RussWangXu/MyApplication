package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;


import com.example.myapplication.engine.FaceEngine;
import com.example.myapplication.utils.CameraUtil;
import com.example.myapplication.utils.ConvertUtil;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RegisterFaceInfoActivity extends Activity {

    private SurfaceView mViewSurface;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private final static String TAG = "ChangeFaceActivity";
    private SurfaceHolder mHolder;
    private Camera mCamera;
        private int mCameraId = 1;
//    private int mCameraId = 0;
    private CameraUtil mCameraUtil;
    private int mWidth = 1920;
    private int mHeight = 1080;

    private boolean isScanning = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(RegisterFaceInfoActivity.this, "信息录入成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face_info);
        mViewSurface = (SurfaceView) findViewById(R.id.view_surface);

        findViewById(R.id.iv_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.change_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getPreViewImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
//                outline.setRoundRect(selfRect, 200);
//            }
//        });
//        mViewSurface.setClipToOutline(true);
        initCamera();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initCamera();
    }

    private void initCamera() {
        //检查权限和硬件
        if (ContextCompat.checkSelfPermission(RegisterFaceInfoActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(RegisterFaceInfoActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RegisterFaceInfoActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            if (!checkCameraHardware(this)) {
                Log.i(TAG, "没有检测到相机硬件");
            } else {
                mHolder = mViewSurface.getHolder();
                mHolder.addCallback(new SurfaceHolder.Callback() {
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
                                        RegisterFaceInfoActivity.this);
                        try {
                            mCamera.setPreviewDisplay(mHolder);
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
                });
            }

        }
    }

    private void getPreViewImage() throws Exception {
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
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(90); /*翻转180度*/ //后置摄像头是90°  前置摄像头是270°
//                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
//                        bmp = Bitmap.createBitmap(bmp, 0, 0, size.width, size.height, matrix, true);
//
//
//                        stream.close();
//                        mCamera.stopPreview();
//                        mCamera.setPreviewCallback(null);
//                        camera.release();
//                        mCamera = null;
////                        IntentUtil.get().goActivityKill(RegisterFaceInfoActivity.this, UserInfoLoginActivity.class);
////                        Toast.makeText(RegisterFaceInfoActivity.this, "人脸信息录入成功", Toast.LENGTH_SHORT).show();
//
//
//                    }
//                } catch (Exception e) {
//
//                }

                if(FaceEngine.FACEDETECTOR!=null&&FaceEngine.FACERECOGNIZER!=null&&FaceEngine.POINTDETECTOR!=null){
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
                                        SeetaImageData RegistSeetaImageData = ConvertUtil.ConvertToSeetaImageData(bm);
                                        SeetaRect[] faceRects = FaceEngine.FACEDETECTOR.Detect(RegistSeetaImageData);
                                        if(faceRects.length>0){
                                            //获取人脸区域（这里只有一个所以取0）
                                            SeetaRect faceRect = faceRects[0];
                                            SeetaPointF[] seetaPoints = FaceEngine.POINTDETECTOR.Detect(RegistSeetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                                            FaceEngine.FACERECOGNIZER.Register(RegistSeetaImageData, seetaPoints);//将人脸注册到SeetaFace2数据库
                                            handler.sendEmptyMessage(0);
                                        }else {
                                            //如果检测不到人脸给予如下提示
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(RegisterFaceInfoActivity.this, "请保持手机不要晃动", Toast.LENGTH_SHORT).show();
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


    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
}
