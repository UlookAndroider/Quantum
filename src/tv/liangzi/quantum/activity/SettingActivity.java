package tv.liangzi.quantum.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;

import java.io.IOException;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.bean.ServiceStatus;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.view.SwitchButton;

public class SettingActivity extends BaseActivity implements OnClickListener{

	private String userId;
	private String id;
	private PeopleDetails user;
	private ImageView im_user_dead;
	private LinearLayout rule;
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
		rule= (LinearLayout) findViewById(R.id.ll_rule);
		LinearLayout ll_clear= (LinearLayout) findViewById(R.id.ll_clear);
		ll_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new SweetAlertDialog(SettingActivity.this, SweetAlertDialog.SUCCESS_TYPE)
						.setTitleText("清除缓存")
						.setContentText("You clicked the button!")
						.show();
			}
		});
		LinearLayout ll_about= (LinearLayout) findViewById(R.id.ll_about);
		ll_about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(SettingActivity.this, AboutActivity.class));
				finish();
			}
		});
		SwitchButton sb = (SwitchButton) findViewById(R.id.wiperSwitch1);
		 sb.setOnChangeListener(new SwitchButton.OnChangeListener() {

			       @Override
			           public void onChange(SwitchButton sb, boolean state) {
				              // TODO Auto-generated method stub
				                Log.d("switchButton", state ? "开":"关");
				               Toast.makeText(SettingActivity.this, state ? "开":"关", Toast.LENGTH_SHORT).show();
				           }
		       });


LinearLayout llshare= (LinearLayout) findViewById(R.id.ll_share);
		llshare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
// 设置分享内容
				mController.setShareContent("ULLOK Android下载地址：http://www.liangzi.tv/");
// 设置分享图片, 参数2为图片的url地址
				mController.setShareMedia(new UMImage(SettingActivity.this,
						"http://www.umeng.com/images/pic/banner_module_social.png"));
				mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN,SHARE_MEDIA.TENCENT);
				mController.openShare(SettingActivity.this, false);


			}
		});
	}

	private void initdisplay(PeopleDetails user) {

	}

	@Override
	public void initListeners() {
		rule.setOnClickListener(this);
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
			case R.id.ll_rule:
				startActivity(new Intent(SettingActivity.this,Rulectivity.class));
				break;

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











