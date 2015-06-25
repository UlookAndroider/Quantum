package tv.liangzi.quantum.fragment;


import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import me.drakeet.library.UIButton;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.activity.TimePickerActivity;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;

public class StartFragment extends BaseFragment implements SurfaceHolder.Callback, PreviewCallback,View.OnClickListener {

	private static final String TAG = "CollectFragment";
	private TextView mTitleTv;
	EditText txttime;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static  final int REQUEST_IMAGE=1;
	private static  final int RESULT_OK=-1;
	private static final int REQUEST_CODE=2;
	
	private Camera mCamera;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private View view;
	private static final int SystemVideoRecord=0;
	public static StartFragment newInstance() {
		StartFragment collectFragment = new StartFragment();
		return collectFragment;
	}

	private String photo;
    //对话框相关

    private TextView schedule;
    private UIButton now;

    private RelativeLayout schduleLayout;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_start, container,
				false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initViews(View view) {
        schedule=(TextView) view.findViewById(R.id.tv_schedule);
        now=(UIButton)view.findViewById(R.id.tv_now);
        schduleLayout= (RelativeLayout) view.findViewById(R.id.rl_send_schedule);
		photo= (String) SharedPreferencesUtils.getParam(getActivity(),"photo","");
            initListener(view);
    }

    private void initListener(View view) {

        schedule.setClickable(true);
        now.setOnClickListener(this);
        schedule.setOnClickListener(this);
		initSurfaceView(view);
    }

    private void initSurfaceView(View view) {
		// TODO Auto-generated method stub
		surfaceView = (SurfaceView) view.findViewById(R.id.surface_record);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public String getFragmentName() {
		return TAG;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera=Camera.open();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Camera.Parameters parameters = mCamera.getParameters();
		   parameters.setRotation(90);
		mCamera.setDisplayOrientation(90);
		mCamera.setParameters(parameters);
		    try {
				mCamera.setPreviewDisplay(holder);
			  } catch (IOException e) {
			      e.printStackTrace();
				mCamera.release();
				mCamera=null;
			    }
		mCamera.startPreview();//开始预览

}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
        if(mCamera!=null) {
        	mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.im_up_couver:
//				Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
//				startActivityForResult(intent, 0x123);
				Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);

// 是否显示调用相机拍照
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

// 最大图片选择数量
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT,1);

// 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);

				startActivityForResult(intent, REQUEST_IMAGE);
                break;
            case R.id.tv_schedule:
                Intent intent1 =new Intent(getActivity(), TimePickerActivity.class);
				intent1.putExtra("living",false);
                startActivity(intent1);
//                schduleLayout.setVisibility(View.GONE);
                break;
            case R.id.tv_now:
				Intent intent2 =new Intent(getActivity(), TimePickerActivity.class);
				intent2.putExtra("living",true);
				startActivity(intent2);
//                Intent intenta =new Intent(getActivity(), StartLiveActivity.class);
//                .startActivity(intenta);
                
                break;
            default:
                break;
        }
    }

    private void releaseCamera() {

        if (mCamera != null) {

            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();// 停掉原来摄像头的预览
            mCamera.release();
            mCamera = null;


        }


    }

	/**
	 * 截图功能
	 * @param absolutePath
	 */
	public void crop(String absolutePath){
		File file=new File(absolutePath);
		Uri uri = Uri.fromFile(file);
		cropImage(uri,800,600,2);
//		Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//		openAlbumIntent.setData(uri);
//		startActivityForResult(openAlbumIntent, REQUEST_CODE);
	}
	//截取图片
	public void cropImage(Uri uri, int outputX, int outputY, int requestCode){
		//裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		//裁剪框的比例，1：1
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 3);
		//裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		//图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, requestCode);
	}
	class CameraThread implements Runnable
	{
		@Override
		public void run()
		{
//				initSurfaceView(view);
			//   备用子线程
			Log.e("log", "发出请求");

		}
	}
}
