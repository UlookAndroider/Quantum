package tv.liangzi.quantum.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.bean.ServiceStatus;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.view.XListView;

public class SettingActivity extends BaseActivity implements OnClickListener{

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



	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					break;
				case 2:
					break;
				case 3:

					Toast.makeText(SettingActivity.this,"取消关注成功",Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_setting);
	}

	@Override
	public void initViews() {




	}

	private void initdisplay(PeopleDetails user) {

	}

	@Override
	public void initListeners() {
//		bt_user_follow.setOnClickListener(this);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
//			case R.id.bt_user_follow:
//				break;

		}
		
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
					for(Map.Entry<String, Object> entry : InfoMap.entrySet()) {
						String key = entry.getKey();
						Object newstr = entry.getValue();
						System.out.println("key:" + key + "," + newstr + ",");
					}
					PeopleDetails peopleDetails = gson.fromJson(response.body().charStream(), PeopleDetails.class);
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
//					Log.e("videoInfoActivity", peopleDetails.toString());
				}
			}
		});
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
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
//				Log.e("exploreFragment", "size....." + VideoList.size());
				String json = gson.toJson(serviceStatus);
			}
		});

	}

	}











