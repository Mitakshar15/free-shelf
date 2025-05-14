package com.freeshelf.api.data.domain.space;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.freeshelf.api.data.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "space_images", indexes = {@Index(name = "idx_image_space", columnList = "space_id"),
    @Index(name = "idx_image_primary", columnList = "primary_flag")})
public class SpaceImage extends BaseEntity {
  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "space_image_id")
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "space_id")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @ToString.Exclude
  private StorageSpace space;

  @Column(nullable = false)
  private String imageUrl;

  @Column(length = 200)
  private String caption;

  @Column(name = "primary_flag", nullable = false)
  private boolean primary = false;

}
