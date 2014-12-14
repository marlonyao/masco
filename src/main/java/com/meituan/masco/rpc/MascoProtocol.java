package com.meituan.masco.rpc;

import java.nio.ByteBuffer;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.transport.TTransport;

public class MascoProtocol extends TProtocol {
	private TProtocol serializer;

	public MascoProtocol(MascoTransport transport) {
		super(transport);
	}

	public static class Factory implements TProtocolFactory {
		private int serializerId;

		public Factory() {
			this(MascoTransport.SERIALIZER_BINARY);
		}

		public Factory(int serializerId) {
			this.serializerId = serializerId;
		}

		@Override
		public TProtocol getProtocol(TTransport trans) {
			if (trans instanceof MascoTransport) {
				return new MascoProtocol((MascoTransport)trans);
			} else {
				return new MascoProtocol(new MascoTransport(trans, serializerId));
			}
		}
	}

	@Override
	public MascoTransport getTransport() {
		return (MascoTransport)super.getTransport();
	}

	@Override
	public void writeMessageBegin(TMessage message) throws TException {
		getTransport().beforeWriteMessageBegin();
		resetCurrentSerializer();
		serializer.writeMessageBegin(message);
	}

	@Override
	public void writeMessageEnd() throws TException {
		try {
			serializer.writeMessageEnd();
		} finally {
			getTransport().afterWriteMessageEnd();
		}
	}

	@Override
	public void writeStructBegin(TStruct struct) throws TException {
		serializer.writeStructBegin(struct);
	}

	@Override
	public void writeStructEnd() throws TException {
		serializer.writeStructEnd();
	}

	@Override
	public void writeFieldBegin(TField field) throws TException {
		serializer.writeFieldBegin(field);
	}

	@Override
	public void writeFieldEnd() throws TException {
		serializer.writeFieldEnd();
	}

	@Override
	public void writeFieldStop() throws TException {
		serializer.writeFieldStop();
	}

	@Override
	public void writeMapBegin(TMap map) throws TException {
		serializer.writeMapBegin(map);
	}

	@Override
	public void writeMapEnd() throws TException {
		serializer.writeMapEnd();
	}

	@Override
	public void writeListBegin(TList list) throws TException {
		serializer.writeListBegin(list);
	}

	@Override
	public void writeListEnd() throws TException {
		serializer.writeListEnd();
	}

	@Override
	public void writeSetBegin(TSet set) throws TException {
		serializer.writeSetBegin(set);
	}

	@Override
	public void writeSetEnd() throws TException {
		serializer.writeSetEnd();
	}

	@Override
	public void writeBool(boolean b) throws TException {
		serializer.writeBool(b);
	}

	@Override
	public void writeByte(byte b) throws TException {
		serializer.writeByte(b);
	}

	@Override
	public void writeI16(short i16) throws TException {
		serializer.writeI16(i16);
	}

	@Override
	public void writeI32(int i32) throws TException {
		serializer.writeI32(i32);
	}

	@Override
	public void writeI64(long i64) throws TException {
		serializer.writeI64(i64);
	}

	@Override
	public void writeDouble(double dub) throws TException {
		serializer.writeDouble(dub);
	}

	@Override
	public void writeString(String str) throws TException {
		serializer.writeString(str);
	}

	@Override
	public void writeBinary(ByteBuffer buf) throws TException {
		serializer.writeBinary(buf);
	}

	@Override
	public TMessage readMessageBegin() throws TException {
		MascoTransport transport = getTransport();
		transport.beforeReadMessageBegin();
		resetCurrentSerializer();
		return serializer.readMessageBegin();
	}

	private TProtocol resetCurrentSerializer() throws TException {
		MascoTransport transport = getTransport();
		int serializerId = transport.getSerializerId();
		if (serializerId == MascoTransport.SERIALIZER_BINARY) {
			serializer = new TBinaryProtocol(transport);
		} else if (serializerId == MascoTransport.SERIALIZER_COMPACT) {
			serializer = new TCompactProtocol(transport);
		} else {
			throw new TProtocolException("unknown serializerId: " + serializerId);
		}
		return serializer;
	}

	@Override
	public void readMessageEnd() throws TException {
		try {
			serializer.readMessageEnd();
		} finally {
			getTransport().afterReadMessageEnd();
		}
	}

	@Override
	public TStruct readStructBegin() throws TException {
		return serializer.readStructBegin();
	}

	@Override
	public void readStructEnd() throws TException {
		serializer.readStructEnd();
	}

	@Override
	public TField readFieldBegin() throws TException {
		return serializer.readFieldBegin();
	}

	@Override
	public void readFieldEnd() throws TException {
		serializer.readFieldEnd();
	}

	@Override
	public TMap readMapBegin() throws TException {
		return serializer.readMapBegin();
	}

	@Override
	public void readMapEnd() throws TException {
		serializer.readMapEnd();
	}

	@Override
	public TList readListBegin() throws TException {
		return serializer.readListBegin();
	}

	@Override
	public void readListEnd() throws TException {
		serializer.readListEnd();
	}

	@Override
	public TSet readSetBegin() throws TException {
		return serializer.readSetBegin();
	}

	@Override
	public void readSetEnd() throws TException {
		serializer.readSetEnd();
	}

	@Override
	public boolean readBool() throws TException {
		return serializer.readBool();
	}

	@Override
	public byte readByte() throws TException {
		return serializer.readByte();
	}

	@Override
	public short readI16() throws TException {
		return serializer.readI16();
	}

	@Override
	public int readI32() throws TException {
		return serializer.readI32();
	}

	@Override
	public long readI64() throws TException {
		return serializer.readI64();
	}

	@Override
	public double readDouble() throws TException {
		return serializer.readDouble();
	}

	@Override
	public String readString() throws TException {
		return serializer.readString();
	}

	@Override
	public ByteBuffer readBinary() throws TException {
		return serializer.readBinary();
	}

}
