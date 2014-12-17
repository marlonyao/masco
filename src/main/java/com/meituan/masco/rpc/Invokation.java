package com.meituan.masco.rpc;

import java.util.ArrayList;
import java.util.List;

public class Invokation {
	private String name;
	private List params = new ArrayList();

	public String getName() {
		return this.name;
	}

	public List getParams() {
		return params;
	}
}
