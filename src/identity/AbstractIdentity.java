package identity;

abstract public class AbstractIdentity implements Identity {

    protected final static int BASIC_HASHCODE = Identity.class.hashCode();

    /**
     * Utility:
     * Check wether the id is a candidate for being the same.
     * It checks wether classes do match.
     */
    protected final boolean isSameAsCandidate(Identity id) {
        //
        // IDs have to be of same (Java)class
        //
        return (id.getClass() == this.getClass());
    }

    /**
     * utility
     */
    public final boolean isSameAs(Identifiable idable) {
        return isSameAs(idable.getIdentity());
    }

    /**
     * Overload equals() operation the make identities equal in Java sense
     * when the also equal in AbstractIdentity sense.
     */
    @Override
    public boolean equals(Object o) {
        // Optimistic shortcut.
        if (this == o) {
            return true;
        }

        // Pessimistic shortcut
        if (null == o) {
            return false;
        }

        // content check
        if (o instanceof AbstractIdentity) {
            return isSameAs((AbstractIdentity) o);
        }

        // Last ressort: false!
        return false;
    }

    /**
     * hashCode() has to be overloaded in a way that the actual
     * instance of the object does not matter - ONLY the content.
     */
    @Override
    public int hashCode() {
        throw new RuntimeException("hashCode() not implemented in class " + this.getClass());
    }
}



