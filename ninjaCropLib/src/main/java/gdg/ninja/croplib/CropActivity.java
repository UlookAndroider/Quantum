package gdg.ninja.croplib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import gdg.ninja.croplib.Crop.Extra;
import gdg.ninja.croplib.utils.CropView;
import gdg.ninja.croplib.utils.FileUtils;

public class CropActivity extends Activity implements OnClickListener{
	
	private TextView btnCrop;
	private TextView btnCancle;
	private Button btnRotateLeft;
	private Button btnRotateRight;
	private CropView mCropView;
	
	private Uri mSourceUri;
	private Uri mOutputUri;
	private Activity mActivity;
	
	private int mMaxX;
	private int mMaxY;
	
	private int mQuality;

	private String mSourcePath;
	private String mOutputPath;

	@Override
	protected void onCreate(Bundle arg0){
		super.onCreate(arg0);
		setContentView(R.layout.ac_crop_image);
		
		mActivity =CropActivity.this;

		initViews();
		initData();
	}
	
	/* Initiate data from received Intent */
	private void initData(){
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if(extras != null){
			mMaxX = extras.getInt(Crop.Extra.MAX_X, 1024);
			mMaxY = extras.getInt(Crop.Extra.MAX_Y, 1024);
			mQuality = extras.getInt(Crop.Extra.OUTPUT_QUALITY, 95);
			
			mSourceUri = extras.getParcelable(Extra.IMAGE_SOURCE);
			mOutputUri = extras.getParcelable(Extra.IMAGE_OUTPUT);
			
			mSourcePath = FileUtils.getPath(mActivity, mSourceUri);

			if(mOutputUri == null) mOutputUri = mSourceUri;
			
			mOutputPath = FileUtils.getPath(mActivity, mOutputUri);
		}
		
		// Resize image to mMaxY width and mMaxY height
		// ParcelFileDescriptor imageFileDescriptor;
		
		try{
			// imageFileDescriptor = mActivity.getContentResolver()
			// .openFileDescriptor(mSourceUri, "r");
//			Bitmap bmp = ImageResizer.decodeSampledBitmapFromFile(mSourcePath,
//					mMaxX, mMaxY);
//
//			// Set the new bitmap to mCropView
//			mCropView.setImageBitmap(bmp);

			/**
			 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
			 */
			int degree = readPictureDegree(mSourcePath);

			BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
			opts.inSampleSize=2;
			Bitmap cbitmap=BitmapFactory.decodeFile(mSourcePath, opts);
			/**
			 * 把图片旋转为正的方向
			 */
			Bitmap newbitmap = rotaingImageView(degree, cbitmap);
//				iv.setImageBitmap(newbitmap);
			mCropView.setImageBitmap(newbitmap);
		}catch(Exception e){
			finishOnFail();
		}
	}

	private void initViews(){
		btnCrop = (TextView) findViewById(R.id.btn_create);
		btnCancle = (TextView) findViewById(R.id.btn_cancle);
		mCropView = (CropView) findViewById(R.id.img_quest_hint);
		btnCancle.setOnClickListener(this);
		btnCrop.setOnClickListener(this);
	}
	
	private void finishOnFail(){
		setResult(RESULT_CANCELED);
		finish();
	}
	
	/* Write the cropped image to file at mOutputUri */
	private void writeImageToFile(){
		Bitmap resultBitmap = mCropView.getCroppedImage();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		resultBitmap.compress(Bitmap.CompressFormat.JPEG, mQuality, bytes);
		resultBitmap.recycle();
		
		try{
			FileOutputStream fos = new FileOutputStream(mOutputPath);
			fos.write(bytes.toByteArray());
			fos.close();
		}catch(IOException e){
			finishOnFail();
		}
	}

	@Override
	public void onClick(View btn){
		int btn_id = btn.getId();
		if(btn_id == R.id.btn_create){
			writeImageToFile();
			Intent resultIntent = new Intent();
			resultIntent.setData(mOutputUri);
			setResult(RESULT_OK, resultIntent);
			finish();
		}
		else if(btn_id == R.id.btn_cancle){
			finishOnFail();
		}

	}
	/**
	 * 工具类
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree  = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	/*
        * 旋转图片
        * @param angle
        * @param bitmap
        * @return Bitmap
        */
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
		//旋转图片 动作
		Matrix matrix = new Matrix();;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
}

