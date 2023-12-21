package com.showy.utils;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    @Autowired
    HibernateUtil(SessionFactory sessionFactory) {
        HibernateUtil.sessionFactory = sessionFactory;
    }

    public static <T> boolean saveOrUpdate(List<T> objList) {
        boolean success = false;
        Transaction tx = null;

        T objForLog = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            for (T t : objList) {
                objForLog = t;
                session.saveOrUpdate(t);
            }

            tx.commit();
            success = true;

        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }

            log.error("Exception in saveOrUpdate() {} : ErrorObj ({}) | ", objList.getClass().getSimpleName(), objForLog, e);
        }
        return success;
    }

    public static boolean executeQuery(String queryString, Map<String, Object> parametersToSet) {
        boolean success = false;
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Query query = session.createQuery(queryString);

            if (parametersToSet != null && !parametersToSet.isEmpty()) {
                parametersToSet.forEach(query::setParameter);
            }

            query.executeUpdate();
            tx.commit();
            success = true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("Error in executeQuery : ", e);
        }

        return success;
    }

    public static <T> Optional<T> getSingleResult(List<T> list) {
        try {
            if (list != null && !list.isEmpty()) {
                return Optional.ofNullable(list.get(0));
            }
        } catch (Exception e) {
            log.error("Error in getSingleResult(): ", e);
        }
        return Optional.empty();
    }

    public static <T> Optional<T> getSingleResult(String queryString, Map<String, Object> parametersToSet, Class<T> className) {
        return getSingleResult(runQuery(queryString, parametersToSet, className, 1, 0));
    }

    private static <T> List<T> runQueryHelper(String queryString, Map<String, Object> parametersToSet,
                                              Class<T> className,
                                              Integer limit, Integer offset, boolean isNativeQuery) {
        List<T> list = null;
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<T> query;

            if (isNativeQuery) {
                query = session.createNativeQuery(queryString, className);
            } else {
                query = session.createQuery(queryString, className);
            }

            if (parametersToSet != null && !parametersToSet.isEmpty()) {
                parametersToSet.forEach(query::setParameter);
            }

            if (limit != null && offset != null) {
                query.setMaxResults(limit);
                query.setFirstResult(offset);
            }

            list = query.getResultList(); // this never returns null;
            if (list != null && list.size() == 0) {
                list = null;
            }
        } catch (Exception e) {
            log.error("Exception in runQuery() {}: ", className.getSimpleName(), e);
        }
        return list;
    }

    public static <T> List<T> runNativeQuery(String queryString, Map<String, Object> parametersToSet, Class<T> className) {
        return runQueryHelper(queryString, parametersToSet, className, null, null, true);
    }

    public static <T> List<T> runQuery(String queryString, Map<String, Object> parametersToSet, Class<T> className) {
        return runQueryHelper(queryString, parametersToSet, className, null, null, false);
    }

    public static <T> List<T> runQuery(String queryString, Map<String, Object> parametersToSet, Class<T> className, Integer limit, Integer offset) {
        return runQueryHelper(queryString, parametersToSet, className, limit, offset, false);
    }
}
