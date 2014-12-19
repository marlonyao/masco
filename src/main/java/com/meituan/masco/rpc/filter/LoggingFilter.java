package com.meituan.masco.rpc.filter;

import com.meituan.masco.rpc.InvocationController;
import com.meituan.masco.rpc.Invokation;
import com.meituan.masco.rpc.InvokeFilter;
import com.meituan.masco.rpc.InvokeResult;

public class LoggingFilter implements InvokeFilter {

	@Override
	public boolean preInvoke(Invokation invokation) {
		System.out.println("before invoke: " + invokation.getName() + ", uri: " + invokation.getMetadata().get(InvocationController.KEY_URI));
		return false;
	}

	@Override
	public void postInvoke(Invokation invokation, InvokeResult result) {
		System.out.println("after invoke: " + invokation.getName() + ", result: " + result.getResult());
	}

}
