package tv.liangzi.quantum.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import tv.liangzi.quantum.adapter.PeopleAdapter;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.bean.Person;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.OkHttpUtil;

public class SearchActivity extends BaseActivity implements OnClickListener, TextWatcher, View.OnKeyListener,PeopleAdapter.Callback {
	private EditText mEtSearch = null;// 输入搜索内容
	private Button mBtnClearSearchText = null;// 清空搜索信息的按钮
	private LinearLayout mLayoutClearSearchText = null;
	private ListView mListView;
	private PeopleAdapter peopleAdapter;
	private List<PeopleDetails> mlist=new ArrayList<PeopleDetails>();
	private SharedPreferences SearchSp;
	private String accessToken;
	private String userId;
	private String nikeName;
	private String toUserId;
	private ImageView imageView;
	private int mPosition;

	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:
					peopleAdapter.setUser(mlist);
					peopleAdapter.notifyDataSetChanged();
					break;
				case 2:
					mlist.get(mPosition).setIsFollow(true);
					peopleAdapter.setUser(mlist);
//					peopleAdapter.notifyDataSetChanged();
					imageView.setImageResource(R.drawable.ic_concerned);
					Toast.makeText(SearchActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					mlist.get(mPosition).setIsFollow(false);
					peopleAdapter.setUser(mlist);
//					peopleAdapter.notifyDataSetChanged();
				imageView.setImageResource(R.drawable.ic_concerned_no);
				Toast.makeText(SearchActivity.this,"取消关注成功",Toast.LENGTH_SHORT).show();
				break;

					case 4:
						String error= (String) msg.obj;
						Toast.makeText(SearchActivity.this,error,Toast.LENGTH_SHORT).show();
						break;
				case 5:
					String message= (String) msg.obj;
					Toast.makeText(SearchActivity.this,message,Toast.LENGTH_SHORT).show();
					break;
				case 6:
					Toast.makeText(SearchActivity.this,"您所输入的用户不存在！",Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_search);

	}

	@Override
	public void initViews() {
		SearchSp=getSharedPreferences("userInfo", MODE_PRIVATE);
		userId=SearchSp.getString("userId", "0");
		accessToken=SearchSp.getString("accessToken", "0");
		mListView=(ListView)findViewById(R.id.search_listview);
		mEtSearch = (EditText) findViewById(R.id.et_search);
		mBtnClearSearchText = (Button) findViewById(R.id.btn_clear_search_text);
		mLayoutClearSearchText = (LinearLayout) findViewById(R.id.layout_clear_search_text);
		mEtSearch.addTextChangedListener(this);
		mBtnClearSearchText.setOnClickListener(this);
		mEtSearch.setOnKeyListener(this);
		mEtSearch.setOnClickListener(this);
		peopleAdapter=new PeopleAdapter(SearchActivity.this,mlist,this);
		mListView.setAdapter(peopleAdapter);
	}

	@Override
	public void initListeners() {

	}

	@Override
	public void initData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_clear_search_text:
				mEtSearch.setText("");
				mLayoutClearSearchText.setVisibility(View.GONE);
				break;
			default:
				break;
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		int textLength = mEtSearch.getText().length();
		if (textLength > 0) {
			mLayoutClearSearchText.setVisibility(View.VISIBLE);
		} else {
			mLayoutClearSearchText.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		Log.e("log", "keyCode= "+keyCode);
		if (keyCode==KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
			mlist.clear();
			nikeName=mEtSearch.getText().toString().trim();
			if (!nikeName.equals("")&&nikeName!=null){
				Thread userThread = new Thread(new getUserThread());
				userThread.start();
				Log.e("log", "判断不为空搜索 ");
			}else{
				Toast.makeText(SearchActivity.this,"请输入用户昵称！",Toast.LENGTH_SHORT).show();
			}

		}
		return false;
	}

	@Override
	public void click(View v, int position) {
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

	@Override
	public void headClick(View v, int position) {
		Intent intent=new Intent(SearchActivity.this, UserInfoActivity.class);
		intent.putExtra("userId",userId);
		intent.putExtra("userDetail",mlist.get(position));
		startActivity(intent);
	}


	class getUserThread implements Runnable
	{
		@Override
		public void run()
		{
			String url= MyAapplication.IP+"users"+"?userId="+userId+"&nickName="+nikeName;
			try {
				getUsers(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//   备用子线程
			Log.e("log", "查询用户请求");

		}
	}
	void getUsers(String url) throws IOException {
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
					Person person = gson.fromJson(response.body().charStream(), new TypeToken<Person>() {
					}.getType());
					if (person.getResponseCode().equals("200")) {
						if (person.getUsers().size() > 0) {
							mlist = person.getUsers();
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = 6;
							mHandler.sendMessage(msg);
							Log.e("videoInfoActivity", "连cishu chakan ....................");
						}

					} else if (person.getResponseCode().equals("500")) {
						Log.e("videoInfoActivity", "连接服务器失败");
					} else {
						Message msg = new Message();
						msg.what = 5;
						msg.obj = person.getResponseMsg();
						mHandler.sendMessage(msg);
					}


				}
			}
		});
	}

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
			Log.e("log", "发出关注请求");

		}
	}
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

	public void cancleFollow(String url) throws IOException {
		RequestBody formBody = new FormEncodingBuilder()
				.add("accessToken", accessToken)
				.build();
		Request request = new Request.Builder().
				url(url)
				.delete()
				.build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Message msg=new Message();
				msg.what=4;
				msg.obj=e.getMessage();
				mHandler.sendMessage(msg);
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
					msg.what=2;
					mHandler.sendMessage(msg);
				}
			}
		});
	}
}


