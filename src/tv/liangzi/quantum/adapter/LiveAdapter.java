package tv.liangzi.quantum.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import tv.liangzi.quantum.bean.Live;
import tv.liangzi.quantum.fragment.LiveFragment;


public class LiveAdapter extends BaseAdapter   {
	private LayoutInflater inflater;
	private static final int TYPE_LIVE=1;
	private static final int TYPE_WAIT=0;
	private static final int TYPE_LIVE_TITLE=2;
	private static final int TYPE_WAIT_TITLE=3;
	private Context mContext;
	private View livingView;
	private View titleView;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private List<Live> mList;
	OnItemButtonClickListener mListener;
	public LiveAdapter(Context context,List<Live> list){
		inflater=LayoutInflater.from(context);
		mContext=context;
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		mList=list;
		initImageLoaderOptions();
	}
	public void setLives(List<Live> lives){
		mList=lives;
	}
	public void setButtonOnClickListener(OnItemButtonClickListener listener){
		this.mListener=listener;
	}
	public interface OnItemButtonClickListener {
		void onItemClick( View view, int position, int id,int subid);
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
		return 4;
	}
@Override
public int getItemViewType(int position) {
	// TODO Auto-generated method stub
	//0预约 1直播
	int live=mList.get(position).getState();
	if (live==1) {
		return TYPE_LIVE;
	}else if(live==0){
		return TYPE_WAIT;
	}else if(live==2){
		return TYPE_LIVE_TITLE;
	}else
		return TYPE_WAIT_TITLE;

}
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position,  View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int type=getItemViewType(position);

		if (type==TYPE_LIVE){

			LivingHolder livingHolder=null;
			if (convertView==null){
				livingHolder=new LivingHolder();
				livingView = inflater.inflate(
						R.layout.fragment_living_item, null);
				livingHolder.theme= (TextView) livingView.findViewById(R.id.theme_name);
				livingHolder.userName= (TextView) livingView.findViewById(R.id.video_user);
				livingHolder.concernedCount1= (TextView) livingView.findViewById(R.id.living_concerned_count);
				livingHolder.picBackground= (ImageView) livingView.findViewById(R.id.video_pic);
				livingHolder.userHead= (ImageView) livingView.findViewById(R.id.living_head);
				livingView.setTag(livingHolder);
				convertView=livingView;
			}else{
				livingHolder= (LivingHolder) convertView.getTag();
			}
			imageLoader.displayImage(mList.get(position).getPhoto(), livingHolder.userHead, options, animateFirstListener);
			imageLoader.displayImage(mList.get(position).getImg(), livingHolder.picBackground, options, animateFirstListener);
			livingHolder.theme.setText(mList.get(position).getTitle());
			livingHolder.userName.setText(mList.get(position).getNickName());
			livingHolder.concernedCount1.setText(mList.get(position).getSubscibes()+"");
		}else if (type==TYPE_WAIT){

			final ScheduleHolder scheduleHolder;
			if (convertView==null){
				scheduleHolder=new ScheduleHolder();
				livingView = inflater.inflate(
						R.layout.fragment_living_schedule_item, null);
				scheduleHolder.theme= (TextView) livingView.findViewById(R.id.video_schedule_theme);
				scheduleHolder.userName= (TextView) livingView.findViewById(R.id.video_schedule_user);
				scheduleHolder.year= (TextView) livingView.findViewById(R.id.tv_living_year);
				scheduleHolder.hour= (TextView) livingView.findViewById(R.id.tv_living_time);
				scheduleHolder.relativeLayout=(RelativeLayout)livingView.findViewById(R.id.rl_sunscribe);
				scheduleHolder.concernedCount2= (TextView) livingView.findViewById(R.id.tv_schedule_concerned_count);
				scheduleHolder.picBackground= (ImageView) livingView.findViewById(R.id.video_pic);
				scheduleHolder.ulooked= (ImageView) livingView.findViewById(R.id.icon_ulooked);
				scheduleHolder.userHead= (ImageView) livingView.findViewById(R.id.living_schedule_head);
				livingView.setTag(scheduleHolder);
				convertView=livingView;
			}else{
				scheduleHolder= (ScheduleHolder) convertView.getTag();
//				scheduleHolder.ulooked.setImageDrawable(null);
			}
			if (mList.get(position).getSubscibeId()==0){
				scheduleHolder.concernedCount2.setTextColor(Color.WHITE);
				scheduleHolder.ulooked.setImageResource(R.drawable.unsubscribe_middle);
			}else {
				scheduleHolder.concernedCount2.setTextColor(Color.parseColor("#B90B0E"));
				scheduleHolder.ulooked.setImageResource(R.drawable.subscribe_middle);
			}

			scheduleHolder.ulooked.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mListener.onItemClick(scheduleHolder.relativeLayout, position, mList.get(position).getLiveId(), mList.get(position).getSubscibeId());
				}
			});
			imageLoader.displayImage(mList.get(position).getPhoto(), scheduleHolder.userHead, options, animateFirstListener);
			imageLoader.displayImage(mList.get(position).getImg(), scheduleHolder.picBackground, options, animateFirstListener);
			scheduleHolder.theme.setText(mList.get(position).getTitle());
			scheduleHolder.year.setText(mList.get(position).getBegin()+"");
			scheduleHolder.hour.setText(mList.get(position).getBegin()+"");
			scheduleHolder.userName.setText(mList.get(position).getNickName());
			scheduleHolder.concernedCount2.setText(mList.get(position).getSubscibes()+"");
		}else if(type==TYPE_LIVE_TITLE){
			TitleHolder titleHolder=null;
			if (convertView==null){
				titleHolder =new TitleHolder();
				titleView = inflater.inflate(
						R.layout.fragment_living_title_item, null);
				titleHolder.titleImage= (ImageView) titleView.findViewById(R.id.title_image);
				titleView.setTag(titleHolder);
				convertView=titleView;
			}else{
				titleHolder= (TitleHolder) convertView.getTag();
			}
titleHolder.titleImage.setImageResource(R.drawable.section_living);
		}else if(type==TYPE_WAIT_TITLE){
			TitleHolder titleHolder=null;
			if (convertView==null){
				titleHolder =new TitleHolder();
				titleView = inflater.inflate(
						R.layout.fragment_living_title_item, null);
				titleHolder.titleImage= (ImageView) titleView.findViewById(R.id.title_image);
				titleView.setTag(titleHolder);
				convertView=titleView;
			}else{
				titleHolder= (TitleHolder) convertView.getTag();
			}
			titleHolder.titleImage.setImageResource(R.drawable.section_schedule);
		}

		return convertView;
	}
	/**
	 * 现场
	 * @author invinjun
	 *
	 */
	class LivingHolder {
		TextView theme;
		TextView userName;
		TextView concernedCount1;
		ImageView picBackground;
		ImageView userHead;
	}
	/**
	 * 预约
	 * @author invinjun
	 *
	 */
	class ScheduleHolder {
		TextView theme;
		TextView userName;
		TextView concernedCount2;
		TextView year;
		TextView hour;
		ImageView picBackground;
		ImageView userHead;
		ImageView ulooked;
		RelativeLayout relativeLayout;
	}

	/**
	 * 现场or预约抬头
	 */
	class TitleHolder {
		ImageView titleImage;
	}
}

