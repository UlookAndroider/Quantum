package org.fireking.app.imagelib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.fireking.app.imagelib.entity.ImageBean;
import org.fireking.app.imagelib.widget.ImageBrowserActivity;
import org.fireking.app.imagelib.widget.PicSelectActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class HostActivity extends FragmentActivity {
	GridView image_selector;
	MyAdapter adapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.publish);
		image_selector = (GridView) this.findViewById(R.id.image_selector);
		adapter = new MyAdapter(this);
		image_selector.setAdapter(adapter);
	}

	public void test_grally(View view) {
		Intent intent = new Intent(this, ImageBrowserActivity.class);
		List<ImageBean> imagesList = new ArrayList<ImageBean>();
		imagesList
				.add(new ImageBean(
						"http://a.hiphotos.baidu.com/image/pic/item/18d8bc3eb13533fae206ea2cabd3fd1f41345b7b.jpg"));
		imagesList
				.add(new ImageBean(
						"http://g.hiphotos.baidu.com/image/pic/item/1ad5ad6eddc451da9f2e8e8cb5fd5266d11632f8.jpg"));
		imagesList
				.add(new ImageBean(
						"http://c.hiphotos.baidu.com/image/pic/item/b3b7d0a20cf431ad99736b5d4836acaf2edd9834.jpg"));
		imagesList
				.add(new ImageBean(
						"http://d.hiphotos.baidu.com/image/pic/item/6a63f6246b600c33c435e8b7194c510fd9f9a104.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383265_8550.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383243_5120.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383242_3127.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383242_9576.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383242_1721.jpg"));
		imagesList
				.add(new ImageBean(
						"http://img.my.csdn.net/uploads/201407/26/1406383219_5806.jpg"));
		intent.putExtra("images", (Serializable) imagesList);
		intent.putExtra("position", 2);
		intent.putExtra("isdel", true);
		startActivityForResult(intent, 0x456);
	}

	class MyAdapter extends BaseAdapter {
		Context context;
		List<ImageBean> beans;

		public MyAdapter(Context context) {
			this.context = context;
		}

		public void setData(List<ImageBean> beans) {
			this.beans = beans;
		}

		@Override
		public int getCount() {
			return beans == null || beans.size() == 0 ? 0 : beans.size();
		}

		@Override
		public Object getItem(int position) {
			return beans == null || beans.size() == 0 ? null : beans
					.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View
						.inflate(context, R.layout.item_publish, null);
			}
			return convertView;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x123 && resultCode == RESULT_OK) {
			Intent intent = data;
			List<ImageBean> images = (List<ImageBean>) intent
					.getSerializableExtra("images");
			for (ImageBean b : images) {
				System.out.println("<><<><> ???" + b.toString());
			}
		} else if (requestCode == 0x456 && resultCode == RESULT_OK) {
			Intent intent = data;
			List<ImageBean> images = (List<ImageBean>) intent
					.getSerializableExtra("M_LIST");
			System.out.println("返回的数据量:" + images.size());
			for (ImageBean m : images) {
				System.out.println(m.path);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
