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
import cufy.util.Reflect$;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An implementation ready for the interface {@link Bean}. Increases teh ability to store un-fielded properties.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 2 release (16-Feb-2020)
 * @since 09-Dec-2019
 */
public interface FullBean<K, V> extends Bean<K, V> {
	/**
	 * Get a bean for the instance give. The given bean will work as a remote for the given instance. The bean will work as if the given instance is a
	 * bean and returned bean is the actual instance.
	 *
	 * @param instance to get a bean for
	 * @param <K>      the type of keys in the returned bean
	 * @param <V>      the type of values in the returned bean
	 * @return a bean remote for the given instance
	 */
	static <K, V> FullBean<K, V> forInstance(Object instance) {
		return new FullBean<K, V>() {
			/**
			 * The properties set for this full bean.
			 */
			private BeanProperties<K, V> beanProperties;
			@Override
			public BeanProperties<K, V> getBeanProperties() {
				if (this.beanProperties == null) {
					this.beanProperties = new BeanProperties<>(this);
					Set<K> keys = new HashSet<>(10);

					Property property;
					K key;
					for (Field field : Reflect$.getAllFields(instance.getClass()))
						if ((property = field.getAnnotation(Property.class)) != null && !keys.contains(key = this.getKey(field)))
							this.beanProperties.add(new VirtualEntry<>(instance, this, field, property, key));
				}

				return this.beanProperties;
			}
			@Override
			public VirtualEntry<K, V> getEntry(K key) {
				BeanProperties<K, V> beanProperties = Objects.requireNonNull(this.getBeanProperties(), "getBeanProperties()");
				Field field = this.getField(key);

				if (field != null)
					return this.getEntry(key, field);

				return Object$.requireNonNullElseGet(beanProperties.get(key), () -> {
					VirtualEntry<K, V> entry = new VirtualEntry<>(instance, this, beanProperties, key);
					beanProperties.add(entry);
					return entry;
				});
			}
			@Override
			public VirtualEntry<K, V> getEntry(K key, Field field) {
				Objects.requireNonNull(field, "field");
				Property property = Objects.requireNonNull(field.getAnnotation(Property.class), "field.getAnnotation(Property.class)");
				BeanProperties<K, V> beanProperties = Objects.requireNonNull(this.getBeanProperties(), "getBeanProperties()");

				return Object$.requireNonNullElseGet(beanProperties.get(key), () -> {
					VirtualEntry<K, V> entry = new VirtualEntry<>(instance, this, beanProperties, field, property, key);
					beanProperties.add(entry);
					return entry;
				});
			}
			@Override
			public Field getField(K key) {
				BeanProperties<K, V> beanProperties = Objects.requireNonNull(this.getBeanProperties(), "getBeanProperties()");
				VirtualEntry<K, V> entry = beanProperties.get(key);

				if (entry == null) {
					for (Field field : Reflect$.getAllFields(instance.getClass()))
						if (field.isAnnotationPresent(Property.class) && Objects.equals(key, this.getKey(field)))
							return field;
					return null;
				} else {
					return entry.field;
				}
			}
		};
	}
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
