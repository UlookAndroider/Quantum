package tv.liangzi.quantum.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.HTTPKey;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.config.PushReceiver;
import tv.liangzi.quantum.utils.ImageToSD;
import tv.liangzi.quantum.utils.MD5Util;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;


public class LoginActivity extends BaseActivity implements OnClickListener {
	private final Gson gson = new Gson();
	Map<String, Object> mapJson = new HashMap<String, Object>();
	Request request;
	final HTTPKey httpKey=new HTTPKey();
	SharedPreferences UserSP;
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
	// 编码类型
	public static final MediaType JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //handler 消息
	private static final int MESSAGE_SUCCEED=0;
	private static final int MESSAGE_FAILED=1;
	MyAapplication app;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_SUCCEED:
                	Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
					Intent intent=new Intent(LoginActivity.this,MainActivity.class);
					startActivity(intent);
					finish();
                    break;

                case MESSAGE_FAILED:
                	Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                	String str=(String) msg.obj;
                	Toast.makeText(LoginActivity.this, "登录失败原因："+str, Toast.LENGTH_SHORT).show();
                	break;
                default:
                    break;
            }
        }

    };

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_login);
		String appId = "wx349ba941cb9119c7";
		String appSecret = "1c74a6f8f5c327dd3015d23b2626fdc7";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this,appId,appSecret);
		wxHandler.addToSocialSDK();
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

	}

    @Override
    public void initViews() {
		String userId= (String) SharedPreferencesUtils.getParam(LoginActivity.this, "userId", "");

    	//数据本地化处理，暂时保存早sp下
		UserSP=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		LinearLayout loginBtnLayout= (LinearLayout) findViewById(R.id.layout_login_btn);
		if (userId!=null&&!userId.equals("")){
			loginBtnLayout.setVisibility(View.GONE);
				startActivity(new Intent(this,MainActivity.class));
			finish();
		}
      ImageButton singBtn=(ImageButton) findViewById(R.id.login_weixin);
		ImageButton tv_weibo=(ImageButton) findViewById(R.id.tv_weibo);
      tv_weibo.setOnClickListener(this);
	  singBtn.setOnClickListener(this);

    }

    

    
    protected void getWeiBoInfo() {
		// TODO Auto-generated method stub
    	mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, new UMDataListener() {
			@Override
			public void onStart() {
//    	        Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(int status, Map<String, Object> info) {
				if (status == 200 && info != null) {
					FormEncodingBuilder formBody = new FormEncodingBuilder();
					StringBuilder sb = new StringBuilder();
					Set<String> keys = info.keySet();
					String photo = null;
					String nickName=null;
					String	account=null;
					for (String key : keys) {
						if (key.equals("uid")) {
								account= String.valueOf(info.get(key));
							formBody.add(httpKey.USER_ACCOUNT, account);
						} else if (key.equals("access_token")) {
							String token = (String) info.get(key);
							formBody.add(httpKey.USER_PASSWORD, token);
							mapJson.put(httpKey.USER_PASSWORD, token);
						} else if (key.equals("screen_name")) {
							nickName= (String) info.get(key);
							formBody.add(httpKey.USER_SINA_NICKNAME, nickName);
						}
						else if (key.equals("profile_image_url")) {
							 photo = (String) info.get(key);
							formBody.add(httpKey.USER_PHOTO, photo);
						}else if (key.equals("description")) {
							String value = (String) info.get(key);
							formBody.add(httpKey.USER_SIGN, value);
						}else if (key.equals("location")) {
							String value = (String) info.get(key);
							formBody.add(httpKey.KEY_ADDR, value);
						}
//    	             
					}
					formBody.add(httpKey.KEY_TYPE, 1 + "");
					formBody.add(httpKey.KEY_CLIENT_TYPE, 3 + "");
					formBody.add("gtClientId", PushReceiver.clientid.toString());
					Log.e("gtClientId", "gtClientId="+PushReceiver.clientid.toString());
					try {
						post(MyAapplication.IP + "session", formBody);
//						ImageToSD.loadImageFromUrl(photo, nickName);
					} catch (IOException e) {
						e.printStackTrace();
					}
					String EmAccount=MD5Util.stringToMD5(account);
					EMlogin(EmAccount, EmAccount);
					Log.e("TestData", sb.toString());
				} else {
					Log.e("TestData", "发生错误：" + status);
				}
			}
		});
	}

	protected void getWeiBo() {
		// TODO Auto-generated method stub
    	mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
			@Override
			public void onError(SocializeException e, SHARE_MEDIA platform) {
			}

			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
					//回去微博相关信息
					getWeiBoInfo();
					Toast.makeText(LoginActivity.this, "授权成功.", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
			}

			@Override
			public void onStart(SHARE_MEDIA platform) {
			}


		});
	}

	protected void getWeiXin() {
		mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
			@Override
			public void onStart(SHARE_MEDIA platform) {
				Toast.makeText(LoginActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(SocializeException e, SHARE_MEDIA platform) {
				Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(Bundle info, SHARE_MEDIA platform) {
				StringBuilder sb1 = new StringBuilder();
				FormEncodingBuilder EncodingBody = new FormEncodingBuilder();
				Set<String> keySet = info.keySet();
				Iterator<String> iter = keySet.iterator();
				String account = null;
				String token = null;
				String nickName = null;
				String photo = null;
				String addr=null;
				while (iter.hasNext()) {
					String key = iter.next();
//					map.put(key, bundle.getStringArray(key));
					sb1.append(key + "=" + info.getString(key) + "\r\n");
					if (key.equals("openid")) {
						account = String.valueOf(info.getString(key));
						EncodingBody.add(httpKey.USER_ACCOUNT, account);
					} else if (key.equals("access_token")) {
						token = (String) info.getString(key);
						EncodingBody.add(httpKey.USER_PASSWORD, token);
					} else if (key.equals("nickname")) {
						 nickName = (String) info.getString(key);
						EncodingBody.add(httpKey.USER_WECHAT_NICKNAME, nickName);
					}else if (key.equals("headimgurl")) {
						photo = (String) info.get(key);
						EncodingBody.add(httpKey.USER_PHOTO, photo);
					}else if (key.equals("country")) {
						addr = (String) info.get(key);
					}else if (key.equals("province")) {
						addr = addr+"."+(String) info.get(key);
					}else if (key.equals("city")) {
						addr = addr+"."+(String) info.get(key);
						EncodingBody.add(httpKey.KEY_ADDR, addr);
					}

				}
				EncodingBody.add(httpKey.KEY_TYPE, 2 + "");
				EncodingBody.add(httpKey.KEY_CLIENT_TYPE, 3 + "");
				EncodingBody.add("gtClientId", PushReceiver.clientid.toString());
				//请求服务器接口
				Toast.makeText(LoginActivity.this, "http://192.168.1.144:8080/LiangZiServer/sessio...", Toast.LENGTH_SHORT).show();
				try {
					post(MyAapplication.IP + "session", EncodingBody);
					ImageToSD.loadImageFromUrl(photo, nickName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(LoginActivity.this, "授权完成value" + info.toString(), Toast.LENGTH_SHORT).show();
				//获取相关授权信息
				mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, new UMDataListener() {
					@Override
					public void onStart() {
//						Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(int status, Map<String, Object> info) {
						if (status == 200 && info != null) {
							StringBuilder sb = new StringBuilder();
							Set<String> keys = info.keySet();
							for (String key : keys) {
								sb.append(key + "=" + info.get(key).toString() + "\r\n");
							}
							Toast.makeText(LoginActivity.this, "获取平台数据ok...", Toast.LENGTH_SHORT).show();
						} else {
							Log.e("TestData", "发生错误：" + status);
						}
					}
				});
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
				Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
			}
		});
    }

	public void EMlogin(final String userName,String password){
		EMChatManager.getInstance().login(userName,password,new EMCallBack() {//回调
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
//						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						app.setEMuserId(userName);
						Log.d("main", "登陆聊天服务器成功！");
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {
				Log.d("main", "登陆聊天服务器失败！");
			}
		});
	}
	@Override
    public void initListeners() {

    }

    @Override
    public void initData() {
	app=(MyAapplication)getApplication();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */  
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
           ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

  
    void post(String url,FormEncodingBuilder formBody ) throws IOException {
    	
    Request request = new Request.Builder()
        .url(url)
        .post(formBody.build())
        .build();
     
        OkHttpUtil.enqueue(request, new Callback() {
			
			@Override
			public void onResponse(Response response) throws IOException {

				PeopleDetails user = gson.fromJson(response.body().charStream(), PeopleDetails.class);
			if (user.getResponseCode().equals("201") ){
				String EmAccount=MD5Util.stringToMD5(user.getUserId() + "");
				EMlogin(EmAccount, EmAccount);
				mHandler.sendEmptyMessage(MESSAGE_SUCCEED);
				Editor editor = UserSP.edit();//获取编辑器
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
			}else {
				mHandler.sendEmptyMessage(MESSAGE_FAILED);
			}


			}
			
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Message msg=new Message();
				msg.what=3;
				msg.obj=arg1.toString();
				mHandler.sendMessage(msg);
			}
		});
   }
    
   class ServiceThread implements Runnable  
   {  
       @Override  
       public void run()  
       {  
	
    	   //   备用子线程
           Log.e("log", "发出请求");  
 
       }  
   }

@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.tv_weibo:
		getWeiBo();
		break;
	case R.id.login_weixin:
		getWeiXin();
	default:
		break;
	}
} 
  
}



