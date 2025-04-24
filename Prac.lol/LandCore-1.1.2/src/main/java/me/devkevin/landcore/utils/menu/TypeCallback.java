package me.devkevin.landcore.utils.menu;

import java.io.Serializable;

public interface TypeCallback<T> extends Serializable {

	void callback(T data);

}