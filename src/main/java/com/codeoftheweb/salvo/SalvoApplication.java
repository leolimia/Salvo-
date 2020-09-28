package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@SpringBootApplication
public class SalvoApplication {

	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}

	/*@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository,
									  SalvoRepository  salvoRepository,
									  ScoreRepository ScoreRepository) {
		return (args) -> {
			// save
			Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder.encode("24"));
			Player player2 = new Player("c.obrian@ctu.gov",passwordEncoder.encode("42"));
			Player player3 = new Player("kim_bauer@gmail.com",passwordEncoder.encode("kb"));
			Player player4 = new Player("t.almeida@ctu.gov",passwordEncoder.encode("mole"));

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

			Game game1 = new Game(LocalDateTime.now());
			Game game2 = new Game(LocalDateTime.now().plusHours(1));
			Game game3 = new Game(LocalDateTime.now().plusHours(2));
			Game game4 = new Game(LocalDateTime.now().plusHours(3));
			Game game5 = new Game(LocalDateTime.now().plusHours(4));
			Game game6 = new Game(LocalDateTime.now().plusHours(5));
			Game game7 = new Game(LocalDateTime.now().plusHours(6));
			Game game8 = new Game(LocalDateTime.now().plusHours(7));

			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);
			gameRepository.save(game7);
			gameRepository.save(game8);

			GamePlayer gamePlayerA1 = new GamePlayer(game1, player1);
			gamePlayerRepository.save(gamePlayerA1);
			GamePlayer gamePlayerA2 = new GamePlayer(game1, player2);
			gamePlayerRepository.save(gamePlayerA2);

			GamePlayer gamePlayerB1 = new GamePlayer(game2, player1);
			gamePlayerRepository.save(gamePlayerB1);
			GamePlayer gamePlayerB2 = new GamePlayer(game2, player2);
			gamePlayerRepository.save(gamePlayerB2);

			GamePlayer gamePlayerC1 = new GamePlayer(game3, player2);
			gamePlayerRepository.save(gamePlayerC1);
			GamePlayer gamePlayerC2 = new GamePlayer(game3, player4);
			gamePlayerRepository.save(gamePlayerC2);

			GamePlayer gamePlayerD1 = new GamePlayer(game4, player2);
			gamePlayerRepository.save(gamePlayerD1);
			GamePlayer gamePlayerD2 = new GamePlayer(game4, player1);
			gamePlayerRepository.save(gamePlayerD2);

			GamePlayer gamePlayerE1 = new GamePlayer(game5, player4);
			gamePlayerRepository.save(gamePlayerE1);
			GamePlayer gamePlayerE2 = new GamePlayer(game5, player1);
			gamePlayerRepository.save(gamePlayerE2);

			GamePlayer gamePlayerF1 = new GamePlayer(game6, player3);
			gamePlayerRepository.save(gamePlayerF1);

			GamePlayer gamePlayerG1 = new GamePlayer(game7, player4);
			gamePlayerRepository.save(gamePlayerG1);

			GamePlayer gamePlayerH1 = new GamePlayer(game8, player3);
			gamePlayerRepository.save(gamePlayerH1);
			GamePlayer gamePlayerH2 = new GamePlayer(game8, player4);
			gamePlayerRepository.save(gamePlayerH2);

			//game 1
			Ship ship1 = new Ship(gamePlayerA1, "Destroyer", Arrays.asList("H2", "H3", "H4"));
			shipRepository.save(ship1);
			Ship ship2 = new Ship(gamePlayerA1, "Submarine",Arrays.asList("E1", "F1", "G1"));
			shipRepository.save(ship2);
			Ship ship3 = new Ship(gamePlayerA1, "Patrol-Boat",Arrays.asList("B4", "B5"));
			shipRepository.save(ship3);
			Ship ship4 = new Ship(gamePlayerA2, "Destroyer",Arrays.asList("B5", "C5", "D5"));
			shipRepository.save(ship4);
			Ship ship5 = new Ship(gamePlayerA2, "Destroyer",Arrays.asList("F1", "F2"));
			shipRepository.save(ship5);

			//game2
			Ship ship6 = new Ship(gamePlayerB1, "Destroyer",Arrays.asList("B5", "C5", "D5"));
			shipRepository.save(ship6);
			Ship ship7 = new Ship(gamePlayerB1, "Patrol-Boat",Arrays.asList("C6", "C7"));
			shipRepository.save(ship7);
			Ship ship8 = new Ship(gamePlayerB2, "Submarine",Arrays.asList("H2", "H3", "H4"));
			shipRepository.save(ship8);
			Ship ship9 = new Ship(gamePlayerB2, "Patrol-Boat", Arrays.asList("G6", "H6"));
			shipRepository.save(ship9);

			//game3
			Ship ship10 = new Ship(gamePlayerC1, "Destroyer", Arrays.asList("B5", "C5", "D5"));
			shipRepository.save(ship10);
			Ship ship11 = new Ship(gamePlayerC1, "Patrol-Boat", Arrays.asList("C6", "C7"));
			shipRepository.save(ship11);
			Ship ship12 = new Ship(gamePlayerC2, "Submarine", Arrays.asList("A2", "A3", "A4"));
			shipRepository.save(ship12);
			Ship ship13 = new Ship(gamePlayerC2, "Patrol-Boat", Arrays.asList("G6", "H6"));
			shipRepository.save(ship13);

			//game4
			Ship ship14 = new Ship(gamePlayerD1, "Destroyer", Arrays.asList("B5", "C5", "D5"));
			shipRepository.save(ship14);
			Ship ship15 = new Ship(gamePlayerD1, "Patrol-Boat", Arrays.asList("C6", "C7"));
			shipRepository.save(ship15);
			Ship ship16 = new Ship(gamePlayerD2, "Submarine",Arrays.asList("A2", "A3", "A4"));
			shipRepository.save(ship16);
			Ship ship17 = new Ship(gamePlayerD2, "Patrol-Boat", Arrays.asList("G6", "H6"));
			shipRepository.save(ship17);

			//game5
			Ship ship18 = new Ship(gamePlayerE1, "Destroyer",Arrays.asList("B5", "C5", "D5"));
			shipRepository.save(ship18);
			Ship ship19 = new Ship(gamePlayerE1, "Patrol-Boat", Arrays.asList("C6", "C7"));
			shipRepository.save(ship19);
			Ship ship20 = new Ship(gamePlayerE2, "Submarine",Arrays.asList("A2", "A3", "A4"));
			shipRepository.save(ship20);
			Ship ship21 = new Ship(gamePlayerE2, "Patrol-Boat", Arrays.asList("G6", "H6"));
			shipRepository.save(ship21);

			//game6
			Ship ship22 = new Ship(gamePlayerF1, "Destroyer",Arrays.asList("B5", "C5", "D5"));
			shipRepository.save(ship22);
			Ship ship23 = new Ship(gamePlayerF1, "Patrol-Boat", Arrays.asList("C6", "C7"));
			shipRepository.save(ship23);

			//game8 salteo la G porrque no me crearon la batalla '7'
			Ship ship24 = new Ship(gamePlayerH1, "Destroyer",Arrays.asList("B5", "C5", "D5"));
			shipRepository.save(ship24);
			Ship ship25 = new Ship(gamePlayerH1, "Patrol-Boat", Arrays.asList("C6", "C7"));
			shipRepository.save(ship25);
			Ship ship26 = new Ship(gamePlayerH2, "Submarine", Arrays.asList("A2", "A3", "A4"));
			shipRepository.save(ship26);
			Ship ship27 = new Ship(gamePlayerH2, "Patrol-Boat", Arrays.asList("G6", "H6"));
			shipRepository.save(ship27);

			//Salvoes
			// Game 1 turn 1
			Salvo salvo1 = new Salvo(gamePlayerA1,1,Arrays.asList("B5", "C5", "F1"));
			salvoRepository.save(salvo1);
			Salvo salvo2 = new Salvo(gamePlayerA2,1,Arrays.asList("B4", "B5", "B6"));
			salvoRepository.save(salvo2);
			//Game 1 turn 2
			Salvo salvo3 = new Salvo(gamePlayerA1,2,Arrays.asList("F2", "D5"));
			salvoRepository.save(salvo3);
			Salvo salvo4 = new Salvo(gamePlayerA2,2,Arrays.asList("B4", "B5", "B6"));
			salvoRepository.save(salvo4);
			// Game 2 turn 1
			Salvo salvo5 = new Salvo(gamePlayerB1,1,Arrays.asList("A2", "A4", "G6"));
			salvoRepository.save(salvo5);
			Salvo salvo6 = new Salvo(gamePlayerB2,1,Arrays.asList("B5", "D5", "C7"));
			salvoRepository.save(salvo6);
			// Game 2 turn 2
			Salvo salvo7 = new Salvo(gamePlayerB1,2,Arrays.asList("A3", "H6"));
			salvoRepository.save(salvo7);
			Salvo salvo8 = new Salvo(gamePlayerB2,2,Arrays.asList("C5", "C6"));
			salvoRepository.save(salvo8);
			// Game 3 turn 1
			Salvo salvo9 = new Salvo(gamePlayerC1,1,Arrays.asList("G6", "H6", "A4"));
			salvoRepository.save(salvo9);
			Salvo salvo10 = new Salvo(gamePlayerC2,1,Arrays.asList("H1", "H2", "H3"));
			salvoRepository.save(salvo10);
			// Game3  turn 2
			Salvo salvo11 = new Salvo(gamePlayerC1,2,Arrays.asList("A2", "A3", "D8"));
			salvoRepository.save(salvo11);
			Salvo salvo12 = new Salvo(gamePlayerC2,2,Arrays.asList("E1", "F2", "G3"));
			salvoRepository.save(salvo12);
			// Game 4 turn 1
			Salvo salvo13 = new Salvo(gamePlayerD1,1,Arrays.asList("A3", "A4", "F7"));
			salvoRepository.save(salvo13);
			Salvo salvo14 = new Salvo(gamePlayerD2,1,Arrays.asList("B5", "C6", "H1"));
			salvoRepository.save(salvo14);
			// Game4  turn 2
			Salvo salvo15 = new Salvo(gamePlayerD1,2,Arrays.asList("A2", "G6", "H6"));
			salvoRepository.save(salvo15);
			Salvo salvo16 = new Salvo(gamePlayerD2,2,Arrays.asList("C5", "C7", "D5"));
			salvoRepository.save(salvo16);
			// Game 5 turn 1
			Salvo salvo17 = new Salvo(gamePlayerE1,1,Arrays.asList("A1", "A2", "A3"));
			salvoRepository.save(salvo17);
			Salvo salvo18 = new Salvo(gamePlayerE2,1,Arrays.asList("B5", "B6", "C7"));
			salvoRepository.save(salvo18);
			// Game 5  turn 2
			Salvo salvo19 = new Salvo(gamePlayerE1,2,Arrays.asList("G6", "G7", "G8"));
			salvoRepository.save(salvo19);
			Salvo salvo20 = new Salvo(gamePlayerE2,2,Arrays.asList("C6", "D6", "E6"));
			salvoRepository.save(salvo20);
			// Game 5  turn 3
			//Salvo salvo21 = new Salvo(gamePlayerE1,3,Arrays.asList("N/A"));
			//salvoRepository.save(salvo21);
			Salvo salvo22 = new Salvo(gamePlayerE2,3,Arrays.asList("H1", "H8"));
			salvoRepository.save(salvo22);

			//scores
			float  win = 1;
			float  tie = (float)0.5;
			float  lose = 0;


			Score Score1 = new Score(game1, player1, win);
			Score Score2 = new Score(game1, player2, lose);
			Score Score3 = new Score(game2, player1, tie);
			Score Score4 = new Score(game2, player2, tie);
			Score Score5 = new Score(game3, player2, win);
			Score Score6 = new Score(game3, player4, lose);
			Score Score7 = new Score(game4, player2, tie);
			Score Score8 = new Score(game4, player1, tie);
			ScoreRepository.save(Score1);
			ScoreRepository.save(Score2);
			ScoreRepository.save(Score3);
			ScoreRepository.save(Score4);
			ScoreRepository.save(Score5);
			ScoreRepository.save(Score6);
			ScoreRepository.save(Score7);
			ScoreRepository.save(Score8);
		};
	}*/

}

//----------------------Compare player UserName request in PlayerRepository-----------------------
@Configuration //tells Spring to create an instance of this class automatically
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder(); //  PasswordEncoder encrypt the passwords before storing them
	}

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(), //si lo encuentra al player me retorna un  User
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown player: " + inputName);
			}
		});
	}
}
//---------------------- Get authotization to see information   -----------------------
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception { //aqui defino las reglas
		http.authorizeRequests()
				.antMatchers("/api/games", "/api/players", "/api/login","/favicon.ico").permitAll() //modifies the authorization requirements for authorizeRequests
				.antMatchers("/api/**", "/web/game.html").hasAuthority("USER")
				.antMatchers("/web/**").permitAll()
				.anyRequest().authenticated()
				/*.antMatchers("/api/login", "/web/**", "/api/games").permitAll()//resumir mas...*/
				.and()
				.formLogin()
					.usernameParameter("userName") //que busca en mi pagina de login
					.passwordParameter("password")
					.loginPage("/api/login").permitAll()
					.defaultSuccessUrl("/web/games.html")
				.and()
					.logout().logoutUrl("/api/logout");


		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}
