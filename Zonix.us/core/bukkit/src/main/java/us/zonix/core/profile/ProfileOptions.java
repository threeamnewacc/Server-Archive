package us.zonix.core.profile;

import lombok.Data;

@Data
public class ProfileOptions {

	private boolean receivePrivateMessages = true;
	private boolean playPrivateMessageSound = true;
	private boolean socialSpy = false;

}
