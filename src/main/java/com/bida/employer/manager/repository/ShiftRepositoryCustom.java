package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.Shift;
import com.bida.employer.manager.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Repository
public class ShiftRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    public List<Shift> findByFilters(UUID userId, UUID organizationId, boolean unassignedShifts, LocalDateTime from, LocalDateTime to) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Shift> criteriaQuery = criteriaBuilder.createQuery(Shift.class);
        Root<Shift> shiftRoot = criteriaQuery.from(Shift.class);

        List<Predicate> predicates = new LinkedList<>();

        if (unassignedShifts) {
            Predicate predicateUserId = criteriaBuilder.isNull(shiftRoot.get("userId"));
            predicates.add(predicateUserId);
        } else if (userId != null) {
            Predicate predicateUserId = criteriaBuilder.equal(shiftRoot.get("userId"), userId);
            predicates.add(predicateUserId);
        }

        Predicate predicateOrganization = criteriaBuilder.equal(shiftRoot.get("organizationId"), organizationId);
        predicates.add(predicateOrganization);

        Predicate predicateFrom = criteriaBuilder.greaterThanOrEqualTo(shiftRoot.get("shiftFinish"), from);
        predicates.add(predicateFrom);

        Predicate predicateTo = criteriaBuilder.lessThanOrEqualTo(shiftRoot.get("shiftStart"), to);
        predicates.add(predicateTo);

        criteriaQuery.orderBy(criteriaBuilder.asc(shiftRoot.get("shiftStart")));

        Predicate[] predicatesFinal = new Predicate[predicates.size()];
        predicates.toArray(predicatesFinal);
        criteriaQuery.where(predicatesFinal);

        TypedQuery<Shift> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
