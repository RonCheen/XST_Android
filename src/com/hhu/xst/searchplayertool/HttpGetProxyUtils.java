package com.hhu.xst.searchplayertool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

import com.hhu.xst.searchplayertool.Config.ProxyResponse;

/**
 * 代理服务器工具类(原著:hellogv)
 * 
 * @author 蔡有�?E-mail: caiyoufei@looip.cn
 * @version 创建时间�?014-3-3 下午5:54:32
 * 
 */
public class HttpGetProxyUtils {
	final static public String TAG = "HttpGetProxy";
	// 收发Media Player请求的Socket
	private Socket mSckPlayer = null;
	// 服务器的Address
	private SocketAddress mServerAddress;

	/**
	 * 构�?方法
	 * 
	 * @version 更新时间�?014-3-3 下午5:55:10
	 * @param sckPlayer
	 *            收发Media Player请求的Socket
	 * @param address
	 *            服务器的Address
	 */
	public HttpGetProxyUtils(Socket sckPlayer, SocketAddress address) {
		mSckPlayer = sckPlayer;
		mServerAddress = address;
	}

	/**
	 * 发�?预加载至服务�?
	 * 
	 * @version 更新时间�?014-3-3 下午5:04:39
	 * @param fileName
	 *            预加载文�?
	 * @param range
	 *            skip的大�?
	 * @return 已发送的大小，不含skip的大�?
	 */
	public int sendPrebufferToMP(String fileName, long range) {
		// 文件大小的比较�?,如果小于该�?，则没必要进行读取及重发请求
		final int MIN_SIZE = 100 * 1024;
		// 已发送至服务器的数据长度
		int fileBufferSize = 0;
		// 读取数据的存�?
		byte[] file_buffer = new byte[1024];
		// 读取的数据长�?
		int bytes_read = 0;
		// 当前系统时间
		long startTimeMills = System.currentTimeMillis();
		// 获取缓存文件
		File file = new File(fileName);
		// 如果缓存文件不存在，则直接返�?
		if (file.exists() == false) {
			return 0;
		}
		// Range大小超过预缓存的太小，则直接返回0
		if (range > (file.length())) {
			return 0;
		}
		// 可用的预缓存太小，没必要读取以及重发Request
		if (file.length() < MIN_SIZE) {
			return 0;
		}
		// 文件输入�?
		FileInputStream fInputStream = null;
		try {
			// 实例化文件输入流
			fInputStream = new FileInputStream(file);
			if (range > 0) {
				// 新建�?��和rang大小�?��的字节数�?
				byte[] tmp = new byte[(int) range];
				// 从输入流读取信息到字节数组中
				long skipByteCount = fInputStream.read(tmp);
				Log.i(TAG, ">>>skip:" + skipByteCount);
			}

			while ((bytes_read = fInputStream.read(file_buffer)) != -1) {
				// 发�?到服务器
				mSckPlayer.getOutputStream().write(file_buffer, 0, bytes_read);
				// 成功发�?才计算已发�?至服务器的数据长�?
				fileBufferSize += bytes_read;
			}
			mSckPlayer.getOutputStream().flush();
			// 从开始到发�?结束花费的时�?
			long costTime = (System.currentTimeMillis() - startTimeMills);
			Log.i(TAG, ">>>读取预加载�?�?" + costTime);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (fInputStream != null)
					fInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileBufferSize;
	}

	/**
	 * 把服务器的Response的Header去掉
	 * 
	 * @version 更新时间�?014-3-3 下午5:38:11
	 * @param sckServer
	 *            与服务器连接的socket
	 * @param httpParser
	 *            Http报文处理�?
	 * @return
	 * @throws IOException
	 *             抛出的异�?
	 */
	public ProxyResponse removeResponseHeader(Socket sckServer,
			HttpParser httpParser) throws IOException {
		// 服务器响应类
		ProxyResponse result = null;
		// 从服务器读取数据的长�?
		int bytes_read;
		// 从服务器读取数据的缓�?
		byte[] tmp_buffer = new byte[1024];
		while ((bytes_read = sckServer.getInputStream().read(tmp_buffer)) != -1) {
			// 将读取的内容转换为服务器响应�?
			result = httpParser.getProxyResponse(tmp_buffer, bytes_read);
			// 没Header则�?出本次循�?
			if (result == null) {
				continue;
			}
			// 接收到Response的Header
			if (result._other != null) {// 发�?剩余数据
				sendToMP(result._other);
			}
			break;
		}
		return result;
	}

	/**
	 * 拖动进度条时，重新执行加�?
	 * 
	 * @version 更新时间�?014-3-3 下午4:16:45
	 * @param bytes
	 *            网络返回的数�?
	 * @param length
	 *            网络返回数据的长�?
	 * @throws IOException
	 *             抛出异常
	 */
	public void sendToMP(byte[] bytes, int length) throws IOException {
		mSckPlayer.getOutputStream().write(bytes, 0, length);
		mSckPlayer.getOutputStream().flush();
	}

	/**
	 * 发�?报文给mediaplayer
	 * 
	 * @version 更新时间�?014-3-3 下午5:01:11
	 * @param bytes
	 *            报文内容
	 * @throws IOException
	 *             抛出的异�?
	 */
	public void sendToMP(byte[] bytes) throws IOException {
		if (bytes.length == 0) {
			return;
		}
		mSckPlayer.getOutputStream().write(bytes);
		mSckPlayer.getOutputStream().flush();
	}

	/**
	 * 发�?MediaPlayer的request
	 * 
	 * @version 更新时间�?014-3-3 下午4:06:56
	 * @param requestStr
	 *            报文信息
	 * @return 与服务器连接的socket
	 * @throws IOException
	 *             抛出的异�?
	 */
	public Socket sentToServer(String requestStr) throws IOException {
		Socket sckServer = new Socket();
		// 连接服务�?
		sckServer.connect(mServerAddress);
		// 发�?MediaPlayer的请�?
		sckServer.getOutputStream().write(requestStr.getBytes());
		sckServer.getOutputStream().flush();
		return sckServer;
	}
}
