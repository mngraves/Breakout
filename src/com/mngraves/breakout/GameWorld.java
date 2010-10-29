package com.mngraves.breakout;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.Steppable;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author Michael Graves
 * The JBox2d Game World singleton
 *
 */
public final class GameWorld {
    public static final int PIXELS_IN_METER = 30;
    
    public float mTimeStep = (1.0f / 30.0f);
    public int mIterations = 6;
    
    private Body[] mMapBlocks;
    private Body mBall;
    private Body mDeadBlock;
    private Body mSlider;
    private Rect mScreenBounds;
    private float mScreenWidth;
    private float mScreenHeight;

    private AABB mWorldAABB;
    private World mWorld;
    private static final String TAG = "GameWorld";
    
    private static GameWorld mInstance = null;
    
    private GameWorld(){}
    
    public static GameWorld getInstance(){
    	if(mInstance == null){
    		mInstance = new GameWorld();
    	}
    	return mInstance;
    }
    
    /**
     * Create the game slider
     */
    private void createSlider(){
		Bundle data = new Bundle();
		data.putInt(GamePlayActivity.KEY_WIDTH, GamePlayActivity.SLIDER_WIDTH);
		data.putInt(GamePlayActivity.KEY_HEIGHT, GamePlayActivity.SLIDER_DEPTH);
		data.putInt(GamePlayActivity.KEY_X, GamePlayActivity.SLIDER_DEFAULT_X);
		data.putInt(GamePlayActivity.KEY_Y, mScreenBounds.height() - GamePlayActivity.SLIDER_Y_OFFSET);
		data.putInt(GamePlayActivity.KEY_BOX_COLOR, GamePlayActivity.SLIDER_COLOR);
		mSlider = addBox(data);    	
    }
    
    /**
     * Update the slider x axis according to control input
     * @param x the center of the slider
     */
    public synchronized void updateSliderPosition(int x){
    	float locX = xToBox(x);
    	/**
    	 * Correct the coordinate to reflect left edge of slider
    	 */
    	x -= Math.round((GamePlayActivity.SLIDER_WIDTH/2));
    	
    	if(mSlider == null){
    		createSlider();
    	}
    	XForm xform = mSlider.getXForm();
    	Log.d(TAG, "Old slider X: " + xform.position.x);
    	xform.position.x = locX;
    	Log.d(TAG, "New slider X: " + xform.position.x);
    	mSlider.setXForm(xform.position, 0);
    	/**
    	 * Save updated coordinate to the user data
    	 */
    	Bundle data = (Bundle)mSlider.getUserData();
    	data.putInt(GamePlayActivity.KEY_X, x);
    	Log.d(TAG, "saving the new X pixel coordinate: " + x);
    	mSlider.setUserData(data);
    }
    
    
    public void create(Rect bounds) {
    	mScreenBounds = bounds;
    	
    	/*
    	 * Scale screen dimensions to meters
    	 */
    	mScreenWidth = ((float)bounds.width() / (float)PIXELS_IN_METER)/2.0f;
    	mScreenHeight = ((float)bounds.height() / (float)PIXELS_IN_METER)/2.0f;
    	
    	Log.d(TAG, "Screen width: " + mScreenWidth);
    	Log.d(TAG, "Screen height: " + mScreenHeight);
    	Log.d(TAG, "Bounds width: " + bounds.width());
    	Log.d(TAG, "Bounds height: " + bounds.height());
    	
    	// Step 1: Create Physics World Boundaries
        mWorldAABB = new AABB();
        mWorldAABB.lowerBound.set(new Vec2(-mScreenWidth, -mScreenHeight));
        mWorldAABB.upperBound.set(new Vec2(mScreenWidth, mScreenHeight));
        
        Log.d(TAG, "World AABB is valid: " + mWorldAABB.isValid());

        // Step 2: Create Physics World with Gravity
        Vec2 gravity = new Vec2(0.0f, 0.0f);
        boolean doSleep = true;
        mWorld = new World(mWorldAABB, gravity, doSleep);
        mWorld.setContactListener(new BreakoutContactListener());
        mWorld.registerPostStep(new Steppable() {
			
			@Override
			public void step(float dt, int iterations) {
				if(mDeadBlock != null){
					mWorld.destroyBody(mDeadBlock);
					mDeadBlock = null;
					Log.d(TAG, "DESTROYED!!!");
				}
			}
		});
        createTopBox();
        createRightBox();
        //createBottomBox();
        createLeftBox();
    }
    
    private void createTopBox(){
        BodyDef topBodyDef = new BodyDef();
        topBodyDef.position.set(new Vec2(0.0f, -(mScreenHeight+2.0f)));
        Body topBody = mWorld.createBody(topBodyDef);
        PolygonDef topShapeDef = new PolygonDef();
        topShapeDef.setAsBox(mScreenWidth*2.0f, 2.1f);
        topBody.createShape(topShapeDef);
    }

    private void createBottomBox(){
        BodyDef bottomBodyDef = new BodyDef();
        bottomBodyDef.position.set(new Vec2(0.0f, (mScreenHeight+2.0f)));
        Body bottomBody = mWorld.createBody(bottomBodyDef);
        PolygonDef bottomShapeDef = new PolygonDef();
        bottomShapeDef.setAsBox(mScreenWidth*2.0f, 2.1f);
        bottomBody.createShape(bottomShapeDef);    	
    }
    
    private void createRightBox(){
        BodyDef rightBodyDef = new BodyDef();
        rightBodyDef.position.set(new Vec2(mScreenWidth+2.0f, 0.0f));
        Body rightBody = mWorld.createBody(rightBodyDef);
        PolygonDef rightShapeDef = new PolygonDef();
        rightShapeDef.setAsBox(2.1f, mScreenHeight*2.0f);
        rightBody.createShape(rightShapeDef);    	
    }
    
    private void createLeftBox(){
        BodyDef leftBodyDef = new BodyDef();
        leftBodyDef.position.set(new Vec2(-(mScreenWidth+2.0f), 0.0f));
        Body leftBody = mWorld.createBody(leftBodyDef);
        PolygonDef leftShapeDef = new PolygonDef();
        leftShapeDef.setAsBox(2.1f, mScreenHeight*2.0f);
        leftBody.createShape(leftShapeDef);    	
    }
    
    public Body addBox(Bundle data){
    	float width = ((float)data.getInt(GamePlayActivity.KEY_WIDTH)/(float)PIXELS_IN_METER)/2.0f;
    	float height = ((float)data.getInt(GamePlayActivity.KEY_HEIGHT)/(float)PIXELS_IN_METER)/2.0f;
    	float locX = xToBox(data.getInt(GamePlayActivity.KEY_X) + Math.round(data.getInt(GamePlayActivity.KEY_WIDTH)/2));
    	float locY = yToBox(data.getInt(GamePlayActivity.KEY_Y) + Math.round(data.getInt(GamePlayActivity.KEY_HEIGHT)/2));
    	//locX += (locX > 0) ? (width/2) : -(width/2);
    	//locY += (locY > 0) ? (height/2) : -(height/2);
    	
    	Log.d(TAG, "width: " + width + ", height: " + height + ", x: " + locX + ", y: " + locY);
    	
    	BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vec2(locX, locY));
        Body body = mWorld.createBody(bodyDef);
        PolygonDef shapeDef = new PolygonDef();
        shapeDef.setAsBox(width, height);
        body.createShape(shapeDef);
        body.setUserData(data);

        return body;
    }
    
    /**
     * Add the map blocks to the world
     * @param bundles bundles describing the blocks to be added
     */
    public void addMapBlocks(Bundle[] bundles){
    	clearMapBlocks();
    	mMapBlocks = new Body[bundles.length];
    	
    	for(int i = 0; i < bundles.length; i++){
    		mMapBlocks[i] = addBox(bundles[i]);
    	}
    }
    
    /**
     * Remove all map blocks from the world
     */
    private void clearMapBlocks(){
    	if(mMapBlocks != null){
    		for(int i = 0; i < mMapBlocks.length; i++){
    			mWorld.destroyBody(mMapBlocks[i]);
    		}
    	}
    }
    
    public Body getBodyListHead(){
    	return mWorld.getBodyList();
    }
    
    /**
     * Create a new ball at (x,y)
     * @param x the x pixel coordinate
     * @param y the y pixel coordinate
     */
    public synchronized void setBall(int x, int y){
        // Create Dynamic Body
        if(mBall != null){
        	mWorld.destroyBody(mBall);
        }
        /**
         * Scale x & y to meters
         */
        float locX = xToBox(x);
        float locY = yToBox(y);
        Log.d(TAG, "touch X: " + x + ", touch Y: " + y);
        Log.d(TAG, "Ball loc: " + locX + "," + locY);
        
    	BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(locX, locY);
        mBall = mWorld.createBody(bodyDef);
        // Create Shape with Properties
        CircleDef circle = new CircleDef();
        circle.restitution = 1.0f;
        circle.radius = (float)GamePlayActivity.NORMAL_BALL_RADIUS/(float)PIXELS_IN_METER;
        circle.density = 1.0f;
        circle.friction = 0;

        // Assign shape to Body
        mBall.createShape(circle);
        mBall.setMassFromShapes();
        mBall.setLinearVelocity(new Vec2(25.0f, -30.0f));
        
        Log.d(TAG, "Ball is dynamic: " + mBall.isDynamic());
        Log.d(TAG, "Ball string: " + mBall.toString());
        
    }

    public synchronized void update() {
        // Update Physics World
        mWorld.step(mTimeStep, mIterations);
        if(mBall != null){
        	//Log.d(TAG, "Ball sleeping: " + mBall.isSleeping());
        	//Log.d(TAG, "Ball is static: " + mBall.isStatic());
        }
    }
    
    /**
     * Convert the x pixel coordinate to the Box2d x axis
     * @param x the x pixel coordinate
     * @return the x axis location in Box2d world
     */
    private float xToBox(int x){
    	float locX = (float)x / (float)PIXELS_IN_METER;
    	//Log.d(TAG, "Loc x: " + locX);
    	if(locX > (mScreenWidth)){
    		return locX - (mScreenWidth);
    	}
    	return locX - (mScreenWidth);
    }
    
    /**
     * Convert the y pixel coordinate to the Box2d y axis
     * @param y the y pixel coordinate
     * @return the y axis location in the Box2d world
     */
    private float yToBox(int y){
    	float locY = (float)y / (float)PIXELS_IN_METER;
    	//Log.d(TAG, "Loc y: " + locY + " screen height: " + mScreenHeight);
    	
    	if(locY > (mScreenHeight)){
    		return locY - (mScreenHeight);
    	}
    	return locY - (mScreenHeight);
    }
    
    /**
     * Convert the x Box2d coordinate to the x pixel coordinate
     * @param x the Box2d coordinate
     * @return the x pixel coordinate
     */
    private int BoxToX(float x){
    	x = mScreenWidth + x;
    	return Math.round(PIXELS_IN_METER*x);
    }
    
    /**
     * Convert the y Box2d coordinate to the y pixel coordinate
     * @param y the Box2d coordinate
     * @return the y pixel coordinate
     */
    private int BoxToY(float y){
    	y = mScreenHeight + y;
    	return Math.round(PIXELS_IN_METER*y);
    }    
    
    /**
     * 
     * @return the ball's pixel coordinates on the screen
     */
    public Point getBallPosition(){
    	int x = 0;
    	int y = 0;
    	Point point = new Point(x, y);
    	
    	if(mBall != null){
    		Vec2 pos = mBall.getPosition();
    		//Log.d(TAG, "Ball position (Box2d) - x:" + pos.x + ", y:" + pos.y);
    		point.set(BoxToX(pos.x), BoxToY(pos.y));
    		//Log.d(TAG, "Sending ball position: " + BoxToX(pos.x) + ", " + BoxToY(pos.y));
    	}
    	return point;
    }
    
    /**
     * Indicates whether or not the ball is still in play
     * @return true if the ball is still in play
     */
    public boolean isBallInPlay(){
    	if(mBall != null && !mBall.isSleeping() && !mBall.isFrozen()){
    		return true;
    	}
    	return false;
    }
    
    
    /**
     * Handle collisions with our map objects
     *
     */
    private final class BreakoutContactListener implements ContactListener{
    	BreakoutContactListener(){
    		//Log.d(TAG, "constructing listener...");
    	}
		@Override
		public void add(ContactPoint point) {
			Body body = point.shape1.m_body.isDynamic() ? point.shape2.m_body : point.shape1.m_body;
			Bundle data = (Bundle)body.getUserData();
			if(data != null){
				//Log.d(TAG, "add...." + data.toString());
				if(data.getInt(GamePlayActivity.KEY_BOX_HITPOINTS) == 1){
					mDeadBlock = body;
				}
			}
		}

		@Override
		public void persist(ContactPoint point) {
			//Log.d(TAG, "persist...");
			
		}

		@Override
		public void remove(ContactPoint point) {
			
					
		}

		@Override
		public void result(ContactResult point) {
			//Log.d(TAG, "result...");
			
		}
    	
    }
    
}