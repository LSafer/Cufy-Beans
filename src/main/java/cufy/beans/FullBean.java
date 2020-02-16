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

import cufy.util.Object$;

import java.lang.reflect.Field;
import java.util.Objects;

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
	default VirtualEntry<K, V> getEntry(K key) {
		BeanProperties<K, V> beanProperties = Objects.requireNonNull(this.getBeanProperties(), "getBeanProperties()");
		Field field = this.getField(key);

		if (field != null)
			return this.getEntry(key, field);

		return Object$.requireNonNullElseGet(beanProperties.get(key), () -> {
			VirtualEntry<K, V> entry = new VirtualEntry<>(this, this, beanProperties, key);
			beanProperties.add(entry);
			return entry;
		});
	}
	@Override
	default VirtualEntry<K, V> getEntry(K key, Field field) {
		Objects.requireNonNull(field, "field");
		Property property = Objects.requireNonNull(field.getAnnotation(Property.class), "field.getAnnotation(Property.class)");
		BeanProperties<K, V> beanProperties = Objects.requireNonNull(this.getBeanProperties(), "getBeanProperties()");

		return Object$.requireNonNullElseGet(beanProperties.get(key), () -> {
			VirtualEntry<K, V> entry = new VirtualEntry<>(this, this, beanProperties, field, property, key);
			beanProperties.add(entry);
			return entry;
		});
	}
	@Override
	default Field getField(K key) {
		BeanProperties<K, V> beanProperties = Objects.requireNonNull(this.getBeanProperties(), "getBeanProperties()");
		VirtualEntry<K, V> entry = beanProperties.get(key);

		return entry == null ? Bean.super.getField(key) : entry.field;
	}
}
