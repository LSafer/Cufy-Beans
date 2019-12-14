/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *   shall mention that this file has been edited.
 *   By adding a new header (at the bottom of this header)
 *   with the word "Editor" on top of it.
 */

package cufy.beans

/**
 * A trait version of the interface {@link Bean}. Adding more reliability on the implementation.
 *
 * @param <K> the type of the keys on this map
 * @param <V> the type of the values on this map
 * @since 25-Nov-2019
 * @version 1 release (04-Dec-2019)
 * @author LSaferSE
 */
trait TraitBean<K, V> implements FullBean<K, V>, Serializable {
	/**
	 * The additional properties map of this.
	 */
	private transient Properties properties = new Properties<>(this)

	@Override
	Properties getProperties() {
		this.properties
	}

	@Override
	String toString() {
		Iterator<Entry<K, V>> entries = this.entrySet().iterator()

		if (!entries.hasNext()) {
			return "{}"
		} else {
			StringBuilder builder = new StringBuilder("{")

			while (true) {
				Entry<K, V> entry = entries.next()
				Object key = entry.getKey(), value = entry.getValue()

				builder.append(key == this ? "(this Bean)" : key)
						.append('=')
						.append(value == this ? "(this Bean)" : value)

				if (!entries.hasNext()) {
					return builder.append('}').toString()
				}

				builder.append(", ")
			}
		}
	}

	/**
	 * Backdoor initializing method, or custom deserialization method.
	 *
	 * @param stream to initialize this using
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 * @throws IOException            if an I/O error occurs.
	 */
	private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
		for (int length = stream.readInt(), i = 0; i < length; i++)
			this.put((K) stream.readObject(), (V) stream.readObject())
	}

	/**
	 * Custom JSObject serialization method.
	 *
	 * @param stream to use to serialize this
	 * @throws IOException if an I/O error occurs
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		Properties<K, V> properties = this.getProperties()
		stream.writeInt(properties.size())
		for (VirtualEntry<K, V> entry : properties) {
			stream.writeObject(entry.getKey())
			stream.writeObject(entry.getValue())
		}
	}
}
