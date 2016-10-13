/*
 * MicroIdentity.java
 */
package identity;

/**
 * The very basic, very special implementation of <code>AbstractIdentity</code>.
 * 
 */
public final class MicroIdentity extends AbstractIdentity {

    private final byte mID;
    /**
     * create an array with all possible instances for get()
     * @see get
     */
    private final static MicroIdentity[] mInstances = new MicroIdentity[128];

    static {
        for (int i = 0; i < 128; i++) {
            mInstances[i] = new MicroIdentity((byte) i);
        }
    }
    /**
     * the static identity of the type
     * in contrast to this same constant in other identity classes, I don't use the value from <code>Types</code> here,
     * bc. this lead to some strange hen-egg-problems with static setup so that TYPE_ID appeared to be null
     * in certain conditions
     */
    public final static MicroIdentity TYPE_ID = get(0);

    /**
     * Creates a new instance of MicroIdentity.
     */
    public MicroIdentity(byte id) {
        assert id >= 0;
        if (id < 0) {
            throw new IllegalArgumentException("ID value must not be <0: " + id);
        }
        mID = id;
    }

    /**
     * implement Identity
     */
    public boolean isSameAs(Identity id) {
        return isSameAsCandidate(id) && ((MicroIdentity) id).getByte() == getByte();
    }

    public final byte getByte() {
        return mID;
    }

    /**
     * hashCode() has to be overloaded in a way that the actual
     * instance of the object does not matter - ONLY the content.
     */
    @Override
    public int hashCode() {
        return BASIC_HASHCODE ^ (int) getByte();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MicroIdentity other = (MicroIdentity) obj;
        if (this.mID != other.mID) {
            return false;
        }
        return true;
    }

    /**
     * static access to predefined objects
     */
    public final static MicroIdentity get(byte b) {
        return mInstances[b];
    }

    /**
     * static access to predefined objects
     */
    public final static MicroIdentity get(int b) {
        return mInstances[b];
    }

    @Override
    public String toString() {
        return "Micro:" + getByte();
    }
}
