package org.fireking.app.imagelib.widget;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.fireking.app.imagelib.R;
import org.fireking.app.imagelib.R.drawable;
import org.fireking.app.imagelib.R.id;
import org.fireking.app.imagelib.R.layout;
import org.fireking.app.imagelib.entity.AlbumBean;
import org.fireking.app.imagelib.entity.ImageBean;
import org.fireking.app.imagelib.tools.AlbumHelper;
import org.fireking.app.imagelib.tools.Config;
import org.fireking.app.imagelib.tools.NativeImageLoader;
import org.fireking.app.imagelib.tools.Uitls;
import org.fireking.app.imagelib.tools.NativeImageLoader.NativeImageCallBack;
import org.fireking.app.imagelib.view.MyImageView;
import org.fireking.app.imagelib.view.MyImageView.OnMeasureListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author fireking
 * 
 */
public class PicSelectActivity extends FragmentActivity implements
		OnItemClickListener {
	private static final int PHOTO_GRAPH = 1;// ����

	GridView gridView;
	PicSelectAdapter adapter;
	TextView album;
	TextView complete;
	TextView preView;

	TextView back;

	String fileName;// 文件名，路径
	String dirPath;//
	static final int SCAN_OK = 0x1001;

	static boolean isOpened = false;
	PopupWindow popWindow;

	int selected = 0;

	int height = 0;
	List<AlbumBean> mAlbumBean;
	public static final String IMAGES = "images";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.the_picture_selection);
		back = (TextView) this.findViewById(R.id.back);
		album = (TextView) this.findViewById(R.id.album);
		complete = (TextView) this.findViewById(R.id.complete);
		preView = (TextView) this.findViewById(R.id.preview);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		preView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<ImageBean> selecteds = getSelectedItem();
				Intent intent = new Intent();
				intent.putExtra(IMAGES, (Serializable) selecteds);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		album.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isOpened && popWindow != null) {
					height = getWindow().getDecorView().getHeight();
					WindowManager.LayoutParams ll = getWindow().getAttributes();
					ll.alpha = 0.3f;
					getWindow().setAttributes(ll);
					popWindow.showAtLocation(
							findViewById(android.R.id.content),
							Gravity.NO_GRAVITY, 0,
							height - Uitls.dip2px(PicSelectActivity.this, 448));
				} else {
					if (popWindow != null) {
						popWindow.dismiss();
					}
				}
			}
		});
		gridView = (GridView) this.findViewById(R.id.child_grid);
		adapter = new PicSelectAdapter(PicSelectActivity.this, gridView,
				onImageSelectedCountListener);
		gridView.setAdapter(adapter);
		adapter.setOnImageSelectedListener(onImageSelectedListener);
		showPic();
		gridView.setOnItemClickListener(this);
	}

	/**
	 */
	private void takePhoto() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			fileName = getFileName();
			System.out.println(Environment.getExternalStorageDirectory()
					.toString());
			System.out.println(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
			dirPath = Environment.getExternalStorageDirectory().getPath()
					+ Config.getSavePath();
			File tempFile = new File(dirPath);
			if (!tempFile.exists()) {
				tempFile.mkdirs();
			}
			File saveFile = new File(tempFile, fileName + ".jpg");
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
			startActivityForResult(intent, PHOTO_GRAPH);
		} else {
			Toast.makeText(PicSelectActivity.this, "未检测到CDcard，拍照不可用!",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("..." + requestCode + ".." + resultCode + "..."
				+ data);
		if (requestCode == PHOTO_GRAPH && resultCode == RESULT_OK) {
			List<ImageBean> selecteds = new ArrayList<ImageBean>();
			selecteds.add(new ImageBean(null, 0l, null, dirPath + "/"
					+ fileName + ".jpg", false));
			Intent intent = new Intent();
			intent.putExtra(IMAGES, (Serializable) selecteds);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/**
	 */
	private String getFileName() {
		StringBuffer sb = new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		long millis = calendar.getTimeInMillis();
		String[] dictionaries = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
				"V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g",
				"h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
				"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4",
				"5", "6", "7", "8", "9" };
		sb.append("dzc");
		sb.append(millis);
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
			sb.append(dictionaries[random.nextInt(dictionaries.length - 1)]);
		}
		return sb.toString();
	};

	OnImageSelectedCountListener onImageSelectedCountListener = new OnImageSelectedCountListener() {

		@Override
		public int getImageSelectedCount() {
			return selected;
		}
	};

	OnImageSelectedListener onImageSelectedListener = new OnImageSelectedListener() {

		@Override
		public void notifyChecked() {
			selected = getSelectedCount();
			complete.setText("完成(" + selected + "/" + Config.limit + ")");
			preView.setText("预览(" + selected + "/" + Config.limit + ")");
		}
	};

	private void showPic() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				msg.what = SCAN_OK;
				msg.obj = AlbumHelper.newInstance(PicSelectActivity.this)
						.getFolders();
				msg.sendToTarget();
			}
		}).start();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (SCAN_OK == msg.what) {
				mAlbumBean = (List<AlbumBean>) msg.obj;
				if (mAlbumBean != null && mAlbumBean.size() != 0) {
					AlbumBean b = mAlbumBean.get(0);
					adapter.taggle(b);
					popWindow = showPopWindow();
				} else {
					List<ImageBean> sets = new ArrayList<ImageBean>();
					sets.add(new ImageBean());
					AlbumBean b = new AlbumBean("", 1, sets, "");
					adapter.taggle(b);
				}
			}
		};
	};

	/**
	 * ��ȡѡ�е���ֵ
	 * 
	 * @return
	 */
	private int getSelectedCount() {
		int count = 0;
		for (AlbumBean albumBean : mAlbumBean) {
			for (ImageBean b : albumBean.sets) {
				if (b.isChecked == true) {
					count++;
				}
			}
		}
		return count;
	}

	private List<ImageBean> getSelectedItem() {
		int count = 0;
		List<ImageBean> beans = new ArrayList<ImageBean>();
		OK: for (AlbumBean albumBean : mAlbumBean) {
			for (ImageBean b : albumBean.sets) {
				if (b.isChecked == true) {
					beans.add(b);
					count++;
				}
				if (count == Config.limit) {
					break OK;
				}
			}
		}
		return beans;
	}

	private PopupWindow showPopWindow() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.the_picture_selection_pop, null);
		final PopupWindow mPopupWindow = new PopupWindow(view,
				LayoutParams.MATCH_PARENT, Uitls.dip2px(PicSelectActivity.this,
						400), true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		ListView listView = (ListView) view.findViewById(R.id.list);
		AlbumAdapter albumAdapter = new AlbumAdapter(PicSelectActivity.this,
				listView);
		listView.setAdapter(albumAdapter);
		albumAdapter.setData(mAlbumBean);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams ll = getWindow().getAttributes();
				ll.alpha = 1f;
				getWindow().setAttributes(ll);
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlbumBean b = (AlbumBean) parent.getItemAtPosition(position);
				adapter.taggle(b);
				// ����ѡ�е�����
				album.setText(b.folderName);
				mPopupWindow.dismiss();
			}
		});
		return mPopupWindow;
	}

	class AlbumAdapter extends BaseAdapter {
		LayoutInflater inflater;
		List<AlbumBean> albums;
		private Point mPoint = new Point(0, 0);
		ListView mListView;

		public AlbumAdapter(Context context, ListView mListView) {
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.mListView = mListView;
		}

		public void setData(List<AlbumBean> albums) {
			this.albums = albums;
		}

		@Override
		public int getCount() {
			return albums == null || albums.size() == 0 ? 0 : albums.size();
		}

		@Override
		public Object getItem(int position) {
			return albums.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.the_picture_selection_pop_item, null);
				viewHolder.album_count = (TextView) convertView
						.findViewById(R.id.album_count);
				viewHolder.album_name = (TextView) convertView
						.findViewById(R.id.album_name);
				viewHolder.mCheckBox = (CheckBox) convertView
						.findViewById(R.id.album_ck);
				viewHolder.mImageView = (MyImageView) convertView
						.findViewById(R.id.album_image);
				viewHolder.mImageView
						.setOnMeasureListener(new OnMeasureListener() {

							@Override
							public void onMeasureSize(int width, int height) {
								mPoint.set(width, height);
							}
						});
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.mImageView
						.setImageResource(R.drawable.friends_sends_pictures_no);
			}
			final AlbumBean b = (AlbumBean) getItem(position);
			viewHolder.mImageView.setTag(b.thumbnail);

			viewHolder.album_name.setText(b.folderName);
			viewHolder.album_count.setText(b.count + "");

			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(
					b.thumbnail, mPoint, new NativeImageCallBack() {

						@Override
						public void onImageLoader(Bitmap bitmap, String path) {
							ImageView mImageView = (ImageView) mListView
									.findViewWithTag(b.thumbnail);
							if (mImageView != null && bitmap != null) {
								mImageView.setImageBitmap(bitmap);
							}
						}
					});
			if (bitmap != null) {
				viewHolder.mImageView.setImageBitmap(bitmap);
			} else {
				viewHolder.mImageView
						.setImageResource(R.drawable.friends_sends_pictures_no);
			}
			return convertView;
		}
	}

	public interface OnImageSelectedListener {
		void notifyChecked();
	}

	public interface OnImageSelectedCountListener {
		int getImageSelectedCount();
	}

	public static class ViewHolder {
		public MyImageView mImageView;
		public CheckBox mCheckBox;
		public TextView album_name;
		public TextView album_count;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			takePhoto();
		}
	}

}
