package com.hotel.flint.room.domain;

import com.hotel.flint.common.Option;
import com.hotel.flint.common.RoomView;
import com.hotel.flint.common.Season;
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
public class RoomPrices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomInfo roomInfo;

    @Enumerated(value = EnumType.STRING)
    private Season season;

    @Enumerated(value = EnumType.STRING)
    private Option isHoliday;

    @Enumerated(value = EnumType.STRING)
    private RoomView roomView;

    private Double additionalPercentage;

    @Enumerated(value = EnumType.STRING)
    private Option isWeekend;
}

