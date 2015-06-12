package tv.liangzi.quantum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.adapter.ShowLiveActivityAdapter;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import tv.liangzi.quantum.view.MediaController;

public class ShowLiveActivity extends BaseActivity implements OnClickListener,EditText.OnEditorActionListener,EMEventListener {
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
    private int temp;
    int time = 0;
    //    private KeyboardLayout mRoot;
    public static final int MESSAGE_LISTVIEW = 0;
    ImageView imHead;
    private int mLoginBottom;
    private static final int KEYBOARD_SHOW = 0X10;
    private static final int KEYBOARD_HIDE = 0X20;
    private boolean mGetBottom = true;
    private String roomId;
    private String nikeName;
    private String photo;
    private String userid;
    private String shareUrl;
    private String rtmpUrl;
    EMConversation conversation;
    private MediaController mMediaController;
    private String EmuserId;
    private UMSocialService mController = null;
    String MyPhoto;

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
         imHead = (ImageView) findViewById(R.id.im_live_head);
        imHead.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ShowLiveActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
        // TODO Auto-generated method stub
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

			/*
			 * 设置图片以如何的编码方式显示 imageScaleType(ImageScaleType imageScaleType)
			 * EXACTLY :图像将完全按比例缩小的目标大小
			 * EXACTLY_STRETCHED:图片会缩放到目标大小完全 IN_SAMPLE_INT:图像将被二次采样的整数倍
			 * IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
			 * IN_SAMPLE_INT
			 * NONE:图片不会调整
			 */
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
    private void uMengConfig() {
// 首先在您的Activity中添加如下成员变量
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
// 设置分享内容
        mController.setShareContent("量子频道，http://liangzi.tv");
// 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(ShowLiveActivity.this,
                "http://www.baidu.com/img/bdlogo.png"));
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(ShowLiveActivity.this,
                "wx325e62291f322e40", "1c74a6f8f5c327dd3015d23b2626fdc7");
        wxHandler.addToSocialSDK();
        SocializeListeners.SnsPostListener mSnsPostListener = new SocializeListeners.SnsPostListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int stCode,
                                   SocializeEntity entity) {
                if (stCode == 200) {
                    Toast.makeText(ShowLiveActivity.this, "分享成功", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(ShowLiveActivity.this,
                            "分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        mController.registerListener(mSnsPostListener);


    }


    @Override
    public void initListeners() {
        // TODO Auto-generated method stub
        imShare.setOnClickListener(this);
        removeET.setOnEditorActionListener(this);
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

//        uMengConfig();
      MyPhoto= (String) SharedPreferencesUtils.getParam(ShowLiveActivity.this, "photo", "");
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        rtmpUrl = intent.getStringExtra("rtmpUrl");
        userid = intent.getStringExtra("userid");
        nikeName = intent.getStringExtra("nikeName");
        photo=intent.getStringExtra("photo");
        shareUrl = intent.getStringExtra("shareUrl");
//        EmuserId = app.getEMuserId();
        // TODO Auto-generated method stub
//        videoURL = "http://hot.vrs.sohu.com/ipad2025214_4639791893179_5236535.m3u8?plat=17";
//        mMediaController.setMediaPlayer(videoView);
//        videoView.setMediaController(mMediaController);
//        videoView.setVideoPath(rtmpUrl);
//        videoView.start();
        imageLoader.displayImage(photo, imHead, options, animateFirstListener);
        onChatroomViewCreation(roomId);


    }

    @Override
    protected void onResume() {
//        EMChatManager.getInstance().registerEventListener(
//                this,
//                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
//                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});

        super.onResume();
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
                InputMethodManager imm = (InputMethodManager) removeET.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(removeET, InputMethodManager.SHOW_FORCED);
                Log.v(TAG, "anjiandianji");
                break;
            case R.id.im_live_share:

                //设置微信好友分享内容
//                WeiXinShareContent weixinContent = new WeiXinShareContent();
////设置分享文字
//                weixinContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，微信");
////设置title
//                weixinContent.setTitle("友盟社会化分享组件-微信");
////设置分享内容跳转URL
//                weixinContent.setTargetUrl("www.baidu.com");
////设置分享图片
//                weixinContent.setShareImage(new UMImage(this, R.drawable.ic_concerned_no));
//                mController.setShareMedia(weixinContent);
//
//
////设置微信朋友圈分享内容
//                CircleShareContent circleMedia = new CircleShareContent();
//                circleMedia.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，朋友圈");
////设置朋友圈title
//                circleMedia.setTitle("友盟社会化分享组件-朋友圈");
//                circleMedia.setShareImage(new UMImage(this, R.drawable.a));
//                circleMedia.setTargetUrl("www.baidu.com");
//                mController.setShareMedia(circleMedia);
//                mController.openShare(ShowLiveActivity.this, false);
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 消息id
            String msgId = intent.getStringExtra("msgid");
            //根据消息id获取message
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            //获取自定义的属性，第2个参数为返回的默认值
            message.getStringAttribute("nickName", "");
            message.getStringAttribute("photo", "");
            String content=message.getStringAttribute("content", "");
            Toast.makeText(ShowLiveActivity.this,"content="+content,Toast.LENGTH_SHORT);
            Log.e("message",msgId);
            abortBroadcast();
        }
    }

        public void onChatroomViewCreation(String roomIdq) {

//        findViewById(R.id.container_to_group).setVisibility(View.GONE);

//        final ProgressDialog pd = ProgressDialog.show(this, "", "Joining......");
            EMChatManager.getInstance().joinChatRoom(roomId, new EMValueCallBack<EMChatRoom>() {

                @Override
                public void onSuccess(EMChatRoom value) {
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
                    EMLog.d(TAG, "join room failure : " + error);
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

//            @Override
//            public void onInvitationReceived(String roomId, String roomName,
//                                             String inviter, String reason) {
//            }

                @Override
                public void onChatRoomDestroyed(String roomId, String roomName) {
                    if (roomId.equals(roomId)) {
                        finish();
                    }
                }

                @Override
                public void onMemberJoined(String roomId, String participant) {
                    Toast.makeText(ShowLiveActivity.this,"有人加入房间",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onMemberExited(String roomId, String roomName,
                                           String participant) {
                    Toast.makeText(ShowLiveActivity.this,"有人加入房间",Toast.LENGTH_SHORT).show();
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
//            message.setAttribute("funcation", "");
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
//		Spannable span = SmileUtils.getSmiledText(mContext, txtBody.getMessage());
        // 设置内容
        // 设置长按事件监听
//        holder.textViewContent.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
////				activity.startActivityForResult(
////						(new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
////								EMMessage.Type.TXT.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
//                return true;
//            }
//        });

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
    }



