package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toSet;

@RestController
    @RequestMapping("/api")
    public class SalvoController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private ShipRepository shipRepo;

    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @Autowired
    private SalvoRepository salvoRepo;

    @Autowired
    private PlayerRepository playerRepository;

    //---------------------------------------- GET GAME -------------------------------------------------
    //es un servlets
    @GetMapping("/games")
    //'getGameIds' es el nombre de mi funcion
    public Map<String, Object> getGames(Authentication authentication) {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if(!isGuest(authentication)) {
            dto.put("player", (playerRepository.findByUserName(authentication.getName())).toDTO());
        } else {
            // dto.put("player is UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
            dto.put("player", null);
        }
       dto.put("games", gameRepo.findAll().stream().
               //'gameRepo' voy al repositorio a buscar los juegos, ''gameRepo.findAll()' seria la lista,  que fui a buscar.map(Game::toDTO)
               map(Game::toDTO).collect(toSet()));
        return dto;
    }

    //---------------------------------------- Create NEW GAME -------------------------------------------------
    //es un servlets
    @PostMapping("/games")
    //'getGameIds' es el nombre de mi funcion
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {

        ResponseEntity<Map<String, Object>> response;

        if(isGuest(authentication)) {
            response = new ResponseEntity<>(makeMapObject("error","player is UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
        } else {
            Game newGame =   gameRepo.save(new Game(LocalDateTime.now()));
            //para  no usar 'cascade = CascadeType.PERSIST' en game en gameplayer  portque si no tendria que modificar como guarde los games en 'salvoAplication' lo que hago es crearlo el game guardar al game y despues guardarlo en 'gameplayer'
            Player thisPlayer = playerRepository.findByUserName(authentication.getName());
            GamePlayer newGamePlayer = gamePlayerRepo.save(new GamePlayer(newGame, playerRepository.save(thisPlayer)));

            response = new ResponseEntity<>(makeMapObject("gpid",newGamePlayer.getId()),HttpStatus.CREATED);
        }
        //hay que evitar hacer llamados a funciones o a algo en un return
        return response;
    }

    //---------------------------------------- Join a actual game-------------------------------------------------
    //es un servlets
    @RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
    //'getGameIds' es el nombre de mi funcion
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {

        ResponseEntity<Map<String, Object>> response;
        Optional<Game> game = gameRepo.findById(gameId);

        if(isGuest(authentication)) {
            response = new ResponseEntity<>(makeMapObject("error", "Player is UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
        } else if(!game.isPresent()){
            response = new ResponseEntity<>(makeMapObject("error", "Game Id doesnt exist"), HttpStatus.FORBIDDEN);
        } else if (game.get().getPlayers().size() > 1 ) {
            response = new ResponseEntity<>(makeMapObject("error", "Sorry, Game is full..."), HttpStatus.FORBIDDEN);
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            GamePlayer newGamePlayer = gamePlayerRepo.save(new GamePlayer(game.get(),player));
            response = new ResponseEntity<>(makeMapObject("gpid",newGamePlayer.getId()),HttpStatus.CREATED);
        }
        return response;
    }

    //---------------------------------------- Place Ships   -------------------------------------------------
    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable Long gamePlayerId, Authentication authentication,@RequestBody Set<Ship> ships) {

        ResponseEntity<Map<String, Object>> response;
        Optional<GamePlayer> gamePlayerPlace = gamePlayerRepo.findById(gamePlayerId);
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());

        if(isGuest(authentication)) {
            response = new ResponseEntity<>(makeMapObject("error", "no player logged in"), HttpStatus.UNAUTHORIZED);
        } else if  (!gamePlayerPlace.isPresent()){
            response = new ResponseEntity<>(makeMapObject("error", "Game Player Id doesnt exist"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerPlace.get().getPlayer().getId() != currentPlayer.getId()){
            response = new ResponseEntity<>(makeMapObject("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
        } else if(gamePlayerPlace.get().getShips().size() > 0){
            response = new ResponseEntity<>(makeMapObject("error", "Sorry, user already has ships placed"), HttpStatus.FORBIDDEN);
        } else {
            if (ships.size() > 0) {
                // ahora asigno a miiii gameplayer   mi set de ships
                gamePlayerPlace.get().addShips(ships);
                gamePlayerRepo.save(gamePlayerPlace.get());
                response = new ResponseEntity<>(makeMapObject("message", "Success"), HttpStatus.ACCEPTED);
            } else {
                response = new ResponseEntity<>(makeMapObject("error", "You dont  send any  ship"), HttpStatus.FORBIDDEN);
            }
        }
        return response;
    }

    //---------------------------------------- GET GAME VIEW -------------------------------------------------
    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId,Authentication authentication) {

        Optional<GamePlayer> gamePlayerPlace = gamePlayerRepo.findById(gamePlayerId);
        if(isGuest(authentication)) {
            return new ResponseEntity<>(makeMapObject("error", "no player logged in"), HttpStatus.FORBIDDEN);
            }

        Player player = (playerRepository.findByUserName(authentication.getName())); //authentication  es propio de Spring security y me dice que usuario esta autenticado actualmennte
         //necesito conseguir el gamePlayer que corresponde a "/game_view/{gamePlayerId}"

        if(gamePlayerPlace.get().getPlayer().getId() != player.getId()){ //si un jugador no es el principal no esta autorizado a entrar
            return new ResponseEntity<>(makeMapObject("error","Unauthorized"), HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>((gamePlayerPlace.get().game_view()), HttpStatus.OK); //el findbyId  nos tray el 'optional' que puede contener o no el gamePlayer y el 'get' es  el que me saca el gamePlayer del optional
        }
    }

    //---------------------------------------- AUTHENTICATION -------------------------------------------------
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    //---------------------------------------- WRONG GAME_PLAYER -------------------------------------------------
    private boolean wrongGamePlayer(GamePlayer gamePlayer, Player player) {
        boolean correctGP = gamePlayer.getPlayer().getId() != player.getId();
        return correctGP;
    }


    //---------------------------------------- CREATE NEW PLAYER (SIGN UP) -------------------------------------------------

    @PostMapping("/players") // me permite verificar si mi registro de usuario cumple con algunas condiciones y si es asi guarda el password
    public ResponseEntity<Map<String, Object>> register( //ResponseEntity represents the whole HTTP response
            @RequestParam String userName, @RequestParam String password) { //puedo poner todos los parametros que necesite

        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMapObject("error", "Missing data"), HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) !=  null) {
            return new ResponseEntity<>(makeMapObject("error","Name already in use"), HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(userName, passwordEncoder.encode(password))); //"PasswordEncoder" provide by Spring Boot generates 60-character encoded strings, prefixed by the encryption algorithm used.

        return new ResponseEntity<>(makeMapObject("message","Success"), HttpStatus.CREATED);
    }

    //---------------------------------------- MAKE MAP (Para retornar mensajes y errores) -------------------------------------------------
    private  Map<String,Object> makeMapObject(String key, Object value){
        Map<String,Object> map = new LinkedHashMap<>(); //LinkedHashMap se repetirá en el orden en que se colocaron las entradas en el mapa
        map.put(key,value);
        return map;
    }

    //---------------------------------------- Get and Store Salvoes -------------------------------------------------
    @PostMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> placeSalvoes(@PathVariable Long gamePlayerId, Authentication authentication,@RequestBody Salvo salvo) {

        ResponseEntity<Map<String, Object>> response;
        Optional<GamePlayer> gamePlayerSalvo = gamePlayerRepo.findById(gamePlayerId);
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());

        if(isGuest(authentication)) {
            response = new ResponseEntity<>(makeMapObject("error", "no player logged in"), HttpStatus.UNAUTHORIZED);
        } else if  (!gamePlayerSalvo.isPresent()){
            response = new ResponseEntity<>(makeMapObject("error", "Game Player Id doesnt exist"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerSalvo.get().getPlayer().getId() != currentPlayer.getId()){
            response = new ResponseEntity<>(makeMapObject("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
        } else if (newPlayerSalvo(salvo, gamePlayerSalvo.get().getSalvoes())) {
                //le pongo  el contador segun el tamaño de la cantidad de disparos hechos...si pongo un contador desde 0 puede reiniciarse y fallar
                salvo.setTurn(gamePlayerSalvo.get().getSalvoes().size() +1 );
                gamePlayerSalvo.get().addSalvoes(salvo);
                gamePlayerRepo.save(gamePlayerSalvo.get());
                response = new ResponseEntity<>(makeMapObject("message", "Succeed"), HttpStatus.CREATED);
            } else {
                response = new ResponseEntity<>(makeMapObject("error", "user already has salvoes send"), HttpStatus.FORBIDDEN);
            }
        return response;
    }
    //veo que este salvo que recibo no coincida su turno con los de los salvoes de ese gamePlayer
    private boolean newPlayerSalvo(Salvo newSalvo, Set<Salvo> salvoes){
        boolean exist = true;
        for(Salvo salvo : salvoes){
            if(salvo.getTurn() == newSalvo.getTurn()){
                exist = false;
            }
        }
        return exist;
    }
}


/*
    @Autowired
    private GamePlayerRepository gamePlayerRepo;

    @RequestMapping("/game_view/nn")
       public Set<Map<String, Object>> getGamePlayers() {
        return
                gamePlayerRepo.findAll().stream()
                        .map(GamePlayer::toDTO)
                        .collect(toSet());
    }
}

   /* @RequestMapping("/players")
    public List<Player> getPlayer() {
        return playerRepo.findAll();
    }
    @RequestMapping("/gamePlayers")
    public List<GamePlayer> getGamePlayer() {
        return gamePlayerRepo.findAll();
    }*/

    //Map<String, Object> game = new HashMap<String, Object>();
    //  game.put("id", 1);
    //  game.put("created", 1456438201629);
    //  game.put("id", 2);
    //  game.put("created", 1456438201636);
    // game.put("id", 3);

//los restcontrollers me  convierte la respuesta en un JSON