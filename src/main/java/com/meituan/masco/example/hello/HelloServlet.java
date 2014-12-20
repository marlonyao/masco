package com.meituan.masco.example.hello;

import org.apache.thrift.TProcessor;

import com.meituan.masco.generated.hello.HelloService;
import com.meituan.masco.rpc.MascoServlet;
import com.meituan.masco.rpc.ProcessorFactory;
import com.meituan.masco.rpc.filter.LoggingFilter;

public class HelloServlet extends MascoServlet<HelloService.Iface> {
	private static final long serialVersionUID = 1L;

	public HelloServlet() {
		super(new ProcessorFactory<HelloService.Iface>() {
			@Override
			public TProcessor createProcessor(HelloService.Iface handler) {
				return new HelloService.Processor<HelloService.Iface>(handler);
			}
		}, new HelloHandler(), HelloService.Iface.class);
		addFilter(new LoggingFilter());
	}

}
