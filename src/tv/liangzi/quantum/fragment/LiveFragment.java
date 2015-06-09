package tv.liangzi.quantum.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.activity.ChatActivity;
import tv.liangzi.quantum.activity.ShowLiveActivity;
import tv.liangzi.quantum.activity.StartLiveActivity;
import tv.liangzi.quantum.activity.TimePickerActivity;
import tv.liangzi.quantum.activity.UserInfoActivity;
import tv.liangzi.quantum.adapter.LiveAdapter;
import tv.liangzi.quantum.bean.Live;
import tv.liangzi.quantum.bean.LiveVideoStatus;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.fragment.AnimFragment.OnFragmentDismissListener;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;


public class LiveFragment extends BaseFragment implements
		OnItemClickListener, OnClickListener, OnFragmentDismissListener ,LiveAdapter.OnItemButtonClickListener {

	private static final String TAG = "CategoryFragment";
	private Activity mActivity;
	private ZrcListView mListView;
	private TextView mTitleTv;
	private LiveAdapter mAdapter;
	private String userId;
	private String accessToken;
	private List<Live> mLiveVideos=new ArrayList<Live>();
	private List<Live> mReaddVideos=new ArrayList<Live>();
	private Live liveTitle=new Live();
	private Live liveTitle1=new Live();
    private int mPosition;

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
				case 1:

					liveTitle.setState(2);
					mReaddVideos.add(liveTitle);
					for (int i = 0; i <mLiveVideos.size() ; i++) {
						if (mLiveVideos.get(i).getState()==1){
							mReaddVideos.add(mLiveVideos.get(i));
						}
					}
					liveTitle1.setState(3);
					mReaddVideos.add(liveTitle1);
					for (int i = 0; i <mLiveVideos.size() ; i++) {
						if (mLiveVideos.get(i).getState()==0){
							mReaddVideos.add(mLiveVideos.get(i));
						}
					}

					mAdapter.setLives(mLiveVideos);
					mAdapter.notifyDataSetChanged();
					break;
				case 2:
					mListView.setRefreshSuccess("Ulook");
					liveTitle.setState(2);
					mReaddVideos.add(liveTitle);
					for (int i = 0; i <mLiveVideos.size() ; i++) {
						if (mLiveVideos.get(i).getState()==1){
							mReaddVideos.add(mLiveVideos.get(i));
						}
					}
					liveTitle1.setState(3);
					mReaddVideos.add(liveTitle1);
					for (int i = 0; i <mLiveVideos.size() ; i++) {
						if (mLiveVideos.get(i).getState()==0){
							mReaddVideos.add(mLiveVideos.get(i));
						}
					}

					mAdapter.setLives(mReaddVideos);
					mAdapter.notifyDataSetChanged();

					mListView.startLoadMore(); // 开启LoadingMore功能
					break;
				case 3:
					mListView.setLoadMoreSuccess();
					liveTitle.setState(2);
					mReaddVideos.add(liveTitle);
					for (int i = 0; i <mLiveVideos.size() ; i++) {
						if (mLiveVideos.get(i).getState()==1){
							mReaddVideos.add(mLiveVideos.get(i));
						}
					}
					liveTitle1.setState(3);
					mReaddVideos.add(liveTitle1);
					for (int i = 0; i <mLiveVideos.size() ; i++) {
						if (mLiveVideos.get(i).getState()==0){
							mReaddVideos.add(mLiveVideos.get(i));
						}
					}

					mAdapter.setLives(mReaddVideos);
					mAdapter.notifyDataSetChanged();
					break;
//
				case 4:
					String error= (String) msg.obj;
					Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
					break;
				case 5:
					mReaddVideos.get(mPosition).setSubscibeId(1);
					mAdapter.setLives(mReaddVideos);
					Toast.makeText(getActivity(),"订阅成功",Toast.LENGTH_SHORT).show();
//					mAdapter.notifyDataSetChanged();
					break;
				case 6:
					mReaddVideos.get(mPosition).setSubscibeId(0);
					mAdapter.setLives(mReaddVideos);
					Toast.makeText(getActivity(),"取消订阅成功",Toast.LENGTH_SHORT).show();
//					mAdapter.notifyDataSetChanged();
					break;
				case 7:
					Toast.makeText(getActivity(),"直播列表为空",Toast.LENGTH_SHORT).show();
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
		View view = inflater.inflate(R.layout.fragment_live, container,
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
		userId= (String) SharedPreferencesUtils.getParam(getActivity(), "userId", "");
		accessToken= (String) SharedPreferencesUtils.getParam(getActivity(),"accessToken","");
		ImageView imHead=(ImageView) view.findViewById(R.id.im_title_head);

		imHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(),UserInfoActivity.class);
				intent.putExtra("userId",userId);
//				intent.putExtra("userDetail",peopleDetails);
				startActivity(intent);
			}
		});
      mListView=(ZrcListView) view.findViewById(R.id.xlistview);

		freshen="";
		mLiveVideos.clear();
		 Thread liveThread = new Thread(new LiveThread());
		liveThread.start();
		// 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
//		float density = getResources().getDisplayMetrics().density;
//		mListView.setFirstTopOffset((int) (50 * density));

		// 设置下拉刷新的样式
		SimpleHeader header = new SimpleHeader(getActivity());
		header.setTextColor(getResources().getColor(R.color.red_bg));
		header.setCircleColor(getResources().getColor(R.color.red_bg));
		mListView.setHeadable(header);

// 设置加载更多的样式
//		SimpleFooter footer = new SimpleFooter(getActivity());
//		footer.setCircleColor(0xff33bbee);
//		mListView.setFootable(footer);

// 设置列表项出现动画
		mListView.setItemAnimForTopIn(R.anim.topitem_in);
		mListView.setItemAnimForBottomIn(R.anim.bottomitem_in);


		// 下拉刷新事件回调（可选）
		mListView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
			@Override
			public void onStart() {
				mLiveVideos.clear();
				mReaddVideos.clear();
				Thread liveThread = new Thread(new LiveThread());
				liveThread.start();
			}
		});
//		//加载更多回调
//		mListView.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
//			@Override
//			public void onStart() {
//				freshen="footer";
//			lastId =mLiveVideos.get(mLiveVideos.size()-1).getLiveId();
//				Thread liveThread = new Thread(new LiveThread());
//				liveThread.start();
//			}
//		});
        mAdapter=new LiveAdapter(getActivity(), mLiveVideos);
		mAdapter.setButtonOnClickListener(this);
        mListView.setAdapter(mAdapter);

      mListView.setOnItemClickListener(new ZrcListView.OnItemClickListener() {
		  @Override
		  public void onItemClick(ZrcListView parent, View view, int position, long id) {
			  Intent intent=new Intent(getActivity(),ShowLiveActivity.class);
			  intent.putExtra("roomId",mReaddVideos.get(position).getChatroomId());
			  intent.putExtra("rtmpUrl",mReaddVideos.get(position).getRtmpPlayUrl());
			  intent.putExtra("userid",mReaddVideos.get(position).getUserId());
			  intent.putExtra("nikeName",mReaddVideos.get(position).getNickName());
			  intent.putExtra("shareUrl",mReaddVideos.get(position).getShareUrl());
			  startActivity(intent);
		  }
	  });
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
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
		TextView count= (TextView) view.findViewById(R.id.tv_schedule_concerned_count);
		ImageView ulook= (ImageView)view.findViewById(R.id.icon_ulooked);
		if (subid==0){
			count.setTextColor(Color.parseColor("#B90B0E"));
			ulook.setImageResource(R.drawable.subscribe_middle);
			subscribeThread threadSub=new subscribeThread();
			threadSub.setLiveId(id+"");
			threadSub.setSubscribeNum(subid);
			Thread subscribeTh= new Thread(threadSub);
			subscribeTh.start();
		}else{
			count.setTextColor(Color.WHITE);
			ulook.setImageResource(R.drawable.unsubscribe_middle);
			unsubscribeThread threadunSub=new unsubscribeThread();
			threadunSub.setLiveId(id+"");
			threadunSub.setSubscribeNum(subid);
			Thread unsubscribeTh= new Thread(threadunSub);
			unsubscribeTh.start();
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
							if (freshen == "") {
								msg.what = 2;
							} else {
								msg.what = 3;
							}
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
	}
