package me.decce.gnetum;

import java.util.Arrays;

public class TileMask {
	public static final int TILE_SIZE = 16;
	private static final int PADDING = 1;

	private int columns;
	private int rows;
	private boolean[] tiles = new boolean[0];
	private boolean all;

	public void resize(int guiWidth, int guiHeight) {
		columns = (guiWidth + TILE_SIZE - 1) / TILE_SIZE;
		rows = (guiHeight + TILE_SIZE - 1) / TILE_SIZE;
		tiles = new boolean[columns * rows];
		all = true;
	}

	public void clear() {
		Arrays.fill(tiles, false);
		all = false;
	}

	public void markAll() {
		all = true;
	}

	public void mark(int x, int y, int width, int height) {
		if (all || width <= 0 || height <= 0) {
			return;
		}
		int c0 = Math.max(0, Math.floorDiv(x - PADDING, TILE_SIZE));
		int r0 = Math.max(0, Math.floorDiv(y - PADDING, TILE_SIZE));
		int c1 = Math.min(columns - 1, Math.floorDiv(x + width + PADDING - 1, TILE_SIZE));
		int r1 = Math.min(rows - 1, Math.floorDiv(y + height + PADDING - 1, TILE_SIZE));
		if (c0 > c1 || r0 > r1) {
			return;
		}
		for (int r = r0; r <= r1; r++) {
			Arrays.fill(tiles, r * columns + c0, r * columns + c1 + 1, true);
		}
	}

	public boolean isAll() {
		return all;
	}

	public int columns() {
		return columns;
	}

	public int rows() {
		return rows;
	}

	public boolean isMarked(int column, int row) {
		return all || tiles[row * columns + column];
	}
}
