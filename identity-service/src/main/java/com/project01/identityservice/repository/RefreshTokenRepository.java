package com.project01.identityservice.repository;

import com.project01.identityservice.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    Optional<RefreshTokenEntity> findByIdAndAccountId(Long id, String accountId);

    /** Phiên đang hoạt động = chưa thu hồi và chưa hết hạn */
    List<RefreshTokenEntity> findAllByAccountIdAndRevokedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String accountId, LocalDateTime now);

    /** Lịch sử đăng nhập trong một khoảng thời gian, dùng để biết thiết bị đã từng đăng nhập chưa */
    List<RefreshTokenEntity> findAllByAccountIdAndCreatedAtAfter(String accountId, LocalDateTime since);

    /** Thu hồi mọi refresh token còn hiệu lực của tài khoản (đổi mật khẩu, phát hiện token bị đánh cắp...). */
    @Modifying
    @Query("update RefreshTokenEntity t set t.revoked = true where t.accountId = :accountId and t.revoked = false")
    int revokeAllByAccountId(@Param("accountId") String accountId);
}
