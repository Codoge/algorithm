## Board

Our game of Slider is played on an N x N square board, which comprises N 2 squaresknown as cells or positions. We number board positions using the notation (column, row),where (0,0) corresponds to the bottom-left position, and (N-1, N-1) corresponds to the top-right position, i.e., row indices decrease as we move down the board, while column indicesincrease as we move to the right.

Each cell can have up to 4 adjacent cells that are either vertically or horizontally adjacent(e.g., (1,1) has the adjacent cells (0,1), (1,0), (1,2), and (2,1), whereas (0,0) has only 2 adjacent cells).

## Players

There are two players named Vertical and Horizontal. Each player has their own set ofpieces that they can control on the board. We will denote a piece for player Vertical as V,and a piece for player Horizontal as H.

## Example of piece positions

Below is an example of a 6x6 board. It has three H pieces at cells (3,5), (2,4) and (2,3),two V pieces at (3,4) and (0,0), and a blocked at cell (4,3).

![Figure 1](https://github.com/Codoge/algorithm/blob/master/GameAI/SliderGame/pic/Figure%201.png)



## Objective and rules of the game

The initial state of the game is illustrated below for the case of a 6x6 board. The H piecesstart in the top N-1 positions of the leftmost column of the board (i.e., in column 0, rows 1to N-1), while the V pieces start in the rightmost N-1 positions of the bottom row of theboard (i.e., in row 0, column 1 to N-1). Additionally, a selection of positions may be chosenat the beginning of the game to be blocked for the duration of the game.

![屏幕快照 2017-05-27 上午10.53.41](https://github.com/Codoge/algorithm/blob/master/GameAI/SliderGame/pic/Figure%202.png)



A player is chosen arbitrarily to make the first move, and then players take turns at movingone of their pieces. At each move, a player is allowed to move only one piece to an emptycell, i.e., not occupied by another piece or blocked. Player H can move their pieces eitherup, down, or to the right, but never to the left. Player V can move their pieces either left,right, or up, but never down. A piece can be moved only into an adjacent square, eitherhorizontally or vertically, i.e., a piece cannot be moved more than one square at a time,and cannot move diagonally. For example, in the board shown in Figure 3 below, H canmove into any one of the squares marked with an x, while V can move into any one of thesquares marked with a y. H pieces are not allowed to cross the top, bottom, or left edgesof the board. Similarly, V pieces are not allowed to cross the bottom, left, or right edges ofthe board.

![屏幕快照 2017-05-27 上午10.53.48](https://github.com/Codoge/algorithm/blob/master/GameAI/SliderGame/pic/Figure%203.png)

If a player has no legal moves to make on their turn, then they must pass, forfeiting theirturn. Passing is not allowed unless the player has no legal moves. If both players areforced to pass in consecutive turns, then the game ends in a tie.

The objective of each player is to move their pieces off the edge of the board that isopposite their starting position (i.e., the right edge for H and the top edge for V). The firstplayer to achieve this wins the game. For example, in Figure 3, Player V can win if theyhave the next move and if they move their final piece up for their next 3 moves. If Player Hhas the next move, H can win by moving their final piece right for their next 4 moves.