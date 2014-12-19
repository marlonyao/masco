package com.meituan.masco.rpc;

import org.apache.thrift.TProcessor;

public interface ProcessorFactory<I> {
	TProcessor createProcessor(I handler);
}
