const socket = io();

let playerTurn = 1;
let playerPositions = [1, 1];
const boardSize = 100;

document.addEventListener("DOMContentLoaded", () => {
  const board = document.getElementById("board");
  const diceResult = document.getElementById("dice-value");
  const rollDiceButton = document.getElementById("roll-dice");
  const turnIndicator = document.getElementById("turn-indicator");

  generateBoard(board);
  updateTurnIndicator();

  rollDiceButton.addEventListener("click", () => {
    if (!rollDiceButton.disabled) {
      rollDice(playerTurn);
    }
  });

  socket.on("update-game", ({ player, newPosition }) => {
    playerPositions[player - 1] = newPosition;
    updateBoard();
    playerTurn = playerTurn === 1 ? 2 : 1; // Switch turns
    updateTurnIndicator();
  });

  socket.on("dice-result", ({ diceRoll }) => {
    diceResult.textContent = diceRoll;
  });

  socket.on("game-finished", ({ winner }) => {
    alert(`Player ${winner} wins the game!`);
    document.getElementById("roll-dice").disabled = true; // Disable button after game is finished
  });
});

function generateBoard(board) {
  const snakesAndLadders = {
    4: 25, 21: 32, 29: 77,
    30: 7, 43: 75, 47: 16,
    55: 12, 63: 71, 80: 89,
    78: 60, 82: 42, 93: 56,
    99: 76
  };

  let html = "";
  for (let row = 10; row > 0; row--) {
    html += "<tr>";
    for (let col = 1; col <= 10; col++) {
      const cellNumber = (row - 1) * 10 + col;
      let cellClass = "";

      if (snakesAndLadders[cellNumber]) {
        cellClass = snakesAndLadders[cellNumber] > cellNumber ? "ladder" : "snake";
      }

      html += `<td id="cell-${cellNumber}" class="${cellClass}">${cellNumber}</td>`;
    }
    html += "</tr>";
  }
  board.innerHTML = html;
  updateBoard(); // Initial call to set player positions on the board
}

function updateBoard() {
  // Remove previous player positions
  document.querySelectorAll(".player-token").forEach(token => token.remove());

  // Add new player positions
  playerPositions.forEach((position, index) => {
    const cell = document.getElementById(`cell-${position}`);
    if (cell) {
      const playerToken = document.createElement("div");
      playerToken.className = `player-token player-${index + 1}`;
      playerToken.style.backgroundColor = index === 0 ? 'blue' : 'yellow';
      playerToken.style.position = 'absolute';
      playerToken.style.width = '30px';
      playerToken.style.height = '30px';
      playerToken.style.borderRadius = '50%';
      playerToken.style.top = '50%'; // Center vertically
      playerToken.style.left = '50%'; // Center horizontally
      playerToken.style.transform = 'translate(-50%, -50%)'; // Adjust for centering
      cell.style.position = 'relative'; // Ensure the cell is positioned relatively
      cell.appendChild(playerToken);
    }
  });
}

function rollDice(player) {
  const diceRoll = Math.floor(Math.random() * 6) + 1;
  socket.emit("roll-dice", { player, diceRoll });
  document.getElementById("dice-value").textContent = diceRoll;
}

function updateTurnIndicator() {
  const turnIndicator = document.getElementById("turn-indicator");
  turnIndicator.textContent = `Player ${playerTurn}'s Turn`;

  const rollDiceButton = document.getElementById("roll-dice");
  if (playerTurn === 1) {
    rollDiceButton.style.backgroundColor = 'blue';
    rollDiceButton.style.color = 'white';
  } else {
    rollDiceButton.style.backgroundColor = 'yellow';
    rollDiceButton.style.color = 'black';
  }

  rollDiceButton.disabled = !(playerTurn === 1 || playerTurn === 2);
}
