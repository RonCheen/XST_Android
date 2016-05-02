package com.hhu.xst.searchplayertool;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;

import android.text.TextUtils;
import android.util.Log;

import com.hhu.xst.searchplayertool.Config.ProxyRequest;
import com.hhu.xst.searchplayertool.Config.ProxyResponse;

/**
 * 代理服务器类(原著:hellogv)
 * 
 * @author 蔡有�?E-mail: caiyoufei@looip.cn
 * @version 创建时间�?014-3-3 上午11:14:21
 * 
 */
public class HttpGetProxy {
	final static public String TAG = "HttpGetProxy";
	// 避免某些Mediaplayer不播放尾部就结束
	private static final int END_SIZE = 1024 * 1024;
	// 预加载所�?��大小
	private int mBufferSize;
	// 预加载缓存文件的�?��数量
	private int mBufferFileMaximum;
	// URL链接带的端口
	private int remotePort = -1;
	// 远程服务器地�?
	private String remoteHost;
	// 代理服务器使用的端口
	private int localPort;
	// 本地服务器IP地址
	private String localHost;
	// TCP Server,接收Media Player连接
	private ServerSocket localServer = null;
	// 服务器的Address
	private SocketAddress serverAddress;
	// 下载线程
	private DownloadThread downloadThread = null;
	// Response对象
	private ProxyResponse proxyResponse = null;
	// 缓存文件�?
	private String mBufferDirPath = null;
	// 视频缓存ID(由下载时的时间产�?，预加载文件以ID命名
	private String mId;
	// 视频网络URL地址
	private String mUrl;
	// 有效的媒体文件链�?重定向之�?
	private String mMediaUrl;
	// 预加载文件路�?
	private String mMediaFilePath;
	// 预加载是否可�?
	private boolean mEnable = false;
	// 代理�?
	private Proxy proxy = null;
	// 视频转发后的本地URL
	private String localURL = "";

	/**
	 * (构�?方法)初始化代理服务器，并启动代理服务�?
	 * 
	 * @version 更新时间�?014-3-3 上午11:30:06
	 * @param dirPath
	 *            缓存文件夹的路径
	 * @param size
	 *            �?��预加载的大小
	 * @param maximum
	 *            预加载文件最大数
	 */
	public HttpGetProxy(String dirPath, int size, int maximum) {
		try {
			// 设置缓存文件�?
			mBufferDirPath = dirPath;
			// 缓存文件大小
			mBufferSize = size;
			// �?��缓存文件数量
			mBufferFileMaximum = maximum;
			// 本地服务器IP地址
			localHost = Config.LOCAL_IP_ADDRESS;
			// 实例化本地服务器
			localServer = new ServerSocket(0,// 指定监听的特定端�?
					1,// 指定ServerSocket的最大连接数
					InetAddress.getByName(localHost// 指定当前的ServerSocket绑定到哪个IP�?
							));
			// 有ServerSocket自动分配端口
			localPort = localServer.getLocalPort();
			// 启动代理服务�?
			new Thread() {
				public void run() {
					startProxy();
				}
			}.start();
			// 预加载可�?
			mEnable = true;
		} catch (Exception e) {
			// 预加载不可用
			mEnable = false;
		}
	}

	/**
	 * 代理服务器是否可�?
	 * 
	 * @version 更新时间�?014-3-3 上午11:44:33
	 * @return 代理服务器是否可�?true:可用;false:不可�?
	 */
	public boolean getEnable() {
		/**** 判断外部存储器是否可�?****/
		// 得到缓存文件�?
		File dir = new File(mBufferDirPath);
		// 缓存文件夹是否存�?
		mEnable = dir.exists();
		// 如果缓存文件夹不存在，则直接返回false
		if (!mEnable) {
			return mEnable;
		}
		// 如果缓存文件夹存�?则获取可用空间大�?
		long freeSize = Utils.getAvailaleSize(mBufferDirPath);
		// 如果可用空间大于缓存容量，则返回true，否则返回false
		mEnable = (freeSize > mBufferSize);
		return mEnable;
	}

	/**
	 * 停止下载
	 * 
	 * @version 更新时间�?014-3-3 下午1:11:01
	 */
	public void stopDownload() {
		// 如果线程不为空�?且正在下载中
		if (downloadThread != null && downloadThread.isDownloading()) {
			// 执行停止下载
			downloadThread.stopThread();
		}
	}

	/**
	 * �?��预加�?�?��时间只能预加载一个视�?
	 * 
	 * @version 更新时间�?014-3-3 下午1:13:28
	 * @param id
	 *            视频唯一ID，长时间有效
	 * @param url
	 *            视频链接
	 * @param isDownload
	 *            是否重新下载
	 * @throws Exception
	 *             抛出的异�?
	 */
	public void startDownload(String id, String url, boolean isDownload)
			throws Exception {
		// 代理服务器不可用
		if (!getEnable()) {
			return;
		}
		// 清除过去的缓存文�?
		Utils.asynRemoveBufferFile(mBufferDirPath, // 缓存文件路径
				mBufferFileMaximum);// 缓存文件数量
		// 视频唯一ID
		mId = id;
		// 视频链接
		mUrl = url;
		// 获取处理后的缓存文件�?
		String fileName = Utils.getValidFileName(mId);
		// 缓存文件的路�?
		mMediaFilePath = mBufferDirPath + "/" + fileName;
		// 判断缓存文件是否存在，忽略已经缓存过的文�?
		File tmpFile = new File(mMediaFilePath);
		// 如果缓存文件存在且已经达到缓存最大数�?则直接返�?
		if (tmpFile.exists() && tmpFile.length() >= mBufferSize) {
			return;
		}
		// 否则，停止下�?
		stopDownload();
		// 如果�?��重新下载
		if (isDownload) {
			// 新建下载线程
			downloadThread = new DownloadThread(mUrl,// 视频链接
					mMediaFilePath,// 缓存文件路径
					mBufferSize);// 缓存大小
			// �?��下载线程
			downloadThread.startThread();
		}
	}

	/**
	 * 获取播放链接
	 * 
	 * @version 更新时间�?014-3-3 下午1:47:05
	 * @param id
	 *            缓存ID
	 * @return 转发后的本地视频URL地址
	 */
	public String getLocalURL(String id) {
		if (TextUtils.isEmpty(mId)// 如果没预加载�?
				|| mId.equals(id) == false)// 与预加载的Id不符�?
		{
			// 直接返回空地�?
			return "";
		}
		// 代理服务器不可用，则直接返回网络播放地址
		if (!getEnable()) {
			return mUrl;
		}

		return localURL;
	}

	/**
	 * 将网络的URL转换为本地的URL
	 * 
	 * @version 更新时间�?014-3-4 上午11:03:05
	 */
	public void changeNetURL() {
		new Thread() {
			public void run() {
				// 排除HTTP特殊,如重定向
				mMediaUrl = Utils.getRedirectUrl(mUrl);
				// ----获取对应本地代理服务器的链接----//
				// 将重定向后的网络视频URL转换为URI类型
				URI originalURI = URI.create(mMediaUrl);
				// 获取远程主机地址
				remoteHost = originalURI.getHost();
				if (originalURI.getPort() != -1) {// URL带Port
					// 新建远程Socket连接地址
					serverAddress = new InetSocketAddress(remoteHost,
							originalURI.getPort());// 使用默认端口
					// 保存端口，中转时替换
					remotePort = originalURI.getPort();
					// 网络地址替换成本地地�?
					localURL = mMediaUrl.replace(
							remoteHost + ":" + originalURI.getPort(), localHost
									+ ":" + localPort);
				} else {// URL不带Port
					// 新建远程Socket连接地址
					serverAddress = new InetSocketAddress(remoteHost,
							Config.HTTP_PORT);// 使用80端口
					remotePort = -1;
					// 网络地址替换成本地地�?将重定向后的视频播放地址中的-1替换�?
					localURL = mMediaUrl.replace(remoteHost, localHost + ":"
							+ localPort);
				}
			};
		}.start();
	}

	/**
	 * 启动代理服务�?
	 * 
	 * @version 更新时间�?014-3-3 下午2:10:33
	 */
	private void startProxy() {
		while (true) {
			// --------------------------------------
			// 监听MediaPlayer的请求，MediaPlayer->代理服务�?
			// --------------------------------------
			try {
				// 本地连接到代理服务器
				Socket s = localServer.accept();
				// 如果代理不为空，先关闭可能存在的连接
				if (proxy != null) {
					proxy.closeSockets();
				}
				// 实例化代�?
				proxy = new Proxy(s);
				// �?���?��线程启动下载
				new Thread() {
					public void run() {
						try {
							// 本地连接到代理服务器
							Socket s = localServer.accept();
							// 如果代理不为空，先关闭可能存在的连接
							proxy.closeSockets();
							// 实例化代�?
							proxy = new Proxy(s);
							// 代理启动转发
							proxy.run();
						} catch (IOException e) {
							Log.e(TAG, e.toString());
							Log.e(TAG, Utils.getExceptionMessage(e));
						}

					}
				}.start();
				// 代理启动转发
				proxy.run();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				Log.e(TAG, Utils.getExceptionMessage(e));
			}
		}
	}

	/**
	 * 代理转发�?
	 * 
	 * @author 蔡有�?E-mail: caiyoufei@looip.cn
	 * @version 创建时间�?014-3-3 下午2:23:00
	 * 
	 */
	private class Proxy {
		// 收发Media Player请求的Socket
		private Socket sckPlayer = null;
		// 收发Media Server请求的Socket
		private Socket sckServer = null;

		/**
		 * 构�?方法
		 * 
		 * @version 更新时间�?014-3-3 下午2:15:17
		 * @param sckPlayer
		 *            连接
		 */
		public Proxy(Socket sckPlayer) {
			this.sckPlayer = sckPlayer;
		}

		/**
		 * 关闭现有的链�?
		 * 
		 * @version 更新时间�?014-3-3 下午2:14:04
		 */
		public void closeSockets() {
			// �?��新的request之前关闭过去的Socket
			try {
				if (sckPlayer != null) {
					sckPlayer.close();
					sckPlayer = null;
				}

				if (sckServer != null) {
					sckServer.close();
					sckServer = null;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		/**
		 * �?��执行转发
		 * 
		 * @version 更新时间�?014-3-3 下午2:26:34
		 */
		public void run() {
			// Http报文处理�?
			HttpParser httpParser = null;
			// 代理服务器工具类
			HttpGetProxyUtils utils = null;
			// 读取数据的长�?
			int bytes_read;
			// 本地请求数据
			byte[] local_request = new byte[1024];
			// 服务器返回数�?
			byte[] remote_reply = new byte[1024 * 50];
			// 是否发�?了请求头文件
			boolean sentResponseHeader = false;
			try {
				// 先停止下�?
				stopDownload();
				// 实例化Http报文处理�?
				httpParser = new HttpParser(remoteHost,// 远程主机地址
						remotePort,// URL链接带的端口(如果没有则默�?1)
						localHost,// 本地服务器IP地址
						localPort);// 代理服务器使用的端口
				// 代理请求�?
				ProxyRequest request = null;
				while ((bytes_read = sckPlayer.getInputStream().read(
						local_request)) != -1) {
					// 得到处理后的报文信息
					byte[] buffer = httpParser.getRequestBody(local_request,// HTTP得到的数�?
							bytes_read);// 数据长度
					// 如果有数�?
					if (buffer != null) {
						request = httpParser.getProxyRequest(buffer);
						break;
					}
				}
				// 实例化代理服务器工具�?
				utils = new HttpGetProxyUtils(sckPlayer,// 收发MediaPlayer请求的Socket
						serverAddress);// 服务器的Address
				// 判断缓存文件是否已经存在
				boolean isExists = new File(mMediaFilePath).exists();
				// MediaPlayer的request有效
				if (request != null) {
					// 发�?MediaPlayer的request
					sckServer = utils.sentToServer(request._body);
				} else {
					// MediaPlayer的request无效,则关闭现有的链接
					closeSockets();
					return;
				}
				// ------------------------------------------------------
				// 把网络服务器的反馈发到MediaPlayer，网络服务器->代理服务�?>MediaPlayer
				// ------------------------------------------------------
				while (sckServer != null
						&& ((bytes_read = sckServer.getInputStream().read(
								remote_reply)) != -1)) {
					// 如果送了请求头文�?
					if (sentResponseHeader) {
						try {
							// 拖动进度条时，容易在此异常，断开重连
							utils.sendToMP(remote_reply, bytes_read);
						} catch (Exception e) {
							// 发�?异常直接�?��while
							break;
						}
						// 没Response Header则�?出本次循�?
						if (proxyResponse == null) {
							continue;
						}
						// 已完成读�?
						if (proxyResponse._currentPosition > proxyResponse._duration
								- END_SIZE) {
							proxyResponse._currentPosition = -1;
						}
						// 没完成读�?
						else if (proxyResponse._currentPosition != -1) {
							proxyResponse._currentPosition += bytes_read;
						}
						// �?��本次while
						continue;
					}
					// 实例化响应对�?
					proxyResponse = httpParser.getProxyResponse(remote_reply,// 网络返回的数�?
							bytes_read);// 返回数据的大�?
					if (proxyResponse == null) {
						continue;// 没Response Header则�?出本次循�?
					}
					sentResponseHeader = true;
					// 发�?报文给mediaplayer
					utils.sendToMP(proxyResponse._body);
					// 如果缓存文件已经存在
					if (isExists) {// �?��发�?预加载到MediaPlayer
						isExists = false;
						int sentBufferSize = 0;
						// 发�?预加载至服务�?得到已发送的大小
						sentBufferSize = utils.sendPrebufferToMP(
								mMediaFilePath, request._rangePosition);
						// 如果成功发�?预加载，重新发�?请求到服务器
						if (sentBufferSize > 0) {
							// 修改Range后的Request发�?给服务器
							int newRange = (int) (sentBufferSize + request._rangePosition);
							// 获取新的Request内容
							String newRequestStr = httpParser
									.modifyRequestRange(request._body, newRange);
							try {
								if (sckServer != null)
									sckServer.close();
							} catch (IOException ex) {
							}
							// 发�?新的请求到服务器
							sckServer = utils.sentToServer(newRequestStr);
							// 把服务器的Response的Header去掉
							proxyResponse = utils.removeResponseHeader(
									sckServer, httpParser);
							continue;
						}
					}

					// 发�?剩余数据
					if (proxyResponse._other != null) {
						utils.sendToMP(proxyResponse._other);
					}
				}

				// 关闭现有的链�?
				closeSockets();
			} catch (Exception e) {
				Log.e(TAG, e.toString());
				Log.e(TAG, Utils.getExceptionMessage(e));
			}
		}
	}

}