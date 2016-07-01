package com.landasource.rempi.server.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.landasource.rempi.server.model.User;
import com.landasource.rempi.server.rs.user.UserInfo;

@Stateless
public class UserBean {

    @PersistenceContext
    private EntityManager entityManager;

    //@Inject
    private final UserConverter converter = new UserConverter();

    public List<UserInfo> findUsers() {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> query = cb.createQuery(User.class);
        query.from(User.class);

        final TypedQuery<User> tpl = entityManager.createQuery(query);

        final List<User> list = tpl.getResultList();

        return converter.toUserList(list);
    }

    public UserInfo createUser(final UserInfo data) {
        final User user = new User();

        user.setUsername(data.username);

        entityManager.persist(user);

        return converter.toUser(user);
    }

}
