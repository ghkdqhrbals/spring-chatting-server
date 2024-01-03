package chatting.chat.entityDefault;

import com.example.commondto.format.DateFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Slf4j
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@MappedSuperclass
public class BaseTime {

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime createdAt;
    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime updatedAt;

    @PrePersist
    public void onPrePersist(){
        if (this.createdAt == null) {
            this.createdAt = DateFormat.getCurrentTime();
        }
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onPreUpdate(){
        this.updatedAt = DateFormat.getCurrentTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseTime baseTime)) return false;
        return Objects.equals(getCreatedAt(), baseTime.getCreatedAt()) && Objects.equals(getUpdatedAt(), baseTime.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreatedAt(), getUpdatedAt());
    }

    @Override
    public String toString() {
        return "BaseTime{" +
                "createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
