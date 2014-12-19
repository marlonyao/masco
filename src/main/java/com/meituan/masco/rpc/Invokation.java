package com.meituan.masco.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Invokation {
	private String name;
	private List params = new ArrayList();
	private Map<String, Object> metadata = null;

	public Invokation(String name, List params) {
		this.name = name;
		this.params = params;
	}

	public String getName() {
		return this.name;
	}

	public List getParams() {
		return params;
	}

	public Map<String, Object> getMetadata() {
		return this.metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}
}
