package me.devkevin.api;

import com.google.gson.JsonElement;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:39
 */
public interface RequestProcessor {

    /**
     * Whether or not a request should be allowed to be sent.
     * <p></p>
     *
     * @return - If the request should be sent.
     */
    boolean shouldSend();

    /**
     * Send Http Request with a callback on a separate thread or the main thread.
     * <p></p>
     *
     * @param request  - Request to send
     */
    void sendRequestAsync(Request request);

    /**
     * Send Http Request with a callback on a separate thread or the main thread.
     * <p></p>
     *
     * @param request  - Request to send
     * @param callback - Callback to call with the response
     */
    void sendRequestAsync(Request request, Callback callback);

    /**
     * Send Http Request with a callback on a separate thread or the main thread.
     * <p></p>
     *
     * @param request  - Request to send
     * @param callback - Callback to call with the response
     */
    JsonElement sendRequest(Request request, Callback callback);

    /**
     * Send Http Request with a callback async
     * <p></p>
     *
     * @param request  - Request to send
     * @param callback - Callback to call with the response
     * @param async    - Whether the request should be sent on a separate thread.
     */
    void sendRequest(Request request, Callback callback, boolean async);

    /**
     * Send Http Request on the current thread
     * <p></p>
     *
     * @param request - Request to send
     * @return - Response from the request encoded in a Json format.
     */
    JsonElement sendRequest(Request request);

	/*
	Task Managers
	 */

    /**
     * Run provided task on a separate thread.
     * <p></p>
     *
     * @param runnable - Task to run
     */
    void runTaskAsynchronously(Runnable runnable);

    /**
     * Run provided task on the main thread.
     * <p></p>
     *
     * @param runnable - Task to run
     */
    void runTask(Runnable runnable);
}
