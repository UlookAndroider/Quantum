package tv.liangzi.quantum.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.impl.client.DefaultUserTokenHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.activity.UserInfoActivity;
import tv.liangzi.quantum.activity.videoInfoPageActivity;
import tv.liangzi.quantum.adapter.ExploreAdapter;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.bean.ServiceStatus;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.ImageToSD;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import tv.liangzi.quantum.view.TopIndicator.OnTopIndicatorListener;
import tv.liangzi.quantum.view.XListView;
import zrc.widget.SimpleFooter;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcAbsListView;
import zrc.widget.ZrcListView;

public class ExploreFragment extends BaseFragment implements ZrcListView.OnScrollListener,ZrcListView.OnItemClickListener {
	public static ExploreFragment newInstance() {
		ExploreFragment exploreFragment = new ExploreFragment();
		return exploreFragment;
	}
//	private SharedPreferences sp;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					mAdapter.setList(VideoList);
					mAdapter.notifyDataSetChanged();
					break;
				case 2:
					Toast.makeText(getActivity(),"服务器错误",Toast.LENGTH_SHORT).show();
					break;
				default:
					break ;
			}
		}
	};
	public static final String TAG = "HomeFragment";
	private Activity mActivity;
	private TextView mTitleTv;
	private zrc.widget.ZrcListView listView;
	private TabPagerAdapter mPagerAdapter;
	private List<ServiceStatus.Video> Videos=new ArrayList<ServiceStatus.Video>();
	private ExploreAdapter mAdapter;
	List<ServiceStatus.Video> VideoList=new ArrayList<ServiceStatus.Video>();
	private PeopleDetails peopleDetails;
	private String userid;

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
		View view = inflater.inflate(R.layout.fragment_explore, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			initViews(view);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initDisplay();
	}

	private void initViews(View view) throws Exception {
//		sp=getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
//	    peopleDetails= MyAapplication.getApplicationuser();
		ImageView imHead=(ImageView) view.findViewById(R.id.im_title_head);
		String photo= (String) SharedPreferencesUtils.getParam(getActivity(),"photo","");
		userid=(String) SharedPreferencesUtils.getParam(getActivity(),"userId","");
//		imHead.setImageDrawable(ImageToSD.loadImageFromUrl(photo,userid));//图片接口调通后处理
		imHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), UserInfoActivity.class);
				intent.putExtra("userId", userid);
//				intent.putExtra("userDetail", peopleDetails);
				startActivity(intent);
			}
		});
		mTitleTv = (TextView) view.findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.explore);
		 listView=(zrc.widget.ZrcListView) view.findViewById(R.id.xlistview);
		mPagerAdapter = new TabPagerAdapter(getFragmentManager());

//
//		=sp.getString("userId", "0");
		String url="http://101.200.173.120:8080/LiangZiServer/videos?userId=" +""+Integer.valueOf(userid)+"&freshen=header"+"&count="+10;
		// 设置下拉刷新的样式
		SimpleHeader header = new SimpleHeader(getActivity());
		header.setTextColor(getResources().getColor(R.color.red_bg));
		header.setCircleColor(getResources().getColor(R.color.red_bg));
		listView.setHeadable(header);
		listView.refresh(); // 主动下拉刷新

// 设置加载更多的样式
		SimpleFooter footer = new SimpleFooter(getActivity());
		footer.setCircleColor(getResources().getColor(R.color.red_bg));
		listView.setFootable(footer);

// 设置列表项出现动画
		listView.setItemAnimForTopIn(R.anim.topitem_in);
		listView.setItemAnimForBottomIn(R.anim.bottomitem_in);

// 下拉刷新事件回调
		listView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
			@Override
			public void onStart() {
				onRefresh();
			}
		});

// 加载更多事件回调
		listView.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
			@Override
			public void onStart() {
				onLoadMore();
			}
		});
		mAdapter=new ExploreAdapter(getActivity());
		listView.setAdapter(mAdapter);



	}

	private void initDisplay() {

		mPagerAdapter.notifyDataSetChanged();
		listView.setOnItemClickListener(new ZrcListView.OnItemClickListener() {
			@Override
			public void onItemClick(ZrcListView parent, View view, int position, long id) {

			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public String getFragmentName() {
		return TAG;
	}


	public void onRefresh() {
		Log.e(TAG,"调用下拉刷新");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
//				userid = sp.getString("userId", "0");
				String url = "http://101.200.173.120:8080/LiangZiServer/videos?userId=" + "" + Integer.valueOf(userid) + "&freshen=header" + "&count=" + 43;
				try {
					getData(url);
				} catch (Exception e) {
					e.printStackTrace();
				}

//				mAdapter.notifyDataSetChanged();
				onLoad();
			}
		}, 2000);
	}


	public void onLoadMore() {
		Log.e(TAG,"onload more");
		mHandler.postDelayed(new Runnable() {
			@Override
				public void run() {
				long creatTime=0;
				for (int i = VideoList.size()-1; i >0; i--) {
					if (VideoList.get(i).getIsManual().equals("0")) {
						//非人工操作
						creatTime=VideoList.get(i).getCreated();
					}
				}
				String url = "http://101.200.173.120:8080/LiangZiServer/videos?userId=" + "" + Integer.valueOf(userid) + "&freshen=footer" + "&count=" + 10 + "&timeline=" + creatTime;
				try {
					getDataFotter(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
				onLoad();
			}
		}, 2000);
	}


	@Override
	public void onItemClick(ZrcListView parent, View view, int position, long id) {

	}

	@Override
	public void onScrollStateChanged(ZrcAbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(ZrcAbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	private class TabPagerAdapter extends FragmentStatePagerAdapter implements
			ViewPager.OnPageChangeListener {

		public TabPagerAdapter(FragmentManager fm) {
			super(fm);
//			mViewPager.setOnPageChangeListener(this);
		}

		@Override
		public Fragment getItem(int position) {
			HomeTabFragment fragment = (HomeTabFragment) Fragment
					.instantiate(mActivity,
							HomeTabFragment.class.getName());
			fragment.setMsgName("message name " + position);
			return fragment;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
								   int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
//			mTopIndicator.setTabsDisplay(mActivity, position);
		}
	}

	//
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
				if (serviceStatus.getResponseCode().equals("200")){
					VideoList=serviceStatus.getVideos();
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}else {
					Message msg = new Message();
					msg.what = 2;
					mHandler.sendMessage(msg);

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

				if (serviceStatus.getResponseCode().equals("200")){
					VideoList.addAll(VideoList.size(),serviceStatus.getVideos());
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}else {
					Message msg = new Message();
					msg.what = 2;
					mHandler.sendMessage(msg);

				}
			}
		});

	}
	private void onLoad() {
		listView.setRefreshSuccess("Ulook");
		listView.setLoadMoreSuccess();
		listView.startLoadMore();
	}
}
