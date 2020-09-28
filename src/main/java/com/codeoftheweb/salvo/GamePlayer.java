package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
public class GamePlayer {
    @Id     //es un numero que se ele asigna a cada 'GamePlayer' que se crea.
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id") //es el nombre de mi columna nada mas
    private Player player; //creo la clase con la que la voy a relacionar

    @ManyToOne(fetch = FetchType.EAGER) //cascade = CascadeType.ALL
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL) // indica que es una relación bidireccional, es decir, la Entidad 'Salvo' tendrá también una relación hacia la Entidad 'gamePlayer'.
    private Set<Salvo> salvoes = new HashSet<>();

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.date = LocalDateTime.now();
    }

    public GamePlayer() {
    }

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public Score getScore() {
        return player.getScore(this.game); //a este  player en particular  le paso 'this.game' para que me traiga  este  juego en particular
    }

    public void addShips(Set<Ship> ships) { //necesito el metodo 'addShips' para que pueda crear y agregar barcos a mi lista de GamePlayers
        ships.forEach(ship -> {
            ship.setGameplayer(this); //a este ship le agrego 'ESTE' game player
            this.ships.add(ship);// en mi lista de ships de este gameplayer guardo el ship que tiene adentro a este mismo gameplayer
        });
    }
    public void addSalvoes(Salvo salvo) {
        salvo.setGameplayer(this); //a este ship le agrego 'ESTE' game player
            this.salvoes.add(salvo);
        };


    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gpid", id);
        dto.put("id", player.getId());
        dto.put("name", player.getUserName());
        if(getScore() != null) {
            dto.put("score", getScore().getScore()); //uso el metodo 'getscore' para traer de este player de gameplayer el  escore
        }
        return dto;
    }

   /* public GamePlayer getOpponent() {
        GamePlayer opponent = new GamePlayer();
        Set<GamePlayer> gamePlayers = this.getGame().getGamePlayers();
        for (GamePlayer gamePlayer: gamePlayers) {
            if (gamePlayer.getId() != this.id) {
                opponent = gamePlayer;
            }
        }
        return opponent;
    }*/

    public Map<String, Object> game_view() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        //dto.put("idOfGamePlayer", id); ya se lo estoy pasando arriba
        dto.put("created", game.getDate());
        dto.put("gamePlayer", game.getGamePlayers().stream()
                .map(GamePlayer::toDTO).collect(toSet()));
        dto.put("ships", ships.stream()  //
                .map(Ship::toDTO)
                .collect(toSet()));
        dto.put("salvoes", game.getGamePlayers().stream().flatMap(gamePlayer -> gamePlayer.getSalvoes().stream().map(Salvo::toDTO)).collect(toSet()));
        return dto; /*tengo que entrar a game...que me  de cada gameplayer de ese  game,flatMap (me lo  transforma en elementos tipo  lista)  de  cada gamePlayer consigo los  salvos
        pero no quiero todo el salvo por  eso eso hago  un  map  para traer el DTO de salvo.*/
        }
}
