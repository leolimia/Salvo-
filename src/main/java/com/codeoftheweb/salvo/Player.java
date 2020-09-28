package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {
    @Id     //es un numero que se ele asigna a cada 'Player' que se crea
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;

    private String password;

    //mappedBy creo las relaciones entre las cosas.. en este caso con 'player'
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers; //le digo con que me lo va a relacionar

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores;

    public Player() {
    }

    public Player(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    public Score getScore(Game game){
        return scores.stream().filter(e->e.getGame().getId() == game.getId()).findFirst().orElse(null);
        //este metodo getScore que lo llamo en gamePlayer... me retorna un score filtrando de mi lista de 'scores', y me meto en getgame().getId() para filtrar el game que es igual a mi this.game que le paso en gamePlayer.
    }

    @JsonIgnore
    public List<Game> getGames() {
        return gamePlayers.stream().map(sub -> sub.getGame()).collect(toList());
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("PlayerId", id);
        dto.put("email", userName);
        return dto;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }
}




