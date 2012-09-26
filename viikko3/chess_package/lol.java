import java.util.List;
import java.util.ArrayList;

public class lol extends Evaluator
{

	private static final int WIDTH = Position.bCols;
	private static final int HEIGHT = Position.bRows;

	private static class MN // short for MagicalNumbers... but lazy :P
	{
		static final double KING = 1e9;
		static final double QUEEN = 12;
		static final double ROOK = 6;
		static final double BISHOP = 4;
		static final double KNIGHT = 4;
		static final double PAWN = 1;

		static final double BOTH_ROOK_BONUS = 0.55;
		static final double BOTH_BISHOP_BONUS = 0.50;
		static final double BOTH_KNIGHT_BONUS = 0.30;

		static final double PAWN_RANK_WEIGHT = 0.15;

		// add score by each pieces movability.
		// some, like queen and rooks should be able to move
		// as well as possible,
		// king, on the other hand should be well protected.
		static final double KING_MOVABILITY = -0.05;
		static final double QUEEN_MOVABILITY = 0.06;
		static final double ROOK_MOVABILITY = 0.15;
		static final double BISHOP_MOVABILITY = 0.1;
		static final double KNIGHT_MOVABILITY = 0.15;
		static final double PAWN_MOVABILITY = 0.0;

		// eng game king pushage towards enemy king, penaltize the
		// farther we are.
		static final double KING_PUSHAGE = -0.07;

		// more specific pawn ranking,
		// white has mirrored y.
		public static final double PWN_RANKING_TABLE[] = {
			 0,  0,  0,  0,  0,  0,
			 1,  1,  1,  1,  1,  1,
			.5, .5, .7, .7, .5, .5,
			.1, .1, .3, .3, .1, .1,
			 0,-.1,-.2,-.2,-.1,  0,
			 0,  0,  0,  0,  0,  0,
		};
	};

	private class Square
	{
		int x, y;
		int piece;

		public Square(int x, int y, int piece)
		{
			this.x = x;
			this.y = y;
			this.piece = piece;
		}
	};

	private class Pair
	{
		Square a;
		Square b;

		public Pair() { a = null; b = null; }

		public void add(Square s) {
			if (a == null) {
				a = s;
			} else {
				b = s;
			}
		}

		public int count() {
			int ret = 0;
			if (a != null) {
				ret += 1;
			}
			if (b != null) {
				ret += 1;
			}
			return ret;
		}
	};

	private static class MovementGenerator
	{
		public MovementGenerator() {}

		public static boolean outOfBounds(int x, int y)
		{
			return (x >= WIDTH || y >= HEIGHT) || (x < 0 || y < 0);
		}

		public static boolean freeSquare(Position p, int fx, int fy, int tx, int ty)
		{
			if (outOfBounds(tx, ty)) {
				return false;
			}

			return (p.board[tx][ty] == 0);
		}

		// move counters.
		public static int countRook(Position p, Square s)
		{
			if (s == null) {
				return 0;
			}

			int n = 0;

			// right
			for (int i=1; i<6; i++) {
				if (freeSquare(p, s.x, s.y, s.x+i, s.y)) n++;
				else break;
			}

			// up
			for (int i=1; i<6; i++) {
				if (freeSquare(p, s.x, s.y, s.x, s.y+i)) n++;
				else break;
			}

			// left
			for (int i=1; i<6; i++) {
				if (freeSquare(p, s.x, s.y, s.x-i, s.y)) n++;
				else break;
			}

			// down
			for (int i=1; i<6; i++) {
				if (freeSquare(p, s.x, s.y, s.x, s.y-i)) n++;
				else break;
			}

			return n;
		}

		public static int countKnight(Position p, Square s)
		{
			if (s == null) {
				return 0;
			}

			int n=0;

			if (freeSquare(p, s.x, s.y, s.x+1, s.y+2)) ++n;
			if (freeSquare(p, s.x, s.y, s.x+2, s.y+1)) ++n;
			if (freeSquare(p, s.x, s.y, s.x+2, s.y-1)) ++n;
			if (freeSquare(p, s.x, s.y, s.x+1, s.y-2)) ++n;
			if (freeSquare(p, s.x, s.y, s.x-1, s.y-2)) ++n;
			if (freeSquare(p, s.x, s.y, s.x-2, s.y-1)) ++n;
			if (freeSquare(p, s.x, s.y, s.x-2, s.y+1)) ++n;
			if (freeSquare(p, s.x, s.y, s.x-1, s.y+2)) ++n;

			return n;
		}

		public static int countBishop(Position p, Square s)
		{
			if (s == null) {
				return 0;
			}

			int t=0, n=0, m=0;

			for (int w=0; w<4; ++w) {
				switch (w) {
				case 0: n=-1; m=-1; break;
				case 1: n=-1; m= 1; break;
				case 2: n= 1; m=-1; break;
				case 3: n= 1; m= 1; break;
				}

				for (int i=1; i<6; ++i) {
					if (freeSquare(p, s.x, s.y, s.x+(i*m), s.y+(i*n))) ++t;
					else break;
				}
			}

			return t;
		}

		public static int countQueen(Position p, Square s)
		{
			int n=0;

			n += countRook(p, s);
			n += countBishop(p, s);

			return n;
		}

		public static int countKing(Position p, Square s)
		{
			if (s == null) {
				return 0;
			}

			int n=0;

			if (freeSquare(p, s.x, s.y, s.x+1, s.y+1)) ++n;
			if (freeSquare(p, s.x, s.y, s.x+1, s.y  )) ++n;
			if (freeSquare(p, s.x, s.y, s.x+1, s.y-1)) ++n;
			if (freeSquare(p, s.x, s.y, s.x  , s.y-1)) ++n;
			if (freeSquare(p, s.x, s.y, s.x-1, s.y-1)) ++n;
			if (freeSquare(p, s.x, s.y, s.x-1, s.y  )) ++n;
			if (freeSquare(p, s.x, s.y, s.x-1, s.y+1)) ++n;
			if (freeSquare(p, s.x, s.y, s.x  , s.y+1)) ++n;

			return n;
		}

	};

	private double getPawnRankingTableValue(Position p, Square s)
	{
		if (p.board[s.x][s.y] <= 6) { // white
			int index = s.x + (HEIGHT - 1 - s.y) * HEIGHT;
			double value = MN.PWN_RANKING_TABLE[index];
			return MN.PWN_RANKING_TABLE[s.x + (HEIGHT - 1 - s.y) * HEIGHT];
		} else {
			int index = s.x + s.y * HEIGHT;
			double value = MN.PWN_RANKING_TABLE[index];
			return MN.PWN_RANKING_TABLE[s.x + s.y * HEIGHT];
		}
	}

	public double eval(Position p)
	{
		double white = 0;
		double black = 0;

		Square whiteKing = null, blackKing = null;
		Square whiteQueen = null, blackQueen = null;
		Pair whiteRooks = new Pair(), blackRooks = new Pair();
		Pair whiteBishops = new Pair(), blackBishops = new Pair();
		Pair whiteKnights = new Pair(), blackKnights = new Pair();
		List<Square> whitePawns = new ArrayList<Square>();
		List<Square> blackPawns = new ArrayList<Square>();

		for(int x = 0; x < p.board.length; ++x) {
			for(int y = 0; y < p.board[x].length; ++y) {
				if (p.board[x][y] == Position.Empty) {
					continue;
				} else if (p.board[x][y] == Position.WKing) {
					white += MN.KING;
					whiteKing = new Square(x, y, Position.WKing);
				} else if (p.board[x][y] == Position.WQueen) {
					white += MN.QUEEN;
					whiteQueen = new Square(x, y, Position.WQueen);
				} else if (p.board[x][y] == Position.WRook) {
					white += MN.ROOK;
					whiteRooks.add(new Square(x, y, Position.WRook));
				} else if (p.board[x][y] == Position.WBishop) {
					white += MN.BISHOP;
					whiteBishops.add(new Square(x, y, Position.WBishop));
				} else if (p.board[x][y] == Position.WKnight) {
					white += MN.KNIGHT;
					whiteKnights.add(new Square(x, y, Position.WKnight));
				} else if (p.board[x][y] == Position.WPawn) {
					//white += (MN.PAWN + MN.PAWN_RANK_WEIGHT * (y-1));
					white += MN.PAWN;

					whitePawns.add(new Square(x, y, Position.BRook));
				} else if (p.board[x][y] == Position.BKing) {
					black += MN.KING;
					blackKing = new Square(x, y, Position.BKing);
				} else if (p.board[x][y] == Position.BQueen) {
					black += MN.QUEEN;
					blackQueen = new Square(x, y, Position.BQueen);
				} else if (p.board[x][y] == Position.BRook) {
					black += MN.ROOK;
					blackRooks.add(new Square(x, y, Position.BRook));
				} else if (p.board[x][y] == Position.BBishop) {
					black += MN.BISHOP;
					blackBishops.add(new Square(x, y, Position.BBishop));
				} else if (p.board[x][y] == Position.BKnight) {
					black += MN.KNIGHT;
					blackKnights.add(new Square(x, y, Position.BKnight));
				} else if (p.board[x][y] == Position.BPawn) {
					//black += (MN.PAWN + MN.PAWN_RANK_WEIGHT * (Math.abs(Position.bRows-2-y)));
					black += MN.PAWN;

					blackPawns.add(new Square(x, y, Position.BRook));
				}
			}
		}

		// pawn ranking
		for (Square s : whitePawns) {
			white += getPawnRankingTableValue(p, s);
		}
		for (Square s : blackPawns) {
			black += getPawnRankingTableValue(p, s);
		}

		// small bonuses for reserving both.
		if (whiteRooks.count() >= 2) {
			white += MN.BOTH_ROOK_BONUS;
		}
		if (whiteKnights.count() >= 2) {
			white += MN.BOTH_KNIGHT_BONUS;
		}
		if (whiteBishops.count() >= 2) {
			white += MN.BOTH_BISHOP_BONUS;
		}
		if (blackRooks.count() >= 2) {
			black += MN.BOTH_ROOK_BONUS;
		}
		if (blackKnights.count() >= 2) {
			black += MN.BOTH_KNIGHT_BONUS;
		}
		if (blackBishops.count() >= 2) {
			black += MN.BOTH_BISHOP_BONUS;
		}

		// rook movement bonus.
		white += MN.ROOK_MOVABILITY * MovementGenerator.countRook(p, whiteRooks.a);
		white += MN.ROOK_MOVABILITY * MovementGenerator.countRook(p, whiteRooks.b);
		black += MN.ROOK_MOVABILITY * MovementGenerator.countRook(p, blackRooks.a);
		black += MN.ROOK_MOVABILITY * MovementGenerator.countRook(p, blackRooks.b);
		// queen movement bonus
		white += MN.QUEEN_MOVABILITY * MovementGenerator.countQueen(p, whiteQueen);
		black += MN.QUEEN_MOVABILITY * MovementGenerator.countQueen(p, blackQueen);

		if (whitePawns.size() == 0) {
			if (whiteKing != null && blackKing != null) {
				// start pushing towards enemy king.
				double dx = whiteKing.x - blackKing.x;
				double dy = whiteKing.y - blackKing.y;
				double distance = Math.sqrt(dx*dx + dy*dy);
				white += MN.KING_PUSHAGE * distance;
			}
		} else {
			// protection, for the win!
			white += MN.KING_MOVABILITY * MovementGenerator.countKing(p, whiteKing);
		}
		if (blackPawns.size() == 0) {
			if (whiteKing != null && blackKing != null) {
				// start pushing towards enemy king.
				double dx = whiteKing.x - blackKing.x;
				double dy = whiteKing.y - blackKing.y;
				double distance = Math.sqrt(dx*dx + dy*dy);
				black += MN.KING_PUSHAGE * distance;
			}
		} else {
			// protection, for the win!
			black += MN.KING_MOVABILITY * MovementGenerator.countKing(p, blackKing);
		}
		// knight movement bonus
		white += MN.KNIGHT_MOVABILITY * MovementGenerator.countKnight(p, whiteKnights.a);
		white += MN.KNIGHT_MOVABILITY * MovementGenerator.countKnight(p, whiteKnights.b);
		black += MN.KNIGHT_MOVABILITY * MovementGenerator.countKnight(p, blackKnights.a);
		black += MN.KNIGHT_MOVABILITY * MovementGenerator.countKnight(p, blackKnights.b);

		return (white - black);
	}

}