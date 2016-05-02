package com.hhu.xst.searchplayertool;

/**
 * 公用数据�?原著:hellogv)
 * 
 * @author 蔡有�?E-mail: caiyoufei@looip.cn
 * @version 创建时间�?014-3-3 上午11:13:12
 * 
 */
public class Config {
	// 缓存本地IP地址
	final static public String LOCAL_IP_ADDRESS = "127.0.0.1";
	// 缓存地址端口
	final static public int HTTP_PORT = 80;
	// HTTP下载结束标志
	final static public String HTTP_BODY_END = "\r\n\r\n";
	// HTTP响应标志
	final static public String HTTP_RESPONSE_BEGIN = "HTTP/";
	// HTTP请求标志
	final static public String HTTP_REQUEST_BEGIN = "GET ";

	/**
	 * 代理请求�?
	 * 
	 * @author 蔡有�?E-mail: caiyoufei@looip.cn
	 * @version 创建时间�?014-3-3 下午2:44:44
	 * 
	 */
	static public class ProxyRequest {
		// Http Request 内容
		public String _body;
		// Range的位�?
		public long _rangePosition;
	}

	/**
	 * 服务器响应类
	 * 
	 * @author 蔡有�?E-mail: caiyoufei@looip.cn
	 * @version 创建时间�?014-3-3 下午5:40:02
	 * 
	 */
	static public class ProxyResponse {
		// Response的报文内�?
		public byte[] _body;
		// Response报文内容外的内容
		public byte[] _other;
		// 起始位置
		public long _currentPosition;
		// �?��位置
		public long _duration;
	}
}