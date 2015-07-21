package tv.liangzi.quantum.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.activity.ShowLiveActivity;
import tv.liangzi.quantum.activity.UserInfoActivity;
import tv.liangzi.quantum.activity.WatchLiveActivity;
import tv.liangzi.quantum.adapter.LiveAdaptertest;
import tv.liangzi.quantum.bean.Live;
import tv.liangzi.quantum.bean.LiveVideoStatus;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.fragment.AnimFragment.OnFragmentDismissListener;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import tv.liangzi.quantum.view.Rotate3dAnimation;


public class LiveFragmenttest extends BaseFragment implements LiveAdaptertest.OnItemButtonClickListener,AdapterView.OnItemClickListener,
		 OnClickListener, OnFragmentDismissListener ,StickyListHeadersListView.OnHeaderClickListener,
		StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
		StickyListHeadersListView.OnStickyHeaderChangedListener {

	private static final String TAG = "livefragment";
	private Activity mActivity;
//	private ZrcListView mListView;
	private TextView mTitleTv;
//	private LiveAdapter mAdapter;
	private String userId;
	private String accessToken;
	private List<Live> mLiveVideos=new ArrayList<Live>();
	private List<Live> mReaddVideos=new ArrayList<Live>();
	private Live liveTitle=new Live();
	private Live liveTitle1=new Live();
    private int mPosition;
	private int mSubid;
	private View viewContainer;
	private ImageView ulook;
	private StickyListHeadersListView mListView;
//	private TestBaseAdapter mAdapter;
	private LiveAdaptertest mAdapter;
	private SwipeRefreshLayout refreshLayout;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private String freshen="";
	private int  lastId;

	public static LiveFragment newInstance() {
		LiveFragment liveFragment = new LiveFragment();

		return liveFragment;
	}
	public Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 8:
					int state=msg.arg1;
					Live live1= (Live) msg.obj;
					//当前时间在预约时间之后需要更新直播状态为1然后发起直播
					if(state==0&&live1.getReserved()<=System.currentTimeMillis()){
						Intent  intent=new Intent(getActivity(),ShowLiveActivity.class);
						intent.putExtra("Living",live1);
						Log.e("roomid",live1.getChatroomId());
						startActivity(intent);
					}
					//视频状态为2 4 5 6的时候说明直播不存在了，提示视频不存在并且刷新列表
					else if (state==2||state==4||state==5||state==6){
						new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
								.setTitleText("直播状态")
								.setContentText("直播已经结束了!")
								.setConfirmText("确定")
								.show();
						//刷新数据
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
//						mLiveVideos.clear();
								String url = MyAapplication.IP + "lives" + "?userId=" + userId + "&count=" + 90 + "&lastId=" + lastId;
								try {
									mReaddVideos.clear();
									mLiveVideos.clear();
									getLives(url);
								} catch (IOException e) {
									e.printStackTrace();
								}
								refreshLayout.setRefreshing(false);
							}
						}, 1000);
						return;
					}
					//视频状态为1 3的时候 直接发起直播 showActivity直接发起直播流
					else if(state==1||state==3){
						Live live= (Live) msg.obj;
						Intent  intent=new Intent(getActivity(),ShowLiveActivity.class);
						intent.putExtra("Living",live);
						Log.e("roomid",live.getChatroomId());
						startActivity(intent);
					}

					break;
				case 9:
//					Intent intent9=new Intent(getActivity(),ShowLiveActivity.class);
//					intent9.putExtra("Living",mLiveVideos.get(position));
//					Log.e("roomid",mLiveVideos.get(position).getChatroomId());
//					startActivity(intent9);
					break;

				case 2:

					for(Iterator<Live> it = mLiveVideos.iterator();it.hasNext();){

						Live s = it.next();
						if (String.valueOf(s.getUserId()).equals(userId)){
							if (s.getState()==1){
								s.setGroupid("8");
								s.setState(3);
								//直播中终止的视频 要显示恢复直播
								mReaddVideos.add(s);
								it.remove();
;							}else if (s.getState()==0){
								long name=s.getReserved();
								long current=System.currentTimeMillis();
								if (s.getReserved()>System.currentTimeMillis()){
									//预约时间还未到 设置状态值9 即将发起
									s.setState(3);
									s.setGroupid("9");
									mReaddVideos.add(s);
									it.remove();
								}else {
									s.setState(3);
									s.setGroupid("10");
									//预约时间已经到了 设置状态值10点击直播
									mReaddVideos.add(s);
									it.remove();
								}

							}

						}

					}
//
					for (int i = mReaddVideos.size()-1; i >=0 ; i--) {
						mLiveVideos.add(0,mReaddVideos.get(i));
					}

					mAdapter.setLives(mLiveVideos);
					mAdapter.notifyDataSetChanged();

					break;
				case 3:
//					mListView.setLoadMoreSuccess();
//					liveTitle.setState(2);
//					mReaddVideos.add(liveTitle);
//					for (int i = 0; i <mLiveVideos.size() ; i++) {
//						if (mLiveVideos.get(i).getState()==1){
//							mReaddVideos.add(mLiveVideos.get(i));
//						}
//					}
//					liveTitle1.setState(3);
//					mReaddVideos.add(liveTitle1);
//					for (int i = 0; i <mLiveVideos.size() ; i++) {
//						if (mLiveVideos.get(i).getState()==0){
//							mReaddVideos.add(mLiveVideos.get(i));
//						}
//					}
//
//					mAdapter.setLives(mReaddVideos);
//					mAdapter.notifyDataSetChanged();
					break;
//
				case 4:
					String error= (String) msg.obj;
					Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
					break;
				case 5:

//					mAdapter.setLives(mReaddVideos);
					Toast.makeText(getActivity(),"订阅成功",Toast.LENGTH_SHORT).show();
//					mAdapter.notifyDataSetChanged();
					break;
				case 6:
//					mLiveVideos.get(mPosition).setSubscibeId(0);
//					mAdapter.setLives(mReaddVideos);
					Toast.makeText(getActivity(),"取消订阅成功",Toast.LENGTH_SHORT).show();
//					mAdapter.notifyDataSetChanged();
					break;
				case 7:
					Toast.makeText(getActivity(),"直播列表为空",Toast.LENGTH_SHORT).show();
//					mListView.setRefreshSuccess("当前没有视频信息");
//					mAdapter.notifyDataSetChanged();
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_livetest, container,
				false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initViews(View view) {
		initImageLoaderOptions();
		getScreenSize(getActivity());
		userId= (String) SharedPreferencesUtils.getParam(getActivity(), "userInfo","userId", "");
		String photo= (String) SharedPreferencesUtils.getParam(getActivity(), "userInfo", "photo", "");
		accessToken= (String) SharedPreferencesUtils.getParam(getActivity(),"userInfo","accessToken","");
		ImageView imHead=(ImageView) view.findViewById(R.id.im_title_head);
		imageLoader.displayImage(photo, imHead, options, animateFirstListener);
		imHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), UserInfoActivity.class);
//				intent.putExtra("userId",userId);
				startActivity(intent);
			}
		});

		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
//						mLiveVideos.clear();
						String url= MyAapplication.IP+"lives"+"?userId="+userId+"&count="+90+"&lastId="+lastId;
						try {
							mReaddVideos.clear();
							mLiveVideos.clear();
							getLives(url);
						} catch (IOException e) {
							e.printStackTrace();
						}
//						Thread liveThread = new Thread(new LiveThread());
//						liveThread.start();
						refreshLayout.setRefreshing(false);
					}
				}, 1000);
			}
		});


		mAdapter = new LiveAdaptertest(getActivity(), mLiveVideos, Integer.valueOf(userId),screenWidth);
		mAdapter.setButtonOnClickListener(this);
		mListView = (StickyListHeadersListView) view.findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnHeaderClickListener(this);
		mListView.setOnStickyHeaderChangedListener(this);
		mListView.setOnStickyHeaderOffsetChangedListener(this);
//		stickyList.addHeaderView(getActivity().getLayoutInflater().inflate(R.layout.list_header, null));
//		stickyList.addFooterView(getActivity().getLayoutInflater().inflate(R.layout.list_footer, null));
//		stickyList.setEmptyView(view.findViewById(R.id.empty));
		mListView.setDrawingListUnderStickyHeader(true);
		mListView.setAreHeadersSticky(true);
//      mListView=(ZrcListView) view.findViewById(R.id.xlistview);

		 Thread liveThread = new Thread(new LiveThread());
		liveThread.start();


      mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		  @Override
		  public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			  Intent intent;
			  if (mLiveVideos.get(position).getState()==1){

//				  getStateThread stateThread=new getStateThread();
//				  stateThread.setLiveId(mLiveVideos.get(position).getLiveId());
//				  stateThread.setmPosition(position);
//				  Thread thread=new Thread(stateThread);
//				  thread.start();
					  intent=new Intent(getActivity(),WatchLiveActivity.class);
					  intent.putExtra("Living",mLiveVideos.get(position));
					  Log.e("roomid",mLiveVideos.get(position).getChatroomId());
					  startActivity(intent);
			  }else if(mLiveVideos.get(position).getState()==3) {
				  if (mLiveVideos.get(position).getGroupid().equals("8")) {
					  getStateThread stateThread = new getStateThread();
					  stateThread.setLiveId(mLiveVideos.get(position).getLiveId());
					  stateThread.setmPosition(position);
					  Thread thread = new Thread(stateThread);
					  thread.start();
				  } else if (mLiveVideos.get(position).getGroupid().equals("10")) {
					  getStateThread stateThread = new getStateThread();
					  stateThread.setLiveId(mLiveVideos.get(position).getLiveId());
					  stateThread.setmPosition(position);
					  Thread thread = new Thread(stateThread);
					  thread.start();
				  } else if (mLiveVideos.get(position).getGroupid().equals("9")) {
					  //即将发起的 还没发起 不能点击

				  }
			  }
		  }
	  }) ;
	}

	@Override
	public void onResume() {
//		mListView.refresh();
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}




	@Override
	public String getFragmentName() {
		return TAG;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_btn:
//			showAnimFragment();

			RequestBody formBody = new FormEncodingBuilder()
					.add("userId", userId)
//					.add("liveId", toUserId)
					.add("accessToken", accessToken)
					.build();
			break;

		default:
			break;
		}
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
	public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {

	}

	@Override
	public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {

	}

	@Override
	public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (mLiveVideos.get(position).getState()==1){
			Intent intent=new Intent(getActivity(),ShowLiveActivity.class);
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
	/**
	 * ���ز�߶���fragment
	 */
	private void dismissAnimFragment() {
		getFragmentManager().popBackStack();
	}

	@Override
	public void onFragmentDismiss() {
		dismissAnimFragment();
	}



	class LiveThread implements Runnable
	{
		@Override
		public void run()
		{
			String url= MyAapplication.IP+"lives"+"?userId="+userId+"&count="+90+"&freshen="+freshen+"&lastId="+lastId;
			try {
				getLives(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.e("log", "查询直播列表请求"+"lives"+"?userId="+userId+"&count="+10+"&freshen="+freshen+"&lastId="+lastId);
		}
	}
	void getLives(String url) throws IOException {
		Request request = new Request.Builder().
				url(url)
				.build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Message msg = new Message();
				msg.what = 4;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
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
								msg.what = 2;
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
				else{
					Log.e(TAG,"fail");
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
				Log.e("log", "订阅请求");
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
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()){
					Message msg=new Message();
					msg.what=5;
					mHandler.sendMessage(msg);
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
					Log.e("log", "取消订阅请求");
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
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()){
					Message msg=new Message();
					msg.what=6;
					mHandler.sendMessage(msg);
				}
			}
		});
	}
	/**查询直播状态接口
	 * 接口
	 */
	public class getStateThread implements Runnable
	{
		private int mLiveId;
		private int mPosition;
		private void setLiveId(int liveID){
			mLiveId=liveID;
		}
		private void setmPosition(int position){
			mPosition=position;
		}
		@Override
		public void run()
		{
			String url= MyAapplication.IP+"live"+"/"+mLiveId+"?userId="+userId;
			try {
				getLiveState(url,mPosition);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.e(TAG, "获取直播信息请求");
		}
	}
	/**
	 *获取直播状态
	 * @param url
	 * @throws IOException
	 */
	public void getLiveState(String url, final int position) throws IOException {
		Request request = new Request.Builder().
				url(url)
				.get()
				.build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.getMessage();
				Log.e("log", "直播信息请求失败="+e.getMessage().toString());
			}

			@Override
			public void onResponse(Response response) throws IOException {
				Gson gson = new Gson();
				if (response.isSuccessful()) {
					Live liveState = gson.fromJson(response.body().charStream(), new TypeToken<Live>() {
					}.getType());
					if (liveState.getResponseCode().equals("200")) {
						if (liveState!=null) {
							int mState=liveState.getState();
							Message msg = new Message();
							msg.arg1=mState;
							msg.obj=liveState;
							msg.what = 8;
							mHandler.sendMessage(msg);
						} else {
							Log.e(TAG, "201，liveduixiangweikong");
						}

					} else if (liveState.getResponseCode().equals("500")) {
						Log.e("videoInfoActivity", "连接服务器失败");
					} else {
						Log.e(TAG, "其它错误");
					}
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
				.showImageOnLoading(R.drawable.ic_loading)
				.showImageForEmptyUri(R.drawable.default_head)	// 设置图片Uri为空或是错误的时候显示的图片
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
				.bitmapConfig(Bitmap.Config.RGB_565)
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
	public void getScreenSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}
	}
