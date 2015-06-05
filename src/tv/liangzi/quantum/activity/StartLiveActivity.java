package tv.liangzi.quantum.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.IOException;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.adapter.ShowLiveActivityAdapter;
import tv.liangzi.quantum.base.BaseActivity;

public class StartLiveActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback, OnClickListener, TextWatcher{
	private ListView  mListview;
	private String videoURL;
	private ImageView etDiscuss;
    private String TAG="SHOWLIVING";
    private RelativeLayout rl;
    private EditText removeET;
//    private ImageView imLove;
    private ImageView imShare;
    private ImageView imSwitch;

    private int temp;
    int time=0;
    private KeyboardLayout mRoot;
    public static final  int MESSAGE_LISTVIEW=0;
    private int cameraNum=0;

    private int mLoginBottom;
    private static final int KEYBOARD_SHOW = 0X10;
    private static final int KEYBOARD_HIDE = 0X20;
    private boolean mGetBottom = true;


    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case KEYBOARD_HIDE:
                	mListview.setVisibility(View.VISIBLE);
                	removeET.setVisibility(View.GONE);
//                	imLove.setVisibility(View.VISIBLE);
                	imShare.setVisibility(View.VISIBLE);
                	etDiscuss.setVisibility(View.VISIBLE);
//                	mRoot.setPadding(0, 0, 0, 0);
                    break;

                case KEYBOARD_SHOW:
                    int mRootBottom = mRoot.getBottom();
                    mListview.setVisibility(View.GONE);
                    removeET.setVisibility(View.VISIBLE);
//                    imLove.setVisibility(View.GONE);
                	imShare.setVisibility(View.GONE);
                	etDiscuss.setVisibility(View.GONE);
                    Log.d(TAG, "the mLoginBottom is  " + mLoginBottom);
//                    mRoot.setPadding(0, mRootBottom - mLoginBottom, 0, 0);
                    break;

                default:
                    break;
            }
        }

    };
	@Override
	public void setContentView() {
		// TODO Auto-generated method stub

        setContentView(R.layout.activity_sendlive);
//        getWindow().setBackgroundDrawableResource(R.drawable.test_live) ;
	}

	@Override
	public void initViews() {
        initSurfaceView();
        ImageView imHead=(ImageView) findViewById(R.id.im_live_head);
//        TextView tv= (TextView) findViewById(R.id.title_tv);
//        tv.setText("Living Now");
        imHead.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent=new Intent(StartLiveActivity.this,UserInfoActivity.class);
                startActivity(intent);
            }
        });
		// TODO Auto-generated method stub
		mListview=(ListView) findViewById(R.id.lv_live_listview);
        imSwitch= (ImageView) findViewById(R.id.im_live_switch);
		imShare=(ImageView) findViewById(R.id.im_live_share);
        etDiscuss=(ImageView)findViewById(R.id.ibtn_disscuss);
        etDiscuss.setOnClickListener(this);
		LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//		rlView.setLayoutManager(linearLayoutManager);
		removeET=(EditText) findViewById(R.id.et_remove_disscuss);
//
        mRoot= (KeyboardLayout) findViewById(R.id.rl_root);
        mRoot.setOnSizeChangedListener(new KeyboardLayout.onSizeChangedListener() {
            @Override
            public void onChanged(boolean showKeyboard) {
            	if (showKeyboard) {
                    mHandler.sendMessage(mHandler.obtainMessage(KEYBOARD_SHOW));
                    Log.d(TAG, "show keyboard");
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(KEYBOARD_HIDE));
                }


            }
        });
        mRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                // TODO Auto-generated method stub
                if (mGetBottom) {
                    mLoginBottom = etDiscuss.getBottom();//获取登录按钮的位置信息。
                }
                mGetBottom = false;
                return true;
            }
        });

      /*  rl.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){
                    @Override
                    public void onGlobalLayout()
                    {
                    	
                    	int[] location = new int[2];  
                    	removeET.getLocationOnScreen(location);  
                        int x = location[0];  
                        int y = location[1];  
                        int heightDiff = rl.getRootView().getHeight() - removeET.getHeight();
                        Log.v(TAG, "detailMainRL.getRootView().getHeight() = " + y );
                        Log.v(TAG,"第一次y值 " +temp );
                      if (time!=0) {
                        if (temp==y)
                        { // 说明键盘是没弹出状态
                            Log.v(TAG, "键盘没弹起状态");
//                            Message msg=new Message();
//                            mHandler.sendMessage(msg);
//                            try {
//                                Thread.sleep(500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            mListview.setVisibility(View.VISIBLE);
                        } else if(temp>y){
                            Log.v(TAG, "键盘弹起状态");
                            mListview.setVisibility(View.GONE);

                        }
                        
                      }
                      time++;
                    }
                });*/

        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int offset = mRoot.getRootView().getHeight() - mRoot.getHeight();
                //根据视图的偏移值来判断键盘是否显示
//                if (offset > 300) {
//                    Log.v(TAG, "键盘弹起状态-----------");
//                    mHandler.sendMessage(mHandler.obtainMessage(KEYBOARD_SHOW));
//                } else {
//                    Log.v(TAG, "。。。。。。。键盘收起状态");
//                    mHandler.sendMessage(mHandler.obtainMessage(KEYBOARD_HIDE));
//                }

            }
        });


    }

    private void initSurfaceView() {
        surfaceView=(SurfaceView) findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    @Override
	public void initListeners() {
		// TODO Auto-generated method stub
//		playButton.setOnClickListener(this);
//		etDiscuss.addTextChangedListener(this);
        imSwitch.setOnClickListener(this);
		
	}

	@Override
	public void initData() {
        // TODO Auto-generated method stub
//		videoURL="rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";
        videoURL="http://hot.vrs.sohu.com/ipad2025214_4639791893179_5236535.m3u8?plat=17";
        ShowLiveActivityAdapter adapter=new ShowLiveActivityAdapter(StartLiveActivity.this, null);
        mListview.setAdapter(adapter);
//        Uri uri = Uri.parse(videoURL);
//        videoView.setVideoURI(uri);
//        videoView.start();
		 
	}
@Override
public void onWindowFocusChanged(boolean hasFocus) {
	// TODO Auto-generated method stub
	int[] location = new int[2];  
	removeET.getLocationOnScreen(location);  
    temp=location[1]; 
    Log.v(TAG, temp+"temp 坐标。。。。。。。。。。。");
	super.onWindowFocusChanged(hasFocus);
}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
            case R.id.ibtn_disscuss:
            	 Log.e(TAG, "评论按钮被点击。。。。。。。。。。。");
            	removeET.setVisibility(View.VISIBLE);
            	removeET.setFocusable(true);
            	removeET.setFocusableInTouchMode(true);
            	removeET.requestFocus();
            	removeET.findFocus();
//               mListview.setVisibility(View.GONE);
               InputMethodManager imm = ( InputMethodManager ) removeET.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
               imm.showSoftInput(removeET,InputMethodManager.SHOW_FORCED);    
               Log.v(TAG, "anjiandianji");

            case  R.id.im_live_switch:
//                releaseCamera();

                //切换前后摄像头
                int cameraCount = 0;
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

                for(int i = 0; i < cameraCount; i++  ) {
                    Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                    if(cameraNum == 1) {
                        //现在是后置，变更为前置
                        if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                            mCamera.stopPreview();//停掉原来摄像头的预览
                            mCamera.release();//释放资源
                            mCamera = null;//取消原来摄像头
                            mCamera = Camera.open(i);//打开当前选中的摄像头
                            try {
                                mCamera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            mCamera.startPreview();//开始预览
                            cameraNum = 0;
                            break;
                        }
                    } else {
                        //现在是前置， 变更为后置
                        if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                            mCamera.stopPreview();//停掉原来摄像头的预览
                            mCamera.release();//释放资源
                            mCamera = null;//取消原来摄像头
                            mCamera = Camera.open(i);//打开当前选中的摄像头
                            try {
                                mCamera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            mCamera.startPreview();//开始预览
                            cameraNum = 1;
                            break;
                        }
                    }

                }

            default:
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
//        mListview.setVisibility(View.GONE);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera = Camera.open(0);
            try {
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
                mCamera.setPreviewDisplay(holder);
                mCamera.setPreviewCallback(this);
            } catch (IOException e) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }



/**


 * 释放mCamera


 */

    private void releaseCamera() {

        if (mCamera != null) {

            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();// 停掉原来摄像头的预览
            mCamera.release();
            mCamera = null;


        }


    }
}


