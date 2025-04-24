package us.zonix.core.shared.api.callback;

import java.util.logging.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
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
