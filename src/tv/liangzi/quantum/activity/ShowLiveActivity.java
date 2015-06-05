package tv.liangzi.quantum.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.adapter.RecycleAdapter;
import tv.liangzi.quantum.adapter.ShowLiveActivityAdapter;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.config.MyAapplication;

public class ShowLiveActivity extends BaseActivity implements OnClickListener, TextWatcher,EditText.OnEditorActionListener {
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

    private int temp;
    int time = 0;
    private KeyboardLayout mRoot;
    public static final int MESSAGE_LISTVIEW = 0;

    private int mLoginBottom;
    private static final int KEYBOARD_SHOW = 0X10;
    private static final int KEYBOARD_HIDE = 0X20;
    private boolean mGetBottom = true;
    private String  roomId;
    EMConversation conversation;

    private String EmuserId;
    private UMSocialService mController = null;
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
                    int mRootBottom = mRoot.getBottom();
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
//        getWindow().setBackgroundDrawableResource(R.drawable.test_live) ;
    }

    @Override
    public void initViews() {
        ImageView imHead = (ImageView) findViewById(R.id.im_live_head);
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
//        etDiscuss=(ImageView)findViewById(R.id.ibtn_disscuss);
//        etDiscuss.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlView.setLayoutManager(linearLayoutManager);
        removeET = (EditText) findViewById(R.id.et_remove_disscuss);
        RecycleAdapter adapter = new RecycleAdapter(this, null);//暂时传null
        rlView.setAdapter(adapter);
        mRoot = (KeyboardLayout) findViewById(R.id.rl_root);
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
//                    mLoginBottom = etDiscuss.getBottom();//获取登录按钮的位置信息。
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
//                       Message     msg.what=MESSAGE_LISTVIEW;
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
//        // 支持微信朋友圈
//        UMWXHandler wxCircleHandler = new UMWXHandler(ShowLiveActivity.this,
//                "wx325e62291f322e40", "1c74a6f8f5c327dd3015d23b2626fdc7");
//        wxCircleHandler.setToCircle(true);
//        wxCircleHandler.addToSocialSDK();
//        mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT);
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


//		playButton.setOnClickListener(this);
//		etDiscuss.addTextChangedListener(this);


    }

    /**
     * 发送按钮
     * @param textView
     * @param i
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_SEND)
            {
                // 在这里编写自己想要实现的功能
            }
            return false;
    }
    @Override
    public void initData() {
        MyAapplication app = (MyAapplication) getApplication();
        uMengConfig();
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        EmuserId = app.getEMuserId();
        // TODO Auto-generated method stub
        videoURL = "http://hot.vrs.sohu.com/ipad2025214_4639791893179_5236535.m3u8?plat=17";
        ShowLiveActivityAdapter adapter = new ShowLiveActivityAdapter(ShowLiveActivity.this, null);
        mListview.setAdapter(adapter);
        Uri uri = Uri.parse(videoURL);
        videoView.setVideoURI(uri);
        videoView.start();
        onChatroomViewCreation(roomId);
        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(roomId)) {
                    finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                if (roomId.equals(roomId)) {
                    finish();
                }
            }

        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        int[] location = new int[2];
        removeET.getLocationOnScreen(location);
        temp = location[1];
        Log.v(TAG, temp + "temp 坐标。。。。。。。。。。。");
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
                InputMethodManager imm = (InputMethodManager) removeET.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(removeET, InputMethodManager.SHOW_FORCED);
                Log.v(TAG, "anjiandianji");
                break;
            case R.id.im_live_share:

                //设置微信好友分享内容
                WeiXinShareContent weixinContent = new WeiXinShareContent();
//设置分享文字
                weixinContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，微信");
//设置title
                weixinContent.setTitle("友盟社会化分享组件-微信");
//设置分享内容跳转URL
                weixinContent.setTargetUrl("www.baidu.com");
//设置分享图片
                weixinContent.setShareImage(new UMImage(this, R.drawable.ic_concerned_no));
                mController.setShareMedia(weixinContent);


//设置微信朋友圈分享内容
                CircleShareContent circleMedia = new CircleShareContent();
                circleMedia.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，朋友圈");
//设置朋友圈title
                circleMedia.setTitle("友盟社会化分享组件-朋友圈");
                circleMedia.setShareImage(new UMImage(this, R.drawable.a));
                circleMedia.setTargetUrl("www.baidu.com");
                mController.setShareMedia(circleMedia);
                mController.openShare(ShowLiveActivity.this, false);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
    /**
     * 发送文本消息
     *
     * @param content
     *            message content
     *
     *            boolean resend
     */
    private void sendText(String content) {

        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单聊
            message.setChatType(EMMessage.ChatType.ChatRoom);

            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(roomId);
            // 把messgage加到conversation中
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
//            adapter.refreshSelectLast();
//            mEditTextContent.setText("");

            setResult(RESULT_OK);

        }
    }
    public void onChatroomViewCreation(final String roomId) {

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

//                        onListViewCreation();
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
    protected void onConversationInit() {

         conversation = EMChatManager.getInstance().getConversationByType(roomId, EMConversation.EMConversationType.ChatRoom);

        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();

        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount <5) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreGroupMsgFromDB(msgId, 5);//pagesize
        }

        // 监听聊天室变化回调
        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {

          /*  @Override
            public void onInvitationReceived(String roomId, String roomName,
                                             String inviter, String reason) {
            }*/

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(roomId)) {
                    finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {

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



}


