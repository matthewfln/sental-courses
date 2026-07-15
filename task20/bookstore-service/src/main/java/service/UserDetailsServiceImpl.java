package service;

import dao.UserDao;
import enums.UserRole;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LogManager.getLogger(UserDetailsServiceImpl.class);
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserDao userDao, @Lazy PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void initDefaultUser() {
        if (userDao.findByUsername("admin") == null) {
            LOGGER.info("База пользователей пуста. Создаем пользователя 'admin' по умолчанию...");
            int randomId = (new java.util.Random()).nextInt(100000);
            String encodedPassword = passwordEncoder.encode("password");

            User adminUser = new User(randomId, "admin", encodedPassword, UserRole.ROLE_ADMIN);
            userDao.save(adminUser);
            LOGGER.info("Пользователь 'admin' с паролем 'password' успешно сохранен в БД!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            LOGGER.warn("Пользователь с логином '{}' не найден.", username);
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        LOGGER.debug("Пользователь '{}' успешно загружен из БД.", username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
