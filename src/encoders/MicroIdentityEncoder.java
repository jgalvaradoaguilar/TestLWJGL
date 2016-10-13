/*
 * MicroIdentityEncoder.java
 */
package encoders;

import identity.Identity;
import identity.MicroIdentity;
import java.nio.ByteBuffer;

/**
 *
 * @author Herkules
 */
public class MicroIdentityEncoder implements Encoder {

    /** one single instance should be sufficient */
    public final static MicroIdentityEncoder It = new MicroIdentityEncoder();

    static {
        IO.registerDecoder (MicroIdentity.TYPE_ID, MicroIdentityDecoder.It);

        IO.registerEncoder(MicroIdentity.class, MicroIdentityEncoder.It);
    }

    /**
     * Creates a new instance of MicroIdentityEncoder.
     */
    private MicroIdentityEncoder() {
    }

    /**
     * set highest bit as a marker for the microID and write that one single byte
     */
    public void encode(ByteBuffer buf, Identity id, IO io) throws EncodeException {
        if (!(id instanceof MicroIdentity)) {
            throw new EncodeException();
        }
        byte b = ((MicroIdentity) id).getByte();
        b |= 0x80;
        buf.put(b);
    }

    /**
     * Write a microID as a classifier for something else to a ByteBuffer.
     */
    public final void encodeByte(ByteBuffer buf, MicroIdentity id) throws EncodeException {
        buf.put(id.getByte());
    }
}
