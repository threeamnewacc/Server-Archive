package us.zonix.core.shared.api.request;

import java.util.Map;

public interface Request {

	String getPath();

	Map<String, Object> toMap();

}
