let app = new Vue({
	el: "#app",
	data: {
		players: [],
		leaderboard: [],
		playerSave: "",
		userName: "",
		password: "",
		message: "",
		nMessage: "",
		nGMessage: "",
		logplayer: "",
		logplayerId: "",
		userNameRules:
			"User name must be a valid email, contain at least 5 characters a '@' and cant contain spaces inside, at the beginning or end  ",
		passwordRules: "Password must be at least 2 characters or numbers",
		newUser: false,
	},
	methods: {
		init: function () {
			fetch("/api/games") //no uso http://localhost:8080/api/games uso solo /api/games porque Así indicas que desde la raíz del proyecto vas a ir a api y luego a games sin necesidad de saber en qué dominio estás (que puede cambiar).
				.then(function (response) {
					return response.json();
				})
				.then(function (myJson) {
					app.players = myJson;
					app.playerSave = myJson.player;
					app.clearLeaderboard();
					app.createLeaderboard();
					app.user();
					app.time();
				})
				.catch(function (error) {
					console.log("If there is any error you will catch them here");
				});
		},
		clearLeaderboard: function () {
			if (this.leaderboard != null) {
				app.leaderboard.splice(0);
			}
		},
		createLeaderboard: function () {
			app.players.games.forEach((e) => {
				e.players.forEach((i) => {
					var a = this.leaderboard.findIndex((h) => h.playerName == i.name);
					if (a === -1 && i.score != undefined) {
						var playerScore = {
							playerName: i.name,
							playerScore: i.score,
							playerWins: i.score == 1 ? 1 : 0,
							playerTie: i.score == 0.5 ? 1 : 0,
							playerLose: i.score == 0 ? 1 : 0,
						};

						this.leaderboard.push(playerScore); //una vez que termino de cargar los datos de mi nuevo jugador ahi si lo creo/pusheo en mi vue.
					} else {
						if (i.score != undefined) {
							app.leaderboard[a].playerScore += i.score;
							if (i.score == 1) {
								app.leaderboard[a].playerWins++;
							} else if (i.score == 0) {
								app.leaderboard[a].playerLose++;
							} else {
								app.leaderboard[a].playerTie++;
							}
						}
					}
				});
			});
		},
		back: function () {
			app.message = "";
			app.userName = "";
			app.password = "";
			app.logplayer = "";
			app.logplayerId = "";
		},
		sortedArray: function () {
			function compare(a, b) {
				return a.playerScore - b.playerScore;
			}
			return this.leaderboard.sort(compare);
		},
		login: function () {
			if (this.userCondition(this.userName, this.password)) {
				app.message = "";
				$.post("/api/login", {
					userName: this.userName,
					password: this.password,
				})
					.done(function () {
						sessionStorage.setItem("user", app.userName);
						sessionStorage.setItem("pass", app.password);
						var userData = sessionStorage.getItem("user");
						var userPass = sessionStorage.getItem("pass");
						app.init();
					})
					.fail(function () {
						app.message =
							"The User Name/Password specified is not registered or not valid :o ";
					});
			}
		},
		logout: function () {
			$.post("/api/logout").done(app.back);
			app.init();
		},
		newPlayer: function () {
			app.newUser = !app.newUser;
			app.back();
		},
		signin: function () {
			if (this.userCondition(this.userName, this.password)) {
				app.message = "";
				let userName = this.userName;
				let password = this.password;
				$.post("/api/players", {
					userName: userName,
					password: password,
				})
					.done(function () {
						app.nMessage = "New Player is ready congrats!";
						app.newUser = false;
						app.message = "";
						$.post("/api/login", {
							userName: userName,
							password: password,
						})
							.done(function () {
								app.init();
								this.user();
							})
							.fail(function () {
								app.message =
									"The User Name/Password specified is not registered or not valid :o ";
							});
					})
					.fail(function (json) {
						app.message = json.responseJSON.error;
					}, this.back());
			}
		},
		userCondition: function (name, password) {
			if (name.length < 5) {
				app.message = "User name must be 5 character longer";
			} else if (!name.includes("@")) {
				app.message = "User name must contain '@'";
			} else if (password.length < 2) {
				app.message = "Password must be atleast 2 character longer";
			} else if (/^ *$/.test(name)) {
				//\s means "any whitespace character" (spaces, tabs, vertical tabs, formfeeds, line breaks, etc.)
				app.message =
					"User name cant contain space inside, at the beginning or end :) ";
			} else {
				return true;
			}
		},
		newGame: function () {
			$.post("/api/games", { method: "POST" })
				.done(function (data) {
					app.goTo(data.gpid);
				})
				.fail(function () {
					app.nGMessage = "You must be Login to creat a New Game :/ ";
					$("#error").show(data.responseJSON.error);
				});
		},
		joinThisGame: function (gameId) {
			$.post("/api/games/" + gameId + "/players")
				.done(function (data) {
					app.goTo(data.gpid);
					$("#error").show("problema");
				})
				.fail(function (data) {
					app.message = data.responseJSON.error;
					$("#error").show("problema");
				});
		},
		user: function () {
			if (this.playerSave == null) {
				app.logplayer = "";
			} else {
				app.logplayer = this.players.player.email;
				app.logplayerId = this.players.player.PlayerId;
				app.message = "welcome  " + this.logplayer + " ready to play?";
			}
		},
		goTo: function (id) {
			window.location.href = "/web/game.html?gp=" + id;
		},
		time: function () {
			app.players.games.forEach((e) => {
				let date = new Date(e.created);
				e.created = ` ${date.getDate()}/${
					date.getMonth() + 1
				}/${date.getFullYear()}, ${date.getHours()}:${date.getMinutes()} Hs`;
			});
		},
	},
});

app.init();
