package com.example.my2048;

import com.example.my2048.GameLayout.ShowScoreListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tvScore;
	private GameLayout gameLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvScore = (TextView) findViewById(R.id.id_score);
		gameLayout = ((GameLayout)findViewById(R.id.id_game2048));
		
		ShowScoreListener scoreListener = new ShowScoreListener() {
			@Override
			public void showScore() {
				tvScore.setText("Score: " + gameLayout.getScore());
			}
		};
		gameLayout.setShowScoreListener(scoreListener);
	}
}
