package com.meituan.masco.rpc;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TByteArrayOutputStream;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class MascoTransport extends TTransport {

	public static final byte MAGIC_NUMBER_HIGH = (byte)0xc8;
	public static final byte MAGIC_NUMBER_LOW = (byte)0xc0;

	public static final int SERIALIZER_BINARY = 1;
	public static final int SERIALIZER_COMPACT = 2;

	// underlying transport
	private final TTransport transport;

	private int serializerId;

	private String url = null;
	private Map<String, String> headers = new HashMap<String, String>();

	private TByteArrayOutputStream writeBuffer = new TByteArrayOutputStream(1024);
	private TMemoryInputTransport readBuffer = new TMemoryInputTransport(new byte[0]);

	public MascoTransport(TTransport transport) {
		this(transport, SERIALIZER_BINARY);
	}

	public MascoTransport(TTransport transport, int defaultSerializerId) {
		this.transport = transport;
		this.serializerId = defaultSerializerId;
	}

	@Override
	public boolean isOpen() {
		return transport.isOpen();
	}

	@Override
	public void open() throws TTransportException {
		transport.open();
	}

	@Override
	public void close() {
		transport.close();
	}

	public void beforeReadMessageBegin() throws TTransportException {
		readHeader();
	}

	public void afterReadMessageEnd() throws TTransportException {

	}

	public void beforeWriteMessageBegin() throws TTransportException {

	}

	public void afterWriteMessageEnd() throws TTransportException {

	}

	@Override
	public int read(byte[] buf, int off, int len) throws TTransportException {
		return readBuffer.read(buf, off, len);
	}

	private void readHeader() throws TTransportException {
		byte[] bytes = new byte[12];
		transport.readAll(bytes, 0, 12);
		// 0-1: magic number
		if (bytes[0] != MAGIC_NUMBER_HIGH || bytes[1] != MAGIC_NUMBER_LOW) {
			throw new TTransportException(String.format("Bad magic number: 0x%02X%02X", bytes[0], bytes[1]));
		}

		// 2: serializer id
		serializerId = bytes[2] & 0xff;
		//System.out.println("read serializerId: " + serializerId);

		// 3-5: padding
		// 6-7: header size
		int headerSize = ((bytes[6] & 0xff) << 8) | (bytes[7] & 0xff);
		// 8-11: body size
		int bodySize = ((bytes[8] & 0xff) << 24) | ((bytes[9] & 0xff) << 16) | ((bytes[10] & 0xff) << 8) | (bytes[11] & 0xff);
		if (bodySize < 0) {
			throw new TTransportException("Negative body size: " + bodySize);
		}
		if (headerSize > 0) {
			byte[] headerBuf = new byte[headerSize];
			transport.readAll(headerBuf, 0, headerSize);
			//this.headerBuf = headerBuf;
		}
		byte[] bodyBuf = new byte[bodySize];
		transport.readAll(bodyBuf, 0, bodySize);
		readBuffer.reset(bodyBuf);

	}

	@Override
	public void write(byte[] buf, int off, int len) throws TTransportException {
		writeBuffer.write(buf, off, len);
	}

	@Override
	public void flush() throws TTransportException {
		byte[] bytes = new byte[12];
		bytes[0] = MAGIC_NUMBER_HIGH;
		bytes[1] = MAGIC_NUMBER_LOW;
		// TODO: where does serializeId come from?
		bytes[2] = (byte)(serializerId & 0xff);
		bytes[3] = 0; bytes[4] = 0; bytes[5] = 0;
		// TODO: what does if header is not empty?
		bytes[6] = 0; bytes[7] = 0;
		int bodySize = writeBuffer.len();
		bytes[8] = (byte)((bodySize >> 24) & 0xff);
		bytes[9] = (byte)((bodySize >> 16) & 0xff);
		bytes[10] = (byte)((bodySize >> 8) & 0xff);
		bytes[11] = (byte)(bodySize & 0xff);

		byte[] buf = writeBuffer.get();
		writeBuffer.reset();

		transport.write(bytes);
		//transport.write(buf, 0, bodySize);
		transport.write(buf);
		transport.flush();
	}

	@Override
	public byte[] getBuffer() {
		return readBuffer.getBuffer();
	}

	@Override
	public int getBufferPosition() {
		return readBuffer.getBufferPosition();
	}

	@Override
	public int getBytesRemainingInBuffer() {
		return readBuffer.getBytesRemainingInBuffer();
	}

	@Override
	public void consumeBuffer(int len) {
		readBuffer.consumeBuffer(len);
	}

	public int getSerializerId() {
		return serializerId;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	public void addHeaders(Map<String, String> headers) {
		headers.putAll(headers);
	}

	public String getHeader(String name) {
		return headers.get(name);
	}

	public Map<String, String> getHeaders() {
		return this.headers;
	}
}
