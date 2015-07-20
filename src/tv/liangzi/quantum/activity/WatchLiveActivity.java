package tv.liangzi.quantum.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
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
import com.easemob.exceptions.EaseMobException;
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
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.umeng.socialize.controller.UMSocialService;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.adapter.ShowLiveActivityAdapter;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.Live;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import tv.liangzi.quantum.view.FavorLayout;
import tv.liangzi.quantum.view.LoadingDialog;
import tv.liangzi.quantum.view.MediaController;
import tv.liangzi.quantum.view.SharePopupWindow;
import tv.liangzi.quantum.view.TipsToast;

public class WatchLiveActivity extends BaseActivity implements
        OnClickListener,
        AbsListView.OnScrollListener,
        EditText.OnEditorActionListener,
        EMEventListener
        ,IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnPreparedListener

{
    private com.pili.pldroid.player.widget.VideoView videoView;
    private ListView mListview;
    //	private ImageButton playButton;
    private String videoURL;
    //	private ImageView etDiscuss;
//    private RecyclerView rlView;
    private String TAG = "watchLiveActivity";
    private RelativeLayout rl;
    private EditText removeET;
    private ImageView imLove;
    ImageView image;
    private ImageView imShare;
    ShowLiveActivityAdapter adapter;
    private static TipsToast tipsToast;
    private LoadingDialog dialog;
    //private KeyboardLayout mRoot;
    public static final int MESSAGE_LISTVIEW = 0;
    private ImageView imHead;
    private TextView followImage;
    private static final int KEYBOARD_SHOW = 0X10;
    private static final int KEYBOARD_HIDE = 0X20;
    private static final int ROOM_ADD = 0;
    private static final int ROOM_DELETE = 1;
    private static final int LIVE_STATE = 3;
    private static final int IS_FOLLOW = 4;
    private static final int EXIT_HOST = 6;
    private static final int START_STREAM = 0X90;
    private static final int MSG_ANIMATION_FINISHED=5;
    private boolean mGetBottom = true;
    private String roomId;
    private String nikeName;
    private String photo;
    private String userid;
    private String shareUrl;
    private String rtmpUrl;
    private int audiences;
    private int loves;
    private int liveId;
    EMConversation conversation;
    private MediaController mMediaController;
    private String EmuserId;
    private UMSocialService mController = null;
    private int mDuration;
    private String MyPhoto;
    private String MynickName;
    private String MyUserId;
    private String accessToken;
    private TextView nickName;
    private TextView audienceCount;
    private TextView lovers;
    private TextView themeName;
    private Live  living;
    private PeopleDetails person;
    private AnimationDrawable mAnimationDrawable;
    private static final String ACTION = "com.liangzi.SENDBROADCAST";
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private ImageButton mBtnStartAnim;
    private FavorLayout favorlayout;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case KEYBOARD_HIDE:
                    mListview.setVisibility(View.VISIBLE);
//                	removeET.setVisibility(View.GONE);
                    imLove.setVisibility(View.VISIBLE);
                    imShare.setVisibility(View.VISIBLE);
//                	etDiscuss.setVisibility(View.VISIBLE);
//                	mRoot.setPadding(0, 0, 0, 0);
                    break;

                case KEYBOARD_SHOW:
//                    int mRootBottom = mRoot.getBottom();
                    mListview.setVisibility(View.GONE);
//                    removeET.setVisibility(View.VISIBLE);
                    imLove.setVisibility(View.GONE);
                    imShare.setVisibility(View.GONE);
//                	etDiscuss.setVisibility(View.GONE);
//                    mRoot.setPadding(0, mRootBottom - mLoginBottom, 0, 0);
                    break;

                case ROOM_ADD:
                    audienceCount.setText(++audiences+"");
                    break;
                case ROOM_DELETE:
                    if (audiences>0)
                        audienceCount.setText(--audiences+"");
                    break;
                case 2:
                    followImage.setVisibility(View.GONE);
                    Toast.makeText(WatchLiveActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    audienceCount.setText(++audiences+"");
                    break;
                case LIVE_STATE:
                    int state=msg.arg1;
                    if (state==0||state==2||state==4||state==5){
                        Toast.makeText(WatchLiveActivity.this,"state="+state,Toast.LENGTH_SHORT).show();
                        Toast.makeText(WatchLiveActivity.this,"直播间已过期",Toast.LENGTH_SHORT).show();
                        new SweetAlertDialog(WatchLiveActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("直播状态")
                                .setContentText("直播已经结束了!")
                                .setConfirmText("确定")
                                .show();
                        return;
                    }else if(state==1||state==3){
                        living=(Live) msg.obj;
                        Thread userThread = new Thread(new getUserThread());
                        userThread.start();
                        lovers.setText(String.valueOf(loves));
                    }
                    break;
                case IS_FOLLOW:
                    if (person.isFollow()){
                        followImage.setVisibility(View.GONE);
                    }else
                        followImage.setVisibility(View.VISIBLE);

                    break;
                case MSG_ANIMATION_FINISHED:
                    mAnimationDrawable.stop();
                    image.setVisibility(View.GONE);
                    break;
                case EXIT_HOST:
                    new SweetAlertDialog(WatchLiveActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("直播已结束")
                            .setContentText("主播已经结束了直播，再看点别的吧！")
                            .setConfirmText("确 定")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
//                                    sDialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void setContentView() {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_watchlive);
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
        videoView = (com.pili.pldroid.player.widget.VideoView) findViewById(R.id.videoview);
        videoView.setOnPreparedListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnErrorListener(this);
//        rlView = (RecyclerView) findViewById(R.id.recyclerView);
        imShare = (ImageView) findViewById(R.id.im_live_share);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        rlView.setLayoutManager(linearLayoutManager);
        removeET = (EditText) findViewById(R.id.et_remove_disscuss);
        followImage = (TextView) findViewById(R.id.tv_live_follow);
        image= (ImageView) findViewById(R.id.logo_frame);
//		image.setBackgroundResource(R.drawable.logo_round);
//        Animation anim = AnimationUtils.loadAnimation();
//        mAnimationDrawable = (AnimationDrawable) image.getBackground();
//         playAnimation();
        mmswoon();
        mBtnStartAnim= (ImageButton) findViewById(R.id.btn_love);
        favorlayout= (FavorLayout) findViewById(R.id.favorlayout);
        mBtnStartAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorlayout.addFavor();
                sendDIYMessage("sendMyLove");
                ++loves;
                lovers.setText(String.valueOf(loves));
                final String url= MyAapplication.IP+"liveLike";
                try {
                    addLovePost(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


//        RecycleAdapter adapter = new RecycleAdapter(this, null);//暂时传null
//        rlView.setAdapter(adapter);

    }

    @Override
    public void initData() {
        MyPhoto= (String) SharedPreferencesUtils.getParam(WatchLiveActivity.this, "userInfo", "photo", "");
        MyUserId=(String) SharedPreferencesUtils.getParam(WatchLiveActivity.this, "userInfo", "userId", "");
        MynickName=(String) SharedPreferencesUtils.getParam(WatchLiveActivity.this, "userInfo", "nickName", "");
        accessToken=(String) SharedPreferencesUtils.getParam(WatchLiveActivity.this, "userInfo", "accessToken", "");
        Intent intent = getIntent();
        living = intent.getParcelableExtra("Living");
        roomId=living.getChatroomId();
        rtmpUrl = living.getRtmpPlayUrl();
        userid = living.getUserId()+"";
        nikeName = living.getNickName();
        photo=living.getPhoto();
        String title=living.getTitle();
        themeName.setText(title);
        audiences=living.getAudiences();
        loves=living.getLikes();
        liveId=living.getLiveId();
        shareUrl = living.getShareUrl();

        // TODO Auto-generated method stub
        getStateThread stateThread=new getStateThread();
        stateThread.setLiveId(liveId);
        Thread thread=new Thread(stateThread);
        thread.start();
        themeName.setText(title);
        nickName.setText(nikeName);
        audienceCount.setText(audiences + "");
        Log.e("room","onChatroomViewCreation之前 roomid="+roomId);
        imageLoader.displayImage(photo, imHead, options, animateFirstListener);
        videoView.setVideoPath(rtmpUrl);
        onChatroomViewCreation();
    }


    public void mmswoon(){

        image.setBackgroundResource(R.drawable.logo_round);

        mAnimationDrawable = (AnimationDrawable)image.getBackground();

        mAnimationDrawable.start();



        int duration = 0;

        for(int i=0;i<mAnimationDrawable.getNumberOfFrames();i++){

            duration += mAnimationDrawable.getDuration(i);

        }

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {

            public void run() {

                //此处调用第二个动画播放方法
                image.setBackgroundResource(R.drawable.end_round);

                AnimationDrawable animationDrawable = (AnimationDrawable)image.getBackground();

                animationDrawable.start();
            }

        }, duration);



    }



    @Override
    public void initListeners() {
        // TODO Auto-generated method stub
        imShare.setOnClickListener(this);
        imHead.setOnClickListener(this);
        followImage.setOnClickListener(this);
        removeET.setOnEditorActionListener(this);
        findViewById(R.id.tv_close).setOnClickListener(this);
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});
        EMChat.getInstance().setAppInited();

        //只有注册了广播才能接收到新消息，目前离线消息，在线消息都是走接收消息的广播（离线消息目前无法监听，在登录以后，接收消息广播会执行一次拿到所有的离线消息）
        imageLoader.init(ImageLoaderConfiguration.createDefault(WatchLiveActivity.this));
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("您确定要离开么?")
                    .setCancelText("取消")
                    .setConfirmText("确定")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.cancel();
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
    /**
     * 初始化缓存设置
     */
    private void initImageLoaderOptions(){
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.index_iv02)			// 设置图片下载期间显示的图片
                .showImageOnLoading(R.drawable.ic_loading)
                .showImageForEmptyUri(R.drawable.default_head)	// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.a)		// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//			.displayer(new Ro undedBitmapDisplayer(20))	// 设置成圆角图片
                .build();
        // 创建配置过得DisplayImageOption对象

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

    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    /**
     * 播放监听
     * @param iMediaPlayer
     */
    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        videoView.stopPlayback();
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        videoView.setVideoPath(rtmpUrl);
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int i1) {
        Log.e(TAG,what+"");
        mAnimationDrawable.stop();
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        Message msgdel=new Message();
        msgdel.what=MSG_ANIMATION_FINISHED;
        mHandler.sendMessageDelayed(msgdel,1000);

        Log.e(TAG,"onprepare");
    }




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
            // 在这里编写自己想要实现的功能
            sendText(removeET.getText().toString().trim(),"");
        }
        return false;
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

                SharePopupWindow share = new SharePopupWindow(WatchLiveActivity.this, mHandler, shareUrl);
//				share.setPlatformActionListener(MainActivity.this);
                share.showShareWindow();
                // 显示窗口 (设置layout在PopupWindow中显示的位置)
                share.showAtLocation(WatchLiveActivity.this.getLayoutInflater().inflate(R.layout.activity_showlive, null),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_close:
//                mCameraStreamingManager.startStreaming();
//videoView.stopPlayback();
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("您确定要离开么?")
                        .setCancelText("取消")
                        .setConfirmText("确定")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
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
                break;
            case R.id.im_live_head:
                Intent intent = new Intent(WatchLiveActivity.this, UserInfoActivity.class);
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
    /**
     * 点赞的接口
     * @param url
     * @throws IOException
     */
    void addLovePost(String url) throws IOException {

        RequestBody formBody = new FormEncodingBuilder()
                .add("userId", MyUserId)
                .add("liveId", String.valueOf(liveId))
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
                Log.i(TAG, "点赞成功");
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
                EMLog.e(TAG, "joining room : " +"roomId="+roomId);
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
                                WatchLiveActivity.this,
                                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewCMDMessage,EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
                                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});
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
        adapter = new ShowLiveActivityAdapter(WatchLiveActivity.this, roomId);
        mListview.setAdapter(adapter);
//        adapter.refreshSelectLast();

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
            conversation.loadMoreGroupMsgFromDB(msgId, 20);//pagesize
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
    /**
     * 发送文本消息
     *
     * @param content message content
     *                <p>
     *                boolean resend
     */
    private void sendText(String content,String funcation) {

        if (content!=null) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            TextMessageBody txtBody = new TextMessageBody(content);
            message.addBody(txtBody);
            message.setAttribute("nikeName", MynickName);
            message.setAttribute("photo", MyPhoto);
            message.setAttribute("content", content);
            message.setAttribute("funcation", funcation);
            // 如果是群聊，设置chattype,默认是单聊
            message.setChatType(EMMessage.ChatType.ChatRoom);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(roomId);
            // 把messgage加到conversation中
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.refreshSelectLast();
//            setListViewHeightBasedOnChildren(mListview);
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
//                    Log.e("adapter", "message=" + ((TextMessageBody) message.getBody()).getMessage());
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
        WatchLiveActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // send success
//                EMLog.d("adapter", "message status : " + message.status);
                if (message.status == EMMessage.Status.SUCCESS) {
                } else if (message.status == EMMessage.Status.FAIL) {
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
//        Log.e("adapter","监听");
        EMMessage message;
        switch (emNotifierEvent.getEvent()) {

            case EventNewMessage: // 接收新消息
            {
                message = (EMMessage) emNotifierEvent.getData();
                Log.e("show","newMessage");
                TextMessageBody body = (TextMessageBody) message.getBody();
                body.getMessage();
                try {
                    String content =message.getStringAttribute("content");
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                //获取自定义的属性，第2个参数为返回的默认值
                message.getStringAttribute("nikeName", null);
                message.getStringAttribute("photo", null);
                message.getStringAttribute("content", null);
// 把messgage加到conversation中
                conversation.addMessage(message);
                // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
                adapter.refreshSelectLast();
//                setListViewHeightBasedOnChildren(mListview);
                //如果是当前会话的消息，刷新聊天页面
//                            refreshUIWithNewMessage();
                //声音和震动提示有新消息
//                            HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                break;
            }
            case EventDeliveryAck: {//接收已发送回执
                Log.e("show","message=");
                break;
            }

            case EventNewCMDMessage: {//接收透传消息
                Log.e("show","收到透传消息");
                EMMessage cmdMewssage = (EMMessage) emNotifierEvent.getData();

                String function=cmdMewssage.getStringAttribute("function","");
                if (function.equals("sendMyLove")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            favorlayout.addFavor();
                            ++loves;
                            lovers.setText(String.valueOf(loves));
                        }
                    });

                }else if(function.equals("hostExit")) {
                    Message msg = new Message();
                    msg.what = EXIT_HOST;
                    mHandler.sendMessage(msg);
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



    public ListView getListView() {
        return mListview;
    }
    /**
     * 发送文本消息
     *
     * @param content message content
     *                <p>
     *                boolean resend
     */
    private void sendDIYMessage(String content,String funcation) {

        if (content!=null) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            TextMessageBody txtBody = new TextMessageBody(content);
            message.addBody(txtBody);
            message.setAttribute("nikeName", MynickName);
            message.setAttribute("photo", MyPhoto);
            message.setAttribute("content", "");
            message.setAttribute("funcation", funcation);
            // 如果是群聊，设置chattype,默认是单聊
            message.setChatType(EMMessage.ChatType.ChatRoom);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(roomId);
            // 把messgage加到conversation中
//            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
//            removeET.setText("");
            handleTextMessage(message);
            setResult(RESULT_OK);

        }
    }
    private void sendDIYMessage(String ACTION){
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        CmdMessageBody txtBody = new CmdMessageBody(ACTION);
        message.addBody(txtBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
// 增加自己特定的属性,目前sdk支持int,boolean,String这三种属性，可以设置多个扩展属性
        message.setAttribute("function", "sendMyLove");
        message.setReceipt(roomId);
//        setResult(RESULT_OK);
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
    /**查询直播状态接口
     * 接口
     */
    public class getStateThread implements Runnable
    {
        private int mLiveId;
        private int mPosition;
        private void setLiveId(int liveID){
            mLiveId=liveID;
        }
        @Override
        public void run()
        {
            String url= MyAapplication.IP+"live"+"/"+mLiveId+"?userId="+userid;
            try {
                getLiveState(url);
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
    public void getLiveState(String url) throws IOException {
        Request request = new Request.Builder().
                url(url)
                .get()
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
                    if (liveState.getResponseCode().equals("200")) {
                        if (liveState!=null) {
                            int mState=liveState.getState();
                            Message msg = new Message();
                            msg.arg1=mState;
                            msg.obj=liveState;
                            msg.what = LIVE_STATE;
                            mHandler.sendMessage(msg);
                        } else {
                            Log.e(TAG, "201，liveduixiangweikong");
                        }

                    } else if (liveState.getResponseCode().equals("500")) {
                        Log.e("videoInfoActivity", "连接服务器失败");
                    } else {
                        Log.e(TAG, "其它错误");
                    }
                }
            }
        });
    }
    /**
     * 用户列表接口
     */
    public class getUserThread implements Runnable
    {
        @Override
        public void run()
        {
            String url= MyAapplication.IP+"user"+"?userId="+MyUserId+"&id="+userid;
            try {
                getUser(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //   备用子线程
            Log.e("log", "发出请求");

        }
    }
    /**
     * 获取用户列表
     * @param url
     * @throws IOException
     */
    public void getUser(String url) throws IOException {
        Request request = new Request.Builder().
                url(url)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.getMessage();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Gson gson = new Gson();
                if (response.isSuccessful()){
                    person = gson.fromJson(response.body().charStream(), new TypeToken<PeopleDetails>() {
                    }.getType());
                    Message msg = new Message();
                    msg.what = IS_FOLLOW;
                    mHandler.sendMessage(msg);
                }
            }
        });
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

    private void checkIfAnimationDone() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (mAnimationDrawable.getCurrent() != mAnimationDrawable
                        .getFrame(mAnimationDrawable.getNumberOfFrames() - 1)) {
                    checkIfAnimationDone();
                } else {
                    mHandler.sendEmptyMessage(MSG_ANIMATION_FINISHED);
                }
            }
        }, mDuration);
    };

    public void playAnimation() {
//        if (mView == null) {
//            mView = mContext.getView();
//        }
//        image.setBackgroundResource(R.anim.effect_animation);
//        mAnimationDrawable = (AnimationDrawable) mView.getBackground();
        mAnimationDrawable.start();
        for (int i = 0; i < mAnimationDrawable.getNumberOfFrames(); i++) {
            mDuration += mAnimationDrawable.getDuration(i);
        }
        mHandler.sendEmptyMessage(MSG_ANIMATION_FINISHED);
//        checkIfAnimationDone();
    }

    public int Dp2Px(Context context, float dp) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dp * scale + 0.5f);

    }


//	定义函数动态控制listView的高度

    public void setListViewHeightBasedOnChildren(ListView listView) {

//获取listview的适配器

        ShowLiveActivityAdapter listAdapter = (ShowLiveActivityAdapter) listView.getAdapter(); //item的高度
        if (listAdapter == null) {

            return;

        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {

            View listItem = adapter.getView(i, null, listView);
           int copunt= listAdapter.getCount();
            Log.e(TAG,"count="+copunt);
            listItem.measure(0, 0); //计算子项View 的宽高 //统计所有子项的总高度
             if(copunt<=5&&copunt>=0){
                 Log.e(TAG,"ifcount="+i);
                 totalHeight += Dp2Px(getApplicationContext(),i*40);
//                 totalHeight += Dp2Px(getApplicationContext(),listItem.getMeasuredHeight())+listView.getDividerHeight();
            }else {
                 Log.e(TAG,"else count="+i);
                 totalHeight = Dp2Px(getApplicationContext(),180);
             }

//            ViewGroup.LayoutParams params = listView.getLayoutParams();
//            params.height = 500;
//            listView.setLayoutParams(params);
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        Log.e(TAG,"else totalHeight="+totalHeight);
        listView.setLayoutParams(params);

    }
}




