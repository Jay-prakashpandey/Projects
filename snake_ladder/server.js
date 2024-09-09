const express = require("express");
const http = require("http");
const socketIo = require("socket.io");
const path = require("path");

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

const port = process.env.PORT || 3000;
const playerPositions = [1, 1]; // Player 1 and 2 start at position 1

// Serve static files
app.use(express.static(path.join(__dirname, "public")));

io.on("connection", (socket) => {
  console.log("A player connected");

  socket.on("roll-dice", ({ player, diceRoll }) => {
    const newPosition = movePlayer(player, diceRoll);

    // Check if any player has won
    const winner = playerPositions.findIndex(pos => pos >= 100) + 1;
    if (winner) {
      io.emit("game-finished", { winner });
    } else {
      io.emit("update-game", { player, newPosition });
      io.emit("dice-result", { diceRoll });
    }
  });

  socket.on("disconnect", () => {
    console.log("A player disconnected");
  });
});

function movePlayer(player, diceRoll) {
  playerPositions[player - 1] += diceRoll;

  const snakesAndLadders = {
    4: 25, 21: 32, 29: 77,
    30: 7, 43: 75, 47: 16,
    55: 12, 63: 71, 80: 89,
    78: 60, 82: 42, 93: 56,
    99: 76
  };

  if (snakesAndLadders[playerPositions[player - 1]]) {
    playerPositions[player - 1] = snakesAndLadders[playerPositions[player - 1]];
  }

  if (playerPositions[player - 1] > 100) {
    playerPositions[player - 1] = 100; // Ensure the player does not exceed the board
  }

  return playerPositions[player - 1];
}

server.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
