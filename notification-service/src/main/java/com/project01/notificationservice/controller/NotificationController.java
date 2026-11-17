package com.project01.notificationservice.controller;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.notificationservice.dto.response.NotificationResponse;
import com.project01.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * API thông báo của người dùng đang đăng nhập. userId lấy từ JWT (claim "userId"),
 * không nhận từ request nên không xem được thông báo của người khác.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/me")
public class NotificationController {

    private static final String CLAIM_USER_ID = "userId";

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Danh sách thông báo của tôi", description = "Mới nhất trước, phân trang (size tối đa 50)")
    public ResponseData<Page<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal Jwt jwt,
                                                                       @RequestParam(name = "page",defaultValue = "0") int page,
                                                                       @RequestParam(name = "size",defaultValue = "20") int size) {
        return new ResponseData<Page<NotificationResponse>>()
                .success(notificationService.getMyNotifications(jwt.getClaimAsString(CLAIM_USER_ID), page, size));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Số thông báo chưa đọc", description = "Dùng để hiển thị số trên biểu tượng chuông")
    public ResponseData<Long> countUnread(@AuthenticationPrincipal Jwt jwt) {
        return new ResponseData<Long>().success(notificationService.countUnread(jwt.getClaimAsString(CLAIM_USER_ID)));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Đánh dấu đã đọc")
    public ResponseData<Void> markAsRead(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable(name = "id") String id) {
        notificationService.markAsRead(id, jwt.getClaimAsString(CLAIM_USER_ID));
        return new ResponseData<Void>().success();
    }
}
