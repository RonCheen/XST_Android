package com.hhu.xst.function;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.hhu.xst.main.MainActivity;
import com.hhu.xst.searchplayertool.HttpGetProxy;
import com.jereh.slidingdemo.R;

/**
 * 播放搜索到的 
 * 
 * @author 蔡有�?E-mail: caiyoufei@looip.cn
 * @version 创建时间�?014-3-3 下午6:24:52
 * 
 */
public class SearchPlayerActivity extends Activity {
	// 视频缓存大小
	static private final int PREBUFFER_SIZE = 40 * 1024 * 1024;
	// 视频显示窗口
	private VideoView mVideoView;
	// 视频控制�?
	private MediaController mediaController;
	// 数据处理�?
	private HttpGetProxy proxy;
	// 视频地址
	public static String videoUrl ;
	// private String videoUrl =
	// "http://60.174.241.43/cdn.baidupcs.com/file/95fdd1a5bea09b29e25a060e1ec91a2f?xcode=0395f2f099449e96caa5607d989ddebe686451c3ac86358e&fid=1497041311-250528-624788909&time=1393912671&sign=FDTAXER-DCb740ccc5511e5e8fedcff06b081203-M%2Fg9qV2PtNpP8%2F%2FXpR81SIv8OMk%3D&to=cb&fm=Q,B,T,t&expires=1393915081&rt=sh&r=692428458&logid=4075567800&sh=1&vuk=2133216951&fn=%E9%9A%8B%E5%94%90%E8%8B%B1%E9%9B%843%E7%AC%AC1%E9%9B%86%20%5B320%5D.mp4&wshc_tag=0&wsiphost=ipdbm";
	// 视频ID
	private String id = null;
	// 等待缓冲时间
	private long waittime = 1000;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_search_player);
		setTitle("在线视频播放Demo");
		// 创建预加载视频文件存放文件夹
		new File(getBufferDir()).mkdirs();
		// 初始化VideoView
		mediaController = new MediaController(this);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVideoView.setMediaController(mediaController);
		mVideoView.setOnPreparedListener(mOnPreparedListener);

		// 初始化代理服务器
		proxy = new HttpGetProxy(getBufferDir(),// 预加载视频文件存放路�?
				PREBUFFER_SIZE,// 预加载体�?
				10);// 预加载文件上�?
		// 设置视频ID
		id = System.currentTimeMillis() + "";
		try {
			// �?��缓存视频
			proxy.startDownload(id, videoUrl, true);
			// 转换网络URL为本地URL
			proxy.changeNetURL();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 在缓存等待时间后�?��播放视频
		delayToStartPlay.sendEmptyMessageDelayed(0, waittime);
		// �?��显示MediaController
		showController.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	public void onStop() {
		super.onStop();
		finish();
	//	System.exit(0);  会导致直接退出
	}

	// 视频准备的监�?
	private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			mVideoView.start();
		}
	};
	// 播放视频的消息处�?
	private Handler delayToStartPlay = new Handler() {
		public void handleMessage(Message msg) {
			// 获取本地缓存数据URL
			String proxyUrl = proxy.getLocalURL(id);
			// 设置视频播放�?
			mVideoView.setVideoPath(proxyUrl);
		}
	};
	// 视频控制条的显示
	private Handler showController = new Handler() {
		public void handleMessage(Message msg) {
			mediaController.show(0);
		}
	};

	/**
	 * 创建预加载视频文件存放文件夹
	 * 
	 * @version 更新时间�?014-3-3 下午6:32:18
	 * @return
	 */
	static public String getBufferDir() {
		String bufferDir = "";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			bufferDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/ProxyBuffer/files";
		}
		return bufferDir;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "关于").setIcon(

		android.R.drawable.ic_menu_info_details);

		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "帮助").setIcon(

		android.R.drawable.ic_menu_help);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			new AlertDialog.Builder(this)
					.setMessage(
							"作�?:蔡有飞\n\n版权归上海持创信息技术有限公司所有\n\n任何人不得修改本程序后宣传本作品 ")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									// 按钮事件
								}
							}).setIcon(android.R.drawable.ic_menu_info_details)
					.setTitle("作�?").show();
			break;

		case Menu.FIRST + 2:

			new AlertDialog.Builder(this)
					.setMessage("使用过程中如有问题或建议\n请发邮件至caiyoufei@looip.cn")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									// 按钮事件
								}
							}).setTitle("帮助")
					.setIcon(android.R.drawable.ic_menu_help).show();
			break;
		}
		return false;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(SearchPlayerActivity.this, MainActivity.class);
            startActivity(myIntent);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
