package io.onceonly.db.dao;

public interface IdGenerator {
	Object next(Class<?> entityClass);
}
