const urlParams = new URLSearchParams(window.location.search); //me va a  traer los paramtros de la url
const myParam = urlParams.get("gp"); //me consigue el parametro de mi pagina en particular que se llama 'gp' en este caso y es = a un numero uqe va a ser el player que quiero

let app = new Vue({
	el: "#app",
	data: {
		numbers: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
		letters: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
		information: [],
		actualPlayer: "",
		password: "",
		actualPlayerId: "",
		opponent: " waiting for opponent...",
		opponentId: "",
		games: [],
		location: [],
		message: "",
		counter: "",
		actualSalvoTurn: 0,
		newSalvo: null,
		salvoMessageOk: "",
		salvoMessageProblem: "",
		salvoShot: [],
		successMessage: "",
		shipsLocation: [],
		shipTypeSelected: "",
		shipList: [],
		options: {
			//grilla de 10 x 10
			column: 10,
			row: 10,
			//separacion entre elementos (les llaman widgets)
			verticalMargin: 0,
			//altura de las celdas, si esta en false cuando ahico la pantalla me pone todo en una sola columna
			disableOneColumnMode: true,
			//altura de las filas/celdas
			cellHeight: 40,
			//necesario
			float: true,
			//desabilitando el resize de los widgets
			disableResize: true,
			//false permite mover los widgets, true impide
			staticGrid: false,
		},
		//iniciando la grilla en modo libe statidGridFalse
		grid: null,
	},
	methods: {
		init: function () {
			fetch("/api/game_view/" + myParam, {
				//si dejo   /api/game_view/ me rechaza el mach
				method: "POST",
				body: JSON.stringify({ website: "eldevsin.site" }),
			})
				.then((response) => response.json())
				.then(function (myJson) {
					app.information = myJson;
					app.selector();
					app.gridinit();
					app.clearGrip();
					app.matchShip();
					app.matchSalvoes();
				});
		},
		gridinit: function () {
			if (app.information.ships.length > 0) {
				app.options.staticGrid = true;
			} else {
				app.addShip();
				app.shipMove();
			}
			this.grid = GridStack.init(this.options, "#grid");
		},
		selector: function () {
			this.information.gamePlayer.forEach((e) => {
				if (e.gpid == myParam) {
					app.actualPlayer = e.name;
					app.actualPlayerId = e.id;
				} else {
					app.opponent = " VS " + e.name;
					app.opponentId = e.id;
				}
			});
		},
		clearGrip: function () {
			this.letters.forEach((e) => {
				this.numbers.forEach((i) => {
					let elem = document.getElementById(e + i);
					if (elem != null && elem.classList.contains("hit")) {
						elem.classList.remove("hit");
					} else if (elem != null && elem.classList.contains("salvo")) {
						elem.classList.remove("salvo");
					}
				});
			});
			const ships = document.querySelectorAll(
				"#Submarine,#Carrier,#Patrol-Boat,#Destroyer,#Battleship"
			);
			app.grid.removeAll();
		},
		matchShip: function () {
			if (app.information.ships.length > 0) {
				app.information.ships.forEach((e) => {
					if (!app.shipList.includes(e.shipType)) {
						app.shipList.push;
					}
					if (e.locations[0].charCodeAt(0) == e.locations[1].charCodeAt(0)) {
						(x = parseInt(e.locations[0].charAt(1), 10)),
							(y = e.locations[0].charCodeAt(0) - 65),
							(w = e.locations.length + 1),
							app.grid.addWidget(
								'<div><div id="' +
									e.shipType +
									'" class="grid-stack-item-content ' +
									e.shipType +
									'Horizontal"></div><div/>',
								x,
								y,
								w,
								1
							);
					} else {
						app.grid.addWidget(
							'<div><div id="' +
								e.shipType +
								'" class="grid-stack-item-content ' +
								e.shipType +
								'Vertical"></div><div/>',
							(x = e.locations[0].charAt(1)),
							(y = e.locations[0].charCodeAt(0) - 65),
							1,
							(w = e.locations.length + 1)
						);
					}
				});
			}
		},
		addShip: function () {
			if (
				this.shipTypeSelected != "" &&
				!app.shipList.includes(this.shipTypeSelected)
			) {
				switch (this.shipTypeSelected) {
					case "Submarine":
						app.grid.addWidget(
							'<div><div id="Submarine" class="grid-stack-item-content SubmarineHorizontal"></div><div/>',
							1,
							1,
							3,
							1
						);
						app.shipList.push(this.shipTypeSelected);
						app.shipMove();
						break;
					case "Carrier":
						app.grid.addWidget(
							'<div><div id="Carrier" class="grid-stack-item-content CarrierHorizontal"></div><div/>',
							2,
							1,
							5,
							1
						);
						app.shipList.push(this.shipTypeSelected);
						app.shipMove();
						break;
					case "PatrolBoat":
						app.grid.addWidget(
							'<div><div id="Patrol-Boat" class="grid-stack-item-content Patrol-BoatHorizontal"></div><div/>',
							2,
							4,
							2,
							1
						);
						app.shipList.push(this.shipTypeSelected);
						app.shipMove();
						break;
					case "Destroyer":
						app.grid.addWidget(
							'<div><div id="Destroyer" class="grid-stack-item-content DestroyerHorizontal"></div><div/>',
							6,
							4,
							3,
							1
						);
						app.shipList.push(this.shipTypeSelected);
						app.shipMove();
						break;
					case "Battleship":
						app.grid.addWidget(
							'<div><div id="Battleship" class="grid-stack-item-content BattleshipHorizontal"></div><div/>',
							2,
							8,
							4,
							1
						);
						app.shipList.push(this.shipTypeSelected);
						app.shipMove();
						break;
				}
			}
		},
		matchSalvoes: function () {
			if (app.information.salvoes != "") {
				app.information.salvoes.forEach((e) => {
					e.salvoLocations.forEach((h) => {
						if (this.actualPlayerId == e.playerId) {
							//busco por el id de 'player' no por 'gamePlayer' porque en mis salvoes  guarde el id de 'player'
							var elem = document.getElementById(h);
							elem.classList.add("salvo");
							elem.innerHTML = e.salvoTurn;
							if (e.salvoTurn > app.actualSalvoTurn)
								app.actualSalvoTurn = e.salvoTurn;
						} else {
							var elem = document.getElementById(h);
							elem.classList.add("hit");
							elem.innerHTML = e.salvoTurn;
						}
					});
				});
			}
			app.shots();
		},
		shots: function () {
			const salvo = document.querySelectorAll("td.sea");
			app.salvoMessageOk =
				"you must position your shots still have " + 4 + " left";
			salvo.forEach((o) => {
				o.onclick = function (event) {
					let item = event.target;

					if (
						!item.classList.contains("hit") &&
						!item.classList.contains("salvo")
					) {
						if (!item.classList.contains("mira")) {
							if (app.salvoShot.length < 4) {
								let shot = item.id;
								item.classList.add("mira");
								app.salvoShot.push(shot);
								app.salvoMessageOk =
									"you still have " +
									(4 - app.salvoShot.length) +
									" salvos left";
							}
						} else {
							item.classList.remove("mira");
							var i = app.salvoShot.indexOf(item.id);
							if (i !== -1) {
								app.salvoShot.splice(i, 1);
							}
							app.salvoMessageOk =
								"you still have " + (4 - app.salvoShot.length) + " salvos left";
						}
					} else {
						app.salvoMessageProblem = "you have already shot there";
					}
				};
			});
		},
		//guaardo los salvos en mi base de datos
		sendSalvoes: function () {
			if (app.salvoShot.length == 4) {
				//convierto mis variables en un "unico" objeto salvo para enviarlo
				var arr = app.salvoShot;
				var obj = {
					salvoTurn: app.actualSalvoTurn + 1,
					salvoLocations: arr,
				};
				$.post({
					url: "/api/games/players/" + myParam + "/salvos",
					data: JSON.stringify(obj),
					dataType: "text",
					contentType: "application/json",
				})
					.done(function (response) {
						app.salvoMessageOk = "waiting for the opponent's shot...";
					})
					.fail(function (response) {
						app.salvoMessageProblem = response.responseText;
					});
			} else {
				app.salvoMessageProblem = "you must fire all the salvos before";
			}
		},
		logout: function () {
			$.post("/api/logout");
			history.back();
		},
		//regresa a la pagina de las tablas
		buttonBack: function () {
			history.back();
		},
		//una vez colocados los 5 barcos los envia a mi base de datos
		placeShips: function () {
			if (app.shipList.length < 5) {
				app.message =
					"There are still " +
					(5 - app.shipList.length) +
					" boats left, locate them please";
			} else {
				const ships = document.querySelectorAll(
					"#Submarine,#Carrier,#Patrol-Boat,#Destroyer,#Battleship"
				);
				ships.forEach((ship) => {
					let itemContent = ship.parentElement;
					let itemX = parseInt(itemContent.dataset.gsX);
					let itemY = parseInt(itemContent.dataset.gsY);
					let itemWidth = parseInt(itemContent.dataset.gsWidth);
					let itemHeight = parseInt(itemContent.dataset.gsHeight);
					if (itemWidth > itemHeight) {
						app.location = [];
						let n = 1;
						while (n <= itemWidth) {
							app.location.push(String.fromCharCode(itemY + 65) + (itemX + n));
							n++;
						}
					} else {
						app.location = [];
						let n = 0;
						while (n < itemHeight) {
							app.location.push(
								String.fromCharCode(itemY + 65 + n) + (itemX + 1)
							);
							n++;
						}
					}
					this.shipsLocation.push({
						shipType: ship.id,
						locations: this.location,
					});
				});
				$.post({
					url: "/api/games/players/" + myParam + "/ships",
					data: JSON.stringify(app.shipsLocation),
					dataType: "text",
					contentType: "application/json",
				})
					.done(function (response) {
						app.successMessage = "You are ready ";
						app.init();
						$("#button").prop("disabled", true);
					})
					.fail(function (response) {
						app.message = response;
					});
			}
		},
		//permite mover los barcos de 90° a 0°
		shipMove: function () {
			const ships = document.querySelectorAll(
				"#Submarine,#Carrier,#Patrol-Boat,#Destroyer,#Battleship"
			);
			ships.forEach((ship) => {
				//asignando el evento de click a cada nave
				ship.parentElement.onclick = function (event) {
					//obteniendo el ship (widget) al que se le hace click
					//event.target =>  Una referencia a un objeto que lanzo el evento
					let itemContent = event.target;
					//obteniendo valores del widget
					let itemX = parseInt(itemContent.parentElement.dataset.gsX);
					let itemY = parseInt(itemContent.parentElement.dataset.gsY);
					let itemWidth = parseInt(itemContent.parentElement.dataset.gsWidth);
					let itemHeight = parseInt(itemContent.parentElement.dataset.gsHeight);

					//si esta horizontal se rota a vertical sino a horizontal
					if (itemContent.classList.contains(itemContent.id + "Horizontal")) {
						//veiricando que existe espacio disponible para la rotacion
						if (
							app.grid.isAreaEmpty(
								itemX,
								itemY + 1,
								itemHeight,
								itemWidth - 1
							) &&
							itemY + (itemWidth - 1) <= 9
						) {
							//la rotacion del widget es simplemente intercambiar el alto y ancho del widget, ademas se cambia la clase
							app.grid.resize(itemContent.parentElement, itemHeight, itemWidth);
							itemContent.classList.remove(itemContent.id + "Horizontal");
							itemContent.classList.add(itemContent.id + "Vertical");
						} else {
							alert("Espacio no disponible");
						}
					} else {
						if (
							app.grid.isAreaEmpty(
								itemX + 1,
								itemY,
								itemHeight - 1,
								itemWidth
							) &&
							itemX + (itemHeight - 1) <= 9
						) {
							app.grid.resize(itemContent.parentElement, itemHeight, itemWidth);
							itemContent.classList.remove(itemContent.id + "Vertical");
							itemContent.classList.add(itemContent.id + "Horizontal");
						} else {
							alert("Espacio no disponible");
						}
					}
				};
			});
		},
	},
});
//cuando entro a la pagina desde games.html me mantiene la sesión
function stayLoggedIn() {
	if (sessionStorage.getItem("user") != null) {
		$.post("/api/login", {
			userName: sessionStorage.getItem("user"),
			password: sessionStorage.getItem("pass"),
		}).done(function () {
			app.init();
		});
	}
}
stayLoggedIn();
