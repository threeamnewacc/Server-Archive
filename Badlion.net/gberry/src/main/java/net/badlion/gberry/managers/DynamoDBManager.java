package net.badlion.gberry.managers;

import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DynamoDBManager {


	/*
		Must be called ASYNC
	 */
	public static void batchPutItems(final String tableName, List<Item> items, final DynamoDB dynamoDB) throws InterruptedException {
		final Queue<Item> itemQueue = new ConcurrentLinkedQueue<>();

		if (items.size() <= 25) {
			// Single batch put items request
			TableWriteItems batchPlayerDataWrite = new TableWriteItems(tableName);
			batchPlayerDataWrite.withItemsToPut(items);

			DynamoDBManager.batchPutItems(dynamoDB, batchPlayerDataWrite);
		} else {
			// We need to break up our item into batches of 25 or less
			itemQueue.addAll(items);

			new BukkitRunnable() {
				@Override
				public void run() {

					if (itemQueue.isEmpty()) {
						this.cancel();
						return;
					}

					TableWriteItems batchPlayerDataWrite = new TableWriteItems(tableName);
					for (int i = 0; i < 25; i++) {
						if (itemQueue.isEmpty()) {
							break;
						}
						batchPlayerDataWrite.addItemToPut(itemQueue.remove());
					}

					try {
						DynamoDBManager.batchPutItems(dynamoDB, batchPlayerDataWrite);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.runTaskTimerAsynchronously(Gberry.plugin, 0, 15);
		}
	}


	private static void batchPutItems(DynamoDB dynamoDB, TableWriteItems tableWriteItems) throws InterruptedException {
		Map<String, List<WriteRequest>> unprocessed = null;
		int attempts = 0;
		do {
			if (attempts > 0) {
				Bukkit.getLogger().info("[DynamoDB] Batch Write Retry: " + attempts);

				// exponential backoff per DynamoDB recommendation
				Thread.sleep(((1 << attempts) * 1000) / 2);
			}

			// Cap the max sleep time
			if (attempts < 7) {
				attempts++;
			}
			BatchWriteItemOutcome outcome;
			if (unprocessed == null || unprocessed.size() > 0) {
				// handle initial request
				outcome = dynamoDB.batchWriteItem(tableWriteItems);
			} else {
				// handle unprocessed items
				outcome = dynamoDB.batchWriteItemUnprocessed(unprocessed);
			}
			Bukkit.getLogger().info("[DynamoDB] Batch Write Outcome: " + outcome);
			unprocessed = outcome.getUnprocessedItems();
			Bukkit.getLogger().info("[DynamoDB] Batch Write Unprocessed: " + unprocessed);

		} while (unprocessed.size() > 0);
	}
}
