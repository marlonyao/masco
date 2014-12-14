package com.meituan.masco.example.hello;

import com.meituan.masco.generated.hello.HelloService;
import com.meituan.masco.rpc.MascoProtocol;
import com.meituan.masco.rpc.MascoServlet;

public class HelloServlet extends MascoServlet {
	public HelloServlet() {
		super(new HelloService.Processor(createHandler()), new MascoProtocol.Factory());
	}

	private static HelloHandler createHandler() {
		return new HelloHandler();
	}
}
