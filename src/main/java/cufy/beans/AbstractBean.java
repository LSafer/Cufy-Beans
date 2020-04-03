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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * An abstraction for the interface {@link Bean}.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSafer
 * @version 18 release (03-Apr-2020)
 * @since 11 Jun 2019
 **/
public abstract class AbstractBean<K, V> implements FullBean<K, V>, Serializable {
	/**
	 * A set of the entries of this.
	 */
	protected transient Set<Map.Entry<K, V>> entrySet;
	/**
	 * A set of the keys in this.
	 */
	protected transient Set<K> keySet;
	/**
	 * A set of the values in this.
	 */
	protected transient Collection<V> values;

	@Override
	public Set<K> keySet() {
		if (this.keySet == null) {
			this.keySet = FullBean.super.keySet();
		}

		return this.keySet;
	}

	@Override
	public Collection<V> values() {
		if (this.values == null) {
			this.values = FullBean.super.values();
		}

		return this.values;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		if (this.entrySet == null) {
			this.entrySet = FullBean.super.entrySet();
		}

		return this.entrySet;
	}

	@Override
	public String toString() {
		Iterator<Map.Entry<K, V>> entries = this.entrySet().iterator();

		if (!entries.hasNext()) {
			return "{}";
		} else {
			StringBuilder builder = new StringBuilder("{");

			while (true) {
				Map.Entry<K, V> entry = entries.next();
				Object key = entry.getKey(), value = entry.getValue();

				builder.append(key == this ? "(this Bean)" : key)
						.append('=')
						.append(value == this ? "(this Bean)" : value);

				if (!entries.hasNext()) {
					return builder.append('}').toString();
				}

				builder.append(", ");
			}
		}
	}

	/**
	 * Deserialization method.
	 *
	 * @param stream to initialize this using
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 * @throws IOException            if an I/O error occurs.
	 * @throws NullPointerException   if the given 'stream' is null
	 */
	private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
		Objects.requireNonNull(stream, "stream");

		int length = stream.readInt();
		for (int i = 0; i < length; i++)
			this.put((K) stream.readObject(), (V) stream.readObject());
	}

	/**
	 * Serialization method.
	 *
	 * @param stream to use to serialize this
	 * @throws IOException          if an I/O error occurs
	 * @throws NullPointerException if the given 'stream' is null
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		Objects.requireNonNull(stream, "stream");

		stream.writeInt(this.size());
		for (Entry<K, V> entry : this.entrySet()) {
			stream.writeObject(entry.getKey());
			stream.writeObject(entry.getValue());
		}
	}
}
