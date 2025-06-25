package isima.georganise.app.entity.dao;

import isima.georganise.app.entity.dto.UserCreationDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "USERS")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, unique = true)
    private Long userId;

    @Column(name = "NICKNAME", nullable = false, unique = true)
    private String nickname;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "TOKEN", unique = true)
    private UUID authToken;

    public User(@NotNull UserCreationDTO user) {
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.authToken = UUID.randomUUID();
    }
}
