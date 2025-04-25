package us.zonix.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "client_cosmetics")
public class ClientCosmetics {

	@Id private String uuid;

	private String activeCape;
	private String cosmetics;

	private boolean activeWings;
	private boolean hasWings;

	public void addCosmetic(String cosmetic) {
		if (this.hasCosmetic(cosmetic)) {
			return;
		}

		if (this.cosmetics == null) {
			this.cosmetics = new JsonArray().toString();
		}

		JsonArray array = new JsonParser().parse(this.cosmetics).getAsJsonArray();
		array.add(cosmetic);
		this.cosmetics = array.toString();
	}

	public void removeCosmetic(String cosmetic) {
		if (this.cosmetics == null) {
			this.cosmetics = new JsonArray().toString();
		}

		JsonArray array = new JsonParser().parse(this.cosmetics).getAsJsonArray();
		array.remove(new JsonPrimitive(cosmetic));
		this.cosmetics = array.toString();
	}

	public boolean hasCosmetic(String cosmetic) {
		if (this.cosmetics == null) {
			this.cosmetics = new JsonArray().toString();
		}

		JsonArray array = new JsonParser().parse(this.cosmetics).getAsJsonArray();
		return array.contains(new JsonPrimitive(cosmetic));
	}

}
