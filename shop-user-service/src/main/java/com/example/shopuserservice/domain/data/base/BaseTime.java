package com.example.shopuserservice.domain.data.base;

import com.example.commondto.format.DateFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass
public class BaseTime {

    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime createdAt;
    @Column(columnDefinition = "timestamp(6)")
    protected LocalDateTime updatedAt;

    @PrePersist
    public void onPrePersist(){
        this.createdAt = DateFormat.getCurrentTime();
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
