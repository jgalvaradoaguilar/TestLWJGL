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
public interface Decoder
{
	Identity decode(ByteBuffer buf, IO io) throws DecodeException;
}
