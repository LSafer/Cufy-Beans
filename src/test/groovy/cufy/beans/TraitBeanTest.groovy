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

import cufy.lang.TypedValue
import org.junit.Assert
import org.junit.Test

@SuppressWarnings("JavaDoc")
class TraitBeanTest {
	@Test(/*timeout = 50L*/)
	void struct_put_get_size() {
		def bean = new TestTraitBean()

		bean[false] = "67"
		bean["A"] = "B"

		Assert.assertEquals("Wrong size calculation", 2, bean.size())
		Assert.assertEquals("Fielded value not stored", 67, bean[false])
		Assert.assertEquals("Non-fielded value not stored", "B", bean["A"])
	}

	static class TestTraitBean implements TraitBean {
		@Bean.Property(key = @TypedValue(value = "false", type = Boolean.class), onTypeMismatch = Bean.CAST)
		public Integer i
	}
}
