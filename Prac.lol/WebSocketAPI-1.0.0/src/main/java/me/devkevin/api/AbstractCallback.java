package me.devkevin.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.logging.Logger;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:35
 */
@Getter @RequiredArgsConstructor
public abstract class AbstractCallback implements Callback, ErrorCallback {
    private final String errorMessage;
    private boolean errorCalled = false;

    @Override
    public void onError(String message) {
        this.errorCalled = true;
        if (!this.errorMessage.isEmpty()) {
            Logger.getGlobal().severe(this.errorMessage);
        }

        Logger.getGlobal().severe(message);
    }

    public void throwException() throws Exception {
        if (this.errorCalled) {
            throw new Exception(this.errorMessage);
        }
    }
}
