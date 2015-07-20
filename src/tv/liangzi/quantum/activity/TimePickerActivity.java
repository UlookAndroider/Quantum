package tv.liangzi.quantum.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import gdg.ninja.croplib.Crop;
import gdg.ninja.croplib.utils.FileUtils;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.bean.HTTPKey;
import tv.liangzi.quantum.bean.Live;
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
	 private me.drakeet.library.UIButton Ensure;
	private me.drakeet.library.UIButton UnEnsure;
	 private int selectedHour;
	 private int selectedMin;
	 int temp;
	//直播主题条件
	private EditText nameTopic;
	private LinearLayout upCouver;
	private RelativeLayout rl_living;
	private RelativeLayout rl_sunscribe;
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
	private String commonUploadToken;
	private String userId;
	private String nickName;
	private String photoUrl;
	private String state="0";
	private String filePath =MyAapplication.HEAD_PATH;
	private InputFilter filter;
	File cropFile;
	String upImage;
	private SpotsDialog dialog;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;		// DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	public Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 1:

					break;
				case 2:
					if(living){
						Intent intent=new Intent(TimePickerActivity.this,ShowLiveActivity.class);
//						mLiveVideo.getStream().getHosts().setPublish(mLiveVideo.getHlsPublishUrl());
//						mLiveVideo.getStream().getHosts().getPlay().setHls(mLiveVideo.getHlsPlayUrl());
//						mLiveVideo.getStream().getHosts().getPlay().setRtmp(mLiveVideo.getRtmpPlayUrl());
						intent.putExtra("Living", mLiveVideo);
						dialog.dismiss();
						startActivity(intent);
						cropFile.delete();
						finish();
					}else{
						Intent intent=new Intent(TimePickerActivity.this,MainActivity.class);
//						mLiveVideo.getStream().getHosts().setPublish(mLiveVideo.getHlsPublishUrl());
//						mLiveVideo.getStream().getHosts().getPlay().setHls(mLiveVideo.getHlsPlayUrl());
//						mLiveVideo.getStream().getHosts().getPlay().setRtmp(mLiveVideo.getRtmpPlayUrl());
						intent.putExtra("Living",mLiveVideo);
						cropFile.delete();
//						dialog.dismiss();
						startActivity(intent);
						finish();
					}
					break;
				case 3:
					break;
//
				case 4:
					String error= (String) msg.obj;
					Toast.makeText(TimePickerActivity.this,error,Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					break;
				case 5:
					dialog.dismiss();
//					String message= (String) msg.obj;
//					Toast.makeText(SearchActivity.this,message,Toast.LENGTH_SHORT).show();
					break;
				case 6:
					dialog.dismiss();
//					Toast.makeText(getActivity(),"您所输入的用户不存在！",Toast.LENGTH_SHORT).show();
					break;
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
		userId= (String) SharedPreferencesUtils.getParam(this, "userInfo","userId", "");
		nickName= (String) SharedPreferencesUtils.getParam(this, "userInfo", HTTPKey.USER_NICKNAME, "");
		photoUrl= (String) SharedPreferencesUtils.getParam(this, "userInfo", HTTPKey.USER_PHOTO, "");
		accessToken = (String) SharedPreferencesUtils.getParam(this, "userInfo","accessToken", "");
		commonUploadToken= (String) SharedPreferencesUtils.getParam(this, "userInfo","commonUploadToken", "");
//		photo= (String) SharedPreferencesUtils.getParam(this, "photo", "");
		nameTopic= (EditText) findViewById(R.id.et_name_topic);
		upCouver= (LinearLayout) findViewById(R.id.layout_pic);
		month_pv=(PickerDateView) findViewById(R.id.month_pv);
		rl_living= (RelativeLayout) findViewById(R.id.rl_living_sign);
		rl_sunscribe= (RelativeLayout) findViewById(R.id.rl_sunscribe);
//		day_pv = (PickerView) findViewById(R.id.day_pv);
		hour_pv = (PickerView) findViewById(R.id.hour_pv);
		minute_pv=(PickerView) findViewById(R.id.minute_pv);
		Ensure =(me.drakeet.library.UIButton) findViewById(R.id.tv_order);
		UnEnsure =(me.drakeet.library.UIButton) findViewById(R.id.tv_cancle);

//        ImageView image= (ImageView) findViewById(R.id.logo_frame);
//		image.setBackgroundResource(R.drawable.logo_round);
//		AnimationDrawable anim = (AnimationDrawable) image.getBackground();
//		       anim.start();
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
				if (selectedMin<10){
//					selectedHour=Integer.parseInt(text);
					time.setText(selectedHour+":"+"0"+selectedMin);
				}else{
//					selectedHour=Integer.parseInt(text);
					time.setText(selectedHour+":"+selectedMin);


				}

			}
		});
		minute_pv.setData(seconds);
		minute_pv.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{

				if (selectedMin<10){
//					selectedHour=Integer.parseInt(text);
					time.setText(selectedHour+":"+"0"+selectedMin);
				}else{
//					selectedHour=Integer.parseInt(text);
					time.setText(selectedHour+":"+selectedMin);


				}
			}
		});

	}
	private void initDate() {

		EditTextFilter();
		//直播or预约
		Intent intent=getIntent();
		living=intent.getBooleanExtra("living",false);
		if (living){
			state="1";
			pickView.setVisibility(View.GONE);
			divider.setVisibility(View.GONE);
			ulookIcon.setVisibility(View.GONE);
			rl_living.setVisibility(View.VISIBLE);
			year.setVisibility(View.GONE);
			time.setVisibility(View.GONE);
			Ensure.setBackgroundResource(R.drawable.tv_live);
			UnEnsure.setBackgroundResource(R.drawable.tv_unlive);
		}else {
			rl_sunscribe.setVisibility(View.VISIBLE);
			state="0";
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
		selectedHour=getNowHour();
		selectedMin=getNowMinte();
		 for (int i = 0; i <= 7; i++) {

				 DateFormatToString(i);
				
		}
	
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		for (int i = 0; i < 24; i++)
		{
			if (i<10){
				hours.add("0" + i);
			}else

			hours.add("" + i);
		}
		
		for (int i = 0; i < 60; i++)
		{
			seconds.add(i < 10 ? "0" + i : "" + i);
		}

		//截取图片相关

	}

	private void EditTextFilter() {
		final int maxLen = 24;
		 filter = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
				int dindex = 0;
				int count = 0;

				while (count <= maxLen && dindex < dest.length()) {
					char c = dest.charAt(dindex++);
					if (c < 128) {
						count = count + 1;
					} else {
						count = count + 2;
					}
				}

				if (count > maxLen) {
					return dest.subSequence(0, dindex - 1);
				}

				int sindex = 0;
				while (count <= maxLen && sindex < src.length()) {
					char c = src.charAt(sindex++);
					if (c < 128) {
						count = count + 1;
					} else {
						count = count + 2;
					}
				}

				if (count > maxLen) {
					sindex--;
				}

				return src.subSequence(0, sindex);
			}
		};
		nameTopic.setFilters(new InputFilter[]{filter});
	}

	public  String DateFormatToString(int position){
		if (position!=0) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		 date=calendar.getTime();
		 reStr = simpleDateFormat.format(date);
		 if (position<4) {
			 if (position==0){
				 Datas.add("今天");
			 }else
			 Datas.add(reStr);
		}else if(position>=4&&position<7){
			 Datas.add(temp++,reStr);
		} else if (position==7) {
//			 Datas.add(temp,"预约截止日");
		}
		 return reStr;

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.layout_pic:

				if (nameTopic.getText().toString().trim()==null||nameTopic.getText().toString().trim().equals("")){
					Toast.makeText(TimePickerActivity.this,"请输入直播话题后再选择图片",Toast.LENGTH_SHORT).show();
					break;
				}
				chooseImage.setText("选择照片");
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
				Calendar ca = Calendar.getInstance();
				int hour = ca.get(Calendar.HOUR_OF_DAY);
				int min = ca.get(Calendar.MINUTE);
				if (selectDateText.equals(getNowTime())) {
					//day等于当前时间的 进一步判断时分
					if (selectedHour < hour) {
						//小时大于当前
						Toast.makeText(TimePickerActivity.this, "请选择正确的预约时间",
								Toast.LENGTH_SHORT).show();
						return;
					} else if (selectedHour == hour) {
						//小时相同的进一步判断 分钟
						if (selectedMin <=min) {
							Toast.makeText(TimePickerActivity.this, "请选择正确的预约时间",
									Toast.LENGTH_SHORT).show();
							return;
						}
					}
					}
				//day大于现在的选择的直接 ok
//				Toast.makeText(TimePickerActivity.this, "时间ok,day",
//						Toast.LENGTH_SHORT).show();
				 if (!EmptyPic){
					Toast.makeText(TimePickerActivity.this, "请选择图片封面", Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(TimePickerActivity.this, "开始上传图片",
						Toast.LENGTH_SHORT).show();
				dialog = new SpotsDialog(TimePickerActivity.this, R.style.Custom1);
				dialog.show();
				uploadImageToQiNiu();
			}else {
				if (nameTopic.getText().toString().trim() == null || nameTopic.getText().toString().trim().equals("")) {
					Toast.makeText(TimePickerActivity.this, "请输入直播话题后再选择图片", Toast.LENGTH_SHORT).show();
					break;
				}else if (!EmptyPic){
					Toast.makeText(TimePickerActivity.this, "请选择图片封面", Toast.LENGTH_SHORT).show();
					break;
				}
				dialog = new SpotsDialog(TimePickerActivity.this, R.style.Custom);
				dialog.show();
				uploadImageToQiNiu();
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
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
		String    str    =    formatterDay.format(curDate);
		return str;

	}
	public int getNowHour(){
		SimpleDateFormat    formatterDay    =   new    SimpleDateFormat    ("HH");
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
		String    str    =    formatterDay.format(curDate);
		int min= Integer.valueOf(str);
		return min;

	}
	public int getNowMinte(){
		SimpleDateFormat    formatterDay    =   new    SimpleDateFormat    ("mm");
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
		String    str    =    formatterDay.format(curDate);
		int min= Integer.valueOf(str);
		return min;

	}
	public boolean uploadImageToQiNiu(){
		// 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
		UploadManager uploadManager = new UploadManager();
//		data = <File对象、或 文件路径、或 字节数组>
		long key = System.currentTimeMillis();
//		String token = "hcy3ZbColTeL72Wdm7-AQowHPdJH4Ngf7dfcr5eU:qmhW6-lQpViKZk_P3nWbsCbB6s8=:eyJzY29wZSI6ImxpYW5nemkiLCJyZXR1cm5Cb2R5Ijoie1wia2V5XCI6JChrZXkpLFwidXVpZFwiOiQodXVpZCksXCJuYW1lXCI6JChmbmFtZSksXCJzaXplXCI6JChmc2l6ZSksXCJ3XCI6JChpbWFnZUluZm8ud2lkdGgpLFwiaFwiOiQoaW1hZ2VJbmZvLmhlaWdodCksXCJoYXNoXCI6JChldGFnKX0iLCJkZWFkbGluZSI6MTQzNjM4NDY1Nn0=";
		uploadManager.put(cropFile, String.valueOf(key), commonUploadToken,
				new UpCompletionHandler() {
					@Override
					public void complete(String key, ResponseInfo info, JSONObject response) {
						upImage = "http://7xix0q.com2.z0.glb.qiniucdn.com/" + String.valueOf(key);
						Log.e("qiniu", String.valueOf(info) + ".jpg");
						Thread liveThread = new Thread(new LiveThread());
						liveThread.start();
					}
				}, null);
		return true;
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

//				int angle= ImageUtil.getExifOrientation(fileName);
//				if(angle!=0) {  //如果照片出现了 旋转 那么 就更改旋转度数
//					Matrix matrix = new Matrix();
//					matrix.postRotate(angle);
//					Bitmap photoViewBitmap = null;
//					 photoViewBitmap = Bitmap.createBitmap(photoViewBitmap,
//							0, 0, photoViewBitmap.getWidth(), photoViewBitmap.getHeight(), matrix, true);
//				}
//					ImageUtil.rotateBitmapByDegree(fileName,ImageUtil.readPictureDegree(outFile.getAbsolutePath()));
				new Crop(imgSource).output(outSource).start(TimePickerActivity.this);
			}
		} else if (requestCode == RESULT_CANCELED) {
			EmptyPic = false;
			return;
		} else if (requestCode == Crop.REQUEST_CROP) {
			Uri imgResult;
			if(data!=null) {
				try {
					imgResult = data.getData();
					cropFile = new File(new URI(imgResult.toString()));
				} catch (Exception e) {
//				dialog.dismiss();
					e.getMessage().toString();
					return;
				}
				EmptyPic = true;
				imageLoader.displayImage(photoUrl,userHead, options, animateFirstListener);
				preview.setVisibility(View.VISIBLE);
				coverPic.setImageURI(imgResult);
				chooseImage.setText("更换封面");
				theme.setText(nameTopic.getText().toString().trim());
				userName.setText(nickName);
				year.setText(selectDateText);
				time.setText(selectedHour + ":" + selectedMin);
			}
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
						.add("img", upImage)
						.add("title", nameTopic.getText().toString().trim())
						.add("accessToken", accessToken);
				if (!living){
					long mill=DateUtil.getStringToDate(selectDateText + " " + selectedHour + ":" + selectedMin);
					millis=	String.valueOf(mill);
					Body.add("reserved", millis);
				}
				try {
					postLive(url,Body.build());
				} catch (IOException e) {
					cropFile.delete();
					dialog.dismiss();
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
							Log.e("livefragment", "....................");
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
