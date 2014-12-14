package com.meituan.masco.example.banner;

import com.meituan.masco.generated.banner.BannerService;
import com.meituan.masco.rpc.MascoProtocol;
import com.meituan.masco.rpc.MascoServlet;

public class BannerServlet extends MascoServlet {
	public BannerServlet() {
		super(new BannerService.Processor(new BannerHandler()), new MascoProtocol.Factory());
	}
}
