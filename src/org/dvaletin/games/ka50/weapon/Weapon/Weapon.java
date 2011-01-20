package org.dvaletin.games.ka50.Weapon;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public abstract class Weapon extends Sprite {
	
	
	private float mBoundMinX;
	private float mBoundMaxX;
	private float mBoundMinY;
	private float mBoundMaxY;
	
	
	
	public Weapon(final float pX, final float pY, TextureRegion texture, final float pVelocityX, final float pVelocityY){
		super(pX, pY, texture);
		this.setVelocity(pVelocityX, pVelocityY);
	}
	public void setBounds(float pBoundMinX, float pBoundMaxX, float pBoundMinY, float pBoundMaxY){
		mBoundMinX = pBoundMinX;
		mBoundMaxX = pBoundMaxX;
		mBoundMinY = pBoundMinY;
		mBoundMaxY = pBoundMaxY;
	}
	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		if(this.mX < mBoundMinX  || 
				this.mX > mBoundMaxX ||
				this.mY < mBoundMinY ||
				this.mY > mBoundMaxY) {
			//this.destroy;
			
			this.setVisible(false);
		}

		super.onManagedUpdate(pSecondsElapsed);
	}
}
