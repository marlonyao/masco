package com.meituan.masco.example.hello;

import org.apache.thrift.TException;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import com.meituan.masco.generated.hello.HelloService;
import com.meituan.masco.generated.hello.Person;
import com.meituan.masco.rpc.InvocationController;
import com.meituan.masco.rpc.MascoProtocol;
import com.meituan.masco.rpc.MascoTransport;
import com.meituan.masco.rpc.filter.LoggingFilter;

public class HelloClient {

	public static void main(String[] args) throws TException {
		//TSocket transport = new TSocket("localhost", 9090);
		//TTransport transport = new THttpClient("http://www.phpthriftserver.dev.sankuai.com");
		//TTransport transport = new MascoHttpClient("http://www.phpthriftserver.dev.sankuai.com");
		//MascoTransport transport = new MascoTransport(new THttpClient("http://localhost:8080/hello"), MascoTransport.SERIALIZER_COMPACT);
		TTransport transport = new THttpClient("http://localhost:8080/hello?a=b&c=d");
		//TTransport transport = new THttpClient("http://www.phpthriftserver.dev.sankuai.com");

		//TProtocol protocol = new MascoProtocol(transport);
		MascoProtocol.Factory factory = new MascoProtocol.Factory(MascoTransport.SERIALIZER_COMPACT);
		HelloService.Client client = new HelloService.Client(factory.getProtocol(transport));
		transport.open();

		InvocationController<HelloService.Iface> controller = new InvocationController<HelloService.Iface>(client, HelloService.Iface.class);
		controller.addFilter(new LoggingFilter());

		HelloService.Iface proxy = controller.createProxy();
		String result = proxy.hello("Marlon");
		System.out.println(result);
		result = proxy.hello("World");
		System.out.println(result);
		Person p = new Person("Marlon", "Yao");
		result = proxy.helloV2(p);
		System.out.println(result);
	}

}
