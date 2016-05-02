package com.hhu.xst.searchplayertool;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.hhu.xst.searchplayertool.Config.ProxyRequest;
import com.hhu.xst.searchplayertool.Config.ProxyResponse;

/**
 * Http报文处理�?原著:hellogv)
 * 
 * @author 蔡有�?E-mail: caiyoufei@looip.cn
 * @version 创建时间�?014-3-3 下午5:56:37
 * 
 */
public class HttpParser {
	final static public String TAG = "HttpParser";
	// "请求"默认或�?添加的range信息
	final static private String RANGE_PARAMS = "Range: bytes=";
	// "请求"替换后的range信息
	final static private String RANGE_PARAMS_0 = "Range: bytes=0-";
	// "响应"的range信息
	final static private String CONTENT_RANGE_PARAMS = "Content-Range: bytes ";
	// 报文的大�?
	private static final int HEADER_BUFFER_LENGTH_MAX = 1024 * 10;
	// 报文信息
	private byte[] headerBuffer = new byte[HEADER_BUFFER_LENGTH_MAX];
	// 报文长度
	private int headerBufferLength = 0;
	// 链接带的端口
	private int remotePort = -1;
	// 远程主机地址
	private String remoteHost;
	// 代理服务器使用的端口
	private int localPort;
	// 本地服务器IP地址
	private String localHost;

	/**
	 * 
	 * @version 更新时间�?014-3-3 下午6:06:33
	 * @param rHost
	 *            远程主机地址
	 * @param rPort
	 *            URL链接带的端口
	 * @param lHost
	 *            本地服务器IP地址
	 * @param lPort
	 *            代理服务器使用的端口
	 */
	public HttpParser(String rHost, int rPort, String lHost, int lPort) {
		remoteHost = rHost;
		remotePort = rPort;
		localHost = lHost;
		localPort = lPort;
	}

	/**
	 * 清除HTTP报文内容
	 * 
	 * @version 更新时间�?014-3-3 下午2:50:39
	 */
	public void clearHttpBody() {
		// 重设报文信息
		headerBuffer = new byte[HEADER_BUFFER_LENGTH_MAX];
		// 报文头长度清�?
		headerBufferLength = 0;
	}

	/**
	 * 获取Request报文
	 * 
	 * @version 更新时间�?014-3-3 下午2:37:28
	 * @param source
	 *            从HTTP获取的内�?
	 * @param length
	 *            source的内容长�?
	 * @return 报文信息
	 */
	public byte[] getRequestBody(byte[] source, int length) {
		// 获取报文信息列表
		List<byte[]> httpRequest = getHttpBody(Config.HTTP_REQUEST_BEGIN,// "GET "
				Config.HTTP_BODY_END, // "\r\n\r\n"
				source, length);
		// 如果存在报文信息，则返回第一个报文信�?
		if (httpRequest.size() > 0) {
			return httpRequest.get(0);
		}
		return null;
	}

	/**
	 * Request报文解析转换ProxyRequest
	 * 
	 * @version 更新时间�?014-3-3 下午3:19:14
	 * @param bodyBytes
	 *            报文内容
	 * @return 代理请求�?
	 */
	public ProxyRequest getProxyRequest(byte[] bodyBytes) {
		// 实例化代理请求类
		ProxyRequest result = new ProxyRequest();
		// 获取代理请求Body
		result._body = new String(bodyBytes);
		// 把request中的本地ip改为远程ip
		result._body = result._body.replace(localHost, remoteHost);
		// 把代理服务器端口改为原URL端口
		if (remotePort == -1) {
			result._body = result._body.replace(":" + localPort, "");
		} else {
			result._body = result._body.replace(":" + localPort, ":"
					+ remotePort);
		}
		// 不带Range则添加补上，方便后面处理
		if (result._body.contains(RANGE_PARAMS) == false) {
			result._body = result._body.replace(Config.HTTP_BODY_END, "\r\n"
					+ RANGE_PARAMS_0 + Config.HTTP_BODY_END);
		}
		// 获取Range�?��和结束之间的内容
		String rangePosition = Utils.getSubString(result._body, RANGE_PARAMS,
				"-");
		// 获取range位置
		try {
			result._rangePosition = Integer.valueOf(rangePosition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取ProxyResponse
	 * 
	 * @version 更新时间�?014-3-3 下午4:40:18
	 * @param source
	 *            网路返回数据
	 * @param length
	 *            返回数据大小
	 * @return 响应对象
	 */
	public ProxyResponse getProxyResponse(byte[] source, int length) {
		// 获取获取报文信息列表
		List<byte[]> httpResponse = getHttpBody(Config.HTTP_RESPONSE_BEGIN,// "HTTP/"
				Config.HTTP_BODY_END, // "\r\n\r\n"
				source, length);
		// 如果报文信息列表没有数据，则直接返回�?
		if (httpResponse.size() == 0) {
			return null;
		}
		// 实例化一个响应类
		ProxyResponse result = new ProxyResponse();
		// 获取Response正文
		result._body = httpResponse.get(0);
		// 将byte的报文信息转化为string
		String text = new String(result._body);
		// 获取二进制数�?
		if (httpResponse.size() == 2) {
			result._other = httpResponse.get(1);
		}
		// 样例：Content-Range: bytes 2267097-257405191/257405192
		try {
			// 获取起始位置
			String currentPosition = Utils.getSubString(text,
					CONTENT_RANGE_PARAMS, "-");
			try {
				result._currentPosition = Integer.valueOf(currentPosition);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 获取�?��位置
			String startStr = CONTENT_RANGE_PARAMS + currentPosition + "-";
			String duration = Utils.getSubString(text, startStr, "/");
			try {
				result._duration = Integer.valueOf(duration);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			Log.e(TAG, Utils.getExceptionMessage(ex));
		}
		return result;
	}

	/**
	 * 替换Request报文中的Range位置,"Range: bytes=0-" -> "Range: bytes=xx-"
	 * 
	 * @version 更新时间�?014-3-3 下午5:23:15
	 * @param requestStr
	 *            报文信息
	 * @param position
	 *            发�?预加载到服务器后，新的range位置
	 * @return 处理后的报文信息
	 */
	public String modifyRequestRange(String requestStr, int position) {
		String str = Utils.getSubString(requestStr, RANGE_PARAMS, "-");
		str = str + "-";
		String result = requestStr.replaceAll(str, position + "-");
		return result;
	}

	/**
	 * 获取报文内容
	 * 
	 * @version 更新时间�?014-3-3 下午2:45:45
	 * @param beginStr
	 *            �?��内容
	 * @param endStr
	 *            结束内容
	 * @param source
	 *            从HTTP获取的内�?
	 * @param length
	 *            source的内容长�?
	 * @return 报文信息列表
	 */
	private List<byte[]> getHttpBody(String beginStr,// "GET "
			String endStr,// "\r\n\r\n"
			byte[] source,// 从HTTP获取的内�?
			int length) { // source的内容长�?
		// 如果报文长度大于等于报文的默认长度，则清空报�?
		if ((headerBufferLength + length) >= headerBuffer.length) {
			// 清空报文
			clearHttpBody();
		}
		// 将HTTP获取的内容拷贝到报文信息�?
		System.arraycopy(source,// HTTP获取的信�?
				0, // 拷贝�?��位置
				headerBuffer,// 拷贝存储的地�?
				headerBufferLength,// 拷贝存储的开始位�?
				length);// 拷贝的长�?
		// 完成拷贝的报文长�?
		headerBufferLength += length;
		// 报文内容列表
		List<byte[]> result = new ArrayList<byte[]>();
		// 报文内容转换(byte转为string)
		String responseStr = new String(headerBuffer);
		// 如果报文包含�?��和结束的标志
		if (responseStr.contains(beginStr) && responseStr.contains(endStr)) {
			// 获取�?��标志在报文中的开始位�?
			int startIndex = responseStr.indexOf(beginStr,// �?��查找的字�?
					0);// 查找的起始位�?
			// 获取结束标志在报文中的开始位�?
			int endIndex = responseStr.indexOf(endStr,// //�?��查找的字�?
					startIndex);// 查找的起始位�?
			// 获取结束标志在报文中的结束位�?
			endIndex += endStr.length();
			// 新建�?��长度为开始到结束长度的byte数组
			byte[] header = new byte[endIndex - startIndex];
			// 将报文内容中从开始到结束的内容拷贝到新建的byte数组�?
			System.arraycopy(headerBuffer,// �?��拷贝的源数据
					startIndex, // 源数据开始拷贝的起点
					header,// 拷贝的目的地
					0,// 目的地的起始位置
					header.length);// �?��拷贝的数据长�?
			// 将内容添加到列表
			result.add(header);
			// 如果还有数据
			if (headerBufferLength > header.length) {
				byte[] other = new byte[headerBufferLength - header.length];
				System.arraycopy(headerBuffer, header.length, other, 0,
						other.length);
				result.add(other);
			}
			// 清除报文信息
			clearHttpBody();
		}
		return result;
	}

}
