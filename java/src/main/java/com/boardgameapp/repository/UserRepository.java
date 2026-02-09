package com.boardgameapp.repository;

import com.boardgameapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** ユーザーエンティティの永続化を行うリポジトリ。 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** ユーザー名で1件取得する。 */
    Optional<User> findByUsername(String username);

    /** メールアドレスで1件取得する。 */
    Optional<User> findByEmail(String email);

    /** ユーザー名の存在有無を返す。 */
    boolean existsByUsername(String username);

    /** メールアドレスの存在有無を返す。 */
    boolean existsByEmail(String email);
}
