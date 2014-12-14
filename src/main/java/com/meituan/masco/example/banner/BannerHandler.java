package com.meituan.masco.example.banner;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;

import com.meituan.masco.generated.banner.Banner;
import com.meituan.masco.generated.banner.BannerService;
import com.meituan.masco.generated.banner.Location;

public class BannerHandler implements BannerService.Iface {
	private List<Banner> banners = new ArrayList<Banner>();
	private static int idSeq = 1;

	public BannerHandler() {
		banners.add(new Banner(idSeq++, 1, new Location("index", "lottery"),
				"http://p1.meituan.net/0.0.90/tuanpic/557a16515f93493f0861f33ded81b8bf23614.png",
				"http://www.meituan.com/category/jianshen"));
	}

	@Override
	public int createBanner(Banner banner) throws TException {
		banner.id = idSeq++;
		banners.add(banner);
		return banner.id;
	}

	@Override
	public void updateBanner(Banner banner) throws TException {
		for (Banner b : banners) {
			if (b.id == banner.id) {
				b.cityId = banner.cityId;
				b.location = banner.location;
				b.imageUrl = banner.imageUrl;
				b.url = banner.url;
				return;
			}
		}
		throw new TException("Not found banner: " + banner.id);
	}

	@Override
	public List<Banner> getBanners(int cityId) throws TException {
		List<Banner> result = new ArrayList<Banner>();
		for (Banner banner : banners) {
			if (banner.cityId == cityId) {
				result.add(banner);
			}
		}
		return result;
	}

}
