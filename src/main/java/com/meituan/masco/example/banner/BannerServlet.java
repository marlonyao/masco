package com.meituan.masco.example.banner;

import org.apache.thrift.TProcessor;

import com.meituan.masco.generated.banner.BannerService;
import com.meituan.masco.rpc.MascoServlet;
import com.meituan.masco.rpc.ProcessorFactory;

public class BannerServlet extends MascoServlet<BannerService.Iface> {
	public BannerServlet() {
		super(new ProcessorFactory<BannerService.Iface>() {
			@Override
			public TProcessor createProcessor(BannerService.Iface handler) {
				return new BannerService.Processor<BannerService.Iface>(handler);
			}
		}, new BannerHandler(), BannerService.Iface.class);
		//super(new BannerService.Processor(new BannerHandler()), new MascoProtocol.Factory());
	}
}
