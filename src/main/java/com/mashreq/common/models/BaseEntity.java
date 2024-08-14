package com.mashreq.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Base entity class all entities should extend.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseEntity implements Serializable {

  @Id
  @GeneratedValue
  private UUID id;

  @CreatedDate
  @Column(name = "created_on")
  private Instant createdOn;

  @LastModifiedDate
  @Column(name = "modified_on")
  private Instant modifiedOn;
}
