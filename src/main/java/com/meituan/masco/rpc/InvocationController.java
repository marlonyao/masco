package com.meituan.masco.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	public I createProxy() {

		return null;
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
		// security risk: how to only run method in schema
		Class handlerType = handler.getClass();
		// should we have method cache?
		Method foundMethod = null;
		Method[] methods = handlerType.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				if (foundMethod != null) {
					// duplicate method, TODO: more strict method check
					throw new RuntimeException("Duplicated methods named: " + methodName);
				}
				foundMethod = method;
			}
		}
		if (foundMethod == null) {
			throw new RuntimeException("Found no method named: " + methodName);
		}
		try {
			Object handlerResult = foundMethod.invoke(handler, invokation.getParams().toArray());
			return new InvokeResult(handlerResult);
		}  catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			// TODO: more specific exception
			throw new RuntimeException("Invoke handler error", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Invoke handler error", e);
		}
	}
}
