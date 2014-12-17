package com.meituan.masco.rpc;

import java.util.ArrayList;
import java.util.List;

public class InvocationController<I> {
	private I handler;
	private List<InvokeFilter> filters = new ArrayList<InvokeFilter>();

	public InvocationController(I handler) {
		this.handler = handler;
	}

	public void addFilter(InvokeFilter filter) {
		filters.add(filter);
	}

	public InvokeResult invoke(Invokation invokation) {
		for (int i = filters.size()-1; i >= 0; i--) {
			InvokeFilter filter = filters.get(i);
			filter.preInvoke(invokation);
		}
		InvokeResult result = invokeHandler(invokation);
		for (int i = 0; i < filters.size(); i++) {
			InvokeFilter filter = filters.get(i);
			filter.postInvoke(invokation, result);
		}
		return result;
	}

	private InvokeResult invokeHandler(Invokation invokation) {
		String methodName = invokation.getName();
		// how to only run method in schema
		return null;
	}
}
