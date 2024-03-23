package pl.chopy.reserve_court_backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.chopy.reserve_court_backend.model.UserRole;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String hashedPassword;
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;
    private boolean isActive = true;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String phoneNumber;

    public User(String email, String hashedPassword, String phoneNumber, String name, String surname, LocalDate birthDate) {
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
