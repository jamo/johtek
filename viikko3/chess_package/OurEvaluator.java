public class OurEvaluator extends Evaluator
{
  static double[][] blackBoostTable = { { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.15D, 0.2D, 0.2D, 0.15D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.2D, 0.3D, 0.3D, 0.2D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.15D, 0.1D, 0.1D, 0.15D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D } };

  static double[][] whiteBoostTable = { { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.15D, 0.1D, 0.1D, 0.15D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.2D, 0.3D, 0.3D, 0.2D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.15D, 0.2D, 0.2D, 0.15D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D } };

  public double eval(Position paramPosition)
  {
    int i = 0; int j = 0;
    if (6 == 6) i = 1;
    if (6 == 6) j = 1;

    double d = Math.random() - 0.5D;
    for (int k = 0; k < paramPosition.board.length; k++) {
      for (int m = 0; m < paramPosition.board[k].length; m++) {
        if (paramPosition.board[k][m] != 0) {
          if (paramPosition.board[k][m] == 1) d += 1000000000.0D;
          if (paramPosition.board[k][m] == 2) d += 9.0D;
          if (paramPosition.board[k][m] == 3) d += 5.25D;
          if (paramPosition.board[k][m] == 4) d += 3.25D;
          if (paramPosition.board[k][m] == 5) d += 3.0D;
          if (paramPosition.board[k][m] == 6) d += 1.0D;
          if (paramPosition.board[k][m] == 7) d -= 1000000000.0D;
          if (paramPosition.board[k][m] == 8) d -= 8.5D - 0.05D * m;
          if (paramPosition.board[k][m] == 9) d -= 4.75D - 0.05D * m;
          if (paramPosition.board[k][m] == 10) d -= 2.75D - 0.05D * m;
          if (paramPosition.board[k][m] == 11) d -= 2.5D - 0.05D * m;
          if (paramPosition.board[k][m] == 12) d -= 1.0D - 0.1D * m;

          if (Position.isWhitePiece(paramPosition.board[k][m]))
            d += whiteBoostTable[(m + j)][(k + i)];
          else if (Position.isBlackPiece(paramPosition.board[k][m])) {
            d -= blackBoostTable[(m + j)][(k + i)];
          }

        }

      }

    }

    return d;
  }
}