package components.ddrones.blobGenerator;

import java.awt.Color;
import java.util.ArrayList;

import frevoutils.JGridMap.Display;
import frevoutils.JGridMap.JGridMap;

public class BlobMap {
	
	public static final int BLOCKED = 1;
	public static final int FREE = 0;
	public int WIDTH, HEIGHT;
	private int blocked = -1;
	
	public ArrayList<Blob> blobs = new ArrayList<Blob>();
	
	/**
	 * The map to be generated
	 * each cell can contain 0 (free space) or 1 (obstructed by a building)
	 */
	public int map[][];
	public int getNumberofBlocked() {
		if (blocked == -1) calculateBlocked();
		return blocked;
	}
	
	private void calculateBlocked() {
		blocked = 0;
		for (int y=0;y<map.length;y++) {
			for (int x=0;x<map[0].length;x++) {
				if (map[x][y] == BLOCKED) blocked ++;
			}
		}
	}

	public BlobMap(int w, int h) {
		this.WIDTH = w;
		this.HEIGHT = h;
		
		map = new int[w][h];
	}
	
	public void displayMap(String title) {
		JGridMap jgm = new JGridMap(400, 400, WIDTH, HEIGHT, 1);
		Display d = new Display(450, 470, "");
		d.setTitle(title);
		//generate color map
		jgm.addColorToScale(BlobGenerator.BLOCKED, Color.BLACK);
		for (int i=0;i<blobs.size();i++) jgm.addColorToScale(blobs.get(i).id, new Color(Color.HSBtoRGB(0.5f/blobs.size()*i, 1f, 1f)));
		jgm.setData(map);
		d.add(jgm);
		d.setVisible(true);
		jgm.repaint();
	}
}
