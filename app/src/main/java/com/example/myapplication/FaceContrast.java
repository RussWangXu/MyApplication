package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.detector.ObjectDetector;
import com.example.myapplication.engine.FaceEngine;
import com.example.myapplication.utils.ConvertUtil;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.util.Arrays;


public class FaceContrast extends AppCompatActivity {

    //初始化人脸探测器
    private CascadeClassifier faceDetector;
    private Bitmap mBitmap1, mBitmap2;
    private int registerIndex;
    private boolean moduleFlag = false;

    private ImageView iv_01;
    private ImageView iv_02;

    static {
        System.loadLibrary("opencv_java3");
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            iv_02.setImageBitmap((Bitmap) msg.obj);
        }
    };

    private BaseLoaderCallback callback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i("TAG", "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        CascadeClassifier mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e("TAG", "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i("TAG", "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

//                        DetectionBasedTracker mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("TAG", "Failed to load cascade. Exception thrown: " + e);
                    }
                    break;

                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_contrast);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            initView();
            initFace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long lastClickTime; //控制按钮快速点击造成activity多次启动 时间戳

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 2000) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    private void initView() throws IOException {
        Button bt_face_check = findViewById(R.id.bt_face_check);
        iv_01 = findViewById(R.id.iv_01);
        iv_02 = findViewById(R.id.iv_02);
        fileIsExists(Environment.getExternalStorageDirectory().getAbsolutePath() + "/facelogin/xietingfeng.jpg");
//        fileIsExists("file://android_assets/wuyanzu.jpg");

        mBitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.xietingfeng);
        mBitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.xietingfeng2);

        iv_01.setImageBitmap(mBitmap1);
        iv_02.setImageBitmap(mBitmap2);


        bt_face_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                double check_info = 0;
//                Mat mat1 = new Mat();
//                Mat mat2 = new Mat();
//                Mat mat11 = new Mat();
//                Mat mat22 = new Mat();
//                Utils.bitmapToMat(mBitmap1, mat1);
//                Utils.bitmapToMat(mBitmap2, mat2);
//                Imgproc.cvtColor(mat1, mat11, Imgproc.COLOR_BGR2GRAY);
//                Imgproc.cvtColor(mat2, mat22, Imgproc.COLOR_BGR2GRAY);
//                comPareHist(mat11, mat22);//这是计算两张图片的相似都
//                bt_face_check.setText("检测结果：" + check_info);


                //如果人脸模型初始化成功了再操作
                if (isFastDoubleClick()) {
                    Toast.makeText(FaceContrast.this, "请勿频繁点击，稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (moduleFlag) {
                        Toast.makeText(FaceContrast.this, "匹配中请稍等", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //加载进行匹配的图像
//                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tang08);
                                //这里必须进行copy否则修改不了
                                Bitmap copy = mBitmap2.copy(Bitmap.Config.ARGB_8888, true);
                                //利用Bitmap创建Canvas，为了在图像上绘制人脸区域
                                Canvas canvas = new Canvas(copy);
                                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                paint.setColor(Color.RED);
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(3);
                                SeetaImageData seetaImageData = ConvertUtil.ConvertToSeetaImageData(mBitmap2);
                                //人脸检测
                                SeetaRect[] detects = FaceEngine.FACEDETECTOR.Detect(seetaImageData);
                                if (detects.length > 0) {
                                    //将所有检测到的人脸与注册到数据库的人脸进行匹配
                                    for (int i = 0; i < detects.length; i++) {
                                        SeetaRect faceRect = detects[i];
                                        SeetaPointF[] seetaPoints = FaceEngine.POINTDETECTOR.Detect(seetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                                        float[] similarity = new float[1];//用来存储人脸相似度值
                                        int targetIndex = FaceEngine.FACERECOGNIZER.Recognize(seetaImageData, seetaPoints, similarity);//匹配
                                        Log.e("人脸匹配", targetIndex + "=======" + registerIndex + "=====" + similarity[0]);
                                        //如果匹配值大于0.7说明是同一个人
                                        if (similarity[0] > 0.7) {
                                            //将匹配出来的人脸区域绘制出来
                                            android.graphics.Rect rect = new Rect(faceRect.x, faceRect.y, faceRect.x + faceRect.width, faceRect.y + faceRect.height);
                                            canvas.drawRect(rect, paint);
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                bt_face_check.setText("相似度：" + similarity[0]);
                                            }
                                        });
                                    }
                                    //通知主线程更新UI
                                    Message obtain = Message.obtain();
                                    obtain.obj = copy;
                                    handler.sendMessage(obtain);
                                }
                            }
                        }).start();
                    } else {
                        Toast.makeText(FaceContrast.this, "人脸模型尚未初始化成功请稍等", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }

    /**
     * 初始化人脸检测器
     */
    private void initFace() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //利用SeetaFace2提供的转换方法获取SeetaRect（人脸识别结果）
                SeetaImageData RegistSeetaImageData = ConvertUtil.ConvertToSeetaImageData(mBitmap1);
                if (RegistSeetaImageData == null) {
                    return;
                }
                SeetaRect[] faceRects = FaceEngine.FACEDETECTOR.Detect(RegistSeetaImageData);
                if (faceRects.length > 0) {
                    //获取人脸区域（这里只有一个所以取0）
                    SeetaRect faceRect = faceRects[0];
                    SeetaPointF[] seetaPoints = FaceEngine.POINTDETECTOR.Detect(RegistSeetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                    registerIndex = FaceEngine.FACERECOGNIZER.Register(RegistSeetaImageData, seetaPoints);//将人脸注册到SeetaFace2数据库
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FaceContrast.this, "人脸模型初始化成功", Toast.LENGTH_SHORT).show();
                    }
                });
                //模型加载标记
                moduleFlag = true;
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (FaceEngine.FACERECOGNIZER != null) {
            FaceEngine.FACERECOGNIZER.Clear();//清空注册的人脸
//            FaceEngine.FACERECOGNIZER.dispose();
        }
    }

    /**
     * 特征对比
     * 对比的两张图片必须是灰度图
     *
     * @param file1 人脸特征
     * @param file2 人脸特征
     * @return 相似度
     */
//    public double CmpPic(String file1, String file2) {
//
//
//        int l_bins = 20;
//        int hist_size[] = {l_bins};
//
//        float v_ranges[] = {0, 100};
//        float ranges[][] = {v_ranges};
//
//        opencv_core.IplImage Image1 = cvLoadImage(file1, CV_LOAD_IMAGE_GRAYSCALE);
//        opencv_core.IplImage Image2 = cvLoadImage(file2, CV_LOAD_IMAGE_GRAYSCALE);
//
//        IplImage imageArr1[] = {Image1};
//        IplImage imageArr2[] = {Image2};
//
//        opencv_core.CvHistogram Histogram1 = opencv_core.CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
//        opencv_core.CvHistogram Histogram2 = opencv_core.CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
//
//        cvCalcHist(imageArr1, Histogram1, 0, null);
//        cvCalcHist(imageArr2, Histogram2, 0, null);
//
//        cvNormalizeHist(Histogram1, 100.0);
//        cvNormalizeHist(Histogram2, 100.0);
//
//        return cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);
//    }


    /**
     * 提取特征
     *
     * @param context  Context
     * @param fileName 文件名
     * @return 特征图片
     */
    public Bitmap getImage(Context context, String fileName) {
        String filePath = getFilePath(context, fileName);
        if (TextUtils.isEmpty(filePath)) {
            return null;
        } else {
            return BitmapFactory.decodeFile(filePath);
        }
    }

    /**
     * 获取人脸特征路径
     *
     * @param fileName 人脸特征的图片的名字
     * @return 路径
     */
    private String getFilePath(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        // 内存路径
//        return context.getApplicationContext().getFilesDir().getPath() + fileName + ".jpg";
        // 内存卡路径 需要SD卡读取权限
        return Environment.getExternalStorageDirectory() + "/FaceDetect/" + fileName + ".jpg";
    }


    public boolean fileIsExists(String fileName) {
        try {
            File f = new File(fileName);
            if (f.exists()) {
                Log.i("测试", "有这个文件" + fileName);
                return true;
            } else {
                Log.i("测试", "没有这个文件" + fileName);
                return false;
            }
        } catch (Exception e) {
            Log.i("测试", "崩溃");
            return false;
        }
    }

    private Mat mMat0;
    private MatOfInt mChannels[];
    private MatOfInt mHistSize;
    private int mHistSizeNum = 25;
    private MatOfFloat mRanges;
    private Scalar mColorsRGB[];
    private Point mP1;
    private Point mP2;
    private float mBuff[];


    public Mat procSrc2GrayJni(Mat srcMat, int type) {
        Mat grayMat = new Mat();
        Imgproc.cvtColor(srcMat, grayMat, type);//转换为灰度图
        // Imgproc.HoughCircles(rgbMat, gray,Imgproc.CV_HOUGH_GRADIENT, 1, 18);
        // //霍夫变换找园
        mChannels = new MatOfInt[]{new MatOfInt(0), new MatOfInt(1), new MatOfInt(2)};
        mBuff = new float[mHistSizeNum];
        mHistSize = new MatOfInt(mHistSizeNum);
        mRanges = new MatOfFloat(0f, 256f);
        mMat0 = new Mat();
        mColorsRGB = new Scalar[]{new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255)};
        mP1 = new Point();
        mP2 = new Point();


        Mat rgba = srcMat;
        Size sizeRgba = rgba.size();
        Mat hist = new Mat(); //转换直方图进行绘制
        int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
        if (thikness > 5) thikness = 5;
        int offset = (int) ((sizeRgba.width - (5 * mHistSizeNum + 4 * 10) * thikness) / 2);
        // RGB
        for (int c = 0; c < 3; c++) {
            Imgproc.calcHist(Arrays.asList(rgba), mChannels[c], mMat0, hist, mHistSize, mRanges);
            Core.normalize(hist, hist, sizeRgba.height / 2, 0, Core.NORM_INF);
            hist.get(0, 0, mBuff);
            for (int h = 0; h < mHistSizeNum; h++) {
                mP1.x = mP2.x = offset + (c * (mHistSizeNum + 10) + h) * thikness;
                mP1.y = sizeRgba.height - 1;
                mP2.y = mP1.y - 2 - (int) mBuff[h];
                Core.line(rgba, mP1, mP2, mColorsRGB[c], thikness);
            }
        }

        return rgba;
    }

    /**
     * 比较来个矩阵的相似度
     *
     * @param srcMat
     * @param desMat
     */
    public void comPareHist(Mat srcMat, Mat desMat) {

        srcMat.convertTo(srcMat, CvType.CV_32F);
        desMat.convertTo(desMat, CvType.CV_32F);
        double target = Imgproc.compareHist(srcMat, desMat, Imgproc.CV_COMP_CORREL);
        Log.e("TAG", "相似度 ：   ==" + target);
        Toast.makeText(this, "相似度 ：   ==" + target, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 通过OpenCV引擎服务加载并初始化OpenCV类库，所谓OpenCV引擎服务即是
        // OpenCV_2.4.9.2_Manager_2.4_*.apk程序包，存在于OpenCV安装包的apk目录中
//        if (OpenCVLoader.initDebug()){
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this,
//                    callback);
//        }
    }


}