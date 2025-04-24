package com.massivecraft.factions.zcore.persist;

import com.google.gson.Gson;
import com.massivecraft.factions.zcore.util.DiscUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public abstract class EntityCollection<E extends Entity> {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	protected Map<String, E> entityIdMap;
	// These must be instantiated in order to allow for different configuration (orders, comparators etc)
	private Collection<E> entities;
	// If the entities are creative they will create a new instance if a non existent id was requested
	private boolean creative;
	// This is the auto increment for the primary key "id"
	private int nextId;
	// This ugly crap is necessary due to java type erasure
	private Class<E> entityClass;
	// Info on how to persist
	private Gson gson;
	private File file;
	// -------------------------------------------- //
	// DISC
	// -------------------------------------------- //
	// we don't want to let saveToDisc() run multiple iterations simultaneously
	private boolean saveIsRunning = false;

	// -------------------------------------------- //
	// CONSTRUCTORS
	// -------------------------------------------- //
	public EntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> entityIdMap, File file, Gson gson, boolean creative) {
		this.entityClass = entityClass;
		this.entities = entities;
		this.entityIdMap = entityIdMap;
		this.file = file;
		this.gson = gson;
		this.creative = creative;
		this.nextId = 1;

		EM.setEntitiesCollectionForEntityClass(this.entityClass, this);
	}

	public EntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> entityIdMap, File file, Gson gson) {
		this(entityClass, entities, entityIdMap, file, gson, false);
	}

	public boolean isCreative() {
		return creative;
	}

	public void setCreative(boolean creative) {
		this.creative = creative;
	}

	public abstract Type getMapType(); // This is special stuff for GSON.

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	// -------------------------------------------- //
	// GET
	// -------------------------------------------- //
	public Collection<E> getAll() {
		return entities;
	}

	public Map<String, E> getMap() {
		return this.entityIdMap;
	}

	public E getById(String id) {
		if (this.creative) {
			return this.getCreative(id);
		}
		return entityIdMap.get(id);
	}

	public E getCreative(String id) {
		E e = this.entityIdMap.get(id);
		if (e != null) {
			return e;
		}
		return this.create(id);
	}

	public boolean exists(String id) {
		return id != null && entityIdMap.get(id) != null;
	}

	public E getBestIdMatch(String pattern) {
		String id = TextUtil.getBestStartWithCI(this.entityIdMap.keySet(), pattern);
		if (id == null) {
			return null;
		}
		return this.entityIdMap.get(id);
	}

	// -------------------------------------------- //
	// CREATE
	// -------------------------------------------- //
	public synchronized E create() {
		return this.create(this.getNextId());
	}

	public synchronized E create(String id) {
		if (!this.isIdFree(id)) {
			return null;
		}

		E e = null;
		try {
			e = this.entityClass.newInstance();
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}

		e.setId(id);
		this.entities.add(e);
		this.entityIdMap.put(e.getId(), e);
		this.updateNextIdForId(id);
		return e;
	}

	// -------------------------------------------- //
	// ATTACH AND DETACH
	// -------------------------------------------- //
	public void attach(E entity) {
		if (entity.getId() != null) {
			return;
		}
		entity.setId(this.getNextId());
		this.entities.add(entity);
		this.entityIdMap.put(entity.getId(), entity);
	}

	public void detach(E entity) {
		entity.preDetach();
		this.entities.remove(entity);
		this.entityIdMap.remove(entity.getId());
		entity.postDetach();
	}

	public void detach(String id) {
		E entity = this.entityIdMap.get(id);
		if (entity == null) {
			return;
		}
		this.detach(entity);
	}

	public boolean attached(E entity) {
		return this.entities.contains(entity);
	}

	public boolean detached(E entity) {
		return !this.attached(entity);
	}

	public boolean saveToDisc() {
		if (saveIsRunning) {
			return true;
		}
		saveIsRunning = true;

		Map<String, E> entitiesThatShouldBeSaved = new HashMap<String, E>();
		for (E entity : this.entities) {
			if (entity.shouldBeSaved()) {
				entitiesThatShouldBeSaved.put(entity.getId(), entity);
			}
		}

		saveIsRunning = false;
		return this.saveCore(entitiesThatShouldBeSaved);
	}

	private boolean saveCore(Map<String, E> entities) {
		return DiscUtil.writeCatch(this.file, this.gson.toJson(entities));
	}

	public boolean loadFromDisc() {
		Map<String, E> id2entity = this.loadCore();
		if (id2entity == null) {
			return false;
		}
		this.entities.clear();
		this.entities.addAll(id2entity.values());
		this.entityIdMap.clear();
		this.entityIdMap.putAll(id2entity);
		this.fillIds();
		return true;
	}

	private Map<String, E> loadCore() {
		if (!this.file.exists()) {
			return new HashMap<String, E>();
		}

		String content = DiscUtil.readCatch(this.file);
		if (content == null) {
			return null;
		}

		Type type = this.getMapType();
		try {
			return this.gson.fromJson(content, type);
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "JSON error encountered loading \"" + file + "\": " + ex.getLocalizedMessage());

			// backup bad file, so user can attempt to recover something from it
			File backup = new File(file.getPath() + "_bad");
			if (backup.exists()) {
				backup.delete();
			}
			Bukkit.getLogger().log(Level.WARNING, "Backing up copy of bad file to: " + backup);
			file.renameTo(backup);

			return null;
		}
	}

	// -------------------------------------------- //
	// ID MANAGEMENT
	// -------------------------------------------- //
	public String getNextId() {
		while (!isIdFree(this.nextId)) {
			this.nextId += 1;
		}
		return Integer.toString(this.nextId);
	}

	public boolean isIdFree(String id) {
		return !this.entityIdMap.containsKey(id);
	}

	public boolean isIdFree(int id) {
		return this.isIdFree(Integer.toString(id));
	}

	protected synchronized void fillIds() {
		this.nextId = 1;
		for (Entry<String, E> entry : this.entityIdMap.entrySet()) {
			String id = entry.getKey();
			E entity = entry.getValue();
			entity.id = id;
			this.updateNextIdForId(id);
		}
	}

	protected synchronized void updateNextIdForId(int id) {
		if (this.nextId < id) {
			this.nextId = id + 1;
		}
	}

	protected void updateNextIdForId(String id) {
		try {
			int idAsInt = Integer.parseInt(id);
			this.updateNextIdForId(idAsInt);
		} catch (Exception ignored) {
		}
	}
}
