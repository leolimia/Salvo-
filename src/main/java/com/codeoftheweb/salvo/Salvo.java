package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {
    @Id     //es un numero que se ele asigna a cada 'Player' que se crea
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ManyToOne(fetch = FetchType.EAGER) //le estoy pasando varios 'gamePlayer' a cada 'ship'
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer; //el nombre de la propiedad gamePlayer corresponde con el valor del mappedBy definido en la Entidad GamePlayer de el OneToMany, ya que de lo contrario JPA nos arrojará un error.

    @ElementCollection
// for adding lists of simple values, such as numbers and strings, to an entity. @ElementCollection is used to create lists of embeddable objects. An embeddable object is data intended for use only in the object containing it.
    @Column(name = "salvoLocations")
    private List<String> salvoLocations = new ArrayList<>();

    public Salvo(GamePlayer gamePlayer, int turn, List<String> salvoLocations) {
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocations = salvoLocations;
    }

    public void setGameplayer(GamePlayer gameplayer) {
        this.gamePlayer = gameplayer;
    }

    //siempre tengo que colocar el constructor por defecto sino spring no puede inicializarse.
    public Salvo() {
    }

    public long getId() {
        return id;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public long getTurn() {
        return turn;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("playerId", gamePlayer.getPlayer().getId());
        dto.put("salvoTurn", turn);
        dto.put("salvoLocations", salvoLocations);
        return dto;
    }
}
