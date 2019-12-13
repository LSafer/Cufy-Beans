/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *   shall mention that this file has been edited.
 *   By adding a new header (at the bottom of this header)
 *   with the word "Editor" on top of it.
 */

package cufy.beans;

import cufy.util.ObjectUtil;

import java.lang.reflect.Field;

/**
 * An implementation ready for the interface {@link Bean}. Increases teh ability to store un-fielded properties.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 1 release (09-Dec-2019)
 * @since 09-Dec-2019
 */
public interface FullBean<K, V> extends Bean<K, V> {
	@Override
	default VirtualEntry<K, V> getEntry(K key, Field field) {
		ObjectUtil.requireNonNull(field, "field");
		Property property = ObjectUtil.requireNonNull(field.getAnnotation(Property.class), "field.getAnnotation(Property.class)");
		Properties<K, V> properties = ObjectUtil.requireNonNull(this.getProperties(), "properties()");

		return ObjectUtil.requireNonNullElseGet(properties.get(key), () -> {
			VirtualEntry<K, V> entry = new VirtualEntry<>(this, properties, field, property, key);
			properties.add(entry);
			return entry;
		});
	}

	@Override
	default VirtualEntry<K, V> getEntry(K key) {
		Properties<K, V> properties = ObjectUtil.requireNonNull(this.getProperties(), "properties()");
		Field field = this.getField(key);

		return field != null ? this.getEntry(key, field) : ObjectUtil.requireNonNullElseGet(properties.get(key), () -> {
			VirtualEntry<K, V> entry = new VirtualEntry<>(this, properties, key);
			properties.add(entry);
			return entry;
		});
	}

	@Override
	default Field getField(K key) {
		Properties<K, V> properties = ObjectUtil.requireNonNull(this.getProperties(), "properties()");
		VirtualEntry<K, V> entry = properties.get(key);

		return entry == null ? Bean.super.getField(key) : entry.field;
	}

	@Override
	Bean.Properties<K, V> getProperties();
}
