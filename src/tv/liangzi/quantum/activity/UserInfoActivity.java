package tv.liangzi.quantum.activity;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.adapter.ExploreAdapter;
import tv.liangzi.quantum.adapter.UserInfoAdapter;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.bean.ServiceStatus;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import tv.liangzi.quantum.view.XListView;

public class UserInfoActivity extends BaseActivity implements OnClickListener,XListView.IXListViewListener{

	private XListView mListView;
	private String userId;
	private String id;
	private PeopleDetails user;
	private ImageView im_user_dead;
	private TextView tv_user_concerned;
	private TextView tv_user_fans;
	private TextView nikename;
	private TextView place;
	private TextView tv_decrip;
	private ImageView bt_user_follow;
	private String toUserId;
	private String accessToken;
	LocalActivityManager manager = null;
	ViewPager pager = null;
	TabHost tabHost = null;
	TextView t1,t2,t3;

	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private ImageView cursor;// 动画图片

	private  boolean otherUser;
	List<ServiceStatus.Video> VideoList=new ArrayList<ServiceStatus.Video>();
	private ExploreAdapter mAdapter;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();


	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					mAdapter.setList(VideoList);
					mAdapter.notifyDataSetChanged();
					break;
				case 2:
					user.setIsFollow(true);
//					peopleAdapter.notifyDataSetChanged();
					bt_user_follow.setImageResource(R.drawable.followed);
					Toast.makeText(UserInfoActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
					break;
				case 3:

					user.setIsFollow(false);
					bt_user_follow.setImageResource(R.drawable.follow);
					Toast.makeText(UserInfoActivity.this,"取消关注成功",Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_userinfo);
		imageLoader.init(ImageLoaderConfiguration.createDefault(UserInfoActivity.this));


	}

	@Override
	public void initViews() {

		tv_user_concerned= (TextView) findViewById(R.id.tv_user_concerned);
		tv_user_fans=(TextView)findViewById(R.id.tv_user_fens);
		nikename= (TextView) findViewById(R.id.tv_user_nike);
		place=(TextView)findViewById(R.id.tv_place_id);
		im_user_dead= (ImageView) findViewById(R.id.im_user_dead);

		bt_user_follow= (ImageView) findViewById(R.id.bt_user_follow);

//		TextView tv= (TextView) findViewById(R.id.title_tv);
//		tv.setText("个人主页");
//		ImageView imHead=(ImageView) findViewById(R.id.im_title_head);
//       imHead.setVisibility(View.GONE);

		mListView=(XListView) findViewById(R.id.lv_userinfo);



		mAdapter=new ExploreAdapter(UserInfoActivity.this);
		mListView.setAdapter(mAdapter);
		userId= (String) SharedPreferencesUtils.getParam(this, "userId", "");
		accessToken= (String) SharedPreferencesUtils.getParam(this,"accessToken","");
		Intent intent=getIntent();
		if(intent.getSerializableExtra("userDetail")!=null){
			user= (PeopleDetails) intent.getSerializableExtra("userDetail");
			toUserId=user.getUserId()+"";
			initdisplay(user);
			otherUser=true;
		}else {
			otherUser=false;
			tv_user_concerned.setText((String) SharedPreferencesUtils.getParam(this, "focusNum", ""));
			tv_user_fans.setText((String) SharedPreferencesUtils.getParam(this, "fansNum", ""));
			nikename.setText((String) SharedPreferencesUtils.getParam(this, "nickName", ""));
			place.setText((String) SharedPreferencesUtils.getParam(this, "addr", ""));
			imageLoader.displayImage((String) SharedPreferencesUtils.getParam(this, "photo", ""), im_user_dead, options, animateFirstListener);
			bt_user_follow.setImageResource(R.drawable.followed);
			}

		}



//		Thread getUserInfoThread = new Thread(new getUserInfoThread());
//		getUserInfoThread.start();
		

	private void initdisplay(PeopleDetails user) {
		tv_user_concerned.setText(user.getFocusNum()+"");
		tv_user_fans.setText(user.getFansNum()+"");
		nikename.setText(user.getNickName());
		place.setText(user.getAddr());
		imageLoader.displayImage(user.getPhoto(), im_user_dead, options, animateFirstListener);
		if (user.isFollow()){
			bt_user_follow.setImageResource(R.drawable.followed);
		}else{
			bt_user_follow.setImageResource(R.drawable.follow);
		}

	}

	@Override
	public void initListeners() {
		bt_user_follow.setOnClickListener(this);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		String url="http://101.200.173.120:8080/LiangZiServer/videos?userId=" +""+Integer.valueOf(userId)+"&freshen=header"+"&count="+10;
		try {
			getData(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		UserInfoAdapter adapter=new UserInfoAdapter(this, list);
//		mListView.setAdapter(adapter);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
			case R.id.bt_user_follow:
				if (!otherUser){
					startActivity(new Intent(this,SettingActivity.class));
					finish();
				}
		boolean isFollow=user.isFollow();
				if (isFollow){
					Thread deleteThread = new Thread(new DeleteThread());
					deleteThread.start();
				}else {
					Thread postThread = new Thread(new postThread());
					postThread.start();
				}
				break;
//			case R.id.menu_btn:
//
//
//				break;
			default:
				break;

		}
		
	}
	/**
	 * 初始化PageViewer
	 */
	private void initPagerViewer() {
//		pager = (ViewPager) findViewById(R.id.viewpage);
//		final ArrayList<View> list = new ArrayList<View>();
//		Intent intent = new Intent(UserInfoActivity.this, A.class);
//		list.add(getView("A", intent));
//		Intent intent2 = new Intent(UserInfoActivity.this, B.class);
//		list.add(getView("B", intent2));
//
//		pager.setAdapter(new MyPagerAdapter(list));
//		pager.setCurrentItem(0);
//		pager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	/**
	 * 初始化动画
	 */
	private void InitImageView() {
//		cursor = (ImageView) findViewById(R.id.cursor);
//		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.roller)
//				.getWidth();// 获取图片宽度
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int screenW = dm.widthPixels;// 获取分辨率宽度
//		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
//		Matrix matrix = new Matrix();
//		matrix.postTranslate(offset, 0);
//		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}
	/**
	 * Pager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		List<View> list =  new ArrayList<View>();
		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position,
								Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}

	/**
	 * 用户详情接口
	 */
	public class getUserInfoThread implements Runnable
	{
		@Override
		public void run()
		{
			String url= MyAapplication.IP+"user"+"?userId="+userId+"&id="+id;
			try {
				getUserInfo(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//   备用子线程
			Log.e("log", "发出取消请求");
		}
	}

	/**
	 * 获取用户详细信息
	 * @param url
	 * @throws IOException
	 */
	public void getUserInfo(String url) throws IOException {
		Request request = new Request.Builder().
				url(url)
				.build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.getMessage();
			}

			@Override
			public void onResponse(Response response) throws IOException {
				Gson gson = new Gson();
				if (response.isSuccessful()) {
					Map<String,Object> InfoMap = gson.fromJson(response.body().charStream(), new TypeToken<Map<String,Object>>(){}.getType());
//					for(Map.Entry<String, Object> entry : InfoMap.entrySet()) {
//						String key = entry.getKey();
//						Object newstr = entry.getValue();
//						System.out.println("key:" + key + "," + newstr + ",");
//					}
					PeopleDetails peopleDetails = gson.fromJson(response.body().charStream(), PeopleDetails.class);
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
//					Log.e("videoInfoActivity", peopleDetails.toString());
				}
			}
		});
	}
	/**
	 * 初始化缓存设置
	 */
	private void initImageLoaderOptions(){
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
//			.showStubImage(R.drawable.index_iv02)			// 设置图片下载期间显示的图片
				.showImageOnLoading(R.drawable.bg_people)
				.showImageForEmptyUri(R.drawable.bg_live)	// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.headtest)		// 设置图片加载或解码过程中发生错误显示的图片
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
				.displayer(new RoundedBitmapDisplayer(20))	// 设置成圆角图片
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



	public void getData(String url) throws  Exception{
		Request request =new Request.Builder().
				url(url)
				.build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.getMessage();
			}

			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("exploreFragment", "response.toString()=" + response.toString());
				Gson gson = new Gson();
				Log.e("exploreFragment", "response.body().charStream()=" + response.body().toString());
				ServiceStatus serviceStatus = gson.fromJson(response.body().charStream(), new TypeToken<ServiceStatus>() {
				}.getType());
				VideoList=serviceStatus.getVideos();
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
//				Log.e("exploreFragment", "size....." + VideoList.size());
				String json = gson.toJson(serviceStatus);
			}
		});

	}
	public void getDataFotter(String url) throws  Exception{
		Request request =new Request.Builder().
				url(url)
				.build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.getMessage();
			}

			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("exploreFragment", "response.toString()=" + response.toString());
				Gson gson = new Gson();
				Log.e("exploreFragment", "response.body().charStream()=" + response.body().toString());
				ServiceStatus serviceStatus = gson.fromJson(response.body().charStream(), new TypeToken<ServiceStatus>() {
				}.getType());
				VideoList.addAll(VideoList.size(),serviceStatus.getVideos());
				Message msg = new Message();

				msg.what = 1;
				mHandler.sendMessage(msg);
				Log.e("exploreFragment", "size....." + VideoList.size());
				String json = gson.toJson(serviceStatus);
			}
		});

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
	 * 取消关注接口
	 */
	public class DeleteThread implements Runnable
	{
		@Override
		public void run()
		{
			String url= MyAapplication.IP+"follow"+"/"+userId+"/"+toUserId+"?accessToken="+accessToken;
			try {
				cancleFollow(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//   备用子线程
			Log.e("log", "发出取消请求");
		}
	}


	/**
	 * 添加关注
	 * @param url
	 * @throws IOException
	 */
	void followPost(String url) throws IOException {

		RequestBody formBody = new FormEncodingBuilder()
				.add("userId", userId)
				.add("toUserId", toUserId)
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
				if (response.isSuccessful()){
					Message msg=new Message();
					msg.what=2;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	/**
	 * 取消关注
	 * @param url
	 * @throws IOException
	 */
	public void cancleFollow(String url) throws IOException {
		Request request = new Request.Builder().
				url(url)
				.delete()
				.build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.getMessage();
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()){
					Message msg=new Message();
					msg.what=3;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

}


