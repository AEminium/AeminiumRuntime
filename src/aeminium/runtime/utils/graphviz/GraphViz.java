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
