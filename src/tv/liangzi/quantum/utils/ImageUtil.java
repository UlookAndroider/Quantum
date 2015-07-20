package tv.liangzi.quantum.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;




public class ImageUtil {

	/**
	 * 旋转图片，使图片保持正确的方向。
	 * @param bitmap 原始图片
	 * @param degrees 原始图片的角度
	 * @return Bitmap 旋转后的图片
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
		if (degrees == 0 || null == bitmap) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (null != bitmap) {
			bitmap.recycle();
		}
		return bmp;
	}
	/*
	 * 如果图片是png的格式需要转化为jpg再去合成视频，第一步是png转为bitmap
	 */

		   public static Bitmap convertToBitmap(String path) { 
				
				BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inJustDecodeBounds = true;
		        // 获取这个图片的宽和高
		        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回bm为空
		        options.inJustDecodeBounds = false;
		        // 计算缩放比
		        int be = (int) (options.outHeight / (float) 200);
		        if (be <= 0)
		            be = 1;
		        options.inSampleSize = 4; // 图片长宽各缩小1
		        // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
		        bitmap = BitmapFactory.decodeFile(path, options);
		        int w = bitmap.getWidth();
		        int h = bitmap.getHeight();
		        System.out.println(w + " " + h);
		        // savePNG_After(bitmap,path);
		        return bitmap;
			}
		    /**
		     * 保存图片为JPEG
		     * 
		     * @param bitmap
		     * @param path
		     */
		    public static void saveJPGE_After(Bitmap bitmap, String path) {
		    	String ExtensionsJPG=path.replace("png", "jpg");
		        File file = new File(ExtensionsJPG);
		        try {
		            FileOutputStream out = new FileOutputStream(file);
		            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
		                out.flush();
		                out.close();
		            }
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    /**
			* 将彩色图转换为黑白图
			* 
			* @param
			* @return 返回转换好的位图
			*/
			public static Bitmap convertToBlackWhite(Bitmap bmp) {
				int width = bmp.getWidth(); // 获取位图的宽
				int height = bmp.getHeight(); // 获取位图的高
				int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

				bmp.getPixels(pixels, 0, width, 0, 0, width, height);
				int alpha = 0xFF << 24;
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						int grey = pixels[width * i + j];

						int red = ((grey & 0x00FF0000) >> 16);
						int green = ((grey & 0x0000FF00) >> 8);
						int blue = (grey & 0x000000FF);

						grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
						grey = alpha | (grey << 16) | (grey << 8) | grey;
						pixels[width * i + j] = grey;
					}
				}
				Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);

				newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

				Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 380, 460);
				return resizeBmp;
			}

			/**
			 * 将Bitmap存为 .bmp格式图片
			 * @param bitmap
			 */
			public static void saveBmp(Bitmap bitmap, String path) {
				String ExtensionsJPG="";
				if (path.endsWith("jpg")||path.endsWith("JPEG")) {
					ExtensionsJPG=path.replace("jpg", "bmp");
				}else if (path.endsWith("png")) {
					ExtensionsJPG=path.replace("png", "bmp");
				}else {
					ExtensionsJPG=path.replace("JPEG", "bmp");
				}
				
				if (bitmap == null)
					return;
				// 位图大小
				int nBmpWidth = bitmap.getWidth();
				int nBmpHeight = bitmap.getHeight();
				// 图像数据大小
				int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
				try {
					// 存储文件名
					String filename =ExtensionsJPG;
					File file = new File(filename);
					if (!file.exists()) {
						file.createNewFile();
					}
					FileOutputStream fileos = new FileOutputStream(filename);
					// bmp文件头
					int bfType = 0x4d42;
					long bfSize = 14 + 40 + bufferSize;
					int bfReserved1 = 0;
					int bfReserved2 = 0;
					long bfOffBits = 14 + 40;
					// 保存bmp文件头
					writeWord(fileos, bfType);
					writeDword(fileos, bfSize);
					writeWord(fileos, bfReserved1);
					writeWord(fileos, bfReserved2);
					writeDword(fileos, bfOffBits);
					// bmp信息头
					long biSize = 40L;
					long biWidth = nBmpWidth;
					long biHeight = nBmpHeight;
					int biPlanes = 1;
					int biBitCount = 24;
					long biCompression = 0L;
					long biSizeImage = 0L;
					long biXpelsPerMeter = 0L;
					long biYPelsPerMeter = 0L;
					long biClrUsed = 0L;
					long biClrImportant = 0L;
					// 保存bmp信息头
					writeDword(fileos, biSize);
					writeLong(fileos, biWidth);
					writeLong(fileos, biHeight);
					writeWord(fileos, biPlanes);
					writeWord(fileos, biBitCount);
					writeDword(fileos, biCompression);
					writeDword(fileos, biSizeImage);
					writeLong(fileos, biXpelsPerMeter);
					writeLong(fileos, biYPelsPerMeter);
					writeDword(fileos, biClrUsed);
					writeDword(fileos, biClrImportant);
					// 像素扫描
					byte bmpData[] = new byte[bufferSize];
					int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
					for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)
						for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
							int clr = bitmap.getPixel(wRow, nCol);
							bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
							bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
							bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
						}

					fileos.write(bmpData);
					fileos.flush();
					fileos.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			protected static void writeWord(FileOutputStream stream, int value) throws IOException {
				byte[] b = new byte[2];
				b[0] = (byte) (value & 0xff);
				b[1] = (byte) (value >> 8 & 0xff);
				stream.write(b);
			}

			protected static void writeDword(FileOutputStream stream, long value) throws IOException {
				byte[] b = new byte[4];
				b[0] = (byte) (value & 0xff);
				b[1] = (byte) (value >> 8 & 0xff);
				b[2] = (byte) (value >> 16 & 0xff);
				b[3] = (byte) (value >> 24 & 0xff);
				stream.write(b);
			}

			protected static void writeLong(FileOutputStream stream, long value) throws IOException {
				byte[] b = new byte[4];
				b[0] = (byte) (value & 0xff);
				b[1] = (byte) (value >> 8 & 0xff);
				b[2] = (byte) (value >> 16 & 0xff);
				b[3] = (byte) (value >> 24 & 0xff);
				stream.write(b);
			}
	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree  = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	/*
        * 旋转图片
        * @param angle
        * @param bitmap
        * @return Bitmap
        */
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
		//旋转图片 动作
		Matrix matrix = new Matrix();;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	/**
			* 读取图片属性：旋转的角度
	* @param path 图片绝对路径
	* @return degree旋转的角度
	*/

	/**
	 * 得到 图片旋转 的角度
	 * @param filepath
	 * @return
	 */
	public static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						degree = 90;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						degree = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						degree = 270;
						break;
				}
			}
		}
		return degree;
	}

	/**
	 * 将图片按照某个角度进行旋转
	 *
	 * @param bm
	 *            需要旋转的图片
	 * @param degree
	 *            旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}
	public Bitmap getBitmapFromFile(File dst, int width, int height) {
		if (null != dst && dst.exists()) {
			BitmapFactory.Options opts = null;
			if (width > 0 && height > 0) {
				opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(dst.getPath(), opts);
				// 计算图片缩放比例
				final int minSideLength = Math.min(width, height);
				opts.inSampleSize = computeSampleSize(opts, minSideLength,
						width * height);
				opts.inJustDecodeBounds = false;
				opts.inInputShareable = true;
				opts.inPurgeable = true;
			}
			try {
				return BitmapFactory.decodeFile(dst.getPath(), opts);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static int computeSampleSize(BitmapFactory.Options options,
										int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
												int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
				.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}
