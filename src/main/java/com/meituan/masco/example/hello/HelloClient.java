package com.meituan.masco.example.hello;

import com.meituan.masco.generated.hello.Person;
import com.meituan.masco.rpc.EncryptDecryptTransport;
import com.meituan.masco.rpc.MascoTransport;
import org.apache.http.HttpVersion;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.thrift.TException;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import com.meituan.masco.generated.hello.HelloService;
import com.meituan.masco.rpc.MascoProtocol;

public class HelloClient {

	public static void main(String[] args) throws TException {
		//TSocket transport = new TSocket("localhost", 9090);
		//TTransport transport = new THttpClient("http://www.phpthriftserver.dev.sankuai.com");
		//TTransport transport = new MascoHttpClient("http://www.phpthriftserver.dev.sankuai.com");
		//TProtocol protocol = new MascoProtocol(transport);
		//client.addFilter(new AuthenticateFilter(...));
		//client.addFilter(new LoggingFilter(...));


		//MascoTransport transport = new MascoTransport(new THttpClient("http://localhost:8602/hello"));

//        TTransport transport = new EncryptDecryptTransport(new THttpClient("http://localhost:8602/hello"));
//        MascoProtocol.Factory factory = new MascoProtocol.Factory();
//		HelloService.Client client = new HelloService.Client(factory.getProtocol(transport));
//		transport.open();
//		String result = client.hello("Marlon");
//		System.out.println(result);


//		transport
//		result = client.hello("World");
//		System.out.println(result);
//		Person p = new Person("Marlon", "Yao");
//		String result = client.helloV2(p);
//		System.out.println(result);



		String url = "http://localhost:8602/hello";
		BasicHttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
		// Disable Expect-Continue
		params.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		// Enable staleness check
		params.setParameter("http.connection.stalecheck", true);
		HttpConnectionParams.setSoTimeout(params, 10000); // 10 secondes
		HttpConnectionParams.setConnectionTimeout(params, 10000); // 10 secondes

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 8080, PlainSocketFactory
				.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
				.getSocketFactory()));

		BasicClientConnectionManager cm = new BasicClientConnectionManager(
				schemeRegistry);



		THttpClient thc= new THttpClient(url, new DefaultHttpClient(cm, params));
		TTransport transport = new EncryptDecryptTransport(thc);
		MascoProtocol.Factory factory = new MascoProtocol.Factory();
		HelloService.Client client = new HelloService.Client(factory.getProtocol(transport));
		transport.open();
		String result = client.hello("Marlon");
		System.out.println(result);


	}
}
