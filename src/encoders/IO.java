/*
 * Input.java
 */
package encoders;

import identity.Identity;
import identity.MicroIdentity;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Herkules
 */
public class IO {

    private final Map<Identity, Decoder> mDecoder = new HashMap<Identity, Decoder>();
    private final Map<Class, Encoder> mEncoder = new HashMap<Class, Encoder>();

    /** single instance should be sufficient */
    public final static IO It = new IO();

    public final static void registerEncoder(Class classToEncoder, Encoder encoder)
    {
         It.mEncoder.put(classToEncoder, encoder);
    }

    public final static void registerDecoder(Identity identityToDecode, Decoder decoder)
    {
         It.mDecoder.put(identityToDecode, decoder);
    }

    /**
     * ctor
     */
    public IO() {
    }

    public Identity decode(ByteBuffer buf) throws DecodeException {
        // get a byte w/o stepping position
        byte b = buf.get(buf.position());
        Identity idtypeid;

        //
        // special treatment:
        // If highest bit is set, treat the byte as a MicroIdentity itself.
        // Otherwise it is just a byte that describes what follows (that can be
        // expressed as a MicroIdentity again).
        // This way, 0x81 means the MicroIdentity(1). 0x0101 does mean the same
        // (for 0x01 is known to be Types.MICROIDENTITY.getByte())!
        //
        if (MicroIdentityDecoder.It.isMicro(b)) {
            idtypeid = MicroIdentity.TYPE_ID;
        } else {
            idtypeid = MicroIdentity.get(b);
        }

        Decoder decoder = mDecoder.get(idtypeid);
        if (null == decoder) {
            throw new DecodeException("Unexpected identity code: " + b);
        }
        return decoder.decode(buf, this);
    }

    public void encode(ByteBuffer buf, Identity id) throws EncodeException {
        mEncoder.get(id.getClass()).encode(buf, id, this);
    }
}
