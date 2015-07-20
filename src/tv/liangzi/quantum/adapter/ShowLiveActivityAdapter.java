package tv.liangzi.quantum.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.squareup.picasso.Picasso;

import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.activity.ShowLiveActivity;
import tv.liangzi.quantum.activity.WatchLiveActivity;


public class ShowLiveActivityAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ViewHolder holder;
	private EMConversation conversation;
	EMMessage[] temMessages = null;

	private List<String> mList;
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
	private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;
	private Activity activity;
	private Context mContext;
	private  List<EMMessage> messages;
	private OnReceiverListener mReceiver;
	private LinearLayout.LayoutParams lp;
	public interface OnReceiverListener {
		void OnReceive();
	}
	public void setOnReceiverListener(OnReceiverListener receiver){
		mReceiver=receiver;
	}


	public ShowLiveActivityAdapter(Context context,String roomId){
		mContext=context;
		activity = (Activity) context;
		inflater=LayoutInflater.from(context);
		this.conversation = EMChatManager.getInstance().getConversation(roomId);

		lp = new LinearLayout.LayoutParams(100, 100);
	}
	Handler handler = new Handler() {
		private void refreshList() {
			// UI线程不能直接使用conversation.getAllMessages()
			// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
//			temMessages  = (EMMessage[]) .toArray(new EMMessage[conversation.getAllMessages().size()])
					messages=conversation.getAllMessages();
//			 messages=Arrays.asList(temMessages);
			for (int i = 0; i < messages.size(); i++) {
				// getMessage will set message as read status
					conversation.getMessage(i);
			}
			notifyDataSetChanged();
		}

		@Override
		public void handleMessage(android.os.Message message) {
			switch (message.what) {
				case HANDLER_MESSAGE_REFRESH_LIST:
					refreshList();
					break;
				case HANDLER_MESSAGE_SELECT_LAST:
					if (activity instanceof ShowLiveActivity) {
						ListView listView = ((ShowLiveActivity)activity).getListView();
						if (messages.size()>0) {

							listView.setSelection(messages.size() - 1);
						}
					}else{
						ListView listView = ((WatchLiveActivity)activity).getListView();
						if (messages.size() > 0) {
							listView.setSelection(messages.size() - 1);
						}
					}
					break;
				case HANDLER_MESSAGE_SEEK_TO:
					int position = message.arg1;
					if (activity instanceof ShowLiveActivity) {
						ListView listView = ((ShowLiveActivity)activity).getListView();
						listView.setSelection(position);
					}
					break;
				default:
					break;
			}
		}
	};



	/**
	 * 刷新页面, 选择最后一个
	 */
	public void refreshSelectLast() {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
	}
	/**
	 * 刷新页面
	 */
	public void refresh() {
		if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
			return;
		}
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
		handler.sendMessage(msg);
	}

	public EMMessage getItem(int position) {
		if (messages != null && position < messages.size()) {
			return messages.get(position);
		}
		return null;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messages == null ? 0 : messages.size();
	}



	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position,  View convertView, ViewGroup parent) {
		EMMessage message = getItem(position);
//		Log.e("message","getview调用次数="+"位置position="+position);
		// TODO Auto-generated method stub
		if (convertView==null) {
			convertView =inflater.inflate(R.layout.activity_showlive_item,null);
			 holder=new ViewHolder();
			holder.imageView=(ImageView) convertView.findViewById(R.id.im_live_head);
			holder.textViewID=(TextView) convertView.findViewById(R.id.tv_live_id);
			holder.textViewContent=(TextView) convertView.findViewById(R.id.tv_live_content);
			convertView.setTag(holder);
		}else {
			 holder = (ViewHolder) convertView.getTag();
//			 holder.imageView.setImageDrawable(null);
//			 holder.textureView.reset();
//			 holder.loading.setVisibility(View.GONE);
//			 holder.videoPlay.setVisibility(View.GONE);
		}

		String content = null;
		String photo=null;
		String nickName = null;
		String funcation=null;
		try {
			 content=message.getStringAttribute("content");
			 photo=message.getStringAttribute("photo");
			nickName=message.getStringAttribute("nikeName");
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
//		Log.e("", nickName + "、" + content + "、" + photo);

			holder.textViewContent.setText(content);
			holder.textViewID.setText(nickName);
//			imageLoader.displayImage(photo, holder.imageView, options, animateFirstListener);
		Picasso.with(mContext).load(photo) .placeholder(R.drawable.default_head) .error(R.drawable.defult_head).into(holder.imageView);
//		    Picasso.with(mContext).load(photo).into(holder.imageView);
		return convertView;
	}
	 static class ViewHolder{
		ImageView imageView;
		TextView textViewID;
		TextView textViewContent;
		
	}


}

