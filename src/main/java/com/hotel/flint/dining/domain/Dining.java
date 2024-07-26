package com.hotel.flint.dining.domain;

import com.hotel.flint.common.enumdir.DiningName;
import com.hotel.flint.common.enumdir.Option;
import com.hotel.flint.diningreservation.domain.DiningReservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Dining {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int id;
    @Column( nullable = false )
    @Enumerated(EnumType.STRING)
    private DiningName diningName;
    @Column( length = 255, nullable = false)
    private String location;
    @Column( nullable = false )
    @Enumerated(EnumType.STRING)
    private Option breakfastYn;
    @Column( nullable = false )
    private String callNum;
    @Column( nullable = false )
    private LocalTime openTime;
    @Column( nullable = false )
    private LocalTime closeTime;

    @OneToMany(mappedBy = "diningId", cascade = CascadeType.ALL )
    private List<DiningReservation> diningReservations;

}
