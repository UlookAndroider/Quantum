package tv.liangzi.quantum.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.HTTPKey;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.MD5Util;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private RelativeLayout rootLayout;
	private TextView versionText;
	SharedPreferences UserSP;
	final HTTPKey httpKey=new HTTPKey();
	private static final int sleepTime = 2000;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					Toast.makeText(SplashActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//					Intent intent=new Intent(SplashActivity.this,MainActivity.class);
//					startActivity(intent);
//					finish();

//					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//					intent.putExtra("huanxin", true);
//					startActivity(intent);
//					finish();
					break;

				case 2:
					String message= (String) msg.obj;
					Toast.makeText(SplashActivity.this, "登录失败"+message, Toast.LENGTH_SHORT).show();
					break;
				case 3:
					String str=(String) msg.obj;
					Toast.makeText(SplashActivity.this, "登录失败原因："+str, Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}

	};
	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);
		UserSP=this.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
//		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
//		versionText = (TextView) findViewById(R.id.tv_version);

//		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
//		rootLayout.startAnimation(animation);
	}

	@Override
	public void setContentView() {

	}

	@Override
	public void initViews() {

	}

	@Override
	public void initListeners() {

	}

	@Override
	public void initData() {

	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
//				if (HXSDKHelper.getInstance().isLogined()) {
					String userId = (String) SharedPreferencesUtils.getParam(SplashActivity.this,"userInfo", "userId", "");
					if (userId!=null&&!userId.equals("")){
					// ** 免登陆情况 加载所有本地群和会话
					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					//加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
//					EMGroupManager.getInstance().loadAllGroups();
//					EMChatManager.getInstance().loadAllConversations();
					long costTime = System.currentTimeMillis() - start;
					//等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					FormEncodingBuilder formBody = new FormEncodingBuilder();


						String account= (String) SharedPreferencesUtils.getParam(SplashActivity.this,"LoginInfo", httpKey.USER_ACCOUNT, "");
						String password= (String) SharedPreferencesUtils.getParam(SplashActivity.this,"LoginInfo", httpKey.USER_PASSWORD, "");
						String type= (String) SharedPreferencesUtils.getParam(SplashActivity.this, "LoginInfo",httpKey.KEY_TYPE, "");
						String clientType= (String) SharedPreferencesUtils.getParam(SplashActivity.this,"LoginInfo", httpKey.KEY_CLIENT_TYPE, "");
						String photo = (String) SharedPreferencesUtils.getParam(SplashActivity.this,"LoginInfo", "photo", "");
						String wechatNickName = (String) SharedPreferencesUtils.getParam(SplashActivity.this,"LoginInfo", httpKey.USER_WECHAT_NICKNAME, "");
						String sinaNickName = (String) SharedPreferencesUtils.getParam(SplashActivity.this,"LoginInfo", httpKey.USER_SINA_NICKNAME, "");
						String sign= (String) SharedPreferencesUtils.getParam(SplashActivity.this,"LoginInfo", httpKey.USER_SIGN, "");
						String gtClientId = (String) SharedPreferencesUtils.getParam(SplashActivity.this,"LoginInfo", httpKey.USER_GT_CLIENTID, "");
						String addr = (String) SharedPreferencesUtils.getParam(SplashActivity.this,"LoginInfo", httpKey.KEY_ADDR, "");

						formBody.add(httpKey.USER_ACCOUNT, account);
						formBody.add(httpKey.USER_PASSWORD, password);
						formBody.add(httpKey.USER_SINA_NICKNAME, sinaNickName);
						formBody.add(httpKey.USER_WECHAT_NICKNAME, wechatNickName);
						formBody.add(httpKey.USER_PHOTO, photo);
						formBody.add(httpKey.USER_SIGN, sign);
						formBody.add(httpKey.KEY_ADDR, addr);
						formBody.add(httpKey.KEY_TYPE, type);
						formBody.add(httpKey.KEY_CLIENT_TYPE, clientType);
						formBody.add(httpKey.USER_GT_CLIENTID,gtClientId);
//			Log.e("gtClientId", "gtClientId="+PushReceiver.clientid.toString());
						try {
							post(MyAapplication.IP + "session", formBody);
						} catch (IOException e) {
							e.printStackTrace();
						}


					//进入

				} else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
					intent.putExtra("huanxin", false);
						startActivity(intent);
					finish();
				}
			}
		}).start();
	}


	void post(String url, FormEncodingBuilder formBody ) throws IOException {

		Request request = new Request.Builder()
				.url(url)
				.post(formBody.build())
				.build();

		OkHttpUtil.enqueue(request, new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				Gson gson=new Gson();
				PeopleDetails user = gson.fromJson(response.body().charStream(), PeopleDetails.class);
				if (user.getResponseCode().equals("201")) {
					String EmAccount = MD5Util.stringToMD5(user.getUserId() + "");
					EMlogin(EmAccount, EmAccount);
					mHandler.sendEmptyMessage(1);
					SharedPreferences.Editor editor = UserSP.edit();//获取编辑器
					editor.putString("nickName", user.getNickName());
					editor.putString("sinaNickName", user.getSinaNickName());
					editor.putString("wechatNickName", user.getWechatNickName());
					editor.putString("especialUploadToken", user.getEspecialUploadToken());
					editor.putString("photo", user.getPhoto());
					editor.putString("commonUploadToken", user.getCommonUploadToken());
					editor.putString("accessToken", user.getAccessToken());
					editor.putString("userId", user.getUserId() + "");
					editor.putString("addr", user.getAddr());
					editor.putString("focusNum", user.getFocusNum() + "");
					editor.putString("fansNum", user.getFansNum() + "");
					editor.putString("sign", user.getSign());
					editor.putString("lng", user.getLng() + "");
					editor.putString("lat", user.getLat() + "");
					editor.commit();//提交修改
				} else {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = user.getResponseCode();
					mHandler.sendMessage(msg);

				}


			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = arg1.toString();
				mHandler.sendMessage(msg);
			}
		});
	}

	public void EMlogin(final String userName,String password){
		Log.e("huanxin", "EMlogin=" + userName + "---pass=" + password);
		EMChatManager.getInstance().login(userName,password,new EMCallBack() {//回调
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						startActivity(new Intent(SplashActivity.this,MainActivity.class));
						finish();
						Log.e("huanxin", "登陆聊天服务器成功！");
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {
				Log.e("huanxin", "登陆聊天服务器失败！="+message);
			}
		});
	}


}
	/**
	 * 获取当前应用程序的版本号
	 */
//	private String getVersion() {
//		String st = getResources().getString(R.string.Version_number_is_wrong);
//		PackageManager pm = getPackageManager();
//		try {
//			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
//			String version = packinfo.versionName;
//			return version;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//			return st;
//		}
//	}

