package com.hotel.flint.reserve.room.domain;

import com.hotel.flint.common.enumdir.RoomState;
import com.hotel.flint.common.enumdir.RoomView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomInfo roomInfo;

    private Integer roomNumber;

    @Enumerated(value = EnumType.STRING)
    private RoomState roomState;

    @Enumerated(value = EnumType.STRING)
    private RoomView roomView;

    @Column(nullable = false)
    private Integer maxOccupancy;
    @Column(nullable = false)
    private Integer roomArea;

}
