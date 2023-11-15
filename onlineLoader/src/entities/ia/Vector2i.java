package entities.ia;

public class Vector2i {
	public int x, y, z;

	public Vector2i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean equals(Object object) {
		Vector2i vec = (Vector2i) object;
		if (vec.x == this.x && vec.y == this.y && vec.z == this.z) {
			return true;
		}
		return false;
	}
}
