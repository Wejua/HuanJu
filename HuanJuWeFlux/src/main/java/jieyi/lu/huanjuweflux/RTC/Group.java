package jieyi.lu.huanjuweflux.RTC;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("`group`")
public class Group {
    @Id
    private Long id;
    private String name;
    private String description;
    private String avatar;
    private Long ownerId;
    private Integer maxMembers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
