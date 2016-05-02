package com.hhu.xst.searchplayertool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 视频下载的线�?支持断点下载(原著:hellogv)
 * 
 * @author 蔡有�?E-mail: caiyoufei@looip.cn
 * @version 创建时间�?014-3-3 上午10:58:13
 * 
 */
public class DownloadThread extends Thread {
	// 视频下载地址
	private String mUrl;
	// 视频缓存路径
	private String mPath;
	// 已经缓存文件大小
	private long mDownloadSize;
	// 总缓存大�?
	private int mTargetSize;
	// 是否停止下载
	private boolean mStop;
	// 是否正在下载
	private boolean mDownloading;
	// 只能启动下载�?��
	private boolean mStarted;
	// 是否下载出错
	private boolean mError;

	/**
	 * 构�?方法
	 * 
	 * @version 更新时间�?014-3-3 上午10:09:47
	 * @param url
	 *            下载地址
	 * @param savePath
	 *            保存地址
	 * @param targetSize
	 *            下载文件目标大小
	 */
	public DownloadThread(String url, String savePath, int targetSize) {
		// 下载地址
		mUrl = url;
		// 缓存路径
		mPath = savePath;
		// 如果文件存在，则继续
		File file = new File(mPath);
		if (file.exists()) {
			mDownloadSize = file.length();
		} else {
			mDownloadSize = 0;
		}
		// 缓存大小
		mTargetSize = targetSize;
		mStop = false;
		mDownloading = false;
		mStarted = false;
		mError = false;
	}

	@Override
	public void run() {
		// 设置�?��下载为true
		mDownloading = true;
		// �?��下载
		download();
	}

	/**
	 * 
	 * 启动下载线程
	 * 
	 * @version 更新时间�?014-3-3 上午10:53:33
	 */
	public void startThread() {
		// 如果未开始下载，则开始下�?
		if (!mStarted) {
			this.start();
			// 只能启动�?��
			mStarted = true;
		}
	}

	/**
	 * 停止下载线程
	 * 
	 * @version 更新时间�?014-3-3 上午10:53:23
	 */
	public void stopThread() {
		// 设置停止标志位true
		mStop = true;
	}

	/**
	 * 是否正在下载
	 * 
	 * @version 更新时间�?014-3-3 上午10:54:19
	 * @return 是否正在下载的状�?true:正在下载；false：没有下�?
	 */
	public boolean isDownloading() {
		return mDownloading;
	}

	/**
	 * 是否下载异常
	 * 
	 * @version 更新时间�?014-3-3 上午10:55:46
	 * @return 下载是否出现异常(true:出现异常；false：没有异�?
	 */
	public boolean isError() {
		return mError;
	}

	/**
	 * 当前已经缓存的大�?
	 * 
	 * @version 更新时间�?014-3-3 上午10:56:30
	 * @return 已缓存视频的大小
	 */
	public long getDownloadedSize() {
		return mDownloadSize;
	}

	/**
	 * 是否下载成功
	 * 
	 * @version 更新时间�?014-3-3 上午10:57:03
	 * @return 是否下载完成(true：下载完成；false：未下载完成)
	 */
	public boolean isDownloadSuccessed() {
		return (mDownloadSize != 0 && mDownloadSize >= mTargetSize);
	}

	/**
	 * �?��下载视频
	 * 
	 * @version 更新时间�?014-3-3 上午10:21:31
	 */
	private void download() {
		// 下载成功则直接返�?
		if (isDownloadSuccessed()) {
			return;
		}
		// �?��输入流的基类(不能实例�?
		InputStream is = null;
		// 用于向一个文本文件写数据
		FileOutputStream os = null;
		// 如果停止下载也直接返�?
		if (mStop) {
			return;
		}
		try {
			// 将下载地�?��为URL
			URL url = new URL(mUrl);
			// 打开URL连接
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			// 设定请求的方法为"GET"(默认是GET)
			urlConnection.setRequestMethod("GET");
			// 允许重定�?
			urlConnection.setInstanceFollowRedirects(true);
			// 从连接获取输入流
			is = urlConnection.getInputStream();
			// 如果是全新的文件
			if (mDownloadSize == 0) {
				// 则新建一个输出流
				os = new FileOutputStream(mPath);
				// 如果不是全新的文�?
			} else {
				// 则新建一个追加数据输出流(第二个参�?是否追加)
				os = new FileOutputStream(mPath, true);
			}
			// 用于记录从URL连接中读取的数据长度
			int len = 0;
			// 从URL读取的数�?
			byte[] bs = new byte[1024];
			// 如果停止下载则直接返�?
			if (mStop) {
				return;
			}
			while (!mStop // 未强制停�?
					&& mDownloadSize < mTargetSize // 未下载足�?
					&& ((len = is.read(bs)) != -1)) {// 未全部读�?
				// 将读取的数据写入输出�?
				os.write(bs, 0, len);
				// 记录当前文件已下载的大小
				mDownloadSize += len;
			}
		} catch (Exception e) {
			// 如果下载出错，则设置为true
			mError = true;
		} finally {
			// 关闭输出�?
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 关闭输入�?
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 设置正在下载为false
			mDownloading = false;
			/****** 下载出错清除空文�?******/
			// 先获取下载文�?
			File nullFile = new File(mPath);
			// 判断文件是否存在，大小是否为�?
			if (nullFile.exists() && nullFile.length() == 0) {
				// 如果存在而且大小为空，则删除文件
				nullFile.delete();
			}
		}
	}
}
