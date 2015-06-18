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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.bean.PeopleDetails;



public class PeopleAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ViewHolder holder;
	private static final int TYPE_LIVE=0;
	private static final int TYPE_WAIT=1;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private List<PeopleDetails> mList=new ArrayList<PeopleDetails>();
	private Callback mCallback;

	/**
	 * 自定义接口，用于点击的回调
	 */
	public interface Callback {
		public void click(View v,int position);
		public void headClick(View v,int position);
	}

	public PeopleAdapter(Context context,List<PeopleDetails> list,Callback callback){
		inflater=LayoutInflater.from(context);
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		mList=list;
		initImageLoaderOptions();
		mCallback = callback;

	}
	public void setUser(List<PeopleDetails> list){
		mList=list;
	}
	/**
	 * 初始化缓存设置
	 */
	private void initImageLoaderOptions(){
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.index_iv02)			// 设置图片下载期间显示的图片
			.showImageOnLoading(R.drawable.defult_head)
			.showImageForEmptyUri(R.drawable.defult_head)	// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.defult_head)		// 设置图片加载或解码过程中发生错误显示的图片
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
//			.IN_SAMPLE_INTdisplayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
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
		holder=null;
		if (convertView==null) {
			 holder=new ViewHolder();
				convertView =inflater.inflate(R.layout.fragment_people_item,null);
			if (position%2==0){
				convertView.setBackgroundResource(R.color.white);
			}else {
				convertView.setBackgroundResource(R.color.gray_bg_people);
			}
				holder.imageView=(CircleImageView) convertView.findViewById(R.id.im_head);
			    holder.textView=(TextView)convertView.findViewById(R.id.tv_nikename);
				holder.addImage=(ImageView)convertView.findViewById(R.id.im_people_add);
			holder.textViewSign=(TextView)convertView.findViewById(R.id.tv_info);
			    convertView.setTag(holder);
		}else {
			 holder = (ViewHolder) convertView.getTag();
			 holder.imageView.setImageDrawable(null);
		}
		if (mList.get(position).isFollow()){
			holder.addImage.setImageResource(R.drawable.ic_concerned);
		}else{
			holder.addImage.setImageResource(R.drawable.ic_concerned_no);
		}
		holder.addImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.click(v,position);
			}
		});
		holder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.headClick(v,position);
			}
		});
		holder.textViewSign.setText(mList.get(position).getSign());
		holder.textView.setText(mList.get(position).getNickName());
		imageLoader.displayImage(mList.get(position).getPhoto(), holder.imageView, options, animateFirstListener);
		return convertView;
	}
	 static class ViewHolder{
		 CircleImageView imageView;
		 TextView textView;
		 ImageView addImage;
		 TextView textViewSign;

	}

}

