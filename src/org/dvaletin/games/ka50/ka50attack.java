package org.dvaletin.games.ka50;

import javax.microedition.khronos.opengles.GL10;

// import android.app.Activity;
// import android.os.Bundle;


import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.FixedStepEngine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;

import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;


import org.anddev.andengine.entity.shape.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.shape.modifier.SequenceShapeModifier;
import org.anddev.andengine.entity.text.ChangeableText;

import org.anddev.andengine.entity.util.FPSLogger;

import org.anddev.andengine.input.touch.TouchEvent;

import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
//import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.MathUtils;

import org.dvaletin.games.ka50.Helicopter.Ka50;



import android.graphics.Color;




public class ka50attack extends BaseGameActivity implements IAccelerometerListener, IOnSceneTouchListener {
    /** Called when the activity is first created. */
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private static final float MAX_SPEED = 300;

	// ===========================================================
	// Fields
	// ===========================================================

	private BoundCamera mBoundChaseCamera;

	private AnalogOnScreenControl mAnalogOnScreenControl;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;
	private Texture mOnScreenControlTexture;
	
	private Texture mTexture;
	//private TiledTextureRegion mPlayerTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	private Ka50 mHero;
	private ChangeableText mScoreText;
	protected boolean mGameRunning;
	
	private Texture mFontTexture;
	private Font mFont;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
    public void onLoadComplete() {
		
    }
    
    @Override
	public Engine onLoadEngine() {
		this.mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineoptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera);
		engineoptions.getTouchOptions().enableRunOnUpdateThread();
		return new FixedStepEngine(engineoptions, 60 );	
	}

    @Override
	public void onLoadResources() {
		this.mTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		this.mHero = new Ka50(0, 0,  TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/ka-50-green.png", 0, 0, 4, 4), 300, 24); // 72x128
		
		this.mOnScreenControlTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mOnScreenControlBaseTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "gfx/onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "gfx/onscreen_control_knob.png", 128, 0);
		this.mEngine.getTextureManager().loadTextures(this.mTexture, this.mOnScreenControlTexture);
		
		this.mFontTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = FontFactory.createFromAsset(this.mFontTexture, this, "font/Droid.ttf", 32, true, Color.WHITE);
		this.enableAccelerometerSensor(this);
    }

    @Override
	public Scene onLoadScene() {
		//this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mGameRunning = false;
		final Scene scene = new Scene(3);

		try {
			final TMXLoader tmxLoader = new TMXLoader(this, this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			this.mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/desert.tmx");
		} catch (final TMXLoadException tmxle) {
			Debug.e(tmxle);
		}

		final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		scene.getBottomLayer().addEntity(tmxLayer);
		
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), this.mBoundChaseCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, 200, new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				
				ka50attack.this.mHero.setVelocity(ka50attack.this.mHero.getVelocityX() + pValueX*10,
				ka50attack.this.mHero.getVelocityY() + pValueY*10);
				//ka50attack.this.mHero.setRotation(MathUtils.radToDeg((float) Math.atan2(ka50attack.this.mHero.getVelocityX(), -ka50attack.this.mHero.getVelocityY())));
				//ka50attack.this.mHero.setRotation(MathUtils.radToDeg((float) Math.atan2(pValueX, -pValueY)));
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				//FIRE!!!
			}
		});
		analogOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.refreshControlKnobPosition();

		scene.setChildScene(analogOnScreenControl);
		
		

		/* Make the camera not exceed the bounds of the TMXEntity. */
		this.mBoundChaseCamera.setBounds(0, tmxLayer.getWidth(), 0, tmxLayer.getHeight());
		this.mBoundChaseCamera.setBoundsEnabled(true);
		
		this.mHero.setBounds(0, tmxLayer.getWidth(), 0, tmxLayer.getHeight());

		/* Calculate the coordinates for the helicopter, so its centered on the camera. */
		final int centerX = (CAMERA_WIDTH - mHero.getTextureRegion().getTileWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - mHero.getTextureRegion().getTileHeight()) / 2;
		this.mHero.setPosition(centerY, centerY);
		/* Create the sprite and add it to the scene. */
		this.mBoundChaseCamera.setChaseShape(mHero);

		
		scene.getTopLayer().addEntity(mHero);
		
		
		
		//this.mScoreText = new ChangeableText(5, 5, this.mFont, "X=XXXXXXX", "X=XXXXXXX".length());
		//this.mScoreText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//this.mScoreText.setAlpha(0.5f);
		//scene.getLayer(2).addEntity(this.mScoreText);

		
		return scene;
	}

	@Override
	public void onAccelerometerChanged(AccelerometerData pAccelerometerData) {
		float pValueX = pAccelerometerData.getY();
		float pValueY = pAccelerometerData.getX();
		
		//if(Math.sqrt(pValueX*pValueY) > 1.0){
			this.mHero.setRotation(this.mHero.getRotation() + MathUtils.radToDeg((float) Math.atan2(pValueX, pValueY))/72);
		
			//this.mHero.setPosition(this.mHero.getX()+pValueX, this.mHero.getY());
			//this.mHero.setVelocityX(this.mHero.getVelocityX() + pValueX*(float)Math.cos(MathUtils.degToRad(this.mHero.getRotation())));
			//this.mHero.setVelocityY(this.mHero.getVelocityY() + pValueY*(float)Math.sin(MathUtils.degToRad(this.mHero.getRotation())));
			//this.mHero.setVelocityX(pValueX*100);
			//this.mHero.setVelocityY(pValueY*100-50);
		//}

		//

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// 
		return false;
	}
    
}