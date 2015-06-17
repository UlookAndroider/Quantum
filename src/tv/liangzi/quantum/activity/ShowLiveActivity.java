package tv.liangzi.quantum.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
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

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.adapter.ShowLiveActivityAdapter;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.Live;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import tv.liangzi.quantum.view.LoadingDialog;
import tv.liangzi.quantum.view.MediaController;
import tv.liangzi.quantum.view.SharePopupWindow;
import tv.liangzi.quantum.view.TipsToast;

public class ShowLiveActivity extends BaseActivity implements OnClickListener,AbsListView.OnScrollListener,EditText.OnEditorActionListener,EMEventListener {
    private com.pili.pldroid.player.widget.VideoView videoView;
    private ListView mListview;
    //	private ImageButton playButton;
    private String videoURL;
    //	private ImageView etDiscuss;
    private RecyclerView rlView;
    private String TAG = "SHOWLIVING";
    private RelativeLayout rl;
    private EditText removeET;
    private ImageView imLove;
    private ImageView imShare;
    ShowLiveActivityAdapter adapter;
    private static TipsToast tipsToast;
    private LoadingDialog dialog;
    //private KeyboardLayout mRoot;
    public static final int MESSAGE_LISTVIEW = 0;
    ImageView imHead;
    private int mLoginBottom;
    private static final int KEYBOARD_SHOW = 0X10;
    private static final int KEYBOARD_HIDE = 0X20;
    private static final int ROOM_ADD = 0;
    private static final int ROOM_DELETE = 1;
    private boolean mGetBottom = true;
    private String roomId;
    private String nikeName;
    private String photo;
    private String userid;
    private String shareUrl;
    private String rtmpUrl;
    private int audiences;
    EMConversation conversation;
    private MediaController mMediaController;
    private String EmuserId;
    private UMSocialService mController = null;
    private String MyPhoto;
    private String MyUserId;
    private String accessToken;
    private TextView nickName;
    private TextView audienceCount;
    private Live  living;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

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
                    Log.d(TAG, "the mLoginBottom is  " + mLoginBottom);
//                    mRoot.setPadding(0, mRootBottom - mLoginBottom, 0, 0);
                    break;
                case ROOM_ADD:
                    audienceCount.setText(++audiences+"");
                    break;
                case ROOM_DELETE:
                    if (audiences>0)
                    audienceCount.setText(--audiences+"");
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void setContentView() {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_showlive);
    }

    @Override
    public void initViews() {
//        showTips(R.drawable.tips_error, R.string.hello_world);
         imHead = (ImageView) findViewById(R.id.im_live_head);
        audienceCount= (TextView) findViewById(R.id.follow_count);
        // TODO Auto-generated method stub
        nickName=(TextView)findViewById(R.id.tv_live_id);
        mListview = (ListView) findViewById(R.id.lv_live_listview);
        videoView = (com.pili.pldroid.player.widget.VideoView) findViewById(R.id.videoview);
        rlView = (RecyclerView) findViewById(R.id.recyclerView);
        imLove = (ImageView) findViewById(R.id.im_live_love);
        imShare = (ImageView) findViewById(R.id.im_live_share);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlView.setLayoutManager(linearLayoutManager);
        removeET = (EditText) findViewById(R.id.et_remove_disscuss);
//        RecycleAdapter adapter = new RecycleAdapter(this, null);//暂时传null
//        rlView.setAdapter(adapter);
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

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    for (int i=firstVisibleItem;firstVisibleItem<totalItemCount;firstVisibleItem++){
//            absListView.getChildAt(i).setBackgroundTintList();
//        }
//        if (firstVisibleItem<firstVisibleItem+visibleItemCount){
//            absListView.getChildAt(firstVisibleItem).setBackgroundColor(Color.WHITE);
//            absListView.getChildAt(firstVisibleItem++).setBackgroundColor(Color.GREEN);
//            absListView.getChildAt(firstVisibleItem++).setBackgroundColor(Color.RED);
//        }
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
    public void initListeners() {
        // TODO Auto-generated method stub
        imShare.setOnClickListener(this);
        imHead.setOnClickListener(this);
        findViewById(R.id.tv_live_follow).setOnClickListener(this);
                removeET.setOnEditorActionListener(this);
        findViewById(R.id.tv_close).setOnClickListener(this);
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});
        EMChat.getInstance().setAppInited();
        //只有注册了广播才能接收到新消息，目前离线消息，在线消息都是走接收消息的广播（离线消息目前无法监听，在登录以后，接收消息广播会执行一次拿到所有的离线消息）
        imageLoader.init(ImageLoaderConfiguration.createDefault(ShowLiveActivity.this));
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
            sendText(removeET.getText().toString().trim());
        }
        return false;
    }
    @Override
    public void initData() {

        MyPhoto= (String) SharedPreferencesUtils.getParam(ShowLiveActivity.this, "photo", "");
        MyUserId=(String) SharedPreferencesUtils.getParam(ShowLiveActivity.this, "userId", "");
        accessToken=(String) SharedPreferencesUtils.getParam(ShowLiveActivity.this, "accessToken", "");
        Intent intent = getIntent();
        living = intent.getParcelableExtra("Living");
        roomId=living.getChatroomId();
        rtmpUrl = living.getRtmpPlayUrl();
        userid = living.getUserId()+"";
        nikeName = living.getNickName();
        photo=living.getPhoto();
        audiences=living.getAudiences();
        shareUrl = living.getShareUrl();
        // TODO Auto-generated method stub
        nickName.setText(nikeName);
        audienceCount.setText(audiences+"");
        onChatroomViewCreation();
        imageLoader.displayImage(photo, imHead, options, animateFirstListener);
        videoView.setVideoPath(rtmpUrl);
//        videoView.start();




    }

    @Override
    protected void onResume() {

        super.onResume();
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

                 finish();
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
                                    ShowLiveActivity.this,
                                    new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
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
            adapter = new ShowLiveActivityAdapter(ShowLiveActivity.this, roomId);
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
//                        finish();
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
                    Log.e("adapter", "message=" + ((TextMessageBody) message.getBody()).getMessage());
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
                    Toast.makeText(ShowLiveActivity.this, "发送chng", Toast.LENGTH_SHORT)
                            .show();
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
        Log.e("adapter","监听");
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
                // 消息id
//                String msgId = intent.getStringExtra("msgid");
//                //根据消息id获取message
//                EMMessage message = EMChatManager.getInstance().getMessage(msgId);
                //获取自定义的属性，第2个参数为返回的默认值
                message.getStringAttribute("nikeName", null);
                message.getStringAttribute("photo", null);
                message.getStringAttribute("content", null);
// 把messgage加到conversation中
                conversation.addMessage(message);
                // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
                adapter.refreshSelectLast();
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
                Log.e("show","message=");
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




