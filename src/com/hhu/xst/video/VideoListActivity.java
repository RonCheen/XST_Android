package com.hhu.xst.video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hhu.xst.main.MainActivity;
import com.hhu.xst.videotool.MediaHelp;
import com.hhu.xst.videotool.VideoSuperPlayer;
import com.hhu.xst.videotool.VideoSuperPlayer.VideoPlayCallbackImpl;
import com.jereh.slidingdemo.R;
/**
 * 实现视频专题列表
 * @author lenovo
 *
 */
public class VideoListActivity extends Activity {
	String url[] = {"http://lom.zqgame.com/v1/video/LOM_Promo~2.flv",
			"http://7xpl2y.com1.z0.glb.clouddn.com/asdf.mp4",
			"http://192.168.10.122:8080/xst/assets/player/asdf.mp4",
			"http://192.168.10.122:8080/xst/assets/player/6.mp4"
			};
	private List<VideoBean> mList;
	private ListView mListView;
	private boolean isPlaying;
	private int indexPostion = -1;
	private MAdapter mAdapter;
	private Bitmap bitmap;
	 

	@Override
	protected void onDestroy() {
		MediaHelp.release();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		MediaHelp.resume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		MediaHelp.pause();
		super.onPause();
	}
/**
 * 视频播放结束后返回原状态
 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MediaHelp.getInstance().seekTo(data.getIntExtra("position",0));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.jereh.slidingdemo.R.layout.video_layout);
		mListView = (ListView) findViewById(R.id.list);
		mList = new ArrayList<VideoBean>();
		for (int i = 0; i < 10; i++) {
			mList.add(new VideoBean(url[i%4]));
		}
		mAdapter = new MAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if ((indexPostion < mListView.getFirstVisiblePosition() || indexPostion > mListView
						.getLastVisiblePosition()) && isPlaying) {
					indexPostion = -1;
					isPlaying = false;
					mAdapter.notifyDataSetChanged();
					MediaHelp.release();
				}
			}
		});
	}

	class MAdapter extends BaseAdapter {
		private Context context;
		LayoutInflater inflater;

		public MAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public VideoBean getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			GameVideoViewHolder holder = null;
			if (v == null) {
				holder = new GameVideoViewHolder();
				v = inflater.inflate(R.layout.list_video_item, parent, false);
				holder.mVideoViewLayout = (VideoSuperPlayer) v
						.findViewById(R.id.video);
				holder.mPlayBtnView = (ImageView) v.findViewById(R.id.play_btn);
				v.setTag(holder);
			} else {
				holder = (GameVideoViewHolder) v.getTag();
			}
			holder.mPlayBtnView.setOnClickListener(new MyOnclick(
					holder.mPlayBtnView, holder.mVideoViewLayout, position));
			if (indexPostion == position) {
				holder.mVideoViewLayout.setVisibility(View.VISIBLE);
			} else {
				holder.mVideoViewLayout.setVisibility(View.GONE);
				holder.mVideoViewLayout.close();
			}
			return v;
		}

		class MyOnclick implements OnClickListener {
			VideoSuperPlayer mSuperVideoPlayer;
			ImageView mPlayBtnView;
			int position;

			public MyOnclick(ImageView mPlayBtnView,
					VideoSuperPlayer mSuperVideoPlayer, int position) {
				this.position = position;
				this.mSuperVideoPlayer = mSuperVideoPlayer;
				this.mPlayBtnView = mPlayBtnView;
			}

			@Override
			public void onClick(View v) {
				MediaHelp.release();
				indexPostion = position;
				isPlaying = true;
				mSuperVideoPlayer.setVisibility(View.VISIBLE);
				mSuperVideoPlayer.loadAndPlay(MediaHelp.getInstance(), mList
						.get(position).getUrl(), 0, false);
				mSuperVideoPlayer.setVideoPlayCallback(new MyVideoPlayCallback(
						mPlayBtnView, mSuperVideoPlayer, mList.get(position)));
				notifyDataSetChanged();
			}
		}

		class MyVideoPlayCallback implements VideoPlayCallbackImpl {
			ImageView mPlayBtnView;
			VideoSuperPlayer mSuperVideoPlayer;
			VideoBean info;

			public MyVideoPlayCallback(ImageView mPlayBtnView,
					VideoSuperPlayer mSuperVideoPlayer, VideoBean info) {
				this.mPlayBtnView = mPlayBtnView;
				this.info = info;
				this.mSuperVideoPlayer = mSuperVideoPlayer;
			}

			@Override
			public void onCloseVideo() {
				closeVideo();
			}

			@Override
			public void onSwitchPageType() {
				if (((Activity) context).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					Intent intent = new Intent(new Intent(context,
							FullVideoActivity.class));
					intent.putExtra("video", info);
					intent.putExtra("position",
							mSuperVideoPlayer.getCurrentPosition());
					((Activity) context).startActivityForResult(intent, 1);
				}
			}

			@Override
			public void onPlayFinish() {
				closeVideo();
			}

			private void closeVideo() {
				isPlaying = false;
				indexPostion = -1;
				mSuperVideoPlayer.close();
				MediaHelp.release();
				mPlayBtnView.setVisibility(View.VISIBLE);
				mSuperVideoPlayer.setVisibility(View.GONE);
			}

		}

		class GameVideoViewHolder {

			private VideoSuperPlayer mVideoViewLayout;
			private ImageView mPlayBtnView;

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent myIntent = new Intent();
			myIntent = new Intent(VideoListActivity.this, MainActivity.class);
			startActivity(myIntent);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 设置缩略图
	 * 
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Bitmap createVideoThumbnail(String url, int width, int height) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		int kind = MediaStore.Video.Thumbnails.MINI_KIND;
		try {
			if (Build.VERSION.SDK_INT >= 14) {
				retriever.setDataSource(url, new HashMap<String, String>());
			} 
			else {
				retriever.setDataSource(url);
			}
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}
		if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

}
