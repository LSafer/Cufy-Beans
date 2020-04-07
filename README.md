# Beans [![](https://jitpack.io/v/cufyorg/beans.svg)](https://jitpack.io/#cufyorg/beans)
### A bean is a map that it's fields is the properties of it.
- Compatible anywhere. Since it is a map.
- Any object can be a bean. Just with annotations.
- Interface based. Any class can implement.
- fields tris to convert the value before storing it.

### Dependencies
- Util [(cufyorg:util)](https://github.com/cufyorg/util)
- Base [(cufyorg:base)](https://github.com/cufyorg/base)

---

A bean example:

```java 
    class ExBean extends Bean {
        @Property
        int ex_property;
    }
```

A bean for a non-bean instance (fields should be annotated):

```java 
    Bean.forInstance(theInstance);
```

You can override the key (default is a string of field's name) and the type of the property.

```java 
    @Property(key = @MetaObject("newKey"), type = @MetaClazz(Integer.class))
    int ex_property;
```
