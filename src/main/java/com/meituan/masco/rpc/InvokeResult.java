package com.meituan.masco.rpc;

public class InvokeResult {
	private Object result;

	public InvokeResult(Object result) {
		this.result = result;
	}

	public Object getResult() {
		return this.result;
	}
}
