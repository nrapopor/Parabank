package com.parasoft.parabank.test.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;

public abstract class AbstractBeanTestCase<T> extends TestCase {
    private static final String TEST_STRING_VAL1 = "Some Value";
    private static final String TEST_STRING_VAL2 = "Some Other Value";
    
    private final Class<T> beanClass;
    protected T bean;
    
    @SuppressWarnings("unchecked")
    protected AbstractBeanTestCase() {
        beanClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    @Override
    protected void setUp() throws Exception {
        bean = beanClass.newInstance();
    }
    
    public void testEqualsContractMet() {
        assertMeetsEqualsContract(beanClass);
    }

    public void testHashCodeContractMet() {
        assertMeetsHashCodeContract(beanClass);
    }
    
    public void testToString() {
        assertToString(beanClass);
    }
    
    static void assertMeetsEqualsContract(Class<?> classUnderTest) {
        Object o1;
        Object o2;
        try {
            o1 = classUnderTest.newInstance();
            o2 = classUnderTest.newInstance();

            assertTrue("Instances with default constructor not equal (o1.equals(o2))", o1.equals(o2));
            assertTrue("Instances with default constructor not equal (o2.equals(o1))", o2.equals(o1));

            Field[] fields = classUnderTest.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                o1 = classUnderTest.newInstance();
                o2 = classUnderTest.newInstance();
                
                assertEquals("Instance o1 is not equal to itself", o1, o1);
                assertFalse("Instance o1 is equal to null", o1.equals(null));
                assertFalse("Instance o1 is equal to an instance of Object", o1.equals(new Object()));

                Field field = fields[i];
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                toggleField(field, o1, true);
                assertFalse("Instances with o1 having " + field.getName() + " set and o2 having it not set are equal", o1.equals(o2));

                field.set(o2, field.get(o1));
                assertTrue("After setting o2 with the value of the object in o1, the two objects in the field are not equal"
                        , field.get(o1).equals(field.get(o2)));
                assertTrue("Instances with o1 having "
                        + field.getName()
                        + " set and o2 having it set to the same object of type "
                        + field.get(o2).getClass().getName()
                        + " are not equal", o1.equals(o2));

                toggleField(field, o2, false);
                if (field.get(o1).equals(field.get(o2)))  {
                    //Even though we have different instances, they are equal. Let's walk one of them
                    //to see if we can find a field to set
                    Field[] paramFields = field.get(o1).getClass().getDeclaredFields();
                    for (int j = 0; j < paramFields.length; j++) {
                        toggleField(paramFields[j], field.get(o1), true);
                    }
                    assertFalse("After setting o2 with a different " + field.getName() + " than what is in o1, the two objects in the field are equal. "
                            + "This is after an attempt to walk the fields to make them different"
                            , field.get(o1).equals(field.get(o2)));
                } else {
                    assertFalse("Instances with o1 having " + field.getName() + " set and o2 having it set to a different object are equal", o1.equals(o2));
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new AssertionError("Unable to construct an instance of the class under test:" + classUnderTest.getName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new AssertionError("Unable to construct an instance of the class under test:" + classUnderTest.getName());
        }
    }

    static void assertMeetsHashCodeContract(Class<?> classUnderTest) {
        try {
            Field[] fields = classUnderTest.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Object o1 = classUnderTest.newInstance();
                int initialHashCode = o1.hashCode();

                Field field = fields[i];
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                toggleField(field, o1, true);
                int updatedHashCode = o1.hashCode();
                assertFalse("The field " + field.getName() + " was not taken into account for the hashCode contract ", initialHashCode == updatedHashCode);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new AssertionError("Unable to construct an instance of the class under test");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new AssertionError("Unable to construct an instance of the class under test");
        }
    }
    
    static void assertToString(Class<?> classUnderTest) {
        Object o;
        try {
            o = classUnderTest.newInstance();
            String str = o.toString();
            
            assertNotNull(str);
            assertTrue("The toString() method for class " + classUnderTest.getName() + " did not begin with the class name", str.startsWith(classUnderTest.getSimpleName()));
            
            Field[] fields = classUnderTest.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                
                assertTrue("The field " + field.getName() + " did not appear in toString()", str.contains(field.getName()));
            }
            
        }  catch (InstantiationException e) {
            e.printStackTrace();
            throw new AssertionError("Unable to construct an instance of the class under test:" + classUnderTest.getName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new AssertionError("Unable to construct an instance of the class under test:" + classUnderTest.getName());
        }
    }
    
    public static void toggleField(Field field, Object obj, boolean on)
    throws IllegalAccessException, InstantiationException {
        field.setAccessible(true);
        if (field.getType() == String.class) {
            field.set(obj, on ? TEST_STRING_VAL1 : TEST_STRING_VAL2);
        } else if (field.getType() == boolean.class) {
            field.setBoolean(obj, on ? true : false);
        } else if (field.getType() == short.class) {
            field.setShort(obj, on ? (short)1 : (short)0);
        } else if (field.getType() == long.class) {
            field.setLong(obj, on ? 1 : 0);
        } else if (field.getType() == float.class) {
            field.setFloat(obj, on ? 1 : 0);
        } else if (field.getType() == int.class) {
            field.setInt(obj, on ? 1 : 0);
        } else if (field.getType() == Integer.class) {
            field.set(obj, on ? 1 : 0);
        } else if (field.getType() == byte.class) {
            field.setByte(obj, on ? (byte)1 : (byte)0);
        } else if (field.getType() == char.class) {
            field.setChar(obj, on ? (char)1 : (char)0);
        } else if (field.getType() == double.class) {
            field.setDouble(obj, on ? 1 : 0);
        } else if (field.getType() == BigDecimal.class) {
            field.set(obj, on ? new BigDecimal(1) : new BigDecimal(0));
        } else if (field.getType() == Date.class) {
            field.set(obj, on ? new Date() : new Date(0));
        } else if (field.getType().isEnum()) {
            field.set(obj, field.getType().getEnumConstants()[on ? 1 : 0]);
        } else if (Object.class.isAssignableFrom(field.getType())) {
            field.set(obj, field.getType().newInstance());
        } else {
            fail("Don't know how to set a " + field.getType().getName());
        }
    }
}

