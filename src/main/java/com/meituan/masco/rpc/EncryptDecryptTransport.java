package com.meituan.masco.rpc;

import com.meituan.masco.utils.RSATool;
import org.apache.commons.lang3.CharSet;
import org.apache.thrift.TByteArrayOutputStream;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import javax.sql.rowset.CachedRowSet;
import java.io.UnsupportedEncodingException;

/**
 * Created by lihuihui on 14-12-16.
 */
public class EncryptDecryptTransport extends TTransport {

    protected static final int DEFAULT_MAX_LENGTH = 16384000;

    private int maxLength;

    /**
     * Underlying transport
     */
    private TTransport transport = null;

    /**
     * Buffer for output
     */
    private final TByteArrayOutputStream writeBuffer =
            new TByteArrayOutputStream(1024);

    /**
     * Buffer for input
     */
    private volatile TMemoryInputTransport readBuffer= new TMemoryInputTransport(new byte[0]);

    private final String token="abcdef";

    private final byte[] i32buf = new byte[4];
    private final byte[] sign32buf = new byte[4];

    public EncryptDecryptTransport(TTransport transport) {
        this.transport = transport;
        maxLength = DEFAULT_MAX_LENGTH;
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

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        if (readBuffer.getBytesRemainingInBuffer()==0) try {
            readFrame();
        } catch (Exception e) {
           new TTransportException("read error!");
        }
        return readBuffer.read(buf, off, len);
     }


    public void write(byte[] buf, int off, int len) throws TTransportException {
        writeBuffer.write(buf, off, len);
    }

    @Override
    public void flush() throws TTransportException {
        byte[] buf = writeBuffer.get();
        byte[] signData=new byte[0];
        try {
            signData=RSATool.signature(token);
            buf=RSATool.encrypt(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int len = buf.length;
        int signLen=signData.length;
        writeBuffer.reset();
        encodeFrameSize(len, i32buf);
        encodeFrameSize(signLen, sign32buf);
        transport.write(i32buf, 0, 4);
        transport.write(sign32buf, 0, 4);
        transport.write(signData, 0, signLen);
        transport.write(buf, 0, len);
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

    private void readFrame() throws Exception {
        transport.readAll(i32buf, 0, 4);
        int size = decodeFrameSize(i32buf);
        transport.readAll(sign32buf, 0, 4);
        int singnSize = decodeFrameSize(sign32buf);


        if (size < 0||singnSize<0) {
            throw new TTransportException("Read a negative frame size (" + size + ")!");
        }

        if (size > maxLength||singnSize>maxLength) {
            throw new TTransportException("Frame size (" + size + ") larger than max length (" + maxLength + ")!");
        }

        byte[] singBuff = new byte[singnSize];
        transport.readAll(singBuff, 0, singnSize);

        if(!RSATool.byte2HexStr(singBuff).equals(RSATool.byte2HexStr(RSATool.signature(token)))) {
            throw new TTransportException("sign fail!");
        }

        byte[] buff = new byte[size];
        transport.readAll(buff, 0, size);
        buff=RSATool.decrypt(buff);
        readBuffer.reset(buff);

    }

    public static final void encodeFrameSize(final int frameSize, final byte[] buf) {
        buf[0] = (byte)(0xff & (frameSize >> 24));
        buf[1] = (byte)(0xff & (frameSize >> 16));
        buf[2] = (byte)(0xff & (frameSize >> 8));
        buf[3] = (byte)(0xff & (frameSize));
    }

    public static final int decodeFrameSize(final byte[] buf) {
        return
                ((buf[0] & 0xff) << 24) |
                        ((buf[1] & 0xff) << 16) |
                        ((buf[2] & 0xff) <<  8) |
                        ((buf[3] & 0xff));
    }
}
