/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 *
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.utils.graphviz;


public abstract class GraphViz {
	protected final Color DEFAULT_COLOR          = Color.BLACK;
	protected final LineStyle DEFAULT_LINE_STYLE = LineStyle.SOLID;
	protected final Shape DEFAULT_SHAPE          = Shape.ELLIPSE;

	public static <T extends Enum<?>> T getDefaultValue(String configEntry, T defaultValue, T[] values) {
		for ( T value : values ) {
			if ( value.name().equals(configEntry)) {
				return value;
			}
		}
		return defaultValue;
	}

	public static enum RankDir {
		TB,
		BT,
		LR,
		RL;
	}

	public static enum Color {
		RED,
		GREEN,
		BLUE,
		BLACK,
		YELLOW
	}

	public static enum LineStyle {
		SOLID,
		DASHED,
		DOTTED
	}

	public static enum Shape {
		BOX,
		ELLIPSE
	}
}
