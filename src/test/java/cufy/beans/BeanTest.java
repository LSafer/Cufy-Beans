/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * -You can edit this file (except the header).
 *  -If you have change anything in this file. You
 *    shall mention that this file has been edited.
 *    By adding a new header (at the bottom of this header)
 *    with the word "Editor" on top of it.
 */
package cufy.beans;

import cufy.meta.MetaClazz;
import cufy.meta.MetaObject;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"JavaDoc"})
public class BeanTest {
	@Test
	public void _forInstance_put_get_size() {
		Object object = new Object() {
			@Bean.Property(key = @MetaObject(value = "false", type = @MetaClazz(Boolean.class)), type = @MetaClazz(Integer.class))
			private int property0 = 90;
		};

		Bean<Object, Object> bean = Bean.forInstance(object);

		bean.put(false, "700");

		//state
		Assert.assertEquals("Wrong size calc", 1, bean.size());

		//key = false
		Assert.assertEquals("Field value stored wrongly", 700, bean.get(false));
	}

	@Test
	public void _forInstance_struct_put_get_size() {
		Object object = new Object() {
			@Bean.Property(key = @MetaObject(value = "false", type = @MetaClazz(Boolean.class)))
			private Integer integer = 45;
		};

		Bean<Object, Object> bean = Bean.forInstance(object);

		bean.put("A", "B");
		bean.put("A", "R");

		//state
		Assert.assertEquals("Wrong size calc", 2, bean.size());

		bean.put(false, 67);

		//key = false
		Assert.assertEquals("Field value stored wrongly", 67, bean.get(false));

		Assert.assertEquals("Non-field value stored wrongly", "R", bean.get("A"));
	}

	@Test
	public void _getNotExist() {
		Bean bean = new Bean() {
		};

		Assert.assertNull("Not equals", bean.get(null));
	}

	@Test
	public void _struct_put_get_size() {
		Bean<Object, Object> bean = new Bean<Object, Object>() {
			@Bean.Property(key = @MetaObject(value = "false", type = @MetaClazz(Boolean.class)), type = @MetaClazz(Integer.class))
			private int property0 = 90;
		};

		bean.put(false, "700");

		//state
		Assert.assertEquals("Wrong size calc", 1, bean.size());

		//key = false
		Assert.assertEquals("Field value stored wrongly", 700, bean.get(false));
	}

	@Test
	public void clear() {
		//TODO
	}

	@Test
	public void containsKey() {
		//TODO
	}

	@Test
	public void containsValue() {
		//TODO
	}

	@Test
	public void entrySet() {
		//TODO
	}

	@Test
	public void forInstance() {
		//TODO
	}

	@Test
	public void get() {
		//TODO
	}

	@Test
	public void isEmpty() {
		//TODO
	}

	@Test
	public void keySet() {
		//TODO
	}

	@Test
	public void put() {
		//TODO
	}

	@Test
	public void putAll() {
		//TODO
	}

	@Test
	public void remove() {
		//TODO
	}

	@Test
	public void size() {
		//TODO
	}

	@Test
	public void values() {
		//TODO
	}
}
