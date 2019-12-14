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

import cufy.lang.TypedValue;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"JavaDoc"})
public class AbstractBeanTest {
	@SuppressWarnings("OverwrittenKey")
	@Test(timeout = 100)
	public void struct_put_get_size() {
		AbstractBean<Object, Object> bean = new AbstractBean<Object, Object>() {
			@Property(key = @TypedValue(value = "false", type = Boolean.class), onTypeMismatch = CAST)
			private Integer integer = 45;
		};

		bean.put("A", "B");
		bean.put("A", "R");
		bean.put(false, "67");

		//state
		Assert.assertEquals("Wrong size calc", 2, bean.size());

		//key = false
		Assert.assertNotNull("Field value can't be reached", bean.get(false));
		Assert.assertNotEquals("Field value not update", 45, bean.get(false));
		Assert.assertEquals("Field value stored wrongly", 67, bean.get(false));

		//key = "A"
		Assert.assertNotNull("Non-field value can't be reached or not stored", bean.get("A"));
		Assert.assertNotEquals("Non-field value not updated", "B", bean.get("A"));
		Assert.assertEquals("Non-field value stored wrongly", "R", bean.get("A"));
	}
}
