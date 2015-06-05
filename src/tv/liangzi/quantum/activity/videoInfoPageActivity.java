package tv.liangzi.quantum.activity;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.adapter.DiscussAdapter;
import tv.liangzi.quantum.base.BaseActivity;
import tv.liangzi.quantum.bean.ServiceStatus;
import tv.liangzi.quantum.bean.VideoCommits;
import tv.liangzi.quantum.config.MyAapplication;
import tv.liangzi.quantum.mediaplayer.DensityUtil;
import tv.liangzi.quantum.mediaplayer.FullScreenVideoView;
import tv.liangzi.quantum.mediaplayer.LightnessController;
import tv.liangzi.quantum.mediaplayer.VolumnController;
import tv.liangzi.quantum.utils.OkHttpUtil;
import tv.liangzi.quantum.view.SharePopupWindow;
import tv.liangzi.quantum.view.XListView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Display;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class videoInfoPageActivity extends BaseActivity implements OnClickListener, TextWatcher, XListView.IXListViewListener ,View.OnKeyListener {
	private ImageView forword;
	private ImageView love;
	private ImageView discuss;
	private ImageView download;
	private TextView tvLove;
	private TextView tvForword;
	private TextView tvDownload;
	private FullScreenVideoView mVideo;
	private XListView listview;

	private EditText etDiscuss;
	private RelativeLayout rlText;
	//    private RelativeLayout rlOpreation;
	private String TAG = "videoInfoPage";
	int time = 0;
	int temp = 0;
	RelativeLayout rlIput;
	private static final int HIDE_TIME = 5000;
	private SeekBar mSeekBar;
	private ImageView mPlay;
	private TextView mPlayTime;
	private TextView mDurationTime;
	private RelativeLayout rl_videoView;
	private AudioManager mAudioManager;

	private TextView sendTV;

	private boolean sensor_flag = true;
	private boolean stretch_flag = true;
	private SensorManager sm;
	private OrientationSensorListener listener;
	private Sensor sensor;
	private SensorManager sm1;
	private Sensor sensor1;
	private OrientationSensorListener2 listener1;
	public boolean record_flag;

	private float width;
	private float height;
	private ImageView switchbtn;
	private int playTime;
	private String videoURL = "";
	private ServiceStatus.Video videoObj;
	private String videoId;
	private tv.liangzi.quantum.mediaplayer.VolumnController volumnController;
	private int screenWidth;
	private int screenHeight;

	private int orginalLight;
	private RelativeLayout mBottomView;
	private SharedPreferences videoSp;
	private String userId;
	private String photo;
	private String accessToken;
	private DiscussAdapter adapter;
	private View HeadView;



	//文字長度限制
	final int MAX_LENGTH = 20;
	int Rest_Length = MAX_LENGTH;
	String textContent = "";

	//分享相关
	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	private List<VideoCommits.Comments> commits = new ArrayList<VideoCommits.Comments>();
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					if (mVideo.getCurrentPosition() > 0) {
						mPlayTime.setText(formatTime(mVideo.getCurrentPosition()) + "/");
						int progress = mVideo.getCurrentPosition() * 100 / mVideo.getDuration();
						mSeekBar.setProgress(progress);
						if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
							mPlayTime.setText("00:00/");
							mSeekBar.setProgress(0);
						}
						mSeekBar.setSecondaryProgress(mVideo.getBufferPercentage());
					} else {
						mPlayTime.setText("00:00/");
						mSeekBar.setProgress(0);
					}

					break;
				case 2:
					showOrHide();
					break;
				case 3:
					adapter.setmList(commits);
					adapter.notifyDataSetChanged();
					break;
				case 4:
					adapter.setmList(commits);
					adapter.notifyDataSetChanged();
					break;
				case 5:
					VideoCommits.Comments myComment = (VideoCommits.Comments) msg.obj;
					commits.add(0, myComment);
					adapter.setmList(commits);
					adapter.notifyDataSetChanged();
				case 888:
					int orientation = msg.arg1;
//				if (orientation>45&&orientation<135) {
//
//				}else if (orientation>135&&orientation<225){
//
//				}else
					if (orientation > 225 && orientation < 315) {
						System.out.println("切换成横屏");
						videoInfoPageActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						sensor_flag = false;
						stretch_flag = false;
//					showOrHide();

					} else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
						System.out.println("切换成竖屏");
						videoInfoPageActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						sensor_flag = true;
						stretch_flag = true;
//					show();
					}
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_videoinfo);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		videoSp = getSharedPreferences("userInfo", MODE_PRIVATE);
		userId = videoSp.getString("userId", "0");
		accessToken = videoSp.getString("accessToken", "0");
		photo = videoSp.getString("photo", "");
		Intent intent = getIntent();
		videoURL = intent.getStringExtra("playUrl");
		videoObj = (ServiceStatus.Video) intent.getSerializableExtra("videoObj");
		videoId = String.valueOf(videoObj.getVideoId());
	}


	@Override
	public void initViews() {
		//注册重力感应器  屏幕旋转 
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		listener = new OrientationSensorListener(mHandler);
		sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);


		//根据  旋转之后 点击 符合之后 激活sm
		sm1 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor1 = sm1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		listener1 = new OrientationSensorListener2();
		sm1.registerListener(listener1, sensor1, SensorManager.SENSOR_DELAY_UI);


		// TODO Auto-generated method stub
		sendTV = (TextView) findViewById(R.id.tv_send);
		switchbtn = (ImageView) findViewById(R.id.iv_stretch);
		forword = (ImageView) findViewById(R.id.im_forword);
		love = (ImageView) findViewById(R.id.im_love);
//		discuss=(ImageView) findViewById(R.id.im_disscuss);
		download = (ImageView) findViewById(R.id.im_download);
		mVideo = (FullScreenVideoView) findViewById(R.id.VV_videoInfo);
		etDiscuss = (EditText) findViewById(R.id.et_disscuss);
		listview = (XListView) findViewById(R.id.lv_discuss);
		mPlay = (ImageView) findViewById(R.id.play_controller);
		rl_videoView = (RelativeLayout) findViewById(R.id.rl_videoinfo_parent);
		mBottomView = (RelativeLayout) findViewById(R.id.bottom_layout);
		rlText = (RelativeLayout) findViewById(R.id.rl_videoinfo_text);
//       TextView tv_videoinfo=(TextView) findViewById(R.id.tv_info);
//        tv_videoinfo.setMovementMethod(ScrollingMovementMethod.getInstance());
//        rlOpreation= (RelativeLayout) findViewById(R.id.rl_videoinfo_operation);
		rlIput = (RelativeLayout) findViewById(R.id.rl_videoinfo_input);
		rlIput.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {

						int[] location = new int[2];
						rlIput.getLocationOnScreen(location);
						int y = location[1];
						int heightDiff = rlIput.getRootView().getHeight() - rlIput.getHeight();
						Log.v(TAG, "detailMainRL.getRootView().getHeight() = " + y);
						Log.e(TAG, "输入监听状态time " + time);
						if (time != 0) {
							if (temp == y) { // 说明键盘是没弹出状态
								Log.v(TAG, "videoinfo键盘没弹起状态");

							} else if (temp > y) {
								Log.v(TAG, "videoinfo键盘弹起状态");
//	                            rlText.setVisibility(View.INVISIBLE);
//                                listview.setVisibility(View.INVISIBLE);
//                                rlOpreation.setVisibility(View.GONE);
							}

						}
						time++;
					}
				});


		//视频部分
		mPlayTime = (TextView) findViewById(R.id.play_time);
		mDurationTime = (TextView) findViewById(R.id.total_time);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		volumnController = new VolumnController(this);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		width = DensityUtil.getWidthInPx(this);
		height = DensityUtil.getHeightInPx(this);
		threshold = DensityUtil.dip2px(this, 18);

		orginalLight = LightnessController.getLightness(this);

		mPlay.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

		playVideo();

		//按鈕數據初始化
		tvDownload = (TextView) findViewById(R.id.tv_download);
		tvForword = (TextView) findViewById(R.id.tv_forword);
		tvLove = (TextView) findViewById(R.id.tv_love);
		tvLove.setText(videoObj.getLikes() + "");
		tvDownload.setText(videoObj.getDownloads() + "");
		tvForword.setText(videoObj.getShares() + "");
		//頭部佈局 數據加載 然後加入listview的head位置
		HeadView = LayoutInflater.from(this).inflate(
				R.layout.videoinfo_listview_header, null);
		TextView videoName = (TextView) HeadView.findViewById(R.id.tv_title);
		TextView videoClassify = (TextView) HeadView.findViewById(R.id.tv_videoinfo_time);
		TextView videoDescripe = (TextView) HeadView.findViewById(R.id.tv_info);
		TextView videoUpUser = (TextView) HeadView.findViewById(R.id.tv_videoinfo_upload);
//		TextView videoName= (TextView) HeadView.findViewById(R.id.tv_title);
		videoName.setText(videoObj.getTitle());
		videoClassify.setVisibility(View.GONE);
		videoDescripe.setText(videoObj.getDescribe());


// 加载评论
		Thread commentsThread = new Thread(new CommentsThread());
		commentsThread.start();


	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		RelativeLayout.LayoutParams relaParams = (android.widget.RelativeLayout.LayoutParams) rl_videoView.getLayoutParams();
		if (stretch_flag) {
			//切换成竖屏
			relaParams.width = screenWidth;
			relaParams.height = DensityUtil.dip2px(this, 200);
			rl_videoView.setLayoutParams(relaParams);
			etDiscuss.setVisibility(View.VISIBLE);
			Log.e(TAG, "竖屏监听下。。time " + time);
		} else {
			relaParams.width = screenHeight;
			relaParams.height = screenWidth;
			rl_videoView.setLayoutParams(relaParams);
			etDiscuss.setVisibility(View.GONE);
//			time=0;
			Log.e(TAG, "横屏监听下状态time " + time);

		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LightnessController.setLightness(this, orginalLight);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub

		int[] location = new int[2];
		rlIput.getLocationOnScreen(location);
		temp = location[1];
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void initListeners() {
		switchbtn.setOnClickListener(this);
		mVideo.setOnClickListener(this);
		love.setOnClickListener(this);
		listview.setXListViewListener(this);
		listview.setPullRefreshEnable(false);
		listview.setPullLoadEnable(true);
		listview.addHeaderView(HeadView);
		sendTV.setOnClickListener(this);
		etDiscuss.addTextChangedListener(this);
		etDiscuss.setOnKeyListener(this);
		forword.setOnClickListener(this);


	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
//		videoURL="http://hot.vrs.sohu.com/ipad2025214_4639791893179_5236535.m3u8?plat=17";
		adapter = new DiscussAdapter(videoInfoPageActivity.this, commits);
		listview.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.play_controller:

				if (mVideo.isPlaying()) {
					mVideo.pause();
//				mPlay.setVisibility(View.VISIBLE);
					mPlay.setImageResource(R.drawable.video_btn_down);
				} else {
					mVideo.start();
//				mPlay.setVisibility(View.GONE);
					mPlay.setImageResource(R.drawable.video_btn_on);
				}


				break;
			case R.id.VV_videoInfo:
				Toast.makeText(videoInfoPageActivity.this, "videoview点击了", Toast.LENGTH_SHORT).show();
				if (mVideo.isPlaying()) {
					mVideo.pause();
					mPlay.setVisibility(View.VISIBLE);
					mPlay.setImageResource(R.drawable.video_btn_down);
				} else {
//				mVideo.start();
//				mPlay.setVisibility(View.GONE);
////				mPlay.setImageResource(R.drawable.video_btn_on);
				}
			case R.id.iv_stretch:

				sm.unregisterListener(listener);
				Toast.makeText(getApplicationContext(), "点击切换屏幕", Toast.LENGTH_SHORT).show();
				if (stretch_flag) {
					stretch_flag = false;
					//切换成横屏
					videoInfoPageActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

				} else {
					stretch_flag = true;
					//切换成竖屏
					videoInfoPageActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

				}

				break;
			case R.id.im_love:
				Thread serviceThread = new Thread(new ServiceThread());
				serviceThread.start();
				break;
			case R.id.tv_send:
				//發送評論接口
				if (etDiscuss.getText().equals("")) {
					break;
				}
				Thread disscussThread = new Thread(new DisscussThread());
				disscussThread.start();
				hideInput(videoInfoPageActivity.this, sendTV);
				etDiscuss.setText("");
				break;
			case R.id.im_forword:

				SharePopupWindow share = new SharePopupWindow(videoInfoPageActivity.this, mHandler, videoId);
//				share.setPlatformActionListener(MainActivity.this);
				share.showShareWindow();
				// 显示窗口 (设置layout在PopupWindow中显示的位置)
				share.showAtLocation(videoInfoPageActivity.this.getLayoutInflater().inflate(R.layout.activity_videoinfo, null),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//				mController.openShare(videoInfoPageActivity.this, false);
				break;
//			case R.id.thumb:
//				//点赞接口
//				thumb.setImageResource(R.drawable.thumb);
//				break;
			default:
				break;
		}
	}


	//手势部分
	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mHandler.postDelayed(hideRunnable, HIDE_TIME);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mHandler.removeCallbacks(hideRunnable);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
									  boolean fromUser) {
			if (fromUser) {
				int time = progress * mVideo.getDuration() / 100;
				mVideo.seekTo(time);
			}
		}
	};

	private void backward(float delataX) {
		int current = mVideo.getCurrentPosition();
		int backwardTime = (int) (delataX / width * mVideo.getDuration());
		int currentTime = current - backwardTime;
		mVideo.seekTo(currentTime);
		mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
		mPlayTime.setText(formatTime(currentTime) + "/");
	}

	private void forward(float delataX) {
		int current = mVideo.getCurrentPosition();
		int forwardTime = (int) (delataX / width * mVideo.getDuration());
		int currentTime = current + forwardTime;
		mVideo.seekTo(currentTime);
		mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
		mPlayTime.setText(formatTime(currentTime) + "/");
	}

	private void volumeDown(float delatY) {
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int down = (int) (delatY / height * max * 3);
		int volume = Math.max(current - down, 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		int transformatVolume = volume * 100 / max;
		volumnController.show(transformatVolume);
	}

	private void volumeUp(float delatY) {
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int up = (int) ((delatY / height) * max * 3);
		int volume = Math.min(current + up, max);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		int transformatVolume = volume * 100 / max;
		volumnController.show(transformatVolume);
	}

	private void lightDown(float delatY) {
//		int down = (int) (delatY / height * 255 * 3);
//		int transformatLight = LightnessController.getLightness(this) - down;
//		LightnessController.setLightness(this, transformatLight);
	}

	private void lightUp(float delatY) {
//		int up = (int) (delatY / height * 255 * 3);
//		int transformatLight = LightnessController.getLightness(this) + up;
//		LightnessController.setLightness(this, transformatLight);
	}

	@SuppressLint("SimpleDateFormat")
	private String formatTime(long time) {
		DateFormat formatter = new SimpleDateFormat("mm:ss");
		return formatter.format(new Date(time));
	}

	private float mLastMotionX;
	private float mLastMotionY;
	private int startX;
	private int startY;
	private int threshold;
	private boolean isClick = true;

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final float x = event.getX();
			final float y = event.getY();

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mLastMotionX = x;
					mLastMotionY = y;
					startX = (int) x;
					startY = (int) y;
					break;
				case MotionEvent.ACTION_MOVE:
					float deltaX = x - mLastMotionX;
					float deltaY = y - mLastMotionY;
					float absDeltaX = Math.abs(deltaX);
					float absDeltaY = Math.abs(deltaY);
					// 澹伴煶璋冭妭鏍囪瘑
					boolean isAdjustAudio = false;
					if (absDeltaX > threshold && absDeltaY > threshold) {
						if (absDeltaX < absDeltaY) {
							isAdjustAudio = true;
						} else {
							isAdjustAudio = false;
						}
					} else if (absDeltaX < threshold && absDeltaY > threshold) {
						isAdjustAudio = true;
					} else if (absDeltaX > threshold && absDeltaY < threshold) {
						isAdjustAudio = false;
					} else {
						return true;
					}
					if (isAdjustAudio) {
						if (x < width / 2) {
							if (deltaY > 0) {
								lightDown(absDeltaY);
							} else if (deltaY < 0) {
								lightUp(absDeltaY);
							}
						} else {
							if (deltaY > 0) {
								volumeDown(absDeltaY);
							} else if (deltaY < 0) {
								volumeUp(absDeltaY);
							}
						}

					} else {
						if (deltaX > 0) {
							forward(absDeltaX);
						} else if (deltaX < 0) {
							backward(absDeltaX);
						}
					}
					mLastMotionX = x;
					mLastMotionY = y;
					break;
				case MotionEvent.ACTION_UP:
					if (Math.abs(x - startX) > threshold
							|| Math.abs(y - startY) > threshold) {
						isClick = false;
					}
					mLastMotionX = 0;
					mLastMotionY = 0;
					startX = (int) 0;
					if (isClick) {
						showOrHide();
					}
					isClick = true;
					break;

				default:
					break;
			}
			return true;
		}

	};

	//handler部分
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(0);
		mHandler.removeCallbacksAndMessages(null);
	}


	//播放部分
	private void playVideo() {
		mVideo.setVideoPath(videoURL);
		mVideo.requestFocus();
		mVideo.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mVideo.setVideoWidth(mp.getVideoWidth());
				mVideo.setVideoHeight(mp.getVideoHeight());

				mVideo.start();
				if (playTime != 0) {
					mVideo.seekTo(playTime);
				}

				mHandler.removeCallbacks(hideRunnable);
				mHandler.postDelayed(hideRunnable, HIDE_TIME);
				mDurationTime.setText(formatTime(mVideo.getDuration()));
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						mHandler.sendEmptyMessage(1);
					}
				}, 0, 1000);
			}
		});
		mVideo.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mPlay.setImageResource(R.drawable.video_btn_down);
				mPlayTime.setText("00:00/");
				mSeekBar.setProgress(0);
			}
		});
		mVideo.setOnTouchListener(mTouchListener);
	}

	//布局显示与隐藏

	private void showOrHide() {
		if (mBottomView.getVisibility() == View.VISIBLE) {

			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(this,
					R.anim.option_leave_from_bottom);
			animation1.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mBottomView.setVisibility(View.GONE);
				}
			});
			mBottomView.startAnimation(animation1);
		} else {

			mBottomView.setVisibility(View.VISIBLE);
			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(this,
					R.anim.option_entry_from_bottom);
			mBottomView.startAnimation(animation1);
			mHandler.removeCallbacks(hideRunnable);
			mHandler.postDelayed(hideRunnable, HIDE_TIME);
		}
	}

	@Override
	public void onRefresh() {

	}


	@Override
	public void onLoadMore() {
		Log.e(TAG, "onload more");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				String url = MyAapplication.IP + "0/" + videoId + "/videoComments" + "?freshen=footer" + "&timeline=" + commits.get(commits.size() - 1).getCreated();
				try {
					getVideoCommentsMore(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
				listview.stopLoadMore();
			}
		}, 2000);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {


	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (Rest_Length > 0) {
			Rest_Length = MAX_LENGTH - etDiscuss.getText().length();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (Rest_Length == 5) {
			Toast.makeText(videoInfoPageActivity.this, "还能输入" + Rest_Length + "个字", Toast.LENGTH_SHORT).show();
		} else if (Rest_Length == 0) {
			Toast.makeText(videoInfoPageActivity.this, "输入文字已达到上限！", Toast.LENGTH_SHORT).show();

		}

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
			//發送評論接口
			if (etDiscuss.getText().equals("")) {
			}
			Thread disscussThread = new Thread(new DisscussThread());
			disscussThread.start();
//			hideInput(videoInfoPageActivity.this, sendTV);
			etDiscuss.setText("");
		}
		return false;
	}

	private class AnimationImp implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

	/**
	 * 重力感应监听者
	 */
	public class OrientationSensorListener implements SensorEventListener {
		private static final int _DATA_X = 0;
		private static final int _DATA_Y = 1;
		private static final int _DATA_Z = 2;

		public static final int ORIENTATION_UNKNOWN = -1;

		private Handler rotateHandler;

		public OrientationSensorListener(Handler handler) {
			rotateHandler = handler;
		}

		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onSensorChanged(SensorEvent event) {

			if (sensor_flag != stretch_flag)  //只有两个不相同才开始监听行为
			{
				float[] values = event.values;
				int orientation = ORIENTATION_UNKNOWN;
				float X = -values[_DATA_X];
				float Y = -values[_DATA_Y];
				float Z = -values[_DATA_Z];
				float magnitude = X * X + Y * Y;
				// Don't trust the angle if the magnitude is small compared to the y value
				if (magnitude * 4 >= Z * Z) {
					//屏幕旋转时
					float OneEightyOverPi = 57.29577957855f;
					float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
					orientation = 90 - (int) Math.round(angle);
					// normalize to 0 - 359 range
					while (orientation >= 360) {
						orientation -= 360;
					}
					while (orientation < 0) {
						orientation += 360;
					}
				}
				if (rotateHandler != null) {
					rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
				}

			}
		}
	}


	public class OrientationSensorListener2 implements SensorEventListener {
		private static final int _DATA_X = 0;
		private static final int _DATA_Y = 1;
		private static final int _DATA_Z = 2;

		public static final int ORIENTATION_UNKNOWN = -1;

		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onSensorChanged(SensorEvent event) {

			float[] values = event.values;

			int orientation = ORIENTATION_UNKNOWN;
			float X = -values[_DATA_X];
			float Y = -values[_DATA_Y];
			float Z = -values[_DATA_Z];

			/**
			 * 这一段据说是 android源码里面拿出来的计算 屏幕旋转的 不懂 先留着 万一以后懂了呢
			 */
			float magnitude = X * X + Y * Y;
			// Don't trust the angle if the magnitude is small compared to the y value
			if (magnitude * 4 >= Z * Z) {
				//屏幕旋转时
				float OneEightyOverPi = 57.29577957855f;
				float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
				orientation = 90 - (int) Math.round(angle);
				// normalize to 0 - 359 range
				while (orientation >= 360) {
					orientation -= 360;
				}
				while (orientation < 0) {
					orientation += 360;
				}
			}

			if (orientation > 225 && orientation < 315) {  //横屏
				sensor_flag = false;
			} else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {  //竖屏
				sensor_flag = true;
			}

			if (stretch_flag == sensor_flag) {  //点击变成横屏  屏幕 也转横屏 激活
				System.out.println("激活");
				sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);

			}
		}
	}


	private Runnable hideRunnable = new Runnable() {

		@Override
		public void run() {
			showOrHide();
		}
	};


	/**
	 * 用戶點讚接口post
	 *
	 * @param url
	 * @throws IOException
	 */
	void post(String url) throws IOException {

		RequestBody formBody = new FormEncodingBuilder()
				.add("userId", userId)
				.add("videoId", videoId)
				.add("toType", "0")
				.add("accessToken", accessToken)
				.build();

		Request request = new Request.Builder()
				.url(url)
				.post(formBody)
				.build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.getMessage();
			}

			@Override
			public void onResponse(Response response) throws IOException {
				response.body().toString();

				Log.e("videoInfoActivity", "创建成功，还需要对返回code作进一步处理");
			}
		});


	}


	/**
	 * 用戶評論接口post
	 *
	 * @param url
	 * @throws IOException
	 */
	void DisscussPost(String url) throws IOException {
		textContent = etDiscuss.getText().toString();
		RequestBody formBody = new FormEncodingBuilder()
				.add("userId", userId)
				.add("videoId", videoId)
				.add("toType", "0")
				.add("content", textContent)
				.add("accessToken", accessToken)
				.build();

		Request request = new Request.Builder()
				.url(url)
				.post(formBody)
				.build();
		OkHttpUtil.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.getMessage();
			}

			@Override
			public void onResponse(Response response) throws IOException {

				if (response.isSuccessful()) {
					VideoCommits.Comments myComment = new VideoCommits.Comments();
					myComment.setContent(textContent);
					myComment.setPhoto(photo);
					Message msgInfo = new Message();
					msgInfo.what = 5;
					msgInfo.obj = myComment;
					mHandler.sendMessage(msgInfo);
					Log.e("videoInfoActivity", "評論成功");

				} else {
					Log.e("videoInfoActivity", "評論失敗，还需要对返回code作进一步处理");

				}
			}

		});


	}


	/**
	 * get评论列表接口 第一次刷新
	 *
	 * @param url
	 * @throws IOException
	 */
	void getVideoComments(String url) throws IOException {
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


				VideoCommits videoCommits = gson.fromJson(response.body().charStream(), new TypeToken<VideoCommits>() {
				}.getType());
				commits = videoCommits.getComments();
				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessage(msg);
				Log.e("videoInfoActivity", commits.size() + "");
			}
		});


	}

	/**
	 * get评论列表接口 加載更多
	 *
	 * @param url
	 * @throws IOException
	 */
	void getVideoCommentsMore(String url) throws IOException {
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


				VideoCommits videoCommits = gson.fromJson(response.body().charStream(), new TypeToken<VideoCommits>() {
				}.getType());
				commits.addAll(commits.size(), videoCommits.getComments());
				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessage(msg);
				Log.e("videoInfoActivity", commits.size() + "");
			}
		});
	}

	class ServiceThread implements Runnable {
		@Override
		public void run() {
			String LoveUrl = MyAapplication.IP + "videoLike";
			try {
				post(LoveUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//   备用子线程
			Log.e("log", "发出请求");

		}
	}

	class CommentsThread implements Runnable {
		@Override
		public void run() {
			String url = MyAapplication.IP + "0/" + videoId + "/videoComments";
			try {
				getVideoComments(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//   备用子线程
			Log.e("log", "发出请求");

		}
	}

	class DisscussThread implements Runnable {
		@Override
		public void run() {
			String url = MyAapplication.IP + "videoComment";
			try {
				DisscussPost(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//   备用子线程
			Log.e("log", "发出请求");

		}
	}


	/**
	 * 强制隐藏输入法键盘
	 */
	private void hideInput(Context context, View view) {
		InputMethodManager inputMethodManager =
				(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}


}

