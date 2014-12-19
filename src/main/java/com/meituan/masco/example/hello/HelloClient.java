package com.meituan.masco.example.hello;

import org.apache.thrift.TException;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import com.meituan.masco.generated.hello.HelloService;
import com.meituan.masco.rpc.MascoProtocol;
import com.meituan.masco.rpc.MascoTransport;

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
		//client.addFilter(new AuthenticateFilter(...));
		//client.addFilter(new LoggingFilter(...));
		transport.open();
		String result = client.hello("Marlon");
		System.out.println(result);
		result = client.hello("World");
		System.out.println(result);
		//Person p = new Person("Marlon", "Yao");
		//result = client.helloV2(p);
		//System.out.println(result);
	}
}
