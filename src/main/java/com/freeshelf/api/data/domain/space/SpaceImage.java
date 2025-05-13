package com.freeshelf.api.data.domain.space;

import com.freeshelf.api.data.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "space_images", indexes = {@Index(name = "idx_image_space", columnList = "space_id"),
    @Index(name = "idx_image_primary", columnList = "primary_flag")})
public class SpaceImage extends BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "space_image_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "space_id")
  @ToString.Exclude
  private StorageSpace space;

  @Column(nullable = false)
  private String imageUrl;

  @Column(length = 200)
  private String caption;

  @Column(name = "primary_flag", nullable = false)
  private boolean primary = false;

}
