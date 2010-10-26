package com.mngraves.breakout;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

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
    
    public float mTimeStep = ((float)1.0 / (float)30.0);
    public int mIterations = 6;

    private Body mBall;
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
    
    public void create(Rect bounds) {
    	mScreenBounds = bounds;
    	
    	/*
    	 * Scale screen dimensions to meters
    	 */
    	mScreenWidth = ((float)bounds.width() / (float)PIXELS_IN_METER)/(float)2.0;
    	mScreenHeight = ((float)bounds.height() / (float)PIXELS_IN_METER)/(float)2.0;
    	
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
        Vec2 gravity = new Vec2((float) 0.0, (float)0.0);
        boolean doSleep = true;
        mWorld = new World(mWorldAABB, gravity, doSleep); 
        
        createTopBox();
        createRightBox();
        //createBottomBox();
        createLeftBox();
    }
    
    private void createTopBox(){
        BodyDef topBodyDef = new BodyDef();
        topBodyDef.position.set(new Vec2((float)0.0, -(mScreenHeight+(float)2.0)));
        Body topBody = mWorld.createBody(topBodyDef);
        PolygonDef topShapeDef = new PolygonDef();
        topShapeDef.setAsBox(mScreenWidth*(float)2.0, (float)2.1);
        topBody.createShape(topShapeDef);    	
    }

    private void createBottomBox(){
        BodyDef bottomBodyDef = new BodyDef();
        bottomBodyDef.position.set(new Vec2((float)0.0, (mScreenHeight+(float)2.0)));
        Body bottomBody = mWorld.createBody(bottomBodyDef);
        PolygonDef bottomShapeDef = new PolygonDef();
        bottomShapeDef.setAsBox(mScreenWidth*(float)2.0, (float)2.1);
        bottomBody.createShape(bottomShapeDef);    	
    }
    
    private void createRightBox(){
        BodyDef rightBodyDef = new BodyDef();
        rightBodyDef.position.set(new Vec2(mScreenWidth+(float)2.0, (float)0.0));
        Body rightBody = mWorld.createBody(rightBodyDef);
        PolygonDef rightShapeDef = new PolygonDef();
        rightShapeDef.setAsBox((float)2.1, mScreenHeight*(float)2.0);
        rightBody.createShape(rightShapeDef);    	
    }
    
    private void createLeftBox(){
        BodyDef leftBodyDef = new BodyDef();
        leftBodyDef.position.set(new Vec2(-(mScreenWidth+(float)2.0), (float)0.0));
        Body leftBody = mWorld.createBody(leftBodyDef);
        PolygonDef leftShapeDef = new PolygonDef();
        leftShapeDef.setAsBox((float)2.1, mScreenHeight*(float)2.0);
        leftBody.createShape(leftShapeDef);    	
    }
    
    public void addBox(Bundle data){
    	float width = ((float)data.getInt(GamePlayActivity.KEY_WIDTH)/(float)PIXELS_IN_METER)/(float)2.0;
    	float height = ((float)data.getInt(GamePlayActivity.KEY_HEIGHT)/(float)PIXELS_IN_METER)/(float)2.0;
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
        circle.restitution = (float)1.0;
        circle.radius = (float)GamePlayActivity.NORMAL_BALL_RADIUS/(float)PIXELS_IN_METER;
        circle.density = (float) 1.0;
        circle.friction = 0;

        // Assign shape to Body
        mBall.createShape(circle);
        mBall.setMassFromShapes();
        mBall.setLinearVelocity(new Vec2((float)25.0, (float)-30.0));
        
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
}