package org.dvaletin.games.ka50.weapon;

import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class Bullet extends Weapon {
	public Bullet(final float pX, final float pY, TextureRegion texture, final float pVelocityX, final float pVelocityY){
		super(pX, pY, texture, pVelocityX, pVelocityY);
	}
}
