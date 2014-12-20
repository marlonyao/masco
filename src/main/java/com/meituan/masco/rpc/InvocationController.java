package com.meituan.masco.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvocationController<I> {
	public static final String KEY_URI = "builtin.uri";

	private I handler;
	private Class<I> handlerType;
	private List<InvokeFilter> filters = new ArrayList<InvokeFilter>();
	private Map<String, Object> metadata;

	public InvocationController(I handler, Class<I> handlerType) {
		this.handler = handler;
		this.handlerType = handlerType;
		this.metadata = new HashMap<String, Object>();
	}

	public void addFilter(InvokeFilter filter) {
		filters.add(filter);
	}

	public I createProxy() {
		Object proxy = java.lang.reflect.Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{handlerType}, new java.lang.reflect.InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				Invokation invokation = new Invokation(method.getName(), Arrays.asList(args));
				invokation.setMetadata(InvocationController.this.metadata);
				InvokeResult result = InvocationController.this.invoke(invokation);
				return result.getResult();
			}
		});

		@SuppressWarnings("unchecked") I result = (I)proxy;
		return result;
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
		if (handler instanceof InvokeMetadataAware) {
			((InvokeMetadataAware)handler).setMetadata(invokation.getMetadata());
		}
		try {
			Object handlerResult = foundMethod.invoke(handler, invokation.getParams().toArray());
			return new InvokeResult(handlerResult);
		} catch (IllegalAccessException e) {
			// TODO: more specific exception
			throw new RuntimeException("Invoke handler error", e);
		} catch (InvocationTargetException e) {
			// TODO: rethrow application Exception (TException)
			throw new RuntimeException("Invoke handler error", e);
		}
	}

	public void setMetadata(String key, Object value) {
		metadata.put(key, value);
	}

	public Object getMetadata(String key) {
		return metadata.get(key);
	}
}
