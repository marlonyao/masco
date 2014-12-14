package com.meituan.masco.example.hello;

import com.meituan.masco.generated.hello.HelloService;
import com.meituan.masco.generated.hello.Person;

public class HelloHandler implements HelloService.Iface {
	public void ping() {
		System.out.println("ping");
	}

	public String hello(String name) {
		return "Hello, " + name + "!";
	}

    public String helloV2(Person person) {
        return "Hello, " + person.getLastName() + " " + person.getFirstName() + "!";
    }
}
