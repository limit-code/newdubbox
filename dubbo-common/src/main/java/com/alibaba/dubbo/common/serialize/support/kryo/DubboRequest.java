package com.alibaba.dubbo.common.serialize.support.kryo;


import java.io.Serializable;
/**
 * kryo序列化时会给每一个class分配一个序号id，当服务提供者调用其他服务的接口，也就是既是服务提供者又是服务消费者时，kryo序号会混乱。</br>
 * {@link DubboRequest}是请求的包装类,用以解决kryo index序号混乱的问题
 * @author cqzong
 *
 * @param <T>
 */
public class DubboRequest<T> implements Serializable {
	private static final long serialVersionUID = 1765349131408871741L;
	
	private T request;

	public DubboRequest() {
	}
	
	public DubboRequest(T request) {
		this.request = request;
	}

	public T getRequest() {
		return request;
	}

	public void setRequest(T request) {
		this.request = request;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DubboRequest [request=").append(request).append("]");
		return builder.toString();
	}
	
}