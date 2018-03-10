package com.pratha.sudokumaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ProblemActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView input;
    private int[][] problem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);

        input = findViewById(R.id.input);
        //load default problem
        loadProblem(Problems.EASY);

        findViewById(R.id.easy).setOnClickListener(this);
        findViewById(R.id.hard).setOnClickListener(this);
        findViewById(R.id.invalid).setOnClickListener(this);
        findViewById(R.id.solve).setOnClickListener(this);

    }

    private void loadProblem(int[][] problem) {
        this.problem = problem;
        input.setText(new SudokuSolver().problemToString(problem));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.easy:
                loadProblem(Problems.EASY);
                break;
            case R.id.hard:
                loadProblem(Problems.ANTI_BACKTRACK);
                break;
            case R.id.invalid:
                loadProblem(Problems.INVALID);
                break;
            case R.id.solve:
                Intent intent = new Intent(this, SolutionActivity.class);
                intent.putExtra(SolutionActivity.PROBLEM, problem);
                startActivity(intent);
                break;
        }
    }
}
