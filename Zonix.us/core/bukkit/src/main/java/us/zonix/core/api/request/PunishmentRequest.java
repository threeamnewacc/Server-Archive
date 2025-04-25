package us.zonix.core.api.request;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import us.zonix.core.shared.api.request.Request;
import us.zonix.core.util.MapUtil;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class PunishmentRequest implements Request {

    private final String path;

    @Override
    public String getPath() {
        return "/punishment/" + this.path;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    public static final class FetchByUuidRequest extends PunishmentRequest {

        public FetchByUuidRequest(UUID uuid) {
            super("fetch_by_uuid/" + uuid.toString());
        }

    }

    public static final class FetchByStaffUuidRequest extends PunishmentRequest {

        public FetchByStaffUuidRequest(UUID uuid) {
            super("fetch_by_staff_uuid/" + uuid.toString());
        }

    }

    public static final class InsertRequest extends PunishmentRequest {

        private JsonObject data;

        public InsertRequest(JsonObject data) {
            super("insert");

            this.data = data;
        }

        @Override
        public Map<String, Object> toMap() {
            return MapUtil.of(
                    "data", this.data
            );
        }

    }

    public static final class RemoveRequest extends PunishmentRequest {

        private JsonObject data;

        public RemoveRequest(JsonObject data) {
            super("remove");

            this.data = data;
        }

        public Map<String, Object> toMap() {
            return MapUtil.of(
                    "data", this.data
            );
        }

    }

}
