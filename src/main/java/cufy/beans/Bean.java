/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *  shall mention that this file has been edited.
 *  By adding a new header (at the bottom of this header)
 *  with the word "Editor" on top of it.
 */
package cufy.beans;

import cufy.lang.Caster;
import cufy.lang.Global;
import cufy.lang.IllegalAnnotationException;
import cufy.lang.TypedValue;
import cufy.lang.alter.Cast;
import cufy.util.ObjectUtil;
import cufy.util.ReflectUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;

/**
 * An interface changes the act of the fields of the class implementing it. The classes that implement this interface change to be used as a map
 * (JavaScript like). All of the fields of that class will be like a {@link Map.Entry} holder (aka {@link Property} like on beans). Fields not
 * annotated with {@link Property} will be excluded.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 18 release (07-Dec-2019)
 * @since 11-Jun-19
 */
@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
public interface Bean<K, V> extends Map<K, V> {
	/**
	 * A reference represent 'as if not annotated'.
	 */
	String AINA = "aina";
	/**
	 * A reference represents casting an object.
	 */
	String CAST = "cast";
	/**
	 * A reference represents {@link Property#defaultValue()}.
	 */
	String DEFAULT = "defaultValue";
	/**
	 * A reference represents ignoration.
	 */
	String IGNORE = "ignore";
	/**
	 * A reference represents the value 'null'.
	 */
	String NULL = "null";
	/**
	 * A reference represents throwing an exception.
	 */
	String THROW = "throw";

	@Override
	default int size() {
		//Stolen from java.util.AbstractMap.class 😛
		return this.entrySet().size();
	}

	@Override
	default boolean isEmpty() {
		//Stolen from java.util.AbstractMap.class 😛
		return this.size() == 0;
	}

	@Override
	default boolean containsKey(Object key) {
		return this.getEntry((K) key) != null;
	}

	@Override
	default boolean containsValue(Object value) {
		if (value == null) {
			for (Map.Entry<K, V> entry : this.entrySet())
				if (entry.getValue() == null)
					return true;
		} else {
			int hashCode = value.hashCode();
			V value1;
			for (Map.Entry<K, V> entry : this.entrySet())
				if ((value1 = entry.getValue()) != null && hashCode == value1.hashCode() && value.equals(value1))
					return true;
		}

		return false;
	}

	@Override
	default V get(Object key) {
		VirtualEntry<K, V> entry = this.getEntry((K) key);
		return entry == null ? null : entry.getValue();
	}

	@Override
	default V put(K key, V value) {
		VirtualEntry<K, V> entry = this.getEntry(key);
		if (entry == null)
			throw new IllegalArgumentException("Cannot store " + key);
		return entry.setValue(value);
	}

	@Override
	default V remove(Object key) {
		VirtualEntry<K, V> entry = this.getEntry((K) key);
		return entry == null ? null : entry.remove();
	}

	@Override
	default void putAll(Map<? extends K, ? extends V> map) {
		ObjectUtil.requireNonNull(map, "map");
		map.forEach(this::put);
	}

	@Override
	default void clear() {
		//Stolen from java.util.AbstractMap.class 😛
		this.entrySet().clear();
	}

	@Override
	default Set<K> keySet() {
		//Stolen from java.util.AbstractMap.class 😛
		return new AbstractSet<K>() {
			@Override
			public Iterator<K> iterator() {
				return new Iterator<K>() {
					/**
					 * The iterator of the entry set.
					 */
					private Iterator<Entry<K, V>> iterator = Bean.this.entrySet().iterator();

					@Override
					public boolean hasNext() {
						return this.iterator.hasNext();
					}

					@Override
					public K next() {
						return this.iterator.next().getKey();
					}

					@Override
					public void remove() {
						this.iterator.remove();
					}
				};
			}

			@Override
			public int size() {
				return Bean.this.size();
			}

			@Override
			public boolean isEmpty() {
				return Bean.this.isEmpty();
			}

			@Override
			public boolean contains(Object key) {
				return Bean.this.containsKey(key);
			}

			@Override
			public void clear() {
				Bean.this.clear();
			}
		};
	}

	@Override
	default Collection<V> values() {
		//Stolen from java.util.AbstractMap.class 😛
		return new AbstractCollection<V>() {
			public Iterator<V> iterator() {
				return new Iterator<V>() {
					/**
					 * The iterator of the entry set.
					 */
					private Iterator<Entry<K, V>> iterator = Bean.this.entrySet().iterator();

					@Override
					public boolean hasNext() {
						return this.iterator.hasNext();
					}

					@Override
					public V next() {
						return this.iterator.next().getValue();
					}

					@Override
					public void remove() {
						this.iterator.remove();
					}
				};
			}

			public int size() {
				return Bean.this.size();
			}

			public boolean isEmpty() {
				return Bean.this.isEmpty();
			}

			public boolean contains(Object v) {
				return Bean.this.containsValue(v);
			}

			public void clear() {
				Bean.this.clear();
			}
		};
	}

	@Override
	default Set<Map.Entry<K, V>> entrySet() {
		return (Set<Map.Entry<K, V>>) (Object) this.getProperties();
	}

	/**
	 * Get a virtual entry as the key is the given key and the field is the given field.
	 *
	 * @param key   the key for the targeted virtual entry
	 * @param field the field holding the targeted virtual entry
	 * @return a virtual entry for the given params.
	 * @throws NullPointerException if the given field is null or the given field not annotated with {@link Property}
	 */
	default VirtualEntry<K, V> getEntry(K key, Field field) {
		ObjectUtil.requireNonNull(field, "field");
		Property property = ObjectUtil.requireNonNull(field.getAnnotation(Property.class), "field.getAnnotation(Bean.Property.class)");

		return new VirtualEntry<>(this, field, property, key);
	}

	/**
	 * Get a virtual entry as the key is the given key.
	 *
	 * @param key the key for the targeted virtual entry
	 * @return a virtual entry for the given key
	 */
	default VirtualEntry<K, V> getEntry(K key) {
		Field field = this.getField(key);
		return field == null ? null : this.getEntry(key, field);
	}

	/**
	 * Get a virtual entry as the field is the given field.
	 *
	 * @param field the field holding the targeted virtual entry
	 * @return a virtual entry for the given field.
	 * @throws NullPointerException if the given field is null or the given field don't have a property
	 */
	default VirtualEntry<K, V> getEntry(Field field) {
		ObjectUtil.requireNonNull(field, "field");
		return this.getEntry(this.getKey(field), field);
	}

	/**
	 * Get a property-field for the passed key. Or null if not found.
	 *
	 * @param key to get the field for
	 * @return a field that responsible for storing the value for the presented key
	 */
	default Field getField(K key) {
		if (key == null) {
			for (Field field : ReflectUtil.allFields(this.getClass()))
				if (field.isAnnotationPresent(Property.class) && this.getKey(field) == null)
					return field;
		} else {
			int hashCode = key.hashCode();
			K key1;
			for (Field field : ReflectUtil.allFields(this.getClass()))
				if (field.isAnnotationPresent(Property.class) &&
					(key1 = this.getKey(field)) != null && hashCode == key1.hashCode() && key.equals(key1))
					return field;
		}
		return null;
	}

	/**
	 * Get the key of the given field.
	 *
	 * @param field to get the key of
	 * @return the key of the passed field
	 * @throws NullPointerException if the given field is null or the given field not annotated with {@link Property}
	 */
	default K getKey(Field field) {
		ObjectUtil.requireNonNull(field, "field");
		Property property = ObjectUtil.requireNonNull(field.getAnnotation(Property.class), "property");
		TypedValue key;

		if (field.isAnnotationPresent(Property.class))
			if (!(key = property.key()).isnull())
				return (K) TypedValue.util.construct(key);

		return (K) field.getName();
	}

	/**
	 * A map of the property fields of this.
	 *
	 * @return a map of property fields of this
	 */
	default Properties<K, V> getProperties() {
		Properties<K, V> properties = new Properties<>(this);
		Set<K> keys = new HashSet<>(10);

		Property property;
		for (Field field : ReflectUtil.allFields(this.getClass()))
			if ((property = field.getAnnotation(Property.class)) != null)
				properties.add(new VirtualEntry<>(this, field, property, this.getKey(field)));

		return properties;
	}

	/**
	 * Defines whether the annotated field is an entry-field or not.
	 *
	 * @apiNote protected/private fields can't be used as a property (security issues).
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Property {
		/**
		 * The caster that will be used to cast a value to be set. When that value don't match the type of the annotated field.
		 *
		 * @return the caster of the annotated field
		 * @apiNote not changing it may occur some exceptions. Because the default caster is an abstract class an not suppose to be used as a caster!
		 */
		Class<? extends Caster> caster() default Cast.class;

		/**
		 * The default value of this property.
		 *
		 * @return the default value of this property
		 */
		TypedValue defaultValue() default @TypedValue(value = "", isnull = true);

		/**
		 * The key of the annotated entry-field. This will override the default key.
		 *
		 * @return the key of the annotated entry-field
		 */
		TypedValue key() default @TypedValue(value = "", isnull = true);

		/**
		 * What to do when the value is equals to null. And a get call invoked to the property.
		 *
		 * <ul>
		 *     Expected:
		 *     <li>{@link #AINA} as if not annotated</li>
		 *     <li>{@link #DEFAULT} return the default value</li>
		 *     <li>{@link #NULL} return null</li>
		 *     <li>{@link #THROW} throw {@link java.lang.IllegalArgumentException}</li>
		 * </ul>
		 *
		 * @return what to do when trying to get the value but it equals to null
		 * @throws IllegalAnnotationException when set to unexpected value
		 */
		String onGetNull() default NULL;

		/**
		 * What to do when the annotated field to be removed.
		 *
		 * <ul>
		 *     Expected:
		 *     <li>{@link #AINA} as if not annotated</li>
		 *     <li>{@link #DEFAULT} set to the default value</li>
		 *     <li>{@link #IGNORE} ignore the call</li>
		 *     <li>{@link #NULL} nullify the field</li>
		 *     <li>{@link #THROW} throw {@link UnsupportedOperationException}</li>
		 * </ul>
		 *
		 * @return what to do when the annotated field to be removed
		 * @throws IllegalAnnotationException when set to unexpected value
		 */
		String onRemove() default NULL;

		/**
		 * What to do when the annotated field's value to be set to different type.
		 *
		 * <ul>
		 *     Expected:
		 *     <li>{@link #AINA} as if not annotated</li>
		 *     <li>{@link #CAST} cast the value</li>
		 *     <li>{@link #DEFAULT} set to the default value</li>
		 *     <li>{@link #IGNORE} ignore the call</li>
		 *     <li>{@link #NULL} nullify the field</li>
		 *     <li>{@link #THROW} throw {@link IllegalArgumentException}</li>
		 * </ul>
		 *
		 * @return what to do when the annotated field's value to be set to different type.
		 * @throws IllegalAnnotationException when set to unexpected value
		 */
		String onTypeMismatch() default THROW;

		/**
		 * Override the type of the field.
		 *
		 * @return the type of the annotated field's property
		 */
		Class<?> type() default Property.class;
	}

	/**
	 * A properties (virtual entries) collection.
	 */
	class Properties<K, V> extends HashSet<VirtualEntry<K, V>> {
		/**
		 * The bean this object is responsible of.
		 */
		final protected Bean<K, V> bean;

		/**
		 * Constructs a new, empty set; the backing HashMap instance has default initial capacity (16) and load factor (0.75).
		 *
		 * @param bean the bean of this
		 */
		public Properties(Bean<K, V> bean) {
			this.bean = bean;
		}

		@Override
		public Iterator<VirtualEntry<K, V>> iterator() {
			return new Iterator<VirtualEntry<K, V>>() {
				/**
				 * The iterator of the entry set.
				 */
				private Iterator<VirtualEntry<K, V>> iterator = Properties.super.iterator();
				/**
				 * Last key given by {@link #next()}.
				 */
				private K lastKey;

				@Override
				public boolean hasNext() {
					return this.iterator.hasNext();
				}

				@Override
				public VirtualEntry<K, V> next() {
					VirtualEntry<K, V> entry = this.iterator.next();
					this.lastKey = entry.getKey();
					return entry;
				}

				@Override
				public void remove() {
					if (this.lastKey == null)
						throw new IllegalStateException();
					Properties.this.bean.remove(this.lastKey);
				}
			};
		}

		@Override
		public boolean add(VirtualEntry<K, V> property) {
			ObjectUtil.requireNonNull(property, "property");
			if (!super.add(property))
				throw new IllegalStateException("The property " + property.getKey() + " stored twice!");
			return false;
		}

		@Override
		public void clear() {
			this.forEach(VirtualEntry::remove);
		}

		/**
		 * Get the virtual-entry of the given key exist in this set.
		 *
		 * @param key the key to search for
		 * @return an entry of the given key
		 */
		protected VirtualEntry<K, V> get(K key) {
			if (key == null) {
				for (VirtualEntry<K, V> entry : this)
					if (entry.getKey() == null)
						return entry;
			} else {
				int hashCode = key.hashCode();
				K key1;
				for (VirtualEntry<K, V> entry : this)
					if ((key1 = entry.getKey()) != null && hashCode == key.hashCode() && key.equals(key1))
						return entry;
			}
			return null;
		}
	}

	/**
	 * An object to manage entries in the {@link Bean}. The entry is the responsible for (remove, set, get) methods. And it's the one manages the
	 * appliance of operations for the it's targeted key for both field and map containers.
	 *
	 * @param <K> the type of key maintained by this entry
	 * @param <V> the type of mapped value
	 */
	class VirtualEntry<K, V> implements Map.Entry<K, V> {
		/**
		 * The field where this entry is belongs to.
		 */
		final protected Bean<K, V> bean;
		/**
		 * The configurations of the field of this.
		 *
		 * @apiNote not null when {@link #properties} is null
		 */
		final protected Property config;
		/**
		 * The field where this entry is linked to in the {@link Bean} this entry belongs to.
		 *
		 * @apiNote not null when {@link #config} is not null
		 * @apiNote not null when {@link #properties} is null
		 */
		final protected Field field;
		/**
		 * The key represented by this entry.
		 */
		final protected K key;
		/**
		 * The map where extra properties get stored at.
		 *
		 * @apiNote not null when {@link #field} is null
		 * @apiNote not null when {@link #config} is null
		 */
		final protected Properties<K, V> properties;
		/**
		 * The value represented by this entry.
		 */
		protected V value;

		/**
		 * Initialize this.
		 *
		 * @param bean       the object that this entry belongs to
		 * @param properties the map where extra properties get stored at
		 * @param field      the field where this entry is linked to (null if there is no such field)
		 * @param config     the property instance (not-null if the field not null)
		 * @param key        the key represented by this entry
		 * @throws NullPointerException if the given 'bean' or 'properties' or 'field' or 'property' is null
		 */
		protected VirtualEntry(Bean<K, V> bean, Properties<K, V> properties, Field field, Property config, K key) {
			ObjectUtil.requireNonNull(bean, "bean");
			ObjectUtil.requireNonNull(properties, "properties");
			ObjectUtil.requireNonNull(field, "field");
			ObjectUtil.requireNonNull(config, "config");
			this.bean = bean;
			this.key = key;
			this.field = field;
			this.properties = properties;
			this.config = config;
		}

		/**
		 * Initialize this.
		 *
		 * @param bean       the object that this entry belongs to
		 * @param properties the map where extra properties get stored at
		 * @param key        the key represented by this entry
		 * @throws NullPointerException if the given 'bean' or 'properties' is null
		 */
		protected VirtualEntry(Bean<K, V> bean, Properties<K, V> properties, K key) {
			ObjectUtil.requireNonNull(bean, "bean");
			ObjectUtil.requireNonNull(properties, "properties");
			this.bean = bean;
			this.properties = properties;
			this.key = key;
			this.field = null;
			this.config = null;
		}

		/**
		 * Initialize this.
		 *
		 * @param bean   the object that this entry belongs to
		 * @param field  the field where this entry is linked to (null if there is no such field)
		 * @param config the property instance (not-null if the field not null)
		 * @param key    the key represented by this entry
		 * @throws NullPointerException if the given 'bean' or 'field' or 'property' is null
		 */
		protected VirtualEntry(Bean<K, V> bean, Field field, Property config, K key) {
			ObjectUtil.requireNonNull(bean, "bean");
			ObjectUtil.requireNonNull(field, "field");
			ObjectUtil.requireNonNull(config, "config");
			this.bean = bean;
			this.field = field;
			this.config = config;
			this.key = key;
			this.properties = null;
		}

		/**
		 * Initialize this.
		 *
		 * @param bean       the object that this entry belongs to
		 * @param properties the map where extra properties get stored at
		 * @param config     the property instance (not-null if the field not null)
		 * @param key        the key represented by this entry
		 * @throws NullPointerException if the given 'bean' or 'properties' or 'property' is null
		 */
		protected VirtualEntry(Bean<K, V> bean, Properties<K, V> properties, Property config, K key) {
			ObjectUtil.requireNonNull(bean, "bean");
			ObjectUtil.requireNonNull(properties, "properties");
			ObjectUtil.requireNonNull(config, "config");
			this.bean = bean;
			this.properties = properties;
			this.config = config;
			this.key = key;
			this.field = null;
		}

		@Override
		public K getKey() {
			return this.key;
		}

		/**
		 * Returns the value corresponding to this entry.
		 *
		 * @return the value corresponding to this entry
		 * @throws IllegalStateException if the entry was removed
		 */
		@Override
		public V getValue() {
			if (this.properties != null && !this.properties.contains(this))
				throw new IllegalStateException("Invalid entry");
			V value = this.getValue0();

			if (this.config != null && value == null)
				//CONFIGURATION MODS
				switch (this.config.onGetNull()) {
					case AINA:
						break;
					case DEFAULT:
						return (V) TypedValue.util.construct(this.config.defaultValue());
					case NULL:
						return null;
					case THROW:
						throw new IllegalArgumentException("bean.get(" + this.key + ") is null");
					default:
						throw new IllegalAnnotationException("@Property(onGetNull=UnknownConstant)");
				}

			return value;
		}

		/**
		 * Replaces the value corresponding to this entry with the specified value. (Writes through to the map).
		 *
		 * @param value to be set
		 * @return the previous associated value
		 * @throws IllegalArgumentException if the given value isn't instance of the {@link #getType() type} of this
		 * @apiNote when the value get casted this method will call {@link #put} on the {@link Bean} as a notification that the value has changed
		 */
		@SuppressWarnings("OverlyComplexMethod")
		@Override
		public V setValue(V value) {
			Class<V> type;

			if (value != null && !(type = this.getType()).isInstance(value)) {
				if (this.config != null)
					//CONFIGURATIONS MODS
					switch (this.config.onTypeMismatch()) {
						case AINA:
							break;
						case CAST:
							return this.bean.put(this.key, type.cast(Global.get(this.config.caster()).cast(value, type)));
						case DEFAULT:
							return this.bean.put(key, type.cast(TypedValue.util.construct(this.config.defaultValue(), type)));
						case IGNORE:
							return this.bean.put(key, this.getValue());
						case NULL:
							return this.bean.put(key, null);
						case THROW:
							throw new IllegalArgumentException(value.getClass() + " is not an instance of " + type);
						default:
							throw new IllegalAnnotationException("@Property(onTypeMismatch=UnknownConstant)");
					}

				throw new IllegalArgumentException(value.getClass() + " is not an instance of " + type);
			}

			return this.setValue0(value);
		}

		@Override
		public int hashCode() {
			return this.key == null ? 0 : this.key.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof VirtualEntry && (this == o || ObjectUtil.hashEquals(this.key, ((VirtualEntry<?, ?>) o).getKey()));
		}

		@Override
		public String toString() {
			return this.key + "=" + this.getValue();
		}

		/**
		 * Get the type of the objects allowed to be set to this entry.
		 *
		 * @return the type of the value allowed on this entry
		 */
		public Class<V> getType() {
			if (this.config != null) {
				Class<?> specified = this.config.type();
				if (specified != Property.class)
					return (Class<V>) ReflectUtil.objectiveClass(specified);
			}

			if (this.field != null)
				return (Class<V>) ReflectUtil.objectiveClass(this.field.getType());

			return (Class<V>) Object.class;
		}

		/**
		 * Remove this entry from the {@link Bean} where it's belongs to. By removing it from the {@link #bean#getProperties()} in the linked object.
		 * Or set the {@link #value} to null (if this entry linked to a field).
		 *
		 * @return the previous value associated with this.
		 * @apiNote if this linked to a field. And field config annotation set to {@link Property#NULL} then the {@link #put(Object, Object)} on the
		 * linked {@link Bean} will be called
		 * @apiNote you can modify what the behavior to this method by changing {@link Property#onRemove()}
		 */
		public V remove() {
			if (this.config != null)
				//CONFIGURATIONS MODS
				switch (this.config.onRemove()) {
					case AINA:
						break;
					case DEFAULT:
						return this.bean.put(this.key, TypedValue.util.construct(this.config.defaultValue(), this.getType()));
					case IGNORE:
						return this.bean.put(this.key, this.getValue());
					case NULL:
						return this.setValue(null);
					case THROW:
						throw new UnsupportedOperationException("remove");
					default:
						throw new IllegalAnnotationException("@Property(onRemove=UnknownConstant)");
				}

			V value = this.setValue(null);
			this.properties.remove(this.key);
			return value;
		}

		/**
		 * Base value get method. Get the value from this field if it exist. Or set the value to {@link #value} field.
		 *
		 * @return the value on the field of this
		 */
		protected V getValue0() {
			if (this.field == null)
				return this.value;
			else try {
				this.field.setAccessible(true);
				return (V) this.field.get(this.bean);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Base value set method. Set the value to this field if it exists. Or set the value to {@link #value} field.
		 *
		 * @param value to be set
		 * @return the previous value
		 * @implSpec add this entry to {@link #properties}
		 */
		protected V setValue0(V value) {
			V old = this.getValue0();

			if (this.properties != null && this.properties.get(this.key) != this)
				this.properties.add(this);
			if (this.field == null)
				this.value = value;
			else try {
				this.field.setAccessible(true);
				this.field.set(this.bean, value);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}

			return old;
		}
	}
}
