package com.project01.notificationservice.entity;

import com.project01.notificationservice.enums.NotificationChannel;
import com.project01.notificationservice.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * [Danh mục] Mẫu nội dung thông báo. Sửa nội dung email chỉ cần sửa document này, không phải deploy lại.
 * Biến trong mẫu viết dạng {{tenBien}}; biến có hậu tố "Html" được chèn nguyên văn (đã escape từ trước).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ntf_templates")
public class NotificationTemplate {

    @Id
    private String id;

    @Indexed(unique = true)
    private NotificationType code;

    private NotificationChannel channel;
    private String subject;
    private String body;
    private boolean sensitive;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
