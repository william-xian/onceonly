package io.onceonly.util;

import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.Test;

public class OOReflectUtilTest {
	class Zero {
		
	}
	class A<T1> extends Zero {
		T1 id;
	}
	class B extends A<Long> {
		
	}	
	class C<E,ID2> extends A<ID2> {
		E e;
	}
	class D extends C<Long,String> {
		
	}

	@Test
	public void searchGenType() throws NoSuchFieldException, SecurityException {
		A<Integer> a = new A<>();
		Type fieldType = a.getClass().getDeclaredField("id").getGenericType();
		Assert.assertEquals(String.class.getTypeName(), OOReflectUtil.searchGenType(A.class,D.class,fieldType).getTypeName());
	}
}
