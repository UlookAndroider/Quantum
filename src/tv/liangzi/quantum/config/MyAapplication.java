package tv.liangzi.quantum.config;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.easemob.chat.EMChat;
import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

import tv.liangzi.quantum.utils.LogUtils;

public class MyAapplication extends Application {
    public  String EMuserId;
    public  String clientId;//个推clientid
    public static final String IP="http://192.168.1.109:8080/LiangZiServer/";
    public static final String HEAD_PATH= Environment.getExternalStorageDirectory()+ File.separator+"Ulooktemp";
    @Override
public void onCreate() {
        /**
         * 个推初始化
         */
        PushManager.getInstance().initialize(this.getApplicationContext());
        //环信初始化配置
        EMChat.getInstance().init(getApplicationContext());
        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         * @param debugMode
         * 在做代码混淆的时候需要设置成false
         */
        EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
//        EMChat.getInstance().setAppInited();
        initImageLoader(getApplicationContext());//初始化图片缓存
        LogUtils.i("APPLICATION", "--------------------------");
    super.onCreate();


}
    public String getEMuserId() {
        return EMuserId;
    }

    public  void setEMuserId(String EMuserId) {
        this.EMuserId = EMuserId;
    }
/**
 * 配置imageLoadger
 *  Configuration of ImageLoader:
 *  This configuration tuning is custom.
 *  You can tune every option, you may tune some of them,
 *  or you can create default configuration by
 *  ImageLoaderConfiguration.createDefault(this) method.
 *  
 *  Note:
 *  1 enableLogging() // Not necessary in common
 */
public static void initImageLoader(Context context) {
	ImageLoaderConfiguration config = new ImageLoaderConfiguration
                  .Builder(context)
                  .threadPriority(Thread.NORM_PRIORITY - 2)
                  .threadPoolSize(3)
                  .denyCacheImageMultipleSizesInMemory()//当同�?个Uri获取不同大小的图片，缓存到内存时，只缓存�?个�?�默认会缓存多个不同的大小的相同图片
                  .memoryCacheExtraOptions(480, 800) //如果图片尺寸大于了这个参数，那么就会这按照这个参数对图片大小进行限制并缓�?
                  .discCacheFileNameGenerator(new Md5FileNameGenerator())
                  .tasksProcessingOrder(QueueProcessingType.LIFO)
                  .writeDebugLogs()
                  .build();
	
	ImageLoader.getInstance().init(config);
}
}
