package com.freeshelf.api.data.domain.message;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conversations",
    indexes = {@Index(name = "idx_conv_users", columnList = "user1_id,user2_id"),
        @Index(name = "idx_conv_space", columnList = "space_id")})
public class Conversation extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "conversation_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user1_id", nullable = false)
  private User startNode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user2_id", nullable = false)
  private User endNode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "space_id")
  private StorageSpace space;

  @Column(nullable = false)
  private boolean active = true;

  @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
  private List<Message> messages = new ArrayList<>();

}


