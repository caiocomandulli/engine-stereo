package com.comandulli.engine.panoramic.playback.engine.assets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Mesh;

@SuppressWarnings("ConstantConditions")
public class ObjParser {

	public static Mesh loadFromStream(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		ArrayList<Vector3> posArray = new ArrayList<>();
		ArrayList<Vector3> texArray = new ArrayList<>();
		ArrayList<Vector3> normArray = new ArrayList<>();
		ArrayList<Face> faceArray = new ArrayList<>();
		ArrayList<Mesh> meshArray = new ArrayList<>();

		boolean meshPendingClose = false;
		boolean hasMeshDefinition = false;

		int posSize = 0;
		int texSize = 0;
		int normSize = 0;

		while (reader.ready()) {
			try {
				String line = reader.readLine();
				if (line == null) {
                    break;
                }
				if (line.isEmpty()) {
                    continue;
                }
				StringTokenizer tokenizer = new StringTokenizer(line);
				String token = tokenizer.nextToken();
                switch (token) {
                    case "#":
                        continue;
                    case "o":
                        hasMeshDefinition = true;
                        if (meshPendingClose) {
                            Mesh mesh = createMesh(faceArray, posSize, texSize, normSize);
                            meshArray.add(mesh);

                            posArray.clear();
                            texArray.clear();
                            normArray.clear();

                            posSize = 0;
                            texSize = 0;
                            normSize = 0;
                        } else {
                            meshPendingClose = true;
                        }
                        break;
                    case "v":
                        posArray.add(read_point(tokenizer));
                        break;
                    case "vn":
                        normArray.add(read_point(tokenizer));
                        break;
                    case "vt":
                        texArray.add(read_point(tokenizer));
                        break;
                    case "f":
                        if (tokenizer.countTokens() != 3) {
                            throw new UnsupportedOperationException("Only triangles supported");
                        }
                        Face face = new Face(3);
                        while (tokenizer.hasMoreTokens()) {
                            StringTokenizer face_tok = new StringTokenizer(tokenizer.nextToken(), "/");
                            int vt_idx = -1;
                            int vn_idx = -1;
                            int v_idx = Integer.parseInt(face_tok.nextToken());
                            if (face_tok.hasMoreTokens()) {
                                vt_idx = Integer.parseInt(face_tok.nextToken());
                            }
                            if (face_tok.hasMoreTokens()) {
                                vn_idx = Integer.parseInt(face_tok.nextToken());
                            }
                            face.addVertex(posArray.get(v_idx - 1), vt_idx == -1 ? null : texArray.get(vt_idx - 1), vn_idx == -1 ? null : normArray.get(vn_idx - 1));
                        }
                        faceArray.add(face);
                        posSize += face.v.length;
                        texSize += face.vt.length;
                        normSize += face.vn.length;
                        break;
                }
			} catch (NoSuchElementException e) {
				break;
			}
		}
		if (meshPendingClose || !hasMeshDefinition) {
			Mesh mesh = createMesh(faceArray, posSize, texSize, normSize);
			meshArray.add(mesh);
		}
        reader.close();
		return meshArray.get(0);
	}

	private static Vector3 read_point(StringTokenizer tok) {
		Vector3 ret = new Vector3();
		if (tok.hasMoreTokens()) {
			ret.x = Float.parseFloat(tok.nextToken());
			if (tok.hasMoreTokens()) {
				ret.y = Float.parseFloat(tok.nextToken());
				if (tok.hasMoreTokens()) {
					ret.z = Float.parseFloat(tok.nextToken());
				}
			}
		}
		return ret;
	}

	private static class Face {
		final Vector3[] v;
		final Vector3[] vt;
		final Vector3[] vn;
		final int size;
		int count;

		public Face(int size) {
			this.size = size;
			this.count = 0;
			this.v = new Vector3[size];
			this.vt = new Vector3[size];
			this.vn = new Vector3[size];
		}

		public boolean addVertex(Vector3 v, Vector3 vt, Vector3 vn) {
			if (count >= size) {
                return false;
            }
			this.v[count] = v;
			this.vt[count] = vt;
			this.vn[count] = vn;
			count++;
			return true;
		}

		public void pushOnto(FloatBuffer v_buffer, FloatBuffer vt_buffer, FloatBuffer vn_buffer) {
			int i;
			for (i = 0; i < size; i++) {
				v_buffer.put(v[i].x);
				v_buffer.put(v[i].y);
				v_buffer.put(v[i].z);
				if (vt_buffer != null && vt[i] != null) {
					vt_buffer.put(vt[i].x);
					vt_buffer.put(vt[i].y);
				}
				if (vn_buffer != null && vn[i] != null) {
					vn_buffer.put(vn[i].x);
					vn_buffer.put(vn[i].y);
					vn_buffer.put(vn[i].z);
				}
			}
		}
	}

	private static Mesh createMesh(ArrayList<Face> faces, int positionSize, int textureSize, int normalSize) {
		int facesSize = faces.size();
		Mesh mesh = new Mesh(facesSize, positionSize, textureSize, normalSize);
		for (int i = 0; i < facesSize; i++) {
			Face face = faces.get(i);
			face.pushOnto(mesh.positionBuffer, mesh.textureCoordinateBuffer, mesh.normalBuffer);
		}
		mesh.positionBuffer.rewind();
		if (mesh.hasTexture) {
            mesh.textureCoordinateBuffer.rewind();
        }
		if (mesh.hasNormals) {
            mesh.normalBuffer.rewind();
        }
		return mesh;
	}
}
