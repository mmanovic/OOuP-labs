package hr.fer.ooup.lab4.renderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hr.fer.ooup.lab4.gui.Point;

public class SVGRenderer implements Renderer {
	private List<String> lines = new ArrayList<>();
	private String fileName;

	public SVGRenderer(String fileName) {
		super();
		this.fileName = fileName;
		lines.add("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
	}

	public void close() throws IOException {
		FileWriter writer = new FileWriter(new File(fileName));
		for (String line : lines) {
			writer.write(line + "\n");
		}
		writer.write("</svg>");
		writer.flush();
		writer.close();
	}

	@Override
	public void drawLine(Point s, Point e) {
		lines.add("<line x1=\"" + s.getX() + "\" y1=\"" + s.getY() + "\" x2=\"" + e.getX() + "\" y2=\"" + e.getY()
				+ "\" style=\"stroke:#0000ff;\"/>");
	}

	@Override
	public void fillPolygon(Point[] points) {
		StringBuilder sb = new StringBuilder();
		sb.append("<polygon points=\"");
		for (Point point : points) {
			sb.append(point.getX()).append(',').append(point.getY()).append(' ');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\" style=\"stroke:#ff0000; fill:#0000ff;\"/>");
		lines.add(sb.toString());
	}

}
