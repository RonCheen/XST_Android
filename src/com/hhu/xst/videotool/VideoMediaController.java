package com.hhu.xst.videotool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jereh.slidingdemo.R;

public class VideoMediaController extends FrameLayout implements
		SeekBar.OnSeekBarChangeListener, View.OnClickListener {
	private TextView mTimeTxt;
	private ImageView mPlayImg;// 鎾斁鎸夐挳
	private SeekBar mProgressSeekBar;// 鎾斁杩涘害鏉�	private TextView mTimeTxt;// 鎾斁鏃堕棿
	private ImageView mExpandImg;// 鏈�ぇ鍖栨挱�?��寜閽�?private ImageView mShrinkImg;// 缂╂斁鎾斁鎸夐�?
	private MediaControlImpl mMediaControl;
	private ImageView mShrinkImg;

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean isFromUser) {
		if (isFromUser)
			mMediaControl.onProgressTurn(ProgressState.DOING, progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mMediaControl.onProgressTurn(ProgressState.START, 0);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mMediaControl.onProgressTurn(ProgressState.STOP, 0);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.pause) {
			mMediaControl.onPlayTurn();
		} else if (view.getId() == R.id.expand) {
			mMediaControl.onPageTurn();
		} else if (view.getId() == R.id.shrink) {
			mMediaControl.onPageTurn();
		}
	}

	public void setProgressBar(int progress) {
		if (progress < 0)
			progress = 0;
		if (progress > 100)
			progress = 100;
		mProgressSeekBar.setProgress(progress);
	}

	public void setProgressBar(int progress, int secondProgress) {
		if (progress < 0)
			progress = 0;
		if (progress > 100)
			progress = 100;
		if (secondProgress < 0)
			secondProgress = 0;
		if (secondProgress > 100)
			secondProgress = 100;
		mProgressSeekBar.setProgress(progress);
		mProgressSeekBar.setSecondaryProgress(secondProgress);
	}

	public void setPlayState(PlayState playState) {
		mPlayImg.setImageResource(playState.equals(PlayState.PLAY) ? R.drawable.biz_video_pause
				: R.drawable.biz_video_play);
	}

	public void setPageType(PageType pageType) {
		mExpandImg.setVisibility(pageType.equals(PageType.EXPAND) ? GONE
				: VISIBLE);
		mShrinkImg.setVisibility(pageType.equals(PageType.SHRINK) ? GONE
				: VISIBLE);
	}

	public void setPlayProgressTxt(int nowSecond, int allSecond) {
		mTimeTxt.setText(getPlayTime(nowSecond, allSecond));
	}

	public void playFinish(int allTime) {
		mProgressSeekBar.setProgress(0);
		setPlayProgressTxt(0, allTime);
		setPlayState(PlayState.PAUSE);
	}

	public void setMediaControl(MediaControlImpl mediaControl) {
		mMediaControl = mediaControl;
	}

	public VideoMediaController(Context context) {
		super(context);
		initView(context);
	}

	public VideoMediaController(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public VideoMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initView(Context context) {
		View.inflate(context, R.layout.super_video_media_controller, this);
		mPlayImg = (ImageView) findViewById(R.id.pause);
		mProgressSeekBar = (SeekBar) findViewById(R.id.media_controller_progress);
		mTimeTxt = (TextView) findViewById(R.id.time);
		mExpandImg = (ImageView) findViewById(R.id.expand);
		mShrinkImg = (ImageView) findViewById(R.id.shrink);
		initData();
	}

	private void initData() {
		mProgressSeekBar.setOnSeekBarChangeListener(this);
		mPlayImg.setOnClickListener(this);
		mShrinkImg.setOnClickListener(this);
		mExpandImg.setOnClickListener(this);
		setPageType(PageType.SHRINK);
		setPlayState(PlayState.PAUSE);
	}

	@SuppressLint("SimpleDateFormat")
	private String formatPlayTime(long time) {
		DateFormat formatter = new SimpleDateFormat("mm:ss");
		return formatter.format(new Date(time));
	}

	private String getPlayTime(int playSecond, int allSecond) {
		String playSecondStr = "00:00";
		String allSecondStr = "00:00";
		if (playSecond > 0) {
			playSecondStr = formatPlayTime(playSecond);
		}
		if (allSecond > 0) {
			allSecondStr = formatPlayTime(allSecond);
		}
		return playSecondStr + "/" + allSecondStr;
	}

	/**
	 * 鎾斁鏍峰紡 灞曞紑銆佺缉�?��	 */
	public enum PageType {
		EXPAND, SHRINK
	}

	/**
	 * 鎾斁鐘舵� 鎾�?鏆傚�?	 */
	public enum PlayState {
		PLAY, PAUSE
	}

	public enum ProgressState {
		START, DOING, STOP
	}

	public interface MediaControlImpl {
		void onPlayTurn();

		void onPageTurn();

		void onProgressTurn(ProgressState state, int progress);

		void alwaysShowController();
	}

}
