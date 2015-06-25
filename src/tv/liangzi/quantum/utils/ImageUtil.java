package tv.liangzi.quantum.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
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


}
