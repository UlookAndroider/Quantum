package tv.liangzi.quantum.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.activity.UserInfoActivity;
import tv.liangzi.quantum.adapter.ExploreAdapter;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.bean.ServiceStatus;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
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
	private final static String ALBUM_PATH= Environment.getExternalStorageDirectory() + "/download_test/";

	private ImageView mImageView;
	private Bitmap mBitmap;
	private String mFileName;
	private String mSaveMessage;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();


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
		initImageLoaderOptions();
//		new Thread(connectNet).start();
//		new Thread(saveFileRunnable).start();
//		sp=getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
//	    peopleDetails= MyAapplication.getApplicationuser();
		userid=(String) SharedPreferencesUtils.getParam(getActivity(),"userId","");
		String photo= (String) SharedPreferencesUtils.getParam(getActivity(),"photo","");
		ImageView imHead=(ImageView) view.findViewById(R.id.im_title_head);
		imageLoader.displayImage(photo, imHead, options, animateFirstListener);

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

//		listView.setOnItemClickListener(new ZrcListView.OnItemClickListener() {
//			@Override
//			public void onItemClick(ZrcListView parent, View view, int position, long id) {
//
//			}
//		});
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




	private Runnable saveFileRunnable = new Runnable(){
		@Override
		public void run() {
			try {
				saveFile(mBitmap, mFileName);
				mSaveMessage = "图片保存成功！";
			} catch (IOException e) {
				mSaveMessage = "图片保存失败！";
				e.printStackTrace();
			}
			messageHandler.sendMessage(messageHandler.obtainMessage());
		}

	};

	private Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
	};

	/*
     * 连接网络
     * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问
     */
	private Runnable connectNet = new Runnable(){
		@Override
		public void run() {
			try {
				String filePath = "http://img.my.csdn.net/uploads/201402/24/1393242467_3999.jpg";
				mFileName = "test.jpg";

				//以下是取得图片的两种方法
				//////////////// 方法1：取得的是byte数组, 从byte数组生成bitmap
				byte[] data = getImage(filePath);
				if(data!=null){
					mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
				}else{
				}
				////////////////////////////////////////////////////////

				//******** 方法2：取得的是InputStream，直接从InputStream生成bitmap ***********/
				mBitmap = BitmapFactory.decodeStream(getImageStream(filePath));
				//********************************************************************/
				// 发送消息，通知handler在主线程中更新UI
				connectHanlder.sendEmptyMessage(0);
				Log.d(TAG, "set image ...");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	private Handler connectHanlder = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "display image");
			// 更新UI，显示图片
			if (mBitmap != null) {
				mImageView.setImageBitmap(mBitmap);// display image
			}
		}
	};



		/**
		 * Get image from newwork
		 * @param path The path of image
		 * @return byte[]
		 * @throws Exception
		 */
		public byte[] getImage(String path) throws Exception{
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			InputStream inStream = conn.getInputStream();
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				return readStream(inStream);
			}
			return null;
		}

		/**
		 * Get image from newwork
		 * @param path The path of image
		 * @return InputStream
		 * @throws Exception
		 */
		public InputStream getImageStream(String path) throws Exception{
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				return conn.getInputStream();
			}
			return null;
		}
		/**
		 * Get data from stream
		 * @param inStream
		 * @return byte[]
		 * @throws Exception
		 */
		public static byte[] readStream(InputStream inStream) throws Exception{
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while( (len=inStream.read(buffer)) != -1){
				outStream.write(buffer, 0, len);
			}
			outStream.close();
			inStream.close();
			return outStream.toByteArray();
		}

		/**
		 * 保存文件
		 * @param bm
		 * @param fileName
		 * @throws IOException
		 */
		public void saveFile(Bitmap bm, String fileName) throws IOException {
			File dirFile = new File(ALBUM_PATH);
			if(!dirFile.exists()){
				dirFile.mkdir();
			}
			File myCaptureFile = new File(ALBUM_PATH + fileName);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
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
