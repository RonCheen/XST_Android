package com.hhu.xst.video;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.hhu.xst.videotool.MediaHelp;
import com.hhu.xst.videotool.VideoMediaController;
import com.hhu.xst.videotool.VideoSuperPlayer;
import com.hhu.xst.videotool.VideoSuperPlayer.VideoPlayCallbackImpl;
/**
 * 实现视频全屏
 * @author lenovo
 *
 */
public class FullVideoActivity extends Activity {
	public VideoSuperPlayer mVideo;
	public static VideoBean info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 妯睆
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(com.jereh.slidingdemo.R.layout.activity_full);
		mVideo = (VideoSuperPlayer) findViewById(com.jereh.slidingdemo.R.id.video);
		info = (VideoBean) getIntent().getExtras().getSerializable("video");
		mVideo.loadAndPlay(MediaHelp.getInstance(), info.getUrl(), getIntent()
				.getExtras().getInt("position"), true);
		mVideo.setPageType(VideoMediaController.PageType.EXPAND);
		mVideo.setVideoPlayCallback(new VideoPlayCallbackImpl() {

			@Override
			public void onSwitchPageType() {
				if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					finish();
				}
			}

			@Override
			public void onPlayFinish() {
				finish();
			}

			@Override
			public void onCloseVideo() {
				finish();
			}
		});
	}

	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putExtra("position", mVideo.getCurrentPosition());
		setResult(RESULT_OK, intent);
		super.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MediaHelp.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MediaHelp.resume();
	}
}
