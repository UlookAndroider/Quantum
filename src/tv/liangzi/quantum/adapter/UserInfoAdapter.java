package tv.liangzi.quantum.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.bean.Person;


public class UserInfoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ViewHolder holder;
	private static final int TYPE_LIVE=0;
	private static final int TYPE_WAIT=1;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private List<PeopleDetails> mList;
	public UserInfoAdapter(Context context,List<PeopleDetails> list){
		inflater=LayoutInflater.from(context);
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		mList=list;
		initImageLoaderOptions();
	}
	/**
	 * 初始化缓存设置
	 */
	private void initImageLoaderOptions(){
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.index_iv02)			// 设置图片下载期间显示的图片
			.showImageOnLoading(R.drawable.default_head)
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
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
@Override
public int getItemViewType(int position) {
	// TODO Auto-generated method stub
//	boolean live=mList.get(position).isType();
//	if (live) {
//		return TYPE_LIVE;
//	}
	return TYPE_WAIT;
}
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position,  View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		holder=null;
		int type=getItemViewType(position);
		if (convertView==null) {
			 holder=new ViewHolder();
			switch (type){
			
			case TYPE_LIVE:
				convertView =inflater.inflate(R.layout.activity_userinfo_title_item,null);
				holder.textView=(TextView) convertView.findViewById(R.id.tv_userinfo_title);
				break;
			case TYPE_WAIT:
				convertView =inflater.inflate(R.layout.activity_userinfo_video_item,null);
				holder.imageView=(ImageView) convertView.findViewById(R.id.im_user_video);
				
			break;
			default:
				break;
			}
			convertView.setTag(holder);
		}else {
			 holder = (ViewHolder) convertView.getTag();
//			 holder.imageView.setImageDrawable(null);
		}
//		holder.textView.setText("我 的 视 频");
//		imageLoader.displayImage(mList.get(position).getPicUrl(), holder.imageView, options, animateFirstListener);
		return convertView;
	}
	 static class ViewHolder{
		ImageView imageView;
		TextView textView;
		
	}
}

