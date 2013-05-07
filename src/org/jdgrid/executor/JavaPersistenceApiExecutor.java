package org.jdgrid.executor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jdgrid.filter.FieldFilter;
import org.jdgrid.filter.OrderBy;
import org.jdgrid.filter.ResultFilter;
import org.jdgrid.util.Property;


public class JavaPersistenceApiExecutor<T> implements Executor<T> {

	private EntityManager entityManager;
	private ResultFilter filter;
	private CriteriaBuilder criteriaBuilder;
	private Root<T> root;
	
	@SuppressWarnings("rawtypes")
	private CriteriaQuery query;
	private Class<T> domainClass;

	@Override
	public Class<?> getDomainClass() {
		return domainClass;
	}

	public JavaPersistenceApiExecutor(EntityManager entityManager, Class<T> clazz) {
		this.entityManager = entityManager;
		this.domainClass = clazz;
		criteriaBuilder = entityManager.getCriteriaBuilder();
	}

	@Override
	public synchronized List<T> getResultSet(ResultFilter filter) {
		this.filter = filter;
		resetQuery();
		TypedQuery<T> q = this.entityManager.createQuery(this.getQuery());
		
		// Limit
		q.setFirstResult(filter.getRangeStart());
		q.setMaxResults(filter.getResultsPerPage());
		return q.getResultList();
	}

	@Override
	public synchronized long getNumberOfItems(ResultFilter filter) {
		this.filter = filter;
		resetQuery();
		TypedQuery<Long> q = this.entityManager.createQuery(this.getQueryForCount());
		return q.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	protected synchronized void resetQuery() {
		query = criteriaBuilder.createQuery();
		root = query.from(domainClass);
	}

	@SuppressWarnings("unchecked")
	protected CriteriaQuery<T> getQuery() {
		query.select(root);
		query.where(getWhere());
		query.orderBy(getOrderBy());
		return query;
	}

	@SuppressWarnings("unchecked")
	protected CriteriaQuery<Long> getQueryForCount() {
		query.select(criteriaBuilder.count(root));
		query.where(getWhere());
		return query;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected synchronized Predicate getWhere() {
		Predicate clause = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
		for (FieldFilter fieldFilter : filter.getFieldFilters()) {
			String operator = fieldFilter.getOperator();
			
			Class<?> fieldType = Object.class;
			try {
				fieldType = Property.getPropertyDataType(domainClass, fieldFilter.getFieldName());
			} catch (Exception e) { e.printStackTrace(); }
			
			
			if (operator == null) {
				if (String.class.isAssignableFrom(fieldType) || fieldType.isEnum()) {
					operator = "contains";
				}
				else {
					operator = "=";
					
				}
			}
			
			// TODO: Allow also operator like 'lt' & 'gt'

			Path path = getPathCompletePath(root, fieldFilter.getFieldName());
			
			if (fieldType.isEnum()) {
				try {
					for (Method method : fieldType.getDeclaredMethods()) {
						if (method.getName().equals("valueOf")) {
							
							for (Object constant : fieldType.getEnumConstants()) {
								String constantName = constant.toString();
								if (operator.equals("=")) {
									if (constantName.equalsIgnoreCase(fieldFilter.getValue())) {
										Object value = method.invoke(null, constantName);
										clause = criteriaBuilder.and(clause, criteriaBuilder.equal(path, value));
									}
								}
								else {
									String constantNameLower = constantName.toLowerCase();
									
									if (constantNameLower.contains(fieldFilter.getValue().toLowerCase())) {
										Object value = method.invoke(null, constantName);
										clause = criteriaBuilder.and(clause, criteriaBuilder.equal(path, value));
									}
								}
							}
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(boolean.class.isAssignableFrom(fieldType)) {
				if (fieldFilter.getValue().equalsIgnoreCase("true")) {
					boolean value = true;
					clause = criteriaBuilder.and(clause, criteriaBuilder.equal(path, value));
				}
				else if (fieldFilter.getValue().equalsIgnoreCase("false")) {
					boolean value = false;
					clause = criteriaBuilder.and(clause, criteriaBuilder.equal(path, value));
				}
			}
			else {
				if (operator.equals(">")) {
					Integer value = new Integer(fieldFilter.getValue());
					clause = criteriaBuilder.and(clause, criteriaBuilder.gt(path, value));
				} else if (operator.equals("<")) {
					Integer value = new Integer(fieldFilter.getValue());
					clause = criteriaBuilder.and(clause, criteriaBuilder.lt(path, value));
				} else if (operator.equals("=")) {
					clause = criteriaBuilder.and(clause, criteriaBuilder.equal(path, fieldFilter.getValue()));
				} else if (operator.equals("contains")) {
					String value = "%" + fieldFilter.getValue() + "%";
					clause = criteriaBuilder.and(clause, criteriaBuilder.like(path, value));
				}
			}
		}
		return clause;
	}

	protected synchronized List<Order> getOrderBy() {
		// Order By
		List<Order> orderBys = new ArrayList<Order>();
		for (OrderBy orderBy : filter.getOrderBys()) {
			@SuppressWarnings("rawtypes")
			Path path = getPathCompletePath(root, orderBy.getFieldName());
			if (orderBy.isSortAscending()) {
				orderBys.add(criteriaBuilder.asc(path));
			} else {
				orderBys.add(criteriaBuilder.desc(path));
			}
		}
		return orderBys;
	}
	
	protected CriteriaBuilder getCriteriaBuilder() {
		return this.criteriaBuilder;
	}
	
	protected Root<T> getRoot() {
		return this.root;
	}
	
	protected Path<?> getPathCompletePath(Path<?> rootPath, String fieldName) {
		if (fieldName.contains(".")) {
			int first = fieldName.indexOf(".");
			String fieldNameRest = fieldName.substring(first+1);
			String effectiveFieldName = fieldName.substring(0, first);
			
			return this.getPathCompletePath(rootPath.get(effectiveFieldName), fieldNameRest);
		}
		else {
			return rootPath.get(fieldName);
		}
	}
}
