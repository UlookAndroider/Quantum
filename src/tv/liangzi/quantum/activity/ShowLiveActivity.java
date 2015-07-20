package tv.liangzi.quantum.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMConnectionListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingSetting;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.widget.AspectFrameLayout;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.umeng.socialize.controller.UMSocialService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.adapter.ShowLiveActivityAdapter;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.Live;
import tv.liangzi.quantum.bean.Stream;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import tv.liangzi.quantum.view.FavorLayout;
import tv.liangzi.quantum.view.LoadingDialog;
import tv.liangzi.quantum.view.SharePopupWindow;
import tv.liangzi.quantum.view.TipsToast;

public class ShowLiveActivity extends BaseActivity implements CameraStreamingManager.StreamingStateListener,
        OnClickListener,
        ConnectionEventListener,
        AbsListView.OnScrollListener,
        EditText.OnEditorActionListener,
        EMEventListener,
        ShowLiveActivityAdapter.OnReceiverListener
{
    private ListView mListview;
    private String videoURL;
//    private RecyclerView rlView;
    private RelativeLayout rl;
    private EditText removeET;
    private ImageView imSwitch;
    private ImageView imShare;
    ShowLiveActivityAdapter adapter;
    private static TipsToast tipsToast;
    private LoadingDialog dialog;
    public static final int MESSAGE_LISTVIEW = 0;
    private ImageView imHead;
    private TextView followImage;
    private static final int KEYBOARD_SHOW = 0X10;
    private static final int KEYBOARD_HIDE = 0X20;
    private static final int ROOM_ADD = 0;
    private static final int ROOM_DELETE = 1;
    private static final int START_STREAM = 0X90;
    private static final int LIVE_STATE = 0X30;
    private static final int TIMER_START = 0X40;
    private String roomId;
    private TextView themeName;
    private String nikeName;
    private String photo;
    private String userid;
    private  String liveId;
    private String shareUrl;
    private String rtmpUrl;
    private int audiences;
    private int loves;
    EMConversation conversation;
    private String EmuserId;
    private UMSocialService mController = null;
    private String MyPhoto;
    private String MyUserId;
    private String accessToken;
    private TextView nickName;
    AspectFrameLayout afl;
    GLSurfaceView glSurfaceView;
    private TextView audienceCount;
    private TextView lovers;
    private FavorLayout favorlayout;
    private Live  living;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private InputFilter filter;
    private TimerTask  mTimerTask;
    private Timer mTimer;
    private  int state=3;//直播状态码
    private String TAG="showLivingActivity";
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    CameraStreamingManager mCameraStreamingManager;
    private int screenWidth;
    private int screenHeight;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case KEYBOARD_HIDE:
                    mListview.setVisibility(View.VISIBLE);
                    imSwitch.setVisibility(View.VISIBLE);
                    imShare.setVisibility(View.VISIBLE);
                    break;

                case KEYBOARD_SHOW:
                    mListview.setVisibility(View.GONE);
                    imSwitch.setVisibility(View.GONE);
                    imShare.setVisibility(View.GONE);
                    break;

                case START_STREAM:
                    Thread thread=new Thread(new getLiveThread());
                    thread.start();
                    mCameraStreamingManager.startStreaming();
//                    onChatroomViewCreation();
                    StartTimer();
                    break;
                case TIMER_START:
                    int num=msg.arg1;
                    Log.e(TAG,"轮询开始"+"num="+num);
                    break;
                case ROOM_ADD:
                    audienceCount.setText(++audiences+"");
                    break;
                case ROOM_DELETE:
                    if (audiences>0)
                    audienceCount.setText(--audiences+"");
                    break;
                case LIVE_STATE:
                    Log.e(TAG,"LIVE_STATE 查询成功");
                    break;
                case 2:
//                    user.setIsFollow(true);
//					peopleAdapter.notifyDataSetChanged();
                    followImage.setVisibility(View.GONE);
                    Toast.makeText(ShowLiveActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    audienceCount.setText(++audiences+"");
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void setContentView() {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_showlive);
//        View main = getLayoutInflater().from(this).inflate(R.layout.activity_showlive, null);
//        main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        // 隐藏标题栏

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getScreenSize(ShowLiveActivity.this);
    }
    public void getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }
    @Override
    public void initViews() {

        imHead = (ImageView) findViewById(R.id.im_live_head);
        audienceCount = (TextView) findViewById(R.id.follow_count);
        lovers = (TextView) findViewById(R.id.lovers_count);
        themeName= (TextView) findViewById(R.id.tv_theme_name);
        // TODO Auto-generated method stub
        nickName = (TextView) findViewById(R.id.tv_live_id);
        mListview = (ListView) findViewById(R.id.lv_live_listview);
        afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);
//        rlView = (RecyclerView) findViewById(R.id.recyclerView);
        imSwitch = (ImageView) findViewById(R.id.im_live_switch);
        imShare = (ImageView) findViewById(R.id.im_live_share);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        rlView.setLayoutManager(linearLayoutManager);
        removeET = (EditText) findViewById(R.id.et_remove_disscuss);
        followImage = (TextView) findViewById(R.id.tv_live_follow);
        favorlayout= (FavorLayout) findViewById(R.id.favorlayout);
    }

    @Override
    public void initData() {
        MyPhoto= (String) SharedPreferencesUtils.getParam(ShowLiveActivity.this,"userInfo", "photo", "");
        MyUserId=(String) SharedPreferencesUtils.getParam(ShowLiveActivity.this, "userInfo","userId", "");
        accessToken=(String) SharedPreferencesUtils.getParam(ShowLiveActivity.this, "userInfo","accessToken", "");
        Intent intent = getIntent();
        living = intent.getParcelableExtra("Living");
        roomId=living.getChatroomId();
        rtmpUrl = living.getRtmpPlayUrl();
        loves=living.getLikes();
       String rtmpPubUrl = living.getRtmpPublishUrl();
        Stream streamto=living.getStream();
        liveId = living.getLiveId()+"";
        userid = living.getUserId()+"";
        nikeName = living.getNickName();
        photo=living.getPhoto();
        audiences=living.getAudiences();
        shareUrl = living.getShareUrl();

        onChatroomViewCreation();

        // TODO Auto-generated method stub
        String title=living.getTitle();
        themeName.setText(title);
        lovers.setText(String.valueOf(loves));
        nickName.setText(nikeName);
        audienceCount.setText(audiences+"");
        imageLoader.displayImage(photo, imHead, options, animateFirstListener);
//            String publishHost = "pub.z1.glb.pili.qiniup.com";         // such as "f9zdwh.pub.z1.pili.qiniup.com"
//            String streamId = "z1.liangzitest.livetest01";               // such as "z1.live.558cf018e3ba570400000010"
//            String publishKey = "3280ab54";           // such as "c4da83f14319d349"
//            String publishSecurity = "static"; // such as "dynamic" or "static", "dynamic" is recommended
//            String publishHost = "ymf22t.pub.z1.pili.qiniup.com";
//            String streamId = "z1.liangzitv."+rtmpPubUrl.substring(rtmpPubUrl.lastIndexOf("/")+1,rtmpPubUrl.lastIndexOf("?"));               // such as "z1.live.558cf018e3ba570400000010"
//            String publishKey = rtmpPubUrl.substring(rtmpPubUrl.lastIndexOf("=")+1);           // such as "c4da83f14319d349"
//            String publishSecurity = "static";
//            StreamingProfile.Stream stream = new StreamingProfile.Stream(publishHost, streamId, publishKey, publishSecurity);
//            StreamingProfile profile = new StreamingProfile();
//            profile.setQuality(StreamingProfile.QUALITY_MEDIUM1)
//                    .setStream(stream);
//
//            CameraStreamingSetting setting = new CameraStreamingSetting();
//            setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
//                    .setStreamingProfile(profile)
//                    .setCameraPreviewSize(2560, 1440);
           Gson gson=new Gson();
        JSONObject obj = null;
        String streamJson=gson.toJson(streamto);
        try {
             obj=new JSONObject(streamJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StreamingProfile.Stream stream = new StreamingProfile.Stream(obj);

        StreamingProfile profile = new StreamingProfile();
        profile.setQuality(StreamingProfile.QUALITY_MEDIUM3)
                .setStream(stream);

        CameraStreamingSetting setting = new CameraStreamingSetting();
        setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setContinuousFocusModeEnabled(true)
                .setStreamingProfile(profile)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);

        mCameraStreamingManager = new CameraStreamingManager(this,  afl, glSurfaceView);
        mCameraStreamingManager.onPrepare(setting);
        mCameraStreamingManager.setStreamingStateListener(this);
//       videoView.setVideoPath("rtmp://live.z1.glb.pili.qiniucdn.com/liangzitest/livetest01");
    }


    private void EditTextFilter() {
        final int maxLen = 100;
        filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
                int dindex = 0;
                int count = 0;
                while (count <= maxLen && dindex < dest.length()) {
                    char c = dest.charAt(dindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }

                if (count > maxLen) {
                    return dest.subSequence(0, dindex - 1);
                }

                int sindex = 0;
                while (count <= maxLen && sindex < src.length()) {
                    char c = src.charAt(sindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }

                if (count > maxLen) {
                    sindex--;
                }

                return src.subSequence(0, sindex);
            }
        };
        removeET.setFilters(new InputFilter[]{filter});
    }



    @Override
    public void initListeners() {
        EditTextFilter();
        // TODO Auto-generated method stub
        imShare.setOnClickListener(this);
        imHead.setOnClickListener(this);
        followImage.setOnClickListener(this);
        removeET.setOnEditorActionListener(this);
        imSwitch.setOnClickListener(this);
        findViewById(R.id.tv_close).setOnClickListener(this);
        EMChatManager.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
                Log.e("huanxin","conected");
            }

            @Override
            public void onDisconnected(int i) {
                Log.e("huanxin","onDisconnected");

            }
        });
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});
        EMChat.getInstance().setAppInited();
        //只有注册了广播才能接收到新消息，目前离线消息，在线消息都是走接收消息的广播（离线消息目前无法监听，在登录以后，接收消息广播会执行一次拿到所有的离线消息）
        imageLoader.init(ImageLoaderConfiguration.createDefault(ShowLiveActivity.this));
    }
    /**
     * 初始化缓存设置
     */
    private void initImageLoaderOptions(){
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.index_iv02)			// 设置图片下载期间显示的图片
                .showImageOnLoading(R.drawable.ic_loading)
                .showImageForEmptyUri(R.drawable.default_head)	// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.a)        // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//			.displayer(new Ro undedBitmapDisplayer(20))	// 设置成圆角图片
                .build();
        // 创建配置过得DisplayImageOption对象
    }

    @Override
    public void connectionClosed(ConnectionEvent connectionEvent) {
        Log.e("huanxin","sucess="+connectionEvent.toString());
    }

    @Override
    public void connectionErrorOccurred(ConnectionEvent connectionEvent) {
        Log.e("huanxin","error="+connectionEvent.toString());
    }

    @Override
    public void OnReceive() {
        favorlayout.addFavor();
    }

    @Override
    public void onStateChanged(final int state, Object extra) {
        switch (state) {
            case CameraStreamingManager.STATE.PREPARING:
                break;
            case CameraStreamingManager.STATE.READY:
                // start streaming when READY
//                onShutterButtonClick();
//
                mHandler.sendMessageDelayed(mHandler.obtainMessage(START_STREAM),500);
                break;
            case CameraStreamingManager.STATE.CONNECTING:
                Toast.makeText(ShowLiveActivity.this,"CONNECTING",Toast.LENGTH_SHORT).show();
                break;
            case CameraStreamingManager.STATE.STREAMING:
                Toast.makeText(ShowLiveActivity.this,"STREAMING",Toast.LENGTH_SHORT).show();
                break;
            case CameraStreamingManager.STATE.SHUTDOWN:
                break;
            case CameraStreamingManager.STATE.IOERROR:
                break;
            case CameraStreamingManager.STATE.NETBLOCKING:
                break;
            case CameraStreamingManager.STATE.UNKNOWN:
                break;
            case CameraStreamingManager.STATE.CAMERA_SWITCHED:
//                mShutterButtonPressed = false;
                if (extra != null) {
                    Log.i(TAG, "current camera id:" + (Integer)extra);
                }
                Log.i(TAG, "camera switched");
                break;
            case CameraStreamingManager.STATE.TORCH_INFO:
                if (extra != null) {
                    boolean isSupportedTorch = (Boolean) extra;
                    Log.i(TAG, "isSupportedTorch=" + isSupportedTorch);
                    if (isSupportedTorch) {
//                        mTorchBtn.setVisibility(View.VISIBLE);
                    } else {
//                        mTorchBtn.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    /**
     * 图片加载第一次显示监听器
     * @author Administrator
     *
     */
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // 图片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        mCameraStreamingManager.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        mCameraStreamingManager.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraStreamingManager.onDestroy();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }



//    @Override
//    public void onStateChanged(int i) {
//        Log.e("state",i+"");
//        switch (i){
//            case CameraStreamingManager.STATE.PREPARING:
//                Log.e("state",i+"");
//                break;
//            case CameraStreamingManager.STATE.READY:
//                Log.e("state",i+"");
//
//                Toast.makeText(ShowLiveActivity.this,"READY",Toast.LENGTH_SHORT).show();
//                break;
//            case CameraStreamingManager.STATE.CONNECTING:
//                Toast.makeText(ShowLiveActivity.this,"CONNECTING",Toast.LENGTH_SHORT).show();
//                break;
//            case CameraStreamingManager.STATE.STREAMING:
//
//                Toast.makeText(ShowLiveActivity.this,"STREAMING",Toast.LENGTH_SHORT).show();
//                break;
//            case CameraStreamingManager.STATE.SHUTDOWN:
//                Toast.makeText(ShowLiveActivity.this,"SHUTDOWN",Toast.LENGTH_SHORT).show();
//                break;
//            case CameraStreamingManager.STATE.IOERROR:
//                Toast.makeText(ShowLiveActivity.this,"IOERROR",Toast.LENGTH_SHORT).show();
//                break;
//            case CameraStreamingManager.STATE.NETBLOCKING:
//                Toast.makeText(ShowLiveActivity.this,"NETBLOCKING",Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//    }



    /**
     * 发送按钮
     *
     * @param textView
     * @param i
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEND) {
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        // 在这里编写自己想要实现的功能
        sendText(removeET.getText().toString().trim());
    }
});
        }
        return false;
    }
    /**
     * 发送信息线程
     */
    public class sendThread implements Runnable
    {
        @Override
        public void run()
        {

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("您确定要结束直播么?")
                    .setContentText("确定结束后将不能恢复直播!")
                    .setCancelText("取消")
                    .setConfirmText("确定")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            mCameraStreamingManager.stopStreaming();
                            state=2;
                            Thread upLiveThread=new Thread(new upLiveThread());
                            upLiveThread.start();
                            if (mTimer!=null){
                                mTimer.cancel();
                            }
                            sweetAlertDialog.dismiss();
                            finish();
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.cancel();
                            sDialog.dismiss();
                        }
                    })
                    .show();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ibtn_disscuss:
                Log.e(TAG, "评论按钮被点击");
                removeET.setVisibility(View.VISIBLE);
                removeET.setFocusable(true);
                removeET.setFocusableInTouchMode(true);
                removeET.requestFocus();
                removeET.findFocus();
//               mListview.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) removeET.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(removeET, InputMethodManager.SHOW_FORCED);
                Log.v(TAG, "");
                break;

            case R.id.im_live_share:

                SharePopupWindow share = new SharePopupWindow(ShowLiveActivity.this, mHandler, shareUrl);
//				share.setPlatformActionListener(MainActivity.this);
                share.showShareWindow();
                // 显示窗口 (设置layout在PopupWindow中显示的位置)
                share.showAtLocation(ShowLiveActivity.this.getLayoutInflater().inflate(R.layout.activity_showlive, null),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_close:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("您确定要结束直播么?")
                        .setContentText("确定结束后将不能恢复直播!")
                        .setCancelText("取消")
                        .setConfirmText("确定")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                mCameraStreamingManager.stopStreaming();
                                state=2;
                                Thread upLiveThread=new Thread(new upLiveThread());
                                upLiveThread.start();
                                mTimer.cancel();
                                sendDIYMessage("");
                                sweetAlertDialog.dismiss();
                                finish();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                                sDialog.dismiss();
                            }
                        })
                        .show();
//                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Are you sure?")
//                        .setContentText("Won't be able to recover this file!")
//                        .setConfirmText("Yes,delete it!")
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                sDialog.dismissWithAnimation();
//                            }
//                        })
//                        .show();
//                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("直播")
//                        .setContentText("确定要结束直播么？")
//                        .setConfirmText("我确定!")
//                        .show();
//
                break;
            case R.id.im_live_switch:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mCameraStreamingManager.switchCamera();
//                    }
//                }).start();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mCameraStreamingManager.switchCamera();
                    }
                });

                break;
            case R.id.im_live_head:
                Intent intent = new Intent(ShowLiveActivity.this, UserInfoActivity.class);
                intent.putExtra("living",living);
                startActivity(intent);
                break;
            case R.id.tv_live_follow:
                Thread postThread = new Thread(new postThread());
                postThread.start();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 添加关注接口
     */
    class postThread implements Runnable
    {
        @Override
        public void run()
        {
            String url= MyAapplication.IP+"follow";
            try {
                followPost(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //   备用子线程
            Log.e("log", "发出关注请求请求");

        }
    }
    /**
     * 添加关注
     * @param url
     * @throws IOException
     */
    void followPost(String url) throws IOException {

        RequestBody formBody = new FormEncodingBuilder()
                .add("userId", userid)
                .add("toUserId", MyUserId)
                .add("accessToken", accessToken)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

        public void onChatroomViewCreation(){
//        findViewById(R.id.container_to_group).setVisibility(View.GONE);
            Log.e(TAG, "join room  : " +"roomId="+roomId);
//        final ProgressDialog pd = ProgressDialog.show(this, "", "Joining......");
            EMChatManager.getInstance().joinChatRoom(roomId, new EMValueCallBack<EMChatRoom>() {

                @Override
                public void onSuccess(EMChatRoom value) {
                    EMLog.e(TAG, "joining room : " + "roomId=onSuccess" + roomId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                        pd.dismiss();
                            EMChatRoom room = EMChatManager.getInstance().getChatRoom(roomId);
                            if (room != null) {
//                            ((TextView) findViewById(R.id.name)).setText(room.getName());
                            } else {
//                            ((TextView) findViewById(R.id.name)).setText(roomId);
                            }
                            Log.e(TAG, "join room success : " + room.getName());
                            Message msg=new Message();
                            msg.what=0;
                            mHandler.sendMessage(msg);
                            onConversationInit();
                            onListViewCreation();
                            EMChatManager.getInstance().registerEventListener(
                                    ShowLiveActivity.this,
                                    new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventConversationListChanged,EMNotifierEvent.Event.EventNewCMDMessage,EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
                                            EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck,EMNotifierEvent.Event.EventMessageChanged});
                        }
                    });
                }

                @Override
                public void onError(final int error, String errorMsg) {
                    EMLog.e(TAG, "join room failure : " + error+"errorMsg="+errorMsg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                        pd.dismiss();
                        }
                    });
                    finish();
                }
            });
        }

        private void onListViewCreation() {
            adapter = new ShowLiveActivityAdapter(ShowLiveActivity.this, roomId);
            mListview.setAdapter(adapter);
            adapter.setOnReceiverListener(this);

        }

        protected void onConversationInit() {

            conversation = EMChatManager.getInstance().getConversationByType(roomId, EMConversation.EMConversationType.ChatRoom);

            // 把此会话的未读数置为0
            conversation.markAllMessagesAsRead();

            // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
            // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
            final List<EMMessage> msgs = conversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < 20) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).getMsgId();
                }
//                conversation.loadMoreGroupMsgFromDB(msgId, 20);//pagesize
            }

            // 监听聊天室变化回调
            EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {


                @Override
                public void onChatRoomDestroyed(String roomId, String roomName) {
                    if (roomId.equals(roomId)) {
                        finish();
                    }
                }

                @Override
                public void onMemberJoined(String roomId, String participant) {
                    Message msg=new Message();
                    msg.what=0;
                   mHandler.sendMessage(msg);
                    Log.e(TAG,audiences+1+"");
                }

                @Override
                public void onMemberExited(String roomId, String roomName,
                                           String participant) {
                    Message msg=new Message();
                    msg.what=1;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onMemberKicked(String roomId, String roomName,
                                           String participant) {
                    if (roomId.equals(roomId)) {
                        String curUser = EMChatManager.getInstance().getCurrentUser();
                        if (curUser.equals(participant)) {
                            EMChatManager.getInstance().leaveChatRoom(roomId);
                            finish();
                        }
                    }
                }

            });
        }
    private void sendDIYMessage(String action){
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        CmdMessageBody txtBody = new CmdMessageBody(action);
        message.addBody(txtBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
// 增加自己特定的属性,目前sdk支持int,boolean,String这三种属性，可以设置多个扩展属性
        //这里是扩展自文本消息，如果这个自定义的消息需要用到语音或者图片等，可以扩展自语音、图片消息，亦或是位置消息。

// 增加自己特定的属性,目前sdk支持int,boolean,String这三种属性，可以设置多个扩展属性
        message.setAttribute("function", "hostExit");
//        message.setAttribute("attribute2", true);

        message.setReceipt(roomId);
//        conversation.addMessage(message);
//发送消息
        EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });

    }
    /**
     * 发送文本消息
     *
     * @param content message content
     *                <p>
     *                boolean resend
     */
    private void sendText(String content) {

        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            TextMessageBody txtBody = new TextMessageBody(content);
            message.addBody(txtBody);
            message.setAttribute("nikeName", nikeName);
            message.setAttribute("photo", MyPhoto);
            message.setAttribute("content", content);
            message.setAttribute("funcation", "");
            // 如果是群聊，设置chattype,默认是单聊
            message.setChatType(EMMessage.ChatType.ChatRoom);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(roomId);
            // 把messgage加到conversation中
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.refreshSelectLast();
            removeET.setText("");
            handleTextMessage(message);
            setResult(RESULT_OK);

        }
    }

    /**
     * 文本消息
     *
     * @param message
     * @param
     * @param
     */
    private void handleTextMessage(EMMessage message) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();

        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS: // 发送成功
//					holder.pb.setVisibility(View.GONE);
//					holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
//					holder.pb.setVisibility(View.GONE);
//					holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
//					holder.pb.setVisibility(View.VISIBLE);
//					holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendMsgInBackground(message);

            }
        }
    }

    /**
     * 发送消息
     *
     * @param message
     * @param
     */
    public void sendMsgInBackground(final EMMessage message) {
//		holder.staus_iv.setVisibility(View.GONE);
//		holder.pb.setVisibility(View.VISIBLE);

        final long start = System.currentTimeMillis();
        // 调用sdk发送异步发送方法
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

            @Override
            public void onSuccess() {

                updateSendedView(message);
            }

            @Override
            public void onError(int code, String error) {

                updateSendedView(message);
            }

            @Override
            public void onProgress(int progress, String status) {
            }

        });

    }

    /**
     * 更新ui上消息发送状态
     *
     * @param message
     * @param
     */
    private void updateSendedView(final EMMessage message) {
        ShowLiveActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // send success
                EMLog.d("adapter", "message status : " + message.status);
                if (message.status == EMMessage.Status.SUCCESS) {

                } else if (message.status == EMMessage.Status.FAIL) {
                    Toast.makeText(ShowLiveActivity.this, "发送失败", Toast.LENGTH_SHORT)
                            .show();
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

            @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        EMMessage message;
        switch (emNotifierEvent.getEvent()) {
     
            case EventNewMessage: // 接收新消息
            {
                message = (EMMessage) emNotifierEvent.getData();
                TextMessageBody body = (TextMessageBody) message.getBody();
                body.getMessage();
                message.getStringAttribute("nikeName", null);
                message.getStringAttribute("photo", null);
                message.getStringAttribute("content", null);
                String Mymessage=message.getStringAttribute("funcation", null);

                    conversation.addMessage(message);
                    adapter.refreshSelectLast();

                break;
            }
            case EventDeliveryAck: {//接收已发送回执
                Log.e("show","message=");
                break;
            }

            case EventNewCMDMessage: {//接收透传消息
                EMMessage cmdMewssage = (EMMessage) emNotifierEvent.getData();

                String function=cmdMewssage.getStringAttribute("function","");
                if (function.equals("sendMyLove")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ++loves;
                            lovers.setText(String.valueOf(loves));
                            favorlayout.addFavor();
                        }
                    });

                }
                break;
            }

            case EventReadAck: {//接收已读回执
                Log.e("show","message=");
                break;
            }

            case EventOfflineMessage: {//接收离线消息
                Log.e("show","message=");
                break;
            }

            case EventConversationListChanged: {//通知会话列表通知event注册（在某些特殊情况，SDK去删除会话的时候会收到回调监听）
                Log.e("show","message=");
                break;
            }

            case EventLogout:
                break;
            default:
                Log.e("show","message=");
                break;
        }
    }
    public void StartTimer(){
        if(mTimer == null){
            final Integer mTimerID=0;
            final String url= MyAapplication.IP+"live"+"/"+liveId;
            TimerTask  mTimerTask = new TimerTask(){
                 Integer mTimerID=0;
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = TIMER_START;
                    msg.arg1 =mTimerID;
                    mTimerID++;
                    mHandler.sendMessage(msg);
                    Log.e("timer","time="+mTimerID);
                    try {
                        updataLive(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            };
             mTimer = new Timer();
            //第一个参数为执行的mTimerTask
            //第二个参数为延迟得事件，这里写1000得意思是 mTimerTask将延迟1秒执行
            //第三个参数为多久执行一次，这里写1000 表示没1秒执行一次mTimerTask的Run方法
            mTimer.schedule(mTimerTask, 30000,60000);

        }
    }
    /**
     * 接口
     */
    public class getLiveThread implements Runnable
    {
        @Override
        public void run()
        {
            String url= MyAapplication.IP+"live"+"/"+liveId;
            try {
                getLive(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "获取直播信息请求");
        }
    }
    /**
     *获取直播状态
     * @param url
     * @throws IOException
     */
    public void getLive(String url) throws IOException {
        Request request = new Request.Builder().
                url(url)
                .delete()
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.getMessage();
                Log.e("log", "直播信息请求失败="+e.getMessage().toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    Live liveState = gson.fromJson(response.body().charStream(), new TypeToken<Live>() {
                    }.getType());
                    if (liveState.getResponseCode().equals("201")) {
                        if (liveState!=null) {
                            Message msg = new Message();
                            msg.what = LIVE_STATE;
                            mHandler.sendMessage(msg);
                        } else {
//                            Message msg = new Message();
//                            msg.what = 6;
//
//             mHandler.sendMessage(msg);
                            Log.e(TAG, "201，liveduixiangweikong");
                        }

                    } else if (liveState.getResponseCode().equals("500")) {
                        Log.e("videoInfoActivity", "连接服务器失败");
                    } else {
                        Log.e(TAG, "其它错误");
//                        Message msg = new Message();
//                        msg.what = 10;
//                        msg.obj = liveState.getResponseMsg();
//                        mHandler.sendMessage(msg);
                    }
                }
            }
        });
    }
    /**
     * 更新直播状态
     */
    public class upLiveThread implements Runnable
    {
        @Override
        public void run()
        {
             String url= MyAapplication.IP+"live"+"/"+liveId;
            try {
                updataLive(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "获取直播信息请求");
        }
    }
    /**
     *
     * uodatelivestate
     * @param url
     * @throws IOException
     */
    void updataLive(String url) throws IOException {

        RequestBody formBody = new FormEncodingBuilder()
                .add("userId", MyUserId)
                .add("state", String.valueOf(state))
                .add("chatroomId", roomId)
                .add("accessToken", accessToken)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .put(formBody)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "直播信息请求失败="+e.getMessage().toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    Live liveState = gson.fromJson(response.body().charStream(), new TypeToken<Live>() {
                    }.getType());
                    if (liveState.getResponseCode().equals("200")) {
                        if (liveState!=null) {
                            Message msg = new Message();
                            msg.what = LIVE_STATE;
                            mHandler.sendMessage(msg);
                        } else {
//                            Message msg = new Message();
//                            msg.what = 6;
//                            mHandler.sendMessage(msg);
                            Log.e("livefragment", "....................");
                        }

                    } else if (liveState.getResponseCode().equals("500")) {
                        Log.e("videoInfoActivity", "连接服务器失败");
                    } else {
                        Message msg = new Message();
                        msg.what = 10;
                        msg.obj = liveState.getResponseMsg();
                        mHandler.sendMessage(msg);
                    }
                }else{
                    Message msg = new Message();
                    msg.what = 11;
                    mHandler.sendMessage(msg);
                }

            }
        });
    }
    public ListView getListView() {
        return mListview;
    }
    private void showTips(int iconResId, int msgResId) {
        if (tipsToast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tipsToast.cancel();
            }
        } else {
            tipsToast = TipsToast.makeText(getApplication().getBaseContext(), msgResId, TipsToast.LENGTH_SHORT);
        }
        tipsToast.show();
        tipsToast.setIcon(iconResId);
        tipsToast.setText(msgResId);
    }
    }




