/**
 * @author HueyNemud / https://github.com/HueyNemud
 */

function Gamut3D_sRGB_LAB(alpha) {

	const
	rgbcube = {
		black : [ 0, 0, 0 ],
		white : [ 255, 255, 255 ],
		red : [ 255, 0, 0 ],
		green : [ 0, 255, 0 ],
		blue : [ 0, 0, 255 ],
		magenta : [ 255, 0, 255 ],
		cyan : [ 0, 255, 255 ],
		yellow : [ 255, 255, 0 ]
	};

	const
	DIMENSION = 3, DEF_SCALE = 1.0;

	var triangles = alpha * alpha * 6 * 2;

	var attributes = {
		color : {
			itemSize : 3,
			array : new Float32Array(triangles * DIMENSION * DIMENSION),
			numItems : triangles * DIMENSION * DIMENSION
		},
		position : {
			itemSize : 3,
			array : new Float32Array(triangles * DIMENSION * DIMENSION),
			numItems : triangles * DIMENSION * DIMENSION
		}
	};

	var computeFaceCoordinates = function(topleft, topright, botleft, nfaces) {
		var v1 = [], v2 = [];
		var nvertices = nfaces + 1;
		var face = [ nvertices * nvertices * DIMENSION ];
		// Left to right and top to bottom vectors
		v2 = [ (topright[0] - topleft[0]) / nfaces,
				(topright[1] - topleft[1]) / nfaces,
				(topright[2] - topleft[2]) / nfaces ];
		v1 = [ (botleft[0] - topleft[0]) / nfaces,
				(botleft[1] - topleft[1]) / nfaces,
				(botleft[2] - topleft[2]) / nfaces ];

		// Iterate over the rows and columns of the subdivided face
		var r, c;
		for (r = 0; r < nvertices; r++) {
			for (c = 0; c < nvertices; c++) {
				idx = (r * nvertices + c) * DIMENSION;
				face[idx] = topleft[0] + r * v1[0] + c * v2[0];
				face[idx + 1] = topleft[1] + r * v1[1] + c * v2[1];
				face[idx + 2] = topleft[2] + r * v1[2] + c * v2[2];
			}
		}
		return face;
	};

	/*
	 * Build the faces
	 */
	// Blue - Magenta - Red - Black square
	var f1 = computeFaceCoordinates(rgbcube.blue, rgbcube.magenta,
			rgbcube.black, alpha);
	// Magenta - White - Yellow - Red square
	var f2 = computeFaceCoordinates(rgbcube.magenta, rgbcube.white,
			rgbcube.red, alpha);
	// White- Cyan - Green - Yellow square
	var f3 = computeFaceCoordinates(rgbcube.white, rgbcube.cyan,
			rgbcube.yellow, alpha);
	// Cyan - Blue - Black - Green square
	var f4 = computeFaceCoordinates(rgbcube.cyan, rgbcube.blue, rgbcube.green,
			alpha);
	// Cyan - White- Magenta - Blue square (roof)
	var f5 = computeFaceCoordinates(rgbcube.cyan, rgbcube.white, rgbcube.blue,
			alpha);
	// Black - Red - Yellow - Green square (floor)
	var f6 = computeFaceCoordinates(rgbcube.black, rgbcube.red,
			rgbcube.green, alpha);
	/*
	 * Build the triangles and the webgl arrays
	 */
	var quads = f1.concat(f2, f3, f4, f5, f6);
	var quadOffset, fid, row, tl, tr, bl, br, tl_lab, tr_lab, bl_lab, br_lab, quadIdInFace, nquadsperface = alpha
			* alpha;
	for (quadOffset = 0; quadOffset < nquadsperface * 6; quadOffset++) {
		quadIdInFace = quadOffset % nquadsperface;
		fid = Math.floor(quadOffset / nquadsperface);
		row = Math.floor(quadIdInFace / alpha);
		tl = DIMENSION * (quadOffset + row + fid * (2 * (alpha + 1) - 1));
		tr = tl + DIMENSION;
		bl = tl + alpha * DIMENSION + DIMENSION;
		br = bl + DIMENSION;

		// Convert the quad to CIE Lab
		tl_lab = chroma(quads[tl], quads[tl + 1], quads[tl + 2]).lab();
		tr_lab = chroma(quads[tr], quads[tr + 1], quads[tr + 2]).lab();
		bl_lab = chroma(quads[bl], quads[bl + 1], quads[bl + 2]).lab();
		br_lab = chroma(quads[br], quads[br + 1], quads[br + 2]).lab();
		var color = attributes.color.array;
		var position = attributes.position.array;

		pos_offset = 18 * quadOffset;
		// Triangle 1
		position[pos_offset] = tl_lab[0] * DEF_SCALE;
		position[pos_offset + 1] = tl_lab[1] * DEF_SCALE;
		position[pos_offset + 2] = tl_lab[2] * DEF_SCALE;
		color[pos_offset] = quads[tl] / 255; // r
		color[pos_offset + 1] = quads[tl + 1] / 255; // g
		color[pos_offset + 2] = quads[tl + 2] / 255; // b

		position[pos_offset + 3] = bl_lab[0] * DEF_SCALE;
		position[pos_offset + 4] = bl_lab[1] * DEF_SCALE;
		position[pos_offset + 5] = bl_lab[2] * DEF_SCALE;
		color[pos_offset + 3] = quads[bl] / 255;
		color[pos_offset + 4] = quads[bl + 1] / 255;
		color[pos_offset + 5] = quads[bl + 2] / 255;

		position[pos_offset + 6] = br_lab[0] * DEF_SCALE;
		position[pos_offset + 7] = br_lab[1] * DEF_SCALE;
		position[pos_offset + 8] = br_lab[2] * DEF_SCALE;
		color[pos_offset + 6] = quads[br] / 255;
		color[pos_offset + 7] = quads[br + 1] / 255;
		color[pos_offset + 8] = quads[br + 2] / 255;

		// Triangle 2
		position[pos_offset + 9] = tl_lab[0] * DEF_SCALE;
		position[pos_offset + 10] = tl_lab[1] * DEF_SCALE;
		position[pos_offset + 11] = tl_lab[2] * DEF_SCALE;
		color[pos_offset + 9] = quads[tl] / 255;
		color[pos_offset + 10] = quads[tl + 1] / 255;
		color[pos_offset + 11] = quads[tl + 2] / 255;

		position[pos_offset + 12] = br_lab[0] * DEF_SCALE;
		position[pos_offset + 13] = br_lab[1] * DEF_SCALE;
		position[pos_offset + 14] = br_lab[2] * DEF_SCALE;
		color[pos_offset + 12] = quads[br] / 255;
		color[pos_offset + 13] = quads[br + 1] / 255;
		color[pos_offset + 14] = quads[br + 2] / 255;

		position[pos_offset + 15] = tr_lab[0] * DEF_SCALE;
		position[pos_offset + 16] = tr_lab[1] * DEF_SCALE;
		position[pos_offset + 17] = tr_lab[2] * DEF_SCALE;
		color[pos_offset + 15] = quads[tr] / 255;
		color[pos_offset + 16] = quads[tr + 1] / 255;
		color[pos_offset + 17] = quads[tr + 2] / 255;
	}
	return attributes;
}