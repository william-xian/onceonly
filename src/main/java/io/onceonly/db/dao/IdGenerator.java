package io.onceonly.db.dao;

public interface IdGenerator {
	Long next(Class<?> entityClass);
}
