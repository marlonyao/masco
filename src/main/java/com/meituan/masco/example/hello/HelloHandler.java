package com.meituan.masco.example.hello;

import java.util.Map;

import com.meituan.masco.generated.hello.HelloService;
import com.meituan.masco.generated.hello.Person;
import com.meituan.masco.rpc.InvocationController;
import com.meituan.masco.rpc.InvokeMetadataAware;

public class HelloHandler implements HelloService.Iface, InvokeMetadataAware {
	private Map<String, Object> metadata;

	@Override
	public void ping() {
		System.out.println("ping");
	}

	@Override
	public String hello(String name) {
		return "Hello, " + name + "!" + ", " + metadata.get(InvocationController.KEY_URI);
	}

    @Override
	public String helloV2(Person person) {
        return "Hello, " + person.getLastName() + " " + person.getFirstName() + "!";
    }

	@Override
	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}
}
