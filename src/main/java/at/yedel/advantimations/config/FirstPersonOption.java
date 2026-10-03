package at.yedel.advantimations.config;



public interface FirstPersonOption {
    boolean shouldApplyInFirstPerson();
    default <T> T getFirstPersonResult(T originalValue, T newValue) {
        if (shouldApplyInFirstPerson()) {
            return newValue;
        }
        return originalValue;
    }
}
