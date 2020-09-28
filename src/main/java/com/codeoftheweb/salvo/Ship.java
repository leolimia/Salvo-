package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {
    @Id     //es un numero que se ele asigna a cada 'Player' que se crea.
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String shipType;

    @ManyToOne(fetch = FetchType.EAGER) //le estoy pasando varios 'gamePlayer' a cada 'ship'
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection // for adding lists of simple values, such as numbers and strings, to an entity. @ElementCollection is used to create lists of embeddable objects. An embeddable object is data intended for use only in the object containing it.
    @Column(name="locations")
    private List<String> locations= new ArrayList<>();

    public Ship(GamePlayer gamePlayer, String shipType, List<String> locations ) { //constructor        this.gamePlayer=gamePlayer;
        this.locations = locations;
        this.gamePlayer =  gamePlayer;
        this.shipType = shipType;
    }

    public Ship() { };

    public void setGameplayer(GamePlayer gameplayer) {
        this.gamePlayer = gameplayer;
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGameplayer() {
        return gamePlayer;
    }

    public String getShipType() {
        return shipType;
    }

    public List<String> getLocations() {
        return locations;
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("shipId", id);
        dto.put("shipType", shipType);
        dto.put("locations", locations);
        return dto;
    }
}
