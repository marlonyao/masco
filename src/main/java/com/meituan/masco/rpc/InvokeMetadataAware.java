package com.meituan.masco.rpc;

import java.util.Map;

public interface InvokeMetadataAware {
	void setMetadata(Map<String, Object> metadata);
}
