package tv.liangzi.quantum.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.LinkedList;
import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.activity.SearchActivity;
import tv.liangzi.quantum.activity.UserInfoActivity;
import tv.liangzi.quantum.adapter.PeopleAdapter;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.bean.Person;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;

/**
 *  fragment
 * @author dewyze
 *
 */
public class PeopleFragment extends BaseFragment implements OnClickListener,PeopleAdapter.Callback {

	private static final String TAG = "SettingFragment";
	private Activity mActivity;
	private TextView mTitleTv;
	private ListView mListView;
	private PeopleAdapter adapter;
	private List<PeopleDetails> mlist=new ArrayList<PeopleDetails>();
	
	
	private TextView mEtSearch = null;// 输入搜索内容
	private Button mBtnClearSearchText = null;// 清空搜索信息的按钮
	private LinearLayout mLayoutClearSearchText = null;

//	private SharedPreferences peoplerSp;
	private PeopleDetails peopleDetails;
	private String userId;
	private String accessToken;
	private String toUserId;
	private ImageView imageView;
	private int mPosition;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();


	public static PeopleFragment newInstance() {
		PeopleFragment peopleFragment = new PeopleFragment();

		return peopleFragment;
	}

	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					adapter.setUser(mlist);
					adapter.notifyDataSetChanged();
				break;
				case 2:
					mlist.get(mPosition).setIsFollow(true);
					adapter.setUser(mlist);
//					peopleAdapter.notifyDataSetChanged();
					imageView.setImageResource(R.drawable.ic_concerned);
					Toast.makeText(getActivity(), "关注成功", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					mlist.get(mPosition).setIsFollow(false);
					adapter.setUser(mlist);
//					peopleAdapter.notifyDataSetChanged();
					imageView.setImageResource(R.drawable.ic_concerned_no);
					Toast.makeText(getActivity(),"取消关注成功",Toast.LENGTH_SHORT).show();
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
		View view = inflater.inflate(R.layout.fragment_people, container,
				false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
		initEvents();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initViews(View view) {
		initImageLoaderOptions();
//		peoplerSp=getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
		userId= (String) SharedPreferencesUtils.getParam(getActivity(), "userId", "");
		String photo= (String) SharedPreferencesUtils.getParam(getActivity(),"photo","");
		accessToken= (String) SharedPreferencesUtils.getParam(getActivity(),"accessToken","");
		ImageView imHead=(ImageView) view.findViewById(R.id.im_title_head);
		imageLoader.displayImage(photo, imHead, options, animateFirstListener);
		imHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(),UserInfoActivity.class);
//				intent.putExtra("userDetail",peopleDetails);
				startActivity(intent);
			}
		});

//		mTitleTv = (TextView) view.findViewById(R.id.title_tv);
		mEtSearch = (TextView) view.findViewById(R.id.et_searchstyle);
		mBtnClearSearchText = (Button) view.findViewById(R.id.btn_clear_search_text);
		mLayoutClearSearchText = (LinearLayout) view.findViewById(R.id.layout_clear_search_text);
		mEtSearch.setOnClickListener(this);
		initdata();
		mListView=(ListView) view.findViewById(R.id.xlistview);
		adapter=new PeopleAdapter(getActivity(), mlist,this);
		mListView.setAdapter(adapter);
	}

	private void initdata() {
//		Thread userThread = new Thread(new getUserThread());
//		userThread.start();

	}

	private void initEvents() {
	}

	@Override
	public void onResume() {
		Thread userThread = new Thread(new getUserThread());
		userThread.start();
		super.onResume();
	}

	@Override
	public void onDestroy() {
//		Intent intent = new Intent(context, LoginView.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		context.startActivity(intent);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.et_searchstyle:
			Intent intent=new Intent(getActivity(), SearchActivity.class);
			intent.putExtra("userId","");
			startActivity(intent);
			break;

		default:
			break;
		}
	}


	/**
	 * 获取用户列表
	 * @param url
	 * @throws IOException
	 */
	public void getUsers(String url) throws IOException {
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
				if (response.isSuccessful()){
					Person person = gson.fromJson(response.body().charStream(), new TypeToken<Person>() {
					}.getType());
					mlist=person.getUsers();
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
					Log.e("videoInfoActivity", mlist.size() + "");
				}
			}
		});
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
					Log.e("videoInfoActivity", mlist.size() + "");
				}
			}
		});
	}

    //adapter接口回调
	@Override
	public void click(View v,int position) {
		imageView=(ImageView)v;
		mPosition=position;
		boolean isFollowed=mlist.get(position).isFollow();
		toUserId=String.valueOf(mlist.get(position).getUserId());
		if (isFollowed){
			Thread deleteThread = new Thread(new DeleteThread());
			deleteThread.start();
		}else {
			Thread postThread = new Thread(new postThread());
			postThread.start();
		}
	}

	/**
	 *头像点击事件
	 * @param v
	 * @param position
	 */
	@Override
	public void headClick(View v, int position) {
		Intent intent=new Intent(getActivity(), UserInfoActivity.class);
		intent.putExtra("userId",userId);
		intent.putExtra("userDetail",mlist.get(position));
		startActivity(intent);
	}

	/**
	 * 用户列表接口
	 */
	public class getUserThread implements Runnable
	{
		@Override
		public void run()
		{
			String url= MyAapplication.IP+"users"+"?userId="+userId;
			try {
				mlist.clear();
				getUsers(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//   备用子线程
			Log.e("log", "发出请求");

		}
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
			Log.e("log", "发出请求");

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

}
