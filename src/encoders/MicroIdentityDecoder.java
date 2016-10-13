/*
 * MicroIdentityInput.java
 */
package encoders;

import identity.Identity;
import identity.MicroIdentity;
import java.nio.ByteBuffer;

/**
 *
 * @author Herkules
 */
public class MicroIdentityDecoder implements Decoder {

    /** one single instance should be sufficient */
    public final static MicroIdentityDecoder It = new MicroIdentityDecoder();

    /**
     * Creates a new instance of MicroIdentityInput.
     */
    private MicroIdentityDecoder() {
    }

    public Identity decode(ByteBuffer buf, IO io) throws DecodeException {
        byte b = buf.get();
        assert (b & 0x80) != 0;
        if ((b & 0x80) == 0) {
            throw new DecodeException();
        }

        return MicroIdentity.get((byte) (b & 0x7f));
    }

    /**
     * is the byte a MicroIdentity on its own? or just a classifier for some other Identity?
     */
    public boolean isMicro(byte b) {
        return (b & 0x80) != 0;
    }

    /**
     * Decode a MicroIdentity from a raw byte.
     * E.g. as a classifier for other Identity types.
     */
    public final MicroIdentity decodeByte(ByteBuffer buf) throws DecodeException {
        byte b = buf.get();
        assert (b & 0x80) == 0;
        if ((b & 0x80) != 0) {
            throw new DecodeException();
        }

        return MicroIdentity.get(b);
    }
}
