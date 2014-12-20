package com.meituan.masco.example.banner;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import com.meituan.masco.generated.banner.Banner;
import com.meituan.masco.generated.banner.BannerService;
import com.meituan.masco.generated.banner.Location;
import com.meituan.masco.rpc.MascoProtocol;

public class BannerClient {
	public static void main(String[] args) throws TException {
		BannerService.Iface client = createBannerService();

		// create Banner
		Banner banner = new Banner(0, 1, new Location("index", "recommend"),
				"http://p0.meituan.net/0.0.90/deal/xxxxxxxxxxx.png",
				"http://www.meituan.com/deal/11111.html");
		int bannerId = client.createBanner(banner);
		System.out.println("create Banner: " + bannerId);

		// find banners
		List<Banner> banners = client.getBanners(1);
		for (Banner b : banners) {
			System.out.println("---" + b);
		}

		//banner.setId(100);
		//client.updateBanner(banner);
	}

	// TODO: how to hide connection manage?
	private static BannerService.Iface createBannerService() throws TException {
		TTransport transport = new THttpClient("http://localhost:8080/banner");
		MascoProtocol.Factory factory = new MascoProtocol.Factory();
		BannerService.Client client = new BannerService.Client(factory.getProtocol(transport));
		transport.open();
		return client;
	}
}
