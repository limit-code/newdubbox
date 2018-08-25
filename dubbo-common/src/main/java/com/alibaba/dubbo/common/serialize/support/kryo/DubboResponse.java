package com.alibaba.dubbo.common.serialize.support.kryo;

import java.io.Serializable;

/**
 * kryo通用序列化返回结果,解决kryo index序号混乱的问题
 * 
 * @author cqzong
 *
 * @param <T>
 */
public class DubboResponse<T> implements Serializable {
	private static final long serialVersionUID = 5058081480649030877L;

	private int code = 200;

	private T result;

	public DubboResponse() {
	}

	public DubboResponse(int code) {
		this.setCode(code);
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DubboResponse [code=");
		builder.append(code);
		builder.append(", result=");
		builder.append(result);
		builder.append("]");
		return builder.toString();
	}

}