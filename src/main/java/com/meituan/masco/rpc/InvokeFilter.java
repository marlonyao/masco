package com.meituan.masco.rpc;

public interface InvokeFilter {
	boolean preInvoke(Invokation invokation);
	void postInvoke(Invokation invokation, InvokeResult result);
}
