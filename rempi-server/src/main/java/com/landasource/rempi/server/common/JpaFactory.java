package com.landasource.rempi.server.common;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class JpaFactory {

    @PersistenceContext()
    @Produces
    private EntityManager entityManager;

    public void close(@Disposes final EntityManager em) {
        entityManager.close();
    }
}
