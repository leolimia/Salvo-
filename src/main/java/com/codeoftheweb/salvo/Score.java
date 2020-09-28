package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toSet;

@Entity
public class Score {
    @Id     //es un numero que se ele asigna a cada 'Player' que se crea.
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private float score;

    private LocalDateTime finishDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id") //es el nombre de mi columna nada mas
    private Player player;

    public Score(){

    };

    public Score(Game game, Player player, float score) {
        this.score = score;
        this.finishDate = game.getDate().plusMinutes(30);
        this.game = game;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public double getScore() {
        return score;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

}