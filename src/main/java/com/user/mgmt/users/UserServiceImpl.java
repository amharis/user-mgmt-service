package com.user.mgmt.users;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    static final User user1 = new User("user1","john.doe");
    static final User user2 = new User("user2","jane.smith");
    static final User[] users = { user1, user2 };

    @Override
    public User[] getUsers() {
        return users;
    }
}
