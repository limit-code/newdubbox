package com.alibaba.dubbo.common.serialize.support.kryo;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;

import java.io.Serializable;

/**
 * kryo通用序列化返回结果,解决kryo index序号混乱的问题
 * 
 * @author cqzong
 *
 * @param <T>
 */
public class KryoResponse<T> implements Serializable {
	private static final long serialVersionUID = 5058081480649030866L;
	@TaggedFieldSerializer.Tag(1)
	private int code = 200;
	@TaggedFieldSerializer.Tag(2)
	private T result;

	public KryoResponse() {
	}

	public KryoResponse(int code) {
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
		builder.append("KryoResponse [code=");
		builder.append(code);
		builder.append(", result=");
		builder.append(result);
		builder.append("]");
		return builder.toString();
	}

}