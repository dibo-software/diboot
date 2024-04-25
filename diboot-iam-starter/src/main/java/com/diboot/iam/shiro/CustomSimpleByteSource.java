package com.diboot.iam.shiro;

import org.apache.shiro.lang.codec.Base64;
import org.apache.shiro.lang.codec.CodecSupport;
import org.apache.shiro.lang.codec.Hex;
import org.apache.shiro.lang.util.ByteSource;

import java.io.File;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class CustomSimpleByteSource implements ByteSource, Serializable {
    @Serial
    private static final long serialVersionUID = 5175082362119580768L;

    private byte[] bytes;
    private String cachedHex;
    private String cachedBase64;

    public CustomSimpleByteSource() {
    }

    public CustomSimpleByteSource(byte[] bytes) {
        this.bytes = bytes;
    }

    public CustomSimpleByteSource(char[] chars) {
        this.bytes = CodecSupport.toBytes(chars);
    }

    public CustomSimpleByteSource(String string) {
        this.bytes = CodecSupport.toBytes(string);
    }

    public CustomSimpleByteSource(ByteSource source) {
        this.bytes = source.getBytes();
    }

    public CustomSimpleByteSource(File file) {
        this.bytes = (new CustomSimpleByteSource.BytesHelper()).getBytes(file);
    }

    public CustomSimpleByteSource(InputStream stream) {
        this.bytes = (new CustomSimpleByteSource.BytesHelper()).getBytes(stream);
    }

    public static boolean isCompatible(Object o) {
        return o instanceof byte[] || o instanceof char[] || o instanceof String || o instanceof ByteSource || o instanceof File || o instanceof InputStream;
    }

    @Override
    public byte[] getBytes() {
        return this.bytes != null ? Arrays.copyOf(this.bytes, this.bytes.length) : new byte[0];
    }


    @Override
    public String toHex() {
        if (this.cachedHex == null) {
            this.cachedHex = Hex.encodeToString(this.getBytes());
        }
        return this.cachedHex;
    }

    @Override
    public String toBase64() {
        if (this.cachedBase64 == null) {
            this.cachedBase64 = Base64.encodeToString(this.getBytes());
        }

        return this.cachedBase64;
    }

    @Override
    public boolean isEmpty() {
        return this.bytes == null || this.bytes.length == 0;
    }

    @Override
    public String toString() {
        return this.toBase64();
    }

    @Override
    public int hashCode() {
        return this.bytes != null && this.bytes.length != 0 ? Arrays.hashCode(this.bytes) : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ByteSource bs) {
            return Arrays.equals(this.getBytes(), bs.getBytes());
        } else {
            return false;
        }
    }

    private static final class BytesHelper extends CodecSupport {
        private BytesHelper() {
        }

        public byte[] getBytes(File file) {
            return this.toBytes(file);
        }

        public byte[] getBytes(InputStream stream) {
            return this.toBytes(stream);
        }
    }

}
