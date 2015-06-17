package tv.liangzi.quantum.adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.activity.videoInfoPageActivity;
import tv.liangzi.quantum.bean.ServiceStatus;


public class ExploreAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ViewHolder holder;
	private  Context mContext;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private List<ServiceStatus.Video> mList=new ArrayList<ServiceStatus.Video>();
	public ExploreAdapter(Context context){
		mContext=context;
		inflater=LayoutInflater.from(context);
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		initImageLoaderOptions();
	}
	public void  setList(List<ServiceStatus.Video> list){
		mList=list;
	}
	/**
	 * 初始化缓存设置
	 */
	private void initImageLoaderOptions(){
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.index_iv02)			// 设置图片下载期间显示的图片
			.showImageOnLoading(R.drawable.ic_loading)
			.showImageForEmptyUri(R.drawable.ic_loading)	// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.a)		// 设置图片加载或解码过程中发生错误显示的图片	
			.cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
			.cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
			.considerExifParams(true)
			
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
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position,  View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView==null) {
			convertView =inflater.inflate(R.layout.home_listview_item,null);
			holder=new ViewHolder();
			holder.imageView=(ImageView) convertView.findViewById(R.id.video_pic);
			holder.textView=(TextView)convertView.findViewById(R.id.video_name);
			convertView.setTag(holder);
		}else {
			 holder = (ViewHolder) convertView.getTag();
			 holder.imageView.setImageDrawable(null);
		}
		holder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, videoInfoPageActivity.class);
				intent.putExtra("playUrl", mList.get(position).getPlayUrl());
				intent.putExtra("videoObj", mList.get(position));
				mContext.startActivity(intent);
			}
		});

		holder.textView.setText(mList.get(position).getTitle());
//		holder.timeView.setText(mList.get(position).getDuration());
		imageLoader.displayImage(mList.get(position).getImg(), holder.imageView, options, animateFirstListener);
		return convertView;
	}
	 static class ViewHolder{
		ImageView imageView;
		 TextView textView;
//		 TextView timeView;
		
	}
}

