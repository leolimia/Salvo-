package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;


@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime date;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> scores;

    public Game() {
    }

    public Game(LocalDateTime date) {
        this.date = date;
    }

    public List<Player> getPlayers() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gameId", id);
        dto.put("created", date);
        dto.put("players", gamePlayers.stream()
                .map(GamePlayer::toDTO)
                .collect(toSet()));
        return dto;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public long getId() {
        return id;
    }
}
//axius...
            //si lo quisiese convertir de LocalDateTime a String usaria esto:
        /*    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            String formattedDateTime = date.format(formatter);
            return "On date: "+formattedDateTime;*/

