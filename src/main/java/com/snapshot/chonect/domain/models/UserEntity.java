package com.snapshot.chonect.domain.models;

import com.snapshot.chonect.utils.enums.Role;
import com.snapshot.chonect.utils.enums.CustomerType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 50, nullable = false)
    private String firstName;

    @Column(length = 50, nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @Column(name = "terms_version")
    private String termsVersion;

    // @Column(nullable = true)
    private LocalDate birthDate;

    private boolean enabled;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpireAt;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private CountryEntity country;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private LanguageEntity language;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProjectEntity> projects = new java.util.ArrayList<>();

    // aqui empiezan temas de seguridad, desde aqui viene lo turbio jajajaj

    // esto basicamente esta re escribiendo los requerimientos de los detalles del
    // usuario
    // en los interfaces este metodo va a tener la autoridad de retornar las
    // autorizaciones
    // de los metodos que el usuario quiera usar

    // conclucion: es pa permisos jajajajaj
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    // esto es basically un comprobante de si la cuanta sigue aun valida
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // el nombre es explicativo pero igual es para ver si la cuenta esta bloqueada
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // revisa si las credenciales estan expiradas
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
