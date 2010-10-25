package com.mngraves.breakaway;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import android.graphics.Point;

public final class GameWorld {
    public int mTargetFPS = 10;
    public int mTimeStep = (1000 / mTargetFPS);
    public int mIterations = 1;

    private Body[] mBalls;
    private int mBallCount = 0;
    private Body mBall;

    private AABB mWorldAABB;
    private World mWorld;
    private BodyDef mGroundBodyDef;
    private PolygonDef mGroundShapeDef;
    private static final String TAG = "GameWorld";
    
    private static GameWorld mInstance = null;
    
    private GameWorld(){}
    
    public static GameWorld getInstance(){
    	if(mInstance == null){
    		mInstance = new GameWorld();
    	}
    	return mInstance;
    }
    
    public void create() {
        // Step 1: Create Physics World Boundaries
        mWorldAABB = new AABB();
        mWorldAABB.lowerBound.set(new Vec2((float) 0.0, (float) 800.0));
        mWorldAABB.upperBound.set(new Vec2((float) 480.0, (float) 0.0));

        // Step 2: Create Physics World with Gravity
        Vec2 gravity = new Vec2((float) 0.0, (float) 100.0);
        boolean doSleep = true;
        mWorld = new World(mWorldAABB, gravity, doSleep);

        // Step 3: Create Ground Box
        mGroundBodyDef = new BodyDef();
        mGroundBodyDef.position.set(new Vec2((float) 480.0, (float) 800.0));
        Body groundBody = mWorld.createBody(mGroundBodyDef);
        mGroundShapeDef = new PolygonDef();
        mGroundShapeDef.setAsBox((float) 480.0, (float) 10.0);
        groundBody.createShape(mGroundShapeDef);
        mBalls = new Body[100];
        
    }
    
    public synchronized void setBall(int x, int y){
        // Create Dynamic Body
        if(mBall != null){
        	mWorld.destroyBody(mBall);
        }
    	BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((float) x, (float) y);
        mBall = mWorld.createBody(bodyDef);
        // Create Shape with Properties
        CircleDef circle = new CircleDef();
        circle.radius = (float) GamePlayActivity.NORMAL_BALL_RADIUS;
        circle.density = (float) 1.0;

        // Assign shape to Body
        mBall.createShape(circle);
        mBall.setMassFromShapes();
        mBall.setLinearVelocity(new Vec2((float)0.0, (float)-105.0));

    }

    public synchronized void update() {
        // Update Physics World
        mWorld.step((float)0.10, 1);
    }
    
    public Point getBallPosition(){
    	int x = 0;
    	int y = 0;
    	Point point = new Point(x, y);
    	if(mBall != null){
    		Vec2 pos = mBall.getPosition();
    		point.set((int)pos.x, (int)pos.y);
    	}
    	return point;
    }
}