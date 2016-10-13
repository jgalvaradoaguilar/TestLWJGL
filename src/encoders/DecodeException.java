/*
 * DecodeException.java
 */

package encoders;

/**
 *
 * @author Herkules
 */
public class DecodeException extends Exception
{
	/**
	 * Creates a new instance of DecodeException.
	 */
	public DecodeException()
	{
	}
	
	public DecodeException(Throwable cause)
	{
		super(cause);
	}

	public DecodeException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DecodeException(String message)
	{
		super(message);
	}
}
