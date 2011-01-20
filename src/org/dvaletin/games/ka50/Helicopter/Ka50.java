package org.dvaletin.games.ka50.Helicopter;



import org.anddev.andengine.entity.sprite.AnimatedSprite;

import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.dvaletin.games.ka50.ka50attack;
import org.dvaletin.games.ka50.weapon.Bullet;




public class Ka50 extends AnimatedSprite {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final int cMaxArmor = 10;


	// ===========================================================
	// Fields
	// ===========================================================
	private int mGuns;
	private Bullet bullet;
	private int mRockets;
	private int mMaxGuns;
	private int mMaxRockets;
	private int mArmor;

	private float vSpeed;
	private float hSpeed;
	private float mBoundMinX;
	private float mBoundMaxX;
	private float mBoundMinY;
	private float mBoundMaxY;
	private Bullet mBullet;


	
	
	/*
	private void initCar(final Scene pScene) {
		this.mCar = new TiledSprite(20, 20, CAR_SIZE, CAR_SIZE, this.mVehiclesTextureRegion);
		this.mCar.setCurrentTileIndex(0);
		
		final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		this.mCarBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, this.mCar, BodyType.DynamicBody, carFixtureDef);
		
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mCar, this.mCarBody, true, false, true, false));

		pScene.getChild(LAYER_CARS).attachChild(this.mCar);
	}
	*/
	public Ka50 (final float pX, final float pY, TiledTextureRegion texture, int guns, int rockets){
		super(pX, pY, texture);
		this.mGuns = this.mMaxGuns = guns;
		this.mRockets = this.mMaxRockets  = rockets;
		this.mArmor = Ka50.cMaxArmor;

		setDirection();
	}
	
	public void hit (int fire){
		this.mArmor -= fire;
		if (this.mArmor < 0) {
			this.mArmor = 0;
		}
	}
	
	public Boolean isAlive(){
		return this.mArmor > 0;
	}

	public void fire_gun(int bullets) {
		// do actual fire 
		
		// decrease amount of shells
		if(this.mGuns > 0) {
			this.mGuns -= 1;
		}
	}
	
	public void fire_rocket () {
		// do actual fire
		
		// decrease amount of rockets
		if(this.mRockets > 0) {
			this.mRockets -=1;
		}
	}
	public void recharge_rockets () {
		this.mRockets = this.mMaxRockets;
	}
	
	public void recharge_guns (Bullet pBullet) {
		this.mGuns = this.mMaxGuns;
		mBullet = pBullet;
	}
	

	public void recharge (Bullet pBullet) {
		this.recharge_guns(pBullet);
		this.recharge_rockets();
	}
	
	public void setDirection (){
		this.animate(new long[] {100,100,100,100}, 4, 7, true);
	}
	public void move(){
		this.setPosition(this.mX+vSpeed, this.mY+hSpeed);
		
	}
	
	public void updateSpeed(float pValueX, float pValueY){
		vSpeed = pValueX;
		hSpeed = pValueY;
	}
	
	public void setBounds(float pBoundMinX, float pBoundMaxX, float pBoundMinY, float pBoundMaxY){
		mBoundMinX = pBoundMinX;
		mBoundMaxX = pBoundMaxX;
		mBoundMinY = pBoundMinY;
		mBoundMaxY = pBoundMaxY;
	}
	
	protected void onManagedUpdate(final float pSecondsElapsed) {
		if(this.mX < mBoundMinX ) {
			this.mX= mBoundMinX;
		} else if(this.mX + this.getWidth() > mBoundMaxX) {
			this.mX = mBoundMaxX - this.getWidth();
		}

		if(this.mY < mBoundMinY) {
			this.mY = mBoundMinY;
		} else if(this.mY + this.getHeight() > mBoundMaxY) {
			this.mY = mBoundMaxY - this.getHeight();
		}
		this.setVelocity(this.getVelocityX()*0.99f ,
				this.getVelocityY()*0.99f);
		super.onManagedUpdate(pSecondsElapsed);
	}
}
