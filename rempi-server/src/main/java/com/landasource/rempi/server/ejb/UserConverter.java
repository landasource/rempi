package com.landasource.rempi.server.ejb;

import java.util.List;
import java.util.stream.Collectors;

import com.landasource.rempi.server.model.User;
import com.landasource.rempi.server.rs.user.UserInfo;

public class UserConverter {

    public List<UserInfo> toUserList(final List<User> list) {
        return list.stream().map(this::toUser).collect(Collectors.toList());
    }

    public UserInfo toUser(final User user) {
        final UserInfo info = new UserInfo();
        info.username = user.getUsername();
        info.uuid = user.getUuid();
        return info;
    }
}
