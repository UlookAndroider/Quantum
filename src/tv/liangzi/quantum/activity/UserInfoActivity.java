package tv.liangzi.quantum.activity;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
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

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.adapter.LiveAdaptertest;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.Live;
import tv.liangzi.quantum.bean.LiveVideoStatus;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.bean.ServiceStatus;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.fragment.AnimFragment;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import tv.liangzi.quantum.view.Rotate3dAnimation;

public class UserInfoActivity extends BaseActivity implements LiveAdaptertest.OnItemButtonClickListener,AdapterView.OnItemClickListener,
		OnClickListener, AnimFragment.OnFragmentDismissListener,StickyListHeadersListView.OnHeaderClickListener,
		StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
		StickyListHeadersListView.OnStickyHeaderChangedListener{

	private StickyListHeadersListView mListView;
	private String userId;
	private String id;
	private PeopleDetails user;
	private ImageView im_user_dead;
	private TextView tv_user_concerned;
	private TextView tv_user_fans;
	private TextView tv_user_loves;
	private TextView nikename;
	private TextView place;
	private TextView tv_decrip;
	private ImageView bt_user_follow;
	private String toUserId="";
	private String accessToken;
	LocalActivityManager manager = null;
	ViewPager pager = null;
	TabHost tabHost = null;
	TextView t1,t2,t3;
	private int mSubid;

	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private ImageView cursor;// 动画图片
    private int mPosition;
	private static int screenWidth = 0;
	private View viewContainer;
	private ImageView ulook;
	private ImageView menuBtn;
	private ImageView backBtn;
	private  boolean otherUser;
	private List<Live> mLiveVideos=new ArrayList<Live>();
	private LiveAdaptertest mAdapter;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();


	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					mAdapter.setLives(mLiveVideos);
					mAdapter.notifyDataSetChanged();
					break;
				case 2:
					user.setIsFollow(true);
					user.setFansNum(user.getFansNum()+1);
					tv_user_fans.setText(String.valueOf(user.getFansNum()));
//					peopleAdapter.notifyDataSetChanged();
					bt_user_follow.setImageResource(R.drawable.followed);
					Toast.makeText(UserInfoActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					user.setIsFollow(false);
					bt_user_follow.setImageResource(R.drawable.follow);
					user.setFansNum(user.getFansNum()-1);
					tv_user_fans.setText(String.valueOf(user.getFansNum()));
					Toast.makeText(UserInfoActivity.this,"取消关注成功",Toast.LENGTH_SHORT).show();
					break;
				case 4:
					initdisplay(user);
					break;
				case 5:
					mLiveVideos.get(mPosition).setSubscibeId(1);
					mAdapter.setLives(mLiveVideos);
//					Toast.makeText(getActivity(),"订阅成功",Toast.LENGTH_SHORT).show();
//					mAdapter.notifyDataSetChanged();
					break;
				case 6:
					mLiveVideos.get(mPosition).setSubscibeId(0);
					mAdapter.setLives(mLiveVideos);
//					Toast.makeText(getActivity(),"取消订阅成功",Toast.LENGTH_SHORT).show();
//					mAdapter.notifyDataSetChanged();
					break;
				case 7:
					Toast.makeText(UserInfoActivity.this,"直播为空",Toast.LENGTH_SHORT).show();
//					mAdapter.notifyDataSetChanged();
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
		getScreenSize(UserInfoActivity.this);
		imageLoader.init(ImageLoaderConfiguration.createDefault(UserInfoActivity.this));


	}
	public void getScreenSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
	}
	@Override
	public void initViews() {
		userId= (String) SharedPreferencesUtils.getParam(this, "userInfo","userId", "");
		accessToken= (String) SharedPreferencesUtils.getParam(this,"userInfo","accessToken","");
		tv_user_concerned= (TextView) findViewById(R.id.tv_user_concerned);
		tv_user_fans=(TextView)findViewById(R.id.tv_user_fens);
		tv_user_loves= (TextView) findViewById(R.id.tv_user_loves);
		nikename= (TextView) findViewById(R.id.tv_user_nike);
		place=(TextView)findViewById(R.id.tv_place_id);
		im_user_dead= (ImageView) findViewById(R.id.im_user_dead);
		menuBtn=(ImageView)findViewById(R.id.setting_btn);
		bt_user_follow= (ImageView) findViewById(R.id.bt_user_follow);
		backBtn= (ImageView) findViewById(R.id.back_btn);
//		TextView tv= (TextView) findViewById(R.id.title_tv);
//		tv.setText("个人主页");
//		ImageView imHead=(ImageView) findViewById(R.id.im_title_head);
//       imHead.setVisibility(View.GONE);
		mListView= (StickyListHeadersListView) findViewById(R.id.lv_userinfo);
		mAdapter = new LiveAdaptertest(UserInfoActivity.this, mLiveVideos, Integer.valueOf(userId),screenWidth);

		mListView.setAdapter(mAdapter);
		mAdapter.setButtonOnClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnHeaderClickListener(this);
		mListView.setOnStickyHeaderChangedListener(this);
		mListView.setOnStickyHeaderOffsetChangedListener(this);
//		stickyList.addHeaderView(getActivity().getLayoutInflater().inflate(R.layout.list_header, null));
//		stickyList.addFooterView(getActivity().getLayoutInflater().inflate(R.layout.list_footer, null));
		mListView.setEmptyView(findViewById(R.id.tv_empty));
		mListView.setDrawingListUnderStickyHeader(true);
		mListView.setAreHeadersSticky(true);
//      mListView=(ZrcListView) view.findViewById(R.id.xlistview);


		Intent intent=getIntent();
		//来自搜索、排行榜的点击以及直播界面中的头像点击 首先判断是不是用户自己 若不是直接调用接口，如果是直接查询sp信息
		if(intent.getParcelableExtra("userDetail")!=null){
			user= (PeopleDetails) intent.getParcelableExtra("userDetail");
			toUserId=user.getUserId()+"";
			if (userId.equals(toUserId)){
				initDisplaySelf();
			}else {
				otherUser=true;
				Thread getUserInfoThread = new Thread(new getUserInfoThread());
				getUserInfoThread.start();
			}
		}else if(intent.getParcelableExtra("living")!=null){
			Live liveUser= (Live) intent.getParcelableExtra("living");
			toUserId=liveUser.getUserId()+"";
			if (userId.equals(toUserId)){
				initDisplaySelf();
			}else {
				otherUser=true;
				Thread getUserInfoThread = new Thread(new getUserInfoThread());
				getUserInfoThread.start();
			}
			}else
			initDisplaySelf();

		}

	private void initDisplaySelf() {
		otherUser=false;
		menuBtn.setVisibility(View.VISIBLE);
		tv_user_concerned.setText((String) SharedPreferencesUtils.getParam(this, "userInfo", "focusNum", ""));
		tv_user_fans.setText((String) SharedPreferencesUtils.getParam(this,"userInfo", "fansNum", ""));
		tv_user_loves.setText(String.valueOf(SharedPreferencesUtils.getParam(this,"userInfo", "liveLikes", 0)));
		nikename.setText((String) SharedPreferencesUtils.getParam(this, "userInfo","nickName", ""));
		place.setText((String) SharedPreferencesUtils.getParam(this, "userInfo","sign", ""));
		imageLoader.displayImage((String) SharedPreferencesUtils.getParam(this, "userInfo","photo", ""), im_user_dead, options, animateFirstListener);
		bt_user_follow.setVisibility(View.GONE);
	}


	private void initdisplay(PeopleDetails user) {
		tv_user_concerned.setText(String.valueOf(user.getFocusNum()));
		tv_user_fans.setText(String.valueOf(user.getFansNum()));
		tv_user_loves.setText(String.valueOf(user.getLiveLikes()));
		nikename.setText(user.getNickName());
		place.setText(user.getSign());
		imageLoader.displayImage(user.getPhoto(), im_user_dead, options, animateFirstListener);
		if (user.isFollow()){
			bt_user_follow.setImageResource(R.drawable.followed);
		}else{
			bt_user_follow.setImageResource(R.drawable.follow);
		}

	}
	@Override
	public void initListeners() {
		menuBtn.setOnClickListener(this);
		bt_user_follow.setOnClickListener(this);
		backBtn.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
        if (toUserId==null||toUserId.equals("")){
            toUserId=userId;
        }
		String url=MyAapplication.IP+"lives"+"?userId="+userId+"&publisherId="+toUserId+"&count="+50;
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
//				if (!otherUser){
//					startActivity(new Intent(this,SettingActivity.class));
//					finish();
//				}
		boolean isFollow=user.isFollow();
				if (isFollow){
					Thread deleteThread = new Thread(new DeleteThread());
					deleteThread.start();
				}else {
					Thread postThread = new Thread(new postThread());
					postThread.start();
				}
				break;
			case R.id.setting_btn:
				Intent intent=new Intent(UserInfoActivity.this,SettingActivity.class);
				startActivity(intent);
				break;
			case R.id.back_btn:
				finish();
				break;
			default:
				break;

		}
		
	}




	@Override
	public void onFragmentDismiss() {

	}

	@Override
	public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {

	}

	@Override
	public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {

	}

	@Override
	public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {

	}

	/**
	 * 用户详情接口
	 */
	public class getUserInfoThread implements Runnable
	{
		@Override
		public void run()
		{
			String url= MyAapplication.IP+"user"+"?userId="+userId+"&id="+toUserId;
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
					user = gson.fromJson(response.body().charStream(), PeopleDetails.class);
					Message msg = new Message();
					msg.what = 4;
					mHandler.sendMessage(msg);
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
				Gson gson = new Gson();
				if (response.isSuccessful()) {
					LiveVideoStatus liveVideoStatus = gson.fromJson(response.body().charStream(), new TypeToken<LiveVideoStatus>() {
					}.getType());
					if (liveVideoStatus.getResponseCode().equals("200")) {
						if (liveVideoStatus.getLives().size() > 0) {
							mLiveVideos = liveVideoStatus.getLives();
							Message msg = new Message();
								msg.what = 1;
							mHandler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = 7;
							mHandler.sendMessage(msg);
							Log.e("livefragment", "直播数据null....................");
						}

					} else if (liveVideoStatus.getResponseCode().equals("500")) {
						Log.e("videoInfoActivity", "连接服务器失败");
					} else {
						Message msg = new Message();
						msg.what = 5;
						msg.obj = liveVideoStatus.getResponseMsg();
						mHandler.sendMessage(msg);
					}
				}
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
				Message msg = new Message();

				msg.what = 1;
				mHandler.sendMessage(msg);
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
				Log.e("log", "添加关注请求失败="+e.getMessage().toString());
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()){
					Message msg=new Message();
					msg.what=2;
					mHandler.sendMessage(msg);
					Log.e("log", "添加关注success="+response.message().toString());
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
				Log.e("log", "取消请求失败="+e.getMessage().toString());
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()){
					Message msg=new Message();
					msg.what=3;
					mHandler.sendMessage(msg);
					Log.e("log", "取消请求success=" + response.message().toString());
				}{
					Log.e("log", "取消请求fail="+response.message().toString());
				}
			}
		});
	}
    class subscribeThread  implements Runnable
    {
        private String liveId;
        private  int subscribeNum;

        public void setSubscribeNum(int subscribeNum) {
            this.subscribeNum = subscribeNum;
        }

        public void setLiveId(String liveId) {
            this.liveId = liveId;
        }

        @Override
        public void run()
        {
            try {
                FormEncodingBuilder	 Body = new FormEncodingBuilder();
                Body.add("userId", userId)
                        .add("liveId", liveId)
                        .add("accessToken", accessToken);
                Log.e("sub", "订阅请求");
                String url= MyAapplication.IP+"liveSubscribe";
                subscribePost(url,Body);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    void subscribePost(String url,FormEncodingBuilder Body) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .post(Body.build())
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                request.body().toString();
                Message msg=new Message();
                msg.what=4;
                msg.obj=e.getMessage();
                mHandler.sendMessage(msg);
				Log.e("sub", "订阅请求Failure"+e.getMessage().toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()){
                    Message msg=new Message();
                    msg.what=5;
                    mHandler.sendMessage(msg);
					Log.e("sub", "订阅请求success" + response.message().toString());
                }else {
					Log.e("sub", "订阅请求fail"+response.message().toString());
				}

            }
        });
    }

    class unsubscribeThread  implements Runnable
    {
        private String liveId;
        private  int subscribeNum;

        public void setSubscribeNum(int subscribeNum) {
            this.subscribeNum = subscribeNum;
        }

        public void setLiveId(String liveId) {
            this.liveId = liveId;
        }

        @Override
        public void run()
        {
            String url= MyAapplication.IP+"liveSubscribe?"+"accessToken="+accessToken+"&userId="+userId+"&liveId="+liveId;
            try {
                Log.e("sub", "取消订阅请求");
                unsubscribePost(url);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    void unsubscribePost(String url) throws IOException {


        Request	request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                request.body().toString();
                Message msg=new Message();
                msg.what=4;
                msg.obj=e.getMessage();
                mHandler.sendMessage(msg);
				Log.e("sub", "取消订阅请求Failure"+e.getMessage().toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()){
                    Message msg=new Message();
                    msg.what=6;
                    mHandler.sendMessage(msg);
					Log.e("sub", "订阅请求success"+response.message().toString());
                }else{
					response.message().toString();
					Log.e("sub", "取消订阅请求Failure" + response.message().toString());
				}

            }
        });
    }


	/**
	 * adapter中item的点击事件
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(View view, int position, int id, int subid) {
		mPosition=position;
		viewContainer=view;
		mSubid=subid;
		int Subscibes=mLiveVideos.get(mPosition).getSubscibes();
		TextView count= (TextView) view.findViewById(R.id.tv_concerned_count_ulook);
		ulook= (ImageView)view.findViewById(R.id.icon_ulooked);
		// 准备ImageView
//		ulook.setClickable(true);
//		ulook.setFocusable(true);
//		ulook.setOnClickListener(this);

		if (mSubid == 0) {
			mLiveVideos.get(mPosition).setSubscibeId(1);
			//没有预约现在预约
			count.setText(Subscibes+1+"");
			mLiveVideos.get(mPosition).setSubscibes(Subscibes+1);
			count.setTextColor(Color.parseColor("#B90B0E"));

			applyRotation(0, 0, -720,ulook);
			subscribeThread threadSub=new subscribeThread();
			threadSub.setLiveId(id+"");
			threadSub.setSubscribeNum(subid);
			Thread subscribeTh= new Thread(threadSub);
			subscribeTh.start();
		}else{
			mLiveVideos.get(mPosition).setSubscibeId(0);
			//已经预约，现在取消预约
			if (Subscibes>0){
				count.setText(Subscibes-1+"");
				mLiveVideos.get(mPosition).setSubscibes(Subscibes-1);
			}
			count.setTextColor(Color.WHITE);
//			ulook.setImageResource(R.drawable.unsubscribe_middle);
			applyRotation(1, 0, -720,ulook);
			unsubscribeThread threadunSub=new unsubscribeThread();
			threadunSub.setLiveId(id+"");
			threadunSub.setSubscribeNum(subid);
			Thread unsubscribeTh= new Thread(threadunSub);
			unsubscribeTh.start();
		}



	}
	/**
	 * 使用Rotate3d实现翻转
	 * @param position 1-6对应picture1-6；-1对应list
	 * @param start 翻转起始角度
	 * @param end 翻转终止角度
	 */
	private void applyRotation(int position, float start, float end,ImageView imageView) {
		// 计算中心点
		final float centerX = viewContainer.getWidth() / 2.0f;
		final float centerY = viewContainer.getHeight() / 2.0f;
		final Rotate3dAnimation rotation =
				new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
		rotation.setDuration(200);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		//设置监听
		rotation.setAnimationListener(new DisplayNextView(position,imageView));

		viewContainer.startAnimation(rotation);
	}




	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (mLiveVideos.get(position).getState()==1){
			Intent intent=new Intent(UserInfoActivity.this,ShowLiveActivity.class);
			intent.putExtra("Living",mLiveVideos.get(position));
			Log.e("roomid",mLiveVideos.get(position).getChatroomId());
			startActivity(intent);
		}

	}

	/**
	 * 用于监听前90度翻转完成
	 */
	private final class DisplayNextView implements Animation.AnimationListener {
		private final int mPosition;
		private  ImageView mImageview;

		private DisplayNextView(int position,ImageView imageView) {
			mPosition = position;
			mImageview=imageView;
		}

		public void onAnimationStart(Animation animation) {
		}
		//动画结束
		public void onAnimationEnd(Animation animation) {
			viewContainer.post(new SwapViews(mPosition,mImageview));
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * 用于翻转剩下的90度
	 */
	private final class SwapViews implements Runnable {
		private final int mPosition;
		private ImageView mImageView;
		public SwapViews(int position,ImageView imageView) {
			mPosition = position;
			mImageView=imageView;
		}

		public void run() {
			final float centerX = viewContainer.getWidth() / 2.0f;
			final float centerY = viewContainer.getHeight() / 2.0f;
			Rotate3dAnimation rotation;

			if (mPosition ==1) {
				//显示ImageView
//				mImageView.requestFocus();
				rotation = new Rotate3dAnimation(-720, -1440, centerX, centerY, 310.0f, false);
				mImageView.setImageResource(R.drawable.unsubscribe_middle);
			} else{
				rotation = new Rotate3dAnimation(-720, -1440, centerX, centerY, 310.0f, false);
				mImageView.setImageResource(R.drawable.subscribe_middle);
			}

			//显示ImageView
//			mPhotosList.setVisibility(View.GONE);
//			ulook.setVisibility(View.VISIBLE);
//			ulook.requestFocus();
//
//			rotation = new Rotate3dAnimation(90, 180, centerX, centerY, 310.0f, false);

			rotation.setDuration(200);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());//动画播放速度
			//开始动画
			viewContainer.startAnimation(rotation);
		}
	}
}


