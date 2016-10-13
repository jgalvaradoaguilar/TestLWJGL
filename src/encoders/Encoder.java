/*
 * Decoder.java
 */
package encoders;

import identity.Identity;
import java.nio.ByteBuffer;

/**
 *
 * @author Herkules
 */
public interface Encoder
{
	void encode(ByteBuffer buf,Identity id, IO io) throws EncodeException;
}
