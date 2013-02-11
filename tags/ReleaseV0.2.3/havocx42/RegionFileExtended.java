package havocx42;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.Tag;

import pfaeff.IDChanger;

public abstract class RegionFileExtended extends region.RegionFile {

	public RegionFileExtended(File path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	public abstract void convertRegion(IDChanger UI,Tag root,
			final HashMap<BlockUID, BlockUID> translations);
	
	public abstract void convertItems(IDChanger UI,Tag root, HashMap<BlockUID, BlockUID> translations);

	public void convert(IDChanger UI,HashMap<BlockUID, BlockUID> translations) throws IOException {

		// Progress


		// System.out.println("Processing file " + rf.getFile());
		ArrayList<Point> chunks = new ArrayList<Point>();

		// Get available chunks
		for (int x = 0; x < 32; x++) {
			for (int z = 0; z < 32; z++) {
				if (hasChunk(x, z)) {
					chunks.add(new Point(x, z));
				}
			}
		}

		// PROGESSBAR CHUNK
		if(UI!=null){
			UI.pb_chunk.setMaximum(chunks.size() - 1);
		}
		int count_chunk = 0;

		for (Point p : chunks) {
			// Progress
			if(UI!=null){
				UI.pb_chunk.setValue(count_chunk++);
				UI.lb_chunk.setText("Current Chunk: (" + p.x + "; " + p.y+ ")");
			}
			// Read chunks

			DataInputStream input = getChunkDataInputStream(p.x, p.y);
			CompoundTag root = NbtIo.read(input);
			// Find blocks
			convertRegion(UI,root, translations);
			// find blocks and items in chest etc. inventory
			convertItems(UI,root, translations);
			input.close();

			// Write chunks
			DataOutputStream output = getChunkDataOutputStream(p.x, p.y);
			NbtIo.write(root, output);
			output.close();
		}
	}

}