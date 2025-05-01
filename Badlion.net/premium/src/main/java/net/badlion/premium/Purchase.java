package net.badlion.premium;

import org.joda.time.DateTime;

import java.util.UUID;

public class Purchase {

		private UUID uuid;
		private String transactionId;
		private String username;
		private double price;
		private String currency;
		private DateTime timestamp;
		private String email;
		private String ip;
		private int packageId;
		private double packagePrice;
		private DateTime packageExpirationDate;

		public Purchase(String transactionId, UUID uuid, String username, double price, String currency,
		                DateTime timestamp, String email, String ip, int packageId, double packagePrice,
		                DateTime packageExpirationDate) {
			this.uuid = uuid;
			this.transactionId = transactionId;
			this.username = username;
			this.price = price;
			this.currency = currency;
			this.timestamp = timestamp;
			this.email = email;
			this.ip = ip;
			this.packageId = packageId;
			this.packagePrice = packagePrice;
			this.packageExpirationDate = packageExpirationDate;
		}

		public UUID getUuid() {
			return uuid;
		}

		public DateTime getPackageExpirationDate() {
			return packageExpirationDate;
		}

		public DateTime getTimestamp() {
			return timestamp;
		}

		public double getPackagePrice() {
			return packagePrice;
		}

		public double getPrice() {
			return price;
		}

		public int getPackageId() {
			return packageId;
		}

		public String getIp() {
			return ip;
		}

		public String getCurrency() {
			return currency;
		}

		public String getEmail() {
			return email;
		}

		public String getTransactionId() {
			return transactionId;
		}

		public String getUsername() {
			return username;
		}
	}