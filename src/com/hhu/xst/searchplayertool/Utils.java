package com.hhu.xst.searchplayertool;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.os.StatFs;

/**
 * 工具�?原著:hellogv)
 * 
 * @author 蔡有�?E-mail: caiyoufei@looip.cn
 * @version 创建时间�?014-3-3 下午6:14:52
 * 
 */
public class Utils {

	/**
	 * 获取重定向后的URL，即真正有效的链�?
	 * 
	 * @version 更新时间�?014-3-3 下午1:53:20
	 * @param urlString
	 *            网络视频URL地址
	 * @return 重定向后的URL地址
	 */
	static protected String getRedirectUrl(String urlString) {
		String result = urlString;
		// 取得取得默认的HttpClient实例
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// 创建HttpGet实例
		HttpGet request = new HttpGet(urlString);
		try {
			// 重定向设置连接服务器
			httpClient.setRedirectHandler(new RedirectHandler() {
				public URI getLocationURI(HttpResponse response,
						HttpContext context) throws ProtocolException {
					int statusCode = response.getStatusLine().getStatusCode();
					if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
							|| (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
							|| (statusCode == HttpStatus.SC_SEE_OTHER)
							|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
						// 此处重定向处�?
						return null;
					}
					return null;
				}

				public boolean isRedirectRequested(HttpResponse response,
						HttpContext context) {
					return false;
				}

			});
			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
					|| (statusCode == HttpStatus.SC_SEE_OTHER)
					|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				// 从头中取出转向的地址
				Header locationHeader = response.getFirstHeader("Location");
				if (locationHeader != null) {
					// 获取重定向URL地址
					String locationUrl = locationHeader.getValue();
					// 释放连接
					httpClient.getConnectionManager().shutdown();
					// 防止多次重定�?
					return getRedirectUrl(locationUrl);
				}
			}
		} catch (ClientProtocolException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// 释放连接
		httpClient.getConnectionManager().shutdown();
		return result;
	}

	/**
	 * 获取range�?
	 * 
	 * @version 更新时间�?014-3-3 下午3:50:08
	 * @param source
	 *            报文内容
	 * @param startStr
	 *            range�?��标志
	 * @param endStr
	 *            range结束标志
	 * @return range�?��到结束之间的内容
	 */
	static protected String getSubString(String source, String startStr,
			String endStr) {
		// 获取range标志结束的位�?
		int startIndex = source.indexOf(startStr) + startStr.length();
		// 获取range结束标志的位�?
		int endIndex = source.indexOf(endStr, startIndex);
		// 返回range�?��到结束之间的内容
		return source.substring(startIndex, endIndex);
	}

	/**
	 * 获取有效的文件名
	 * 
	 * @version 更新时间�?014-3-3 下午1:34:22
	 * @param str
	 *            传入的文件名
	 * @return 处理后的文件�?
	 */
	static protected String getValidFileName(String str) {
		str = str.replace("\\", "");
		str = str.replace("/", "");
		str = str.replace(":", "");
		str = str.replace("*", "");
		str = str.replace("?", "");
		str = str.replace("\"", "");
		str = str.replace("<", "");
		str = str.replace(">", "");
		str = str.replace("|", "");
		str = str.replace(" ", "_"); // 前面的替换会产生空格,�?��将其�?��替换�?
		return str;
	}

	/**
	 * 获取外部存储器可用的空间
	 * 
	 * @version 更新时间�?014-3-3 上午11:49:47
	 * @param dir
	 *            缓存文件夹路�?
	 * @return 可用空间的大�?
	 */
	@SuppressWarnings("deprecation")
	static protected long getAvailaleSize(String dir) {
		StatFs stat = new StatFs(dir);// dir.getPath());
		// 获取BLOCK数量
		// long totalBlocks = stat.getBlockCount();
		// 获取block的SIZE
		long blockSize = stat.getBlockSize();
		// 可使用的Block的数�?
		long availableBlocks = stat.getAvailableBlocks();
		// 返回可用空间大小
		return availableBlocks * blockSize;
		// totalBlocks * blockSize;//总的大小
	}

	/**
	 * 获取文件夹内的文件，按日期排序，从旧到新
	 * 
	 * @version 更新时间�?014-3-3 下午1:18:12
	 * @param dirPath
	 *            缓存文件的文件夹路径
	 * @return 文件列表
	 */
	static private List<File> getFilesSortByDate(String dirPath) {
		// 新建文件列表
		List<File> result = new ArrayList<File>();
		// 获取文件�?
		File dir = new File(dirPath);
		// 如果文件夹不存在，则直接返回没有数据的列�?
		if (!dir.exists()) {
			return result;
		}
		// 新建文件数组(大小为文件夹里的文件数量)
		File[] files = dir.listFiles();
		// 如果文件为空，或者文件数量为0，则直接返回没有数据的列�?
		if (files == null || files.length == 0) {
			return result;
		}
		// 将数组按日期排序，从旧到�?
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f1.lastModified()).compareTo(
						f2.lastModified());
			}
		});
		// 将文件数组加入到列表
		for (int i = 0; i < files.length; i++) {
			result.add(files[i]);
		}
		return result;
	}

	/**
	 * 删除多余的缓存文�?
	 * 
	 * @version 更新时间�?014-3-3 下午1:16:00
	 * @param dirPath
	 *            缓存文件的文件夹路径
	 * @param maximun
	 *            缓存文件的最大数�?
	 */
	static protected void asynRemoveBufferFile(final String dirPath,
			final int maximun) {
		// �?��个线程删除多余的缓存文件
		new Thread() {
			public void run() {
				// 获取文件夹里的文件列�?
				List<File> lstBufferFile = Utils.getFilesSortByDate(dirPath);
				// 如果获取的文件列表数量大于设置的�?��缓存数量，则将多余的文件删除(之前已经排好序�?从旧到新�?
				while (lstBufferFile.size() > maximun) {
					// 删除文件(每次删除第一个，即最早缓存的)
					lstBufferFile.get(0).delete();
					// 移除列表
					lstBufferFile.remove(0);
				}
			}
		}.start();
	}

	/**
	 * 获取异常信息
	 * 
	 * @version 更新时间�?014-3-3 下午1:28:14
	 * @param ex
	 *            异常�?
	 * @return 异常详情
	 */
	public static String getExceptionMessage(Exception ex) {
		// 异常详情
		String result = "";
		StackTraceElement[] stes = ex.getStackTrace();
		for (int i = 0; i < stes.length; i++) {
			result = result + stes[i].getClassName() + "."
					+ stes[i].getMethodName() + "  " + stes[i].getLineNumber()
					+ "line" + "\r\n";
		}
		return result;
	}
}
