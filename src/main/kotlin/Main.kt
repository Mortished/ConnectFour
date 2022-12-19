
fun main() {
    val game = ConnectFourGame
    game.setPlayerNames()
    game.setBoardDimensions()
    val count = game.chooseMultipleGames()
    game.start(count)
}
