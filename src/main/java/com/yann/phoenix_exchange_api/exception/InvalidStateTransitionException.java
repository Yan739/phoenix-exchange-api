package com.yann.phoenix_exchange_api.exception;

public class InvalidStateTransitionException extends BusinessException {

    public InvalidStateTransitionException(String message) {
        super(message, "INVALID_STATE_TRANSITION");
    }

    public InvalidStateTransitionException(String entityType, Object currentState, Object targetState) {
        super(String.format("Cannot transition %s from %s to %s",
                entityType, currentState, targetState), "INVALID_STATE_TRANSITION");
    }
}