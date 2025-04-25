package us.zonix.core.api.request;

import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import us.zonix.core.rank.Rank;
import us.zonix.core.shared.api.request.Request;
import us.zonix.core.symbols.Symbol;
import us.zonix.core.util.MapUtil;

@RequiredArgsConstructor
public abstract class PlayerRequest implements Request {

	private final String path;

	@Override public String getPath() {
		return "/player/" + this.path;
	}

	@Override public Map<String, Object> toMap() {
		return null;
	}

	public static final class FetchByUuidRequest extends PlayerRequest {

		public FetchByUuidRequest(UUID uuid) {
			super("fetch_by_uuid/" + uuid.toString());
		}

	}

	public static final class FetchByNameRequest extends PlayerRequest {

		public FetchByNameRequest(String name) {
			super("fetch_by_name/" + name);
		}

	}

	public static final class FetchAltsRequest extends PlayerRequest {

		public FetchAltsRequest(UUID uuid) {
			super("fetch_by_ip/" + uuid.toString());
		}

	}

	public static final class SaveRequest extends PlayerRequest {

		private final UUID uuid;
		private final String name;
		private final Long lastLogin;
		private final String lastServer;
		private final String ip;
		private final Rank rank;
		private final Symbol symbol;
		private final String twoFactorAuthentication;
		private final boolean boughtSymbols;
		private final boolean authenticated;

		public SaveRequest(UUID uuid, String name, Long lastLogin, String lastServer, String ip, Rank rank, Symbol symbol, boolean boughtSymbols, String twoFactorAuthentication, boolean authenticated) {
			super("save");

			this.uuid = uuid;
			this.name = name;
			this.lastLogin = lastLogin;
			this.lastServer = lastServer;
			this.ip = ip;
			this.rank = rank;
			this.symbol = symbol;
			this.boughtSymbols = boughtSymbols;
			this.twoFactorAuthentication = twoFactorAuthentication;
			this.authenticated = authenticated;
		}

		@Override
		public Map<String, Object> toMap() {
			JsonObject data = new JsonObject();
			data.addProperty("uuid", this.uuid.toString());
			data.addProperty("name", this.name);
			data.addProperty("last_login", this.lastLogin);
			data.addProperty("last_server", this.lastServer);
			data.addProperty("ip", this.ip);
			data.addProperty("rank", this.rank.name());
			data.addProperty("symbol", this.symbol.name());
			data.addProperty("bought_symbols", this.boughtSymbols);
			data.addProperty("authenticated", this.authenticated);
			data.addProperty("two_factor_authentication", this.twoFactorAuthentication);

			return MapUtil.of(
					"data", data
			);
		}

	}

	public static final class UpdateRegisterRequest extends PlayerRequest {

		private final UUID uuid;
		private final String emailAddress;
		private final String confirmationId;
		private final boolean registered;

		public UpdateRegisterRequest(UUID uuid, String emailAddress, String confirmationId, boolean registered) {
			super("update-register");

			this.uuid = uuid;
			this.emailAddress = emailAddress;
			this.confirmationId = confirmationId;
			this.registered = registered;
		}

		@Override public Map<String, Object> toMap() {
			return MapUtil.of(
					"uuid", this.uuid,
					"emailAddress", this.emailAddress,
					"confirmationId", this.confirmationId,
					"registered", this.registered
			);
		}
	}

	public static final class UpdateRankRequest extends PlayerRequest {

		private final UUID uuid;
		private final Rank rank;

		public UpdateRankRequest(UUID uuid, Rank rank) {
			super("update-rank");

			this.uuid = uuid;
			this.rank = rank;
		}

		@Override
		public Map<String, Object> toMap() {
			return MapUtil.of(
					"uuid", this.uuid.toString(),
					"rank", this.rank.name()
			);
		}
	}

	public static final class UpdateAuthenticationRequest extends PlayerRequest {

		private final UUID uuid;
 		private final String twoFactorAuthentication;
 		private final boolean authenticated;

		public UpdateAuthenticationRequest(UUID uuid, String twoFactorAuthentication, boolean authenticated) {
			super("update-auth");

			this.uuid = uuid;
			this.twoFactorAuthentication = twoFactorAuthentication;
			this.authenticated = authenticated;
		}

		@Override public Map<String, Object> toMap() {
			return MapUtil.of(
					"uuid", this.uuid,
					"twoFactorAuthentication", this.twoFactorAuthentication,
					"authenticated", this.authenticated
			);
		}
 	}

	public static final class UpdateSymbolRequest extends PlayerRequest {

		private final UUID uuid;
		private final Symbol symbol;

		public UpdateSymbolRequest(UUID uuid, Symbol symbol) {
			super("update-symbol");

			this.uuid = uuid;
			this.symbol = symbol;
		}

		@Override
		public Map<String, Object> toMap() {
			return MapUtil.of(
					"uuid", this.uuid.toString(),
					"symbol", this.symbol.name()
			);
		}
	}

	public static final class UpdateBoughtSymbolsRequest extends PlayerRequest {

		private final UUID uuid;
		private final boolean boughtSymbols;

		public UpdateBoughtSymbolsRequest(UUID uuid, boolean boughtSymbols) {
			super("update-bought-symbols");

			this.uuid = uuid;
			this.boughtSymbols = boughtSymbols;
		}

		@Override
		public Map<String, Object> toMap() {
			return MapUtil.of(
					"uuid", this.uuid.toString(),
					"boughtSymbols", this.boughtSymbols
			);
		}
	}

}
