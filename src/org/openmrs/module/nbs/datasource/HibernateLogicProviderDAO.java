/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.nbs.datasource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicExpression;
import org.openmrs.logic.LogicExpressionBinary;
import org.openmrs.logic.LogicTransform;
import org.openmrs.logic.op.Operator;

/**
 * 
 */
public class HibernateLogicProviderDAO implements LogicProviderDAO
{

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	private Criterion getCriterion(LogicExpression logicExpression,
			Date indexDate)
	{
		Operator operator = logicExpression.getOperator();
		Object rightOperand = logicExpression.getRightOperand();
		Object leftOperand = null;

		if (logicExpression instanceof LogicExpressionBinary)
		{
			leftOperand = ((LogicExpressionBinary) logicExpression)
					.getLeftOperand();
		}
		List<Criterion> criterion = new ArrayList<Criterion>();
		String attr = "";
		String token = logicExpression.getRootToken();

		if (token != null && operator != null)
		{
			if (token.equalsIgnoreCase("username"))
				attr = "username";
			else if (token.equalsIgnoreCase("system_Id"))
				attr = "systemId";
			else if (token.equalsIgnoreCase("provider_id"))
			{
				attr = "attr.value";
			} else if (token.equalsIgnoreCase("given_name"))
			{
				attr = "na.givenName";
			} else if (token.equalsIgnoreCase("middle_name"))
			{
				attr = "na.middleName";
			} else if (token.equalsIgnoreCase("family_name"))
			{
				attr = "na.familyName";
			} else
			{
				this.log.error("Illegal or unsupported token:" + token);
			}
		}

		if (operator == null || operator instanceof org.openmrs.logic.op.After
				|| operator instanceof org.openmrs.logic.op.GreaterThan
				|| operator instanceof org.openmrs.logic.op.GreaterThanEquals
				|| operator instanceof org.openmrs.logic.op.Before
				|| operator instanceof org.openmrs.logic.op.LessThan
				|| operator instanceof org.openmrs.logic.op.LessThanEquals
				|| operator instanceof org.openmrs.logic.op.Exists
				|| operator instanceof org.openmrs.logic.op.AsOf
				|| operator instanceof org.openmrs.logic.op.Within)
		{
			// Nothing to do since all String tokens
		} else if (operator == Operator.AND || operator == Operator.OR)
		{

			Criterion leftCriteria = null;
			Criterion rightCriteria = null;

			if (leftOperand instanceof LogicExpression)
			{
				leftCriteria = this.getCriterion((LogicExpression) leftOperand,
						indexDate);
			}
			if (rightOperand instanceof LogicExpression)
			{
				rightCriteria = this.getCriterion(
						(LogicExpression) rightOperand, indexDate);
			}

			if (leftCriteria != null && rightCriteria != null)
			{
				if (operator == Operator.AND)
				{
					criterion
							.add(Restrictions.and(leftCriteria, rightCriteria));
				}
				if (operator == Operator.OR)
				{
					criterion.add(Restrictions.or(leftCriteria, rightCriteria));
				}
			}
		} else if (operator == Operator.NOT)
		{

			Criterion rightCriteria = null;

			if (rightOperand instanceof LogicExpression)
			{
				rightCriteria = this.getCriterion(
						(LogicExpression) rightOperand, indexDate);
			}

			if (rightCriteria != null)
			{
				criterion.add(Restrictions.not(rightCriteria));
			}
		} else if (operator instanceof org.openmrs.logic.op.Contains
				|| operator instanceof org.openmrs.logic.op.Equals)
		{
			criterion.add(Restrictions.eq(attr, rightOperand));

		}
		Criterion c = null;

		for (Criterion crit : criterion)
		{
			if (c == null)
			{
				c = crit;
			} else
			{
				c = Restrictions.and(c, crit);
			}
		}
		return c;
	}

	// Helper function, converts logic service's criteria into Hibernate's
	// criteria
	private List<User> logicToHibernate(LogicExpression expression,
			List<Integer> userIds)
	{
		Criteria criteria = this.sessionFactory.getCurrentSession()
				.createCriteria(User.class).setResultTransformer(
						Criteria.DISTINCT_ROOT_ENTITY);
		criteria = criteria.createAlias("names", "na");
		criteria = criteria.createAlias("attributes", "attr",
				CriteriaSpecification.LEFT_JOIN);
		criteria = criteria.createAlias("attr.attributeType", "type").add(
				Restrictions.eq("type.name", "Provider ID"));
		Date indexDate = Calendar.getInstance().getTime();
		Operator transformOperator = null;
		LogicTransform transform = expression.getTransform();
		Integer numResults = null;

		if (transform != null)
		{
			transformOperator = transform.getTransformOperator();
			numResults = transform.getNumResults();
		}

		if (numResults == null)
		{
			numResults = 1;
		}
		// set the transform and evaluate the right criteria
		// if there is any
		if (transformOperator == Operator.DISTINCT)
		{
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		}
		Criterion c = this.getCriterion(expression, indexDate);
		if (c != null)
		{
			criteria.add(c);
		}
		List<User> results = null;

		criteria.add(Restrictions.in("userId", userIds));
		results.addAll(criteria.list());

		// return a single result per patient for these operators
		// I don't see an easy way to do this in hibernate so I am
		// doing some postprocessing
		if (transformOperator == Operator.FIRST
				|| transformOperator == Operator.LAST)
		{
			HashMap<Integer, ArrayList<User>> nResultMap = new HashMap<Integer, ArrayList<User>>();

			for (User currResult : results)
			{
				Integer currUserId = currResult.getUserId();
				ArrayList<User> prevResults = nResultMap.get(currUserId);
				if (prevResults == null)
				{
					prevResults = new ArrayList<User>();
					nResultMap.put(currUserId, prevResults);
				}

				if (prevResults.size() < numResults)
				{
					prevResults.add(currResult);
				}
			}

			if (nResultMap.values().size() > 0)
			{
				results.clear();

				for (ArrayList<User> currPatientUser : nResultMap.values())
				{
					results.addAll(currPatientUser);
				}
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<User> getProviders(List<Integer> userIds,
			LogicCriteria logicCriteria)
	{
		return logicToHibernate(logicCriteria.getExpression(), userIds);
	}

	public List<Integer> getAllProviders(Integer patientId,
			ArrayList<Integer> encounterList)
	{
		String encounterRestrictions = "";

		if (encounterList != null && encounterList.size() == 0)
		{
			return null;
		}

		if (encounterList != null)
		{
			encounterRestrictions = " and encounter_id in (:encounterList)";
		}

		String sql = "select distinct value_numeric as provider_id from obs where encounter_id in ( "
				+ "select encounter_id from encounter where patient_id=?"
				+ encounterRestrictions
				+ ") "
				+ "and concept_id=? and value_numeric is not null";

		ConceptService conceptService = Context.getConceptService();
		Concept providerUserIdConcept = conceptService
				.getConceptByName("PROVIDER_USER_ID");
		if (providerUserIdConcept == null)
		{
			return null;
		}

		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(
				sql);
		qry.setInteger(0, patientId);
		qry.setInteger(1, providerUserIdConcept.getConceptId());
		if (encounterList != null)
		{
			qry.setParameterList("encounterList", encounterList);
		}
		qry.addScalar("provider_id");
		try
		{
			List<Double> tmpList = qry.list();
			List<Integer> resultList = new ArrayList();

			for (Double item : tmpList)
			{
				resultList.add(item.intValue());

			}
			return resultList;

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
