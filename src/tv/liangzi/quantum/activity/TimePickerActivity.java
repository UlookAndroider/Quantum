package tv.liangzi.quantum.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.cropper.cropwindow.handle.Handle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gdg.ninja.croplib.Crop;
import gdg.ninja.croplib.utils.FileUtils;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.bean.Live;
import tv.liangzi.quantum.bean.LiveVideoStatus;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.utils.CommonUtils;
import tv.liangzi.quantum.utils.DateUtil;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.utils.SharedPreferencesUtils;
import tv.liangzi.quantum.widget.time.PickerDateView;
import tv.liangzi.quantum.widget.time.PickerView;
import tv.liangzi.quantum.widget.time.PickerView.onSelectListener;


/**
 * https://github.com/invinjun/live
 * 
 * @author invinjun
 * 
 */
public class TimePickerActivity extends Activity implements OnClickListener
{
	List<String> Datas = new ArrayList<String>();
	List<String> hours = new ArrayList<String>();
	List<String> seconds = new ArrayList<String>();
	PickerView minute_pv;
	PickerView hour_pv;
	PickerDateView month_pv;
//	PickerView day_pv;
	Calendar calendar;
	Date date;
	Date selectedDate;
	String selectDateText;
	SimpleDateFormat simpleDateFormat;
	SimpleDateFormat format;
	 String reStr ;
	 private TextView Ensure;
	private TextView UnEnsure;
	 private int selectedHour;
	 private int selectedMin;
	 int temp;
	//直播主题条件
	private EditText nameTopic;
	private LinearLayout upCouver;
	private static  final int REQUEST_IMAGE=1;
	private static  final int RESULT_OK=-1;
	private static final int REQUEST_CODE=3;
	private ImageView coverPic;
	private TextView theme;
	private TextView userName;
	private TextView year;
	private TextView time;
	private ImageView userHead;
	private RelativeLayout pickView;
	private ImageView divider;
	private boolean living=false;
	private boolean EmptyPic=false;
	private TextView chooseImage;
	private RelativeLayout preview;
	private String photo;
	private ImageView liveIcon;
	private ImageView ulookIcon;
	private Live mLiveVideo=new Live();
	private String accessToken;
	private String userId;
	private String state="0";
	private String filePath = Environment.getExternalStorageDirectory()+ File.separator+"Ulooktemp";
	public Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:

					break;
				case 2:
					if(living){
						Intent intent=new Intent(TimePickerActivity.this,ShowLiveActivity.class);
						intent.putExtra("roomId",mLiveVideo.getChatroomId());
						intent.putExtra("rtmpUrl",mLiveVideo.getRtmpPlayUrl());
						intent.putExtra("userid",mLiveVideo.getUserId());
						intent.putExtra("nikeName",mLiveVideo.getNickName());
						intent.putExtra("shareUrl", mLiveVideo.getShareUrl());
						startActivity(intent);
						Toast.makeText(TimePickerActivity.this,"直播成功+++++++！！！！",Toast.LENGTH_SHORT).show();
					}else
						Toast.makeText(TimePickerActivity.this,"预约成功+++++++！！！！",Toast.LENGTH_SHORT).show();
					break;
				case 3:
					break;
//
				case 4:
					String error= (String) msg.obj;
					Toast.makeText(TimePickerActivity.this,error,Toast.LENGTH_SHORT).show();
					break;
//				case 5:
//					String message= (String) msg.obj;
//					Toast.makeText(SearchActivity.this,message,Toast.LENGTH_SHORT).show();
//					break;
				case 6:
//					Toast.makeText(getActivity(),"您所输入的用户不存在！",Toast.LENGTH_SHORT).show();
//					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_time_picker);
		userId= (String) SharedPreferencesUtils.getParam(this, "userId", "");
		accessToken = (String) SharedPreferencesUtils.getParam(this, "accessToken", "");
//		photo= (String) SharedPreferencesUtils.getParam(this, "photo", "");
		nameTopic= (EditText) findViewById(R.id.et_name_topic);
		upCouver= (LinearLayout) findViewById(R.id.layout_pic);
		month_pv=(PickerDateView) findViewById(R.id.month_pv);
//		day_pv = (PickerView) findViewById(R.id.day_pv);
		hour_pv = (PickerView) findViewById(R.id.hour_pv);
		minute_pv=(PickerView) findViewById(R.id.minute_pv);
		Ensure =(TextView) findViewById(R.id.tv_order);
		UnEnsure =(TextView) findViewById(R.id.tv_cancle);
        ImageView image= (ImageView) findViewById(R.id.logo_frame);
		image.setBackgroundResource(R.drawable.logo_round);
		AnimationDrawable anim = (AnimationDrawable) image.getBackground();
		       anim.start();
		//图片预览部分
		preview= (RelativeLayout) findViewById(R.id.layout_preview);
		chooseImage= (TextView) findViewById(R.id.choose_image);
		coverPic= (ImageView) findViewById(R.id.video_pic);
		 theme= (TextView) findViewById(R.id.video_name);
		 userName= (TextView) findViewById(R.id.user_name);
		 time=(TextView)findViewById(R.id.tv_living_time1);
		year=(TextView)findViewById(R.id.tv_living_time2);
		 userHead= (ImageView) findViewById(R.id.living_head);
		preview.setVisibility(View.GONE);
		pickView= (RelativeLayout) findViewById(R.id.rl_pick);
		divider = (ImageView) findViewById(R.id.view_divider);
		liveIcon= (ImageView) findViewById(R.id.icon_living);
		ulookIcon= (ImageView) findViewById(R.id.icon_ulook);

		initDate();
		nameTopic.setOnClickListener(this);
		upCouver.setOnClickListener(this);
	Ensure.setOnClickListener(this);
		UnEnsure.setOnClickListener(this);
		month_pv.setData(Datas);
		month_pv.setOnSelectListener(new tv.liangzi.quantum.widget.time.PickerDateView.onSelectListener() {

			@Override
			public void onSelect(String text) {
				// TODO Auto-generated method stub
				try {
//					selectDateText=text;
					selectedDate = format.parse(text);
					selectDateText=format.format(selectedDate);
					year.setText(selectDateText);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		});
		minute_pv.setSelected(0);
		hour_pv.setData(hours);
		hour_pv.setOnSelectListener(new onSelectListener()
		{
			@Override
			public void onSelect(String text)
			{
				selectedHour=Integer.parseInt(text);
				time.setText(selectedHour+":"+selectedMin);

			}
		});
		minute_pv.setData(seconds);
		minute_pv.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				selectedMin=Integer.parseInt(text);
				time.setText(selectedHour+":"+selectedMin);
			}
		});

	}
	private void initDate() {
		//直播or预约
		Intent intent=getIntent();
		living=intent.getBooleanExtra("living",false);
		if (living){
			state="1";
			pickView.setVisibility(View.GONE);
			divider.setVisibility(View.GONE);
			ulookIcon.setVisibility(View.GONE);
			Ensure.setText("确定直播");
			UnEnsure.setText("取消直播");
		}else {
			state="0";
			liveIcon.setVisibility(View.GONE);
		}


		//日历相关
		calendar=Calendar.getInstance();
		date=new Date();
		simpleDateFormat=new SimpleDateFormat("MM-dd EE");
		format=new SimpleDateFormat("MM-dd");
		temp=0;
		// TODO Auto-generated method stub
		//初始化 没有滑动选中日期的值
		
		selectDateText=getNowTime();
		selectedHour=13;
		selectedMin=30;
		 for (int i = 0; i <= 16; i++) {

				 DateFormatToString(i);
				
		}
	
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		for (int i = 1; i < 25; i++)
		{
			hours.add("" + i);
		}
		
		for (int i = 0; i < 60; i++)
		{
			seconds.add(i < 10 ? "0" + i : "" + i);
		}

		//截取图片相关

	}
	public  String DateFormatToString(int position){
		if (position!=0) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		 date=calendar.getTime();
		 reStr = simpleDateFormat.format(date);
		 if (position<8) {
			 Datas.add(reStr);
		}else if(position>=8&&position<15){
			 Datas.add(temp++,reStr);
		} else if (position==15) {
			 Datas.add(temp,"预约截止日");
		}
		 
		 return reStr;
		 
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.layout_pic:

				if (nameTopic.getText().toString().trim()==null||nameTopic.getText().toString().trim().equals("")){
					Toast.makeText(TimePickerActivity.this,"请输入直播话题后再选择图片",Toast.LENGTH_SHORT).show();
					break;
				}
				chooseImage.setText("更换照片");
//				Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
//				startActivityForResult(intent, 0x123);
				Intent intent = new Intent(TimePickerActivity.this, MultiImageSelectorActivity.class);
// 是否显示调用相机拍照
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
// 最大图片选择数量
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
// 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
				startActivityForResult(intent, REQUEST_IMAGE);
				break;
		case R.id.tv_order:
			if (!living) {
				if (selectDateText.equals("预约截止日")) {
					Toast.makeText(TimePickerActivity.this, "请选择正确的时间",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Calendar ca = Calendar.getInstance();
				int hour = ca.get(Calendar.HOUR_OF_DAY);
				int min = ca.get(Calendar.MINUTE);
				if (selectDateText.equals(getNowTime())) {
					//day等于当前时间的 进一步判断时分
					if (selectedHour == hour) {
						//小时相同的进一步判断 分钟
						if (selectedMin > min) {
							Toast.makeText(TimePickerActivity.this, "预约直播成功，请牢记时间哦d- h-m",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(TimePickerActivity.this, "请选择正确的时间。。m",
									Toast.LENGTH_SHORT).show();
							return;
						}
					} else if (selectedHour > hour) {
						//小时大于当前 
						Toast.makeText(TimePickerActivity.this, "预约成功 h",
								Toast.LENGTH_SHORT).show();
					} else if (selectedHour < hour) {
						//小时大于当前 
						Toast.makeText(TimePickerActivity.this, "请重新选择时间 h",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				//day大于现在的选择的直接 ok
				Toast.makeText(TimePickerActivity.this, "时间ok,day",
						Toast.LENGTH_SHORT).show();
				 if (!EmptyPic){
					Toast.makeText(TimePickerActivity.this, "请选择图片封面", Toast.LENGTH_SHORT).show();
					break;
				}
				Toast.makeText(TimePickerActivity.this, "图片ok成功",
						Toast.LENGTH_SHORT).show();
				Thread liveThread= new Thread(new LiveThread());
				liveThread.start();
			}else {
				if (nameTopic.getText().toString().trim() == null || nameTopic.getText().toString().trim().equals("")) {
					Toast.makeText(TimePickerActivity.this, "请输入直播话题后再选择图片", Toast.LENGTH_SHORT).show();
					break;
				}else if (!EmptyPic){
					Toast.makeText(TimePickerActivity.this, "请选择图片封面", Toast.LENGTH_SHORT).show();
					break;
				}
				Thread liveThread= new Thread(new LiveThread());
				liveThread.start();
//				Toast.makeText(TimePickerActivity.this, "预约或直播创建成功", Toast.LENGTH_SHORT).show();

			}

			break;

			case R.id.tv_cancle:
				finish();
				break;
		default:
			break;
		}
	}
	public String getNowTime(){
		SimpleDateFormat    formatterDay    =   new    SimpleDateFormat    ("MM-dd");
//		SimpleDateFormat    formatterHour    =   new    SimpleDateFormat    ("HH:mm");
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
		String    str    =    formatterDay.format(curDate);
		return str;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE) {
			if (resultCode == RESULT_OK) {
				// 获取返回的图片列表
				List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				// 处理你自己的逻辑 ....
				File file = new File(path.get(0));
				String choosePic=path.get(0).substring(path.get(0).lastIndexOf("."));
				String fileName=filePath+File.separator+ CommonUtils.generateFileName()+choosePic;
				File outFile=FileUtils.newFile(fileName);
				Uri imgSource = Uri.fromFile(file);
				Uri outSource = Uri.fromFile(outFile);
				new Crop(imgSource).output(outSource).start(TimePickerActivity.this);
			}
		} else if (requestCode == RESULT_CANCELED) {
			EmptyPic = false;
			return;
		} else if (requestCode == Crop.REQUEST_CROP) {
			Uri imgResult;
			try {
				imgResult = data.getData();
			} catch (Exception e) {
				return;
			}
			EmptyPic = true;
			preview.setVisibility(View.VISIBLE);
			coverPic.setImageURI(imgResult);
			chooseImage.setText("更换封面");
			theme.setText(nameTopic.getText().toString().trim());
			userName.setText(userId);
			year.setText(selectDateText);
			time.setText(selectedHour + ":" + selectedMin);
//			long timemill=DateUtil.getStringToDate(selectDateText+" "+selectedHour+"时"+selectedMin);
//		System.out.print(timemill+"");
		}

	}
		class LiveThread implements Runnable
		{
			@Override
			public void run()
			{
				String millis = "";
				FormEncodingBuilder	 Body = new FormEncodingBuilder();
				String url= MyAapplication.IP+"live";
				Body.add("userId", userId)
						.add("state", state)
						.add("img", "http://img3.imgtn.bdimg.com/it/u=3190314089,2370727376&fm=21&gp=0.jpg")
						.add("title", nameTopic.getText().toString().trim())
						.add("accessToken", accessToken);
				if (!living){
					long mill=DateUtil.getStringToDate(selectDateText + " " + selectedHour + "时" + selectedMin);
					millis=	String.valueOf((mill) - (Calendar.getInstance().getTimeInMillis()));
					Body.add("reserved", millis);
				}
				try {
					postLive(url,Body.build());
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.e("log", "post提交直播请求"+"live");

			}
		}


	void postLive(String url,RequestBody formBody) throws IOException {

		Request request = new Request.Builder()
				.url(url)
				.post(formBody)
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
					mLiveVideo = gson.fromJson(response.body().charStream(), new TypeToken<Live>() {
					}.getType());
					if (mLiveVideo.getResponseCode().equals("201")) {
						if (mLiveVideo!=null) {
							Message msg = new Message();
								msg.what = 2;
							mHandler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = 6;
							mHandler.sendMessage(msg);
							Log.e("livefragment", "n ....................");
						}

					} else if (mLiveVideo.getResponseCode().equals("500")) {
						Log.e("videoInfoActivity", "连接服务器失败");
					} else {
						Message msg = new Message();
						msg.what = 5;
						msg.obj = mLiveVideo.getResponseMsg();
						mHandler.sendMessage(msg);
					}


				}
			}
		});
	}
}
