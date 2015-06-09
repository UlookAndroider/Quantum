package tv.liangzi.quantum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.EMLog;

import java.io.IOException;
import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.base.BaseActivity;

public class StartLiveActivity extends BaseActivity  {
	private ListView  mListview;
    private String TAG="SHOWLIVING";
    private String roomId;
    private String nikeName;
    private String userid;
    private String shareUrl;
    private String rtmpUrl;
    EMConversation conversation;

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub

        setContentView(R.layout.activity_sendlive);
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
                Intent intent = new Intent(StartLiveActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
		// TODO Auto-generated method stub
		mListview=(ListView) findViewById(R.id.lv_live_listview);


    }

    private void initSurfaceView() {
//        surfaceView=(SurfaceView) findViewById(R.id.surfaceview);
//        surfaceHolder = surfaceView.getHolder();
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    @Override
	public void initListeners() {
		// TODO Auto-generated method stub
//		playButton.setOnClickListener(this);
//		etDiscuss.addTextChangedListener(this);
//        imSwitch.setOnClickListener(this);
		
	}

	@Override
	public void initData() {
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        rtmpUrl = intent.getStringExtra("rtmpUrl");
        userid = intent.getStringExtra("userid");
        nikeName = intent.getStringExtra("nikeName");
        shareUrl = intent.getStringExtra("shareUrl");
        // TODO Auto-generated method stub
//		videoURL="rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";
//        StartLiveActivityAdapter adapter=new StartLiveActivityAdapter(StartLiveActivity.this, null);
//        mListview.setAdapter(adapter);
        onChatroomViewCreation(roomId);


        EMChatManager.getInstance().registerEventListener(new EMEventListener() {

            @Override
            public void onEvent(EMNotifierEvent event) {
                // TODO Auto-generated method stub
                EMMessage message = (EMMessage) event.getData();
            }
        });
	}

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            abortBroadcast();

            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation	conversation = EMChatManager.getInstance().getConversation(username);
            // 如果是群聊消息，获取到group id
            if (!username.equals(username)) {
                // 消息不是发给当前会话，return
                return;
            }
            EMChat.getInstance().setAppInited();
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
//                        NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
//                        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
//                        intentFilter.setPriority(3);
//                        registerReceiver(msgReceiver, intentFilter);
//                        EMChatManager.getInstance().registerEventListener(
//                                StartLiveActivity.this,
//                                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
//                                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});
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
//        adapter = new StartLiveActivityAdapter(StartLiveActivity.this, roomId);
//        mListview.setAdapter(adapter);
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
                Toast.makeText(StartLiveActivity.this, "有人加入房间", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {
                Toast.makeText(StartLiveActivity.this, "有人加入房间", Toast.LENGTH_SHORT).show();
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

//    @Override
//    public void onEvent(EMNotifierEvent emNotifierEvent) {
//        EMNotifierEvent.Event a=  emNotifierEvent.getEvent();
//        String b=a.toString();
//
//
//    }
}


