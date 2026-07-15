package service;

import dao.UserDao;
import enums.UserRole;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1, "testuser", "encodedPassword", UserRole.ROLE_USER);
    }

    // ==========================================
    // Тесты для метода loadUserByUsername
    // ==========================================

    @Test
    @DisplayName("loadUserByUsername - Позитивный: Пользователь найден, возвращается UserDetails")
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        when(userDao.findByUsername("testuser")).thenReturn(testUser);

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails, "UserDetails не должен быть null");
        assertEquals("testuser", userDetails.getUsername(), "Имя пользователя должно совпадать");
        assertEquals("encodedPassword", userDetails.getPassword(), "Пароль должен совпадать");
        assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER")),
                "Роль пользователя должна быть ROLE_USER");

        verify(userDao, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("loadUserByUsername - Негативный: Пользователь не найден, выбрасывается UsernameNotFoundException")
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        when(userDao.findByUsername("unknown")).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"),
                "Должно выбрасываться исключение UsernameNotFoundException"
        );

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userDao, times(1)).findByUsername("unknown");
    }

    @Test
    @DisplayName("loadUserByUsername - Негативный: Ошибка БД при поиске, выбрасывается RuntimeException")
    void loadUserByUsername_DatabaseError_ThrowsRuntimeException() {
        // Arrange
        when(userDao.findByUsername(anyString())).thenThrow(new RuntimeException("DB Connection Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userDetailsService.loadUserByUsername("testuser"));
        verify(userDao, times(1)).findByUsername("testuser");
    }

    // ==========================================
    // Тесты для метода initDefaultUser
    // ==========================================

    @Test
    @DisplayName("initDefaultUser - Позитивный: Пользователь admin отсутствует, создается новый")
    void initDefaultUser_AdminNotExists_CreatesDefaultAdmin() {
        // Arrange
        when(userDao.findByUsername("admin")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        // Act
        userDetailsService.initDefaultUser();

        // Assert
        verify(userDao, times(1)).findByUsername("admin");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userDao, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser, "Сохраненный пользователь не должен быть null");
        assertEquals("admin", savedUser.getUsername());
        assertEquals("hashedPassword", savedUser.getPassword());
        assertEquals(UserRole.ROLE_ADMIN, savedUser.getRole());
    }

    @Test
    @DisplayName("initDefaultUser - Позитивный/Альтернативный: Пользователь admin уже существует, сохранение не вызывается")
    void initDefaultUser_AdminAlreadyExists_DoesNothing() {
        // Arrange
        User existingAdmin = new User(99, "admin", "oldHash", UserRole.ROLE_ADMIN);
        when(userDao.findByUsername("admin")).thenReturn(existingAdmin);

        // Act
        userDetailsService.initDefaultUser();

        // Assert
        verify(userDao, times(1)).findByUsername("admin");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("initDefaultUser - Негативный: Ошибка при сохранении пользователя в БД")
    void initDefaultUser_SaveFails_ThrowsException() {
        // Arrange
        when(userDao.findByUsername("admin")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        doThrow(new RuntimeException("SQL Error")).when(userDao).save(any(User.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userDetailsService.initDefaultUser());
        verify(userDao, times(1)).save(any(User.class));
    }
}