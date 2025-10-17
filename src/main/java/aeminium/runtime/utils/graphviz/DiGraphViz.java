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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class DiGraphViz extends GraphViz {
	protected final String name;
	protected final StringBuffer nodes;
	protected final StringBuffer connections;
	protected final String EOL = System.getProperty("line.separator");
	protected final int ranksep;
	protected final RankDir rankdir;

	public DiGraphViz(String name, int ranksep, RankDir rankdir) {
		this.name    = name;
		this.ranksep = ranksep;
		this.rankdir = rankdir;
		nodes        = new StringBuffer();
		connections  = new StringBuffer();
	}

	public String getName() {
		return name;
	}

	public void addNode(int id, String label) {
		addNode(id, label, DEFAULT_SHAPE, DEFAULT_COLOR);
	}

	public void addNode(int id,
					    String label,
					    Shape shape,
					    Color color) {
		nodes.append(String.format("    %12d [label=\"%s\", shape=\"%s\", color=\"%s\"]"+EOL, id, label, shape.name().toLowerCase(), color.name().toLowerCase()));
	}

	public void addConnection(int from, int to) {
		addConnection(from, to, DEFAULT_LINE_STYLE, DEFAULT_COLOR, "");
	}

	public void addConnection(int from,
							  int to,
							  LineStyle lineStyle,
							  Color color,
							  String label) {
		connections.append(String.format("    %12d -> %12d [style=\"%s\", color=\"%s\", fontcolor=\"%s\", label=\"%s\"]"+EOL, from, to, lineStyle.name().toLowerCase(), color.name().toLowerCase(), color.name().toLowerCase(), label));
	}

	public boolean dump(File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			dump(fos);
			fos.close();
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	public boolean dump(OutputStream os ) {
		try {
			dumpOutputStream(os);
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	protected void dumpOutputStream(OutputStream os) throws IOException {
		os.write(String.format("digraph %s {" + EOL, name).getBytes());
		os.write(String.format("    rankdir=%s" + EOL, rankdir.name()).getBytes());
		os.write(String.format("    ranksep=%d" + EOL, ranksep).getBytes());
		os.write(nodes.toString().getBytes());
		os.write(connections.toString().getBytes());
		os.write("}".getBytes());
	}
}
