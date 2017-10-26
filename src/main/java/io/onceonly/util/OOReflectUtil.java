package io.onceonly.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class OOReflectUtil {

	public static Type searchGenType(Class<?> forefather,Class<?> entity,Type fieldType)
	{
		List<Class<?>> classes = new ArrayList<>();
		Type type = null;
		Integer typeIndex = null;
		for(Class<?> clazz = entity;!clazz.equals(forefather);clazz=clazz.getSuperclass()) {
			classes.add(0, clazz);
		}
		classes.add(0, forefather);
		Class<?> father = classes.get(0);
		for(int i = 1; i <classes.size(); i++) {
			Class<?> son = classes.get(i);
			TypeVariable<?>[] param = father.getTypeParameters();
			Type types = son.getGenericSuperclass();
			Type[] genericTypes = null;		
			if(types instanceof ParameterizedType) {
				genericTypes = ((ParameterizedType) types).getActualTypeArguments();
			}
			for(int pi = 0; pi < param.length; pi++){
				if(fieldType.equals(param[pi])) {
					if(genericTypes != null) {
						fieldType = genericTypes[pi];
						typeIndex = pi;
						if(isBaseType(fieldType)) {
							return fieldType;
						}
					}else {
						typeIndex = pi;
						fieldType = null;
					}
					break;
				}
			}
			if(fieldType != null) {
				for(TypeVariable<?> t:son.getTypeParameters()) {
					if(fieldType != null && fieldType.equals(t)) {
						fieldType = t;
						if(isBaseType(fieldType)) {
							return fieldType;
						}
						break;
					}
				}
			}else if(typeIndex != null){
				fieldType=son.getTypeParameters()[typeIndex];
				if(isBaseType(fieldType)) {
					return fieldType;
				}
			}else {
				return  null;
			}
	        father = son;
		}
	    return type;
	}

	@SuppressWarnings("unchecked")
	public static <T> T strToBaseType(Class<T> type,String val) {
		if (type.equals(String.class)) {
			return (T)val;
		}
		if (type.equals(Integer.class)) {
			return (T)Integer.valueOf(val);
		}
		if (type.equals(Long.class)) {
			return (T)Long.valueOf(val);
		}
		if (type.equals(Boolean.class)) {
			return (T)Boolean.valueOf(val);
		}
		if (type.equals(Byte.class)) {
			return (T)Byte.valueOf(val);
		}
		if (type.equals(Short.class)) {
			return (T)Short.valueOf(val);
		}
		if (type.equals(Double.class)) {
			return (T)Double.valueOf(val);
		}
		if (type.equals(Float.class)) {
			return (T)Float.valueOf(val);
		}
		if (type.equals(BigDecimal.class)) {
			return (T)BigDecimal.valueOf(Double.valueOf(val));
		}
		if (type.equals(Date.class)) {
			return (T)Date.valueOf(val);
		}
		return null;
	}
	
	public static boolean isBaseType(Type type) {
		if (type == String.class || type == Character.class || type == char.class) {
			return true;
		}else if (type == Integer.class || type == int.class) {
			return true;
		} else if (type == Long.class || type == long.class) {
			return true;
		}else if (type == Boolean.class || type == boolean.class) {
			return true;
		}else if (type == Byte.class || type == byte.class) {
			return true;
		}else if (type == Short.class || type == short.class) {
			return true;
		}else if (type == Float.class || type == float.class) {
			return true;
		}else if (type == Double.class || type == double.class) {
			return true;
		}else if (type == BigDecimal.class) {
			return true;
		}else if (type == Date.class) {
			return true;
		}
		return false;
	}

	public static boolean isNumber(Object obj) {
		if(obj instanceof Number) {
			return true;
		}
		return false;
	}
	public static boolean isCharacter(Object obj) {
		if ((obj instanceof String) || (obj instanceof Character)) {
			return true;
		}
		return false;
	}
}
