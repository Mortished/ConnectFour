
data class Player(val name: String, val gamePiece: Char)

object ConnectFourGame {
    private var numRows = 6
    private var numColumns = 7
    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var activePlayer: Player
    private lateinit var board: Array<Array<Char>>
    private var gamesCount: Int = 1
    private var firstPlayerScore: Int = 0
    private var secondPlayerScore: Int = 0

    init {
        println("Connect Four")
    }

    fun start(count: Int) {
        println("${player1.name} VS ${player2.name}")
        println("$numRows X $numColumns board")
        if (count == 1) {
            println("Single game")
            printBoard()
            play()
            println("Game over!")
        } else {
            println("Total $count games")
            for (i in 1..count) {
                println("Game #$i")
                printBoard()
                play()
                showScore()
                switchTurn()
                board = Array(numColumns) { Array(numRows) { ' ' } }
            }
            println("Game over!")
        }
    }

    private fun showScore() {
        println("Score")
        println("${player1.name}: $firstPlayerScore ${player2.name}: $secondPlayerScore")
    }

    fun play() {
        var input: String
        while(true) {
            println("${activePlayer.name}'s turn:")

            input = readln()

            if (input == "end") break

            if (!isNumber(input)) {
                println("Incorrect column number")
                continue
            }

            val column = input.toInt()

            if (!isInRange(column)) {
                println("The column number is out of range (1 - $numColumns)")
                continue
            }

            if (isFull(column)) {
                println("Column $column is full")
                continue
            }

            makeMove(column)
            printBoard()
            when (checkState()) {
                player1 -> {
                    println("Player ${player1.name} won")
                    firstPlayerScore += 2
                    break
                }
                player2 -> {
                    println("Player ${player2.name} won")
                    secondPlayerScore += 2
                    break
                }
            }
            if (boardFull()) {
                println("It is a draw")
                firstPlayerScore += 1
                secondPlayerScore += 1
                break
            }

            switchTurn()
        }
    }

    private fun boardFull(): Boolean {
        var topRow = ""
        repeat(numColumns) { column ->
            topRow += board[column][numRows - 1]
        }

        return (!topRow.contains(' '))
    }

    fun setPlayerNames() {
        println("First player's name:")
        player1 = Player(readLine()!!, 'o')
        println("Second player's name:")
        player2 = Player(readLine()!!, '*')

        activePlayer = player1
    }

    fun setBoardDimensions() {
        while(true) {
            println("Set the board dimensions (Rows x Columns)\n" +
                    "Press Enter for default ($numRows x $numColumns)")
            val input = readln().replace("\\s".toRegex(), "")

            if (input == "") break

            if (!input.matches("\\d+[xX]\\d+".toRegex())) {
                println("Invalid input")
                continue
            }

            val (rows: Int, columns: Int) = input.split("[xX]".toRegex()).map { it.toInt() }
            if (rows !in 5..9) {
                println("Board rows should be from 5 to 9")
                continue
            }
            if (columns !in 5..9) {
                println("Board columns should be from 5 to 9")
                continue
            }

            numRows = rows
            numColumns = columns
            break
        }

        board = Array(numColumns) { Array(numRows) { ' ' } }
    }

    private fun printBoard() {
        repeat(numColumns) { column -> print(" ${column + 1}") }
        println()

        for (row in numRows - 1 downTo 0) {
            repeat(numColumns) { column ->
                print("║${board[column][row]}")
            }
            println("║")
        }

        println("╚" + "═╩".repeat(numColumns - 1) + "═╝")
    }

    private fun checkState(): Player? {
        var winningPlayer = checkRows()
        if (winningPlayer != null) return winningPlayer

        winningPlayer = checkColumns()
        if (winningPlayer != null) return winningPlayer

        return checkDiagonals()
    }

    private fun checkDiagonals(): Player? {
        var upString = ""
        var downString = ""
        repeat (numColumns){ column ->
            for (row in numRows - 1 downTo 0) {
                upString += board[column][row]
                downString += board[column][row]
                for (i in 1..3) {
                    if (column + i <= numColumns - 1 && row + i < numRows) {
                        upString += board[column + i][row + i]
                    }
                    if (column + i <= numColumns - 1 && row - i >= 0) {
                        downString += board[column + i][row - i]
                    }
                }
                upString += "@"
                downString += "@"
            }
        }
        var winner = checkSequence(upString)
        if (null != winner) return winner
        winner = checkSequence(downString)
        if (null != winner) return winner

        return null
    }

    private fun checkColumns(): Player? {
        repeat(numColumns) { column ->
            var pieceString = ""
            repeat(numRows) { row ->
                pieceString += board[column][row]
            }
            val winner = checkSequence(pieceString)
            if (null != winner) return winner
        }

        return null
    }

    private fun checkRows(): Player? {
        var winner: Player? = null

        repeat(numRows) { row ->
            var pieceString = ""
            repeat(numColumns) { column ->
                pieceString += board[column][row]
            }
            winner = checkSequence(pieceString)
            if (null != winner) return winner
        }

        return winner
    }

    private fun checkSequence(pieceString: String): Player? {
        if (pieceString.contains(player1.gamePiece.toString().repeat(4))) return player1
        if (pieceString.contains(player2.gamePiece.toString().repeat(4))) return player2

        return null
    }

    private fun isFull(column: Int) = board[column - 1][numRows - 1] != ' '

    private fun makeMove(column: Int) {
        repeat(numRows) {
            if (board[column - 1][it] == ' ') {
                board[column - 1][it] = activePlayer.gamePiece
                return
            }
        }
    }

    private fun isInRange(input: Int) = input in 1..numColumns

    private fun switchTurn() {
        activePlayer = if (activePlayer == player1) player2 else player1
    }

    private fun isNumber(input: String) = input.matches("\\d+".toRegex())

    fun chooseMultipleGames(): Int {
        while (true) {
            println("""
      Do you want to play single or multiple games?
      For a single game, input 1 or press Enter
      Input a number of games:
    """.trimIndent())
            val input = readln()
            if (input.isEmpty()){
                break
            }
            try {
                gamesCount = input.toUInt().toInt()
                if (gamesCount == 0) throw NumberFormatException()
                break
            } catch (e: NumberFormatException) {
                println("Invalid input")
            }
        }
        return gamesCount
    }

}
