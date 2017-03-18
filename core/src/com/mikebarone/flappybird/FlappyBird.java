package com.mikebarone.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background,tubeTop,tubeBottom;
	ShapeRenderer shapeRenderer;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	Circle birdCircle;
	Rectangle[] tubeTopShapes = new Rectangle[4];
	Rectangle[] tubeBottomShapes = new Rectangle[4];


	int gameState = 0;
	float gravity = 2;

	int birdX,tubeTopX,tubeTopY,tubeBottomX,tubeBottomY;
	int tubeTopOffset = 600;
	int tubeBottomOffset = 600;

	int gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	int distanceTraveled = 0;
	int score = 0;
	int scoringTube = 0;

	BitmapFont font;
	Texture gameOver;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();

		tubeTop = new Texture("toptube.png");
		tubeBottom = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		startGame();
	}

	public void startGame(){
		birdY = (Gdx.graphics.getHeight()-birds[0].getHeight())/2;
		for(int i=0; i<numberOfTubes; i++){
			tubeOffset[i] = (randomGenerator.nextFloat()-0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = (Gdx.graphics.getWidth() - tubeTop.getWidth()) / 2 + Gdx.graphics.getWidth() + i*distanceBetweenTubes;

			tubeTopShapes[i] = new Rectangle();
			tubeBottomShapes[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1) {

			if(tubeX[scoringTube] < Gdx.graphics.getWidth() / 2){
				score++;
				Gdx.app.log("Score",String.valueOf(score));
				if(scoringTube < numberOfTubes-1 ){
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}

			if(Gdx.input.justTouched()){
				velocity = -30;

			}

			distanceTraveled += tubeVelocity;

			for(int i=0; i<numberOfTubes; i++) {

				if(tubeX[i] < - tubeTop.getWidth()) {
					tubeX[i] += numberOfTubes*distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat()-0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				} else {
					tubeX[i] -= tubeVelocity;
				}

				tubeTopY = (Gdx.graphics.getHeight() + gap) / 2;
				tubeBottomX = (Gdx.graphics.getWidth() - tubeBottom.getWidth()) / 2;
				tubeBottomY = (Gdx.graphics.getHeight() - gap) / 2 - tubeBottom.getHeight();

				batch.draw(tubeTop, tubeX[i], tubeTopY + tubeOffset[i]);
				batch.draw(tubeBottom, tubeX[i], tubeBottomY + tubeOffset[i]);

				tubeTopShapes[i].set(tubeX[i],tubeTopY + tubeOffset[i],tubeTop.getWidth(),tubeTop.getHeight());
				tubeBottomShapes[i].set(tubeX[i],tubeBottomY + tubeOffset[i],tubeBottom.getWidth(),tubeBottom.getHeight());

			}

			if(birdY > 0) {

				velocity += gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
			}

		} else if(gameState == 0) {

			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		} else if(gameState ==2){
			batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
			if(Gdx.input.justTouched()){
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}

		birdX = (Gdx.graphics.getWidth() - birds[flapState].getWidth()) / 2;

		batch.draw(birds[flapState], birdX, birdY);
		font.draw(batch,String.valueOf(score),100,200);
		batch.end();

		birdCircle.set(Gdx.graphics.getWidth()/2,birdY+birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);



		for(int i=0; i<numberOfTubes; i++) {

			if(Intersector.overlaps(birdCircle,tubeTopShapes[i]) || Intersector.overlaps(birdCircle,tubeBottomShapes[i])){
				gameState = 2;
			}
		}


	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
