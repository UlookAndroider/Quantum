package tv.liangzi.quantum.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.activity.ShowLiveActivity;


public class ShowLiveActivityAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ViewHolder holder;
	private EMConversation conversation;
	EMMessage[] messages = null;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private List<String> mList;
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
	private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;
	private Activity activity;
	private Context mContext;
	public ShowLiveActivityAdapter(Context context,String roomId){
		mContext=context;
		activity = (Activity) context;
		inflater=LayoutInflater.from(context);
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		this.conversation = EMChatManager.getInstance().getConversation(roomId);
		initImageLoaderOptions();
	}
	Handler handler = new Handler() {
		private void refreshList() {
			// UI线程不能直接使用conversation.getAllMessages()
			// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
			messages = (EMMessage[]) conversation.getAllMessages().toArray(new EMMessage[conversation.getAllMessages().size()]);
			for (int i = 0; i < messages.length; i++) {
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
						if (messages.length > 0) {
							listView.setSelection(messages.length - 1);
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
	 * 初始化缓存设置
	 */
	private void initImageLoaderOptions(){
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.index_iv02)			// 设置图片下载期间显示的图片
			.showImageOnLoading(R.drawable.ic_loading)
			.showImageForEmptyUri(R.drawable.default_head)	// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.default_head)		// 设置图片加载或解码过程中发生错误显示的图片
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
			.bitmapConfig(Config.RGB_565)
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
		if (messages != null && position < messages.length) {
			return messages[position];
		}
		return null;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messages == null ? 0 : messages.length;
	}



	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position,  View convertView, ViewGroup parent) {
		EMMessage message = getItem(position);
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
		try {
			 content=message.getStringAttribute("content");
			 photo=message.getStringAttribute("photo");
			nickName=message.getStringAttribute("nickName");
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
		Log.e("",nickName+"、"+content+"、"+photo);
		TextMessageBody body= (TextMessageBody) message.getBody();
		holder.textViewContent.setText(body.getMessage());
		holder.textViewID.setText(nickName);
		imageLoader.displayImage(photo, holder.imageView, options, animateFirstListener);
		return convertView;
	}
	 static class ViewHolder{
		ImageView imageView;
		TextView textViewID;
		TextView textViewContent;
		
	}
	/**
	 * 显示用户头像
	 * @param message
	 * @param imageView
	 */
	private void setUserAvatar(EMMessage message, ImageView imageView){
		if(message.direct == EMMessage.Direct.SEND){
			//显示自己头像
//			UserUtils.setUserAvatar(mContext, EMChatManager.getInstance().getCurrentUser(), imageView);
		}else{
//			UserUtils.setUserAvatar(mContext, message.getFrom(), imageView);
		}
	}


}

