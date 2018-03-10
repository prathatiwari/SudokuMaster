package com.pratha.sudokumaster;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class SolutionActivity extends AppCompatActivity {

    private static final String TAG = "SolutionActivity";
    public static final String PROBLEM = "PROBLEM";
    private TextView output;
    private SolveSudokuTask solveSudokuTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);

        output = findViewById(R.id.output);
        solveSudokuTask = new SolveSudokuTask();
        solveSudokuTask.execute((int[][]) getIntent().getSerializableExtra(PROBLEM));
    }

    @Override
    public void onBackPressed() {
        if (solveSudokuTask != null) {
            solveSudokuTask.cancel(true);
        }
        super.onBackPressed();
    }

    private class SolveSudokuTask extends AsyncTask<int[][], String, Boolean> {
        private static final String TAG = "SolveSudokuTask";
        SudokuSolver sudokuSolver = new SudokuSolver();

        @Override
        protected Boolean doInBackground(int[][]... problems) {
            try {
                if (sudokuSolver.solveSudoku(problems[0], new SudokuSolver.ProgressListener() {
                    @Override
                    public void onUpdateProgress(int[][] solution) {
                        publishProgress(sudokuSolver.problemToString(solution));
                        if (isCancelled()) {
                            sudokuSolver.stop();
                        }
                    }
                })) {
                    Log.d(TAG, "doInBackground: Solved");
                    return true;
                } else {
                    Log.d(TAG, "doInBackground: cant be Solved");
                    return false;
                }
            } catch (InvalidSudoKuException | InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            output.setText(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean isSolved) {
            if (isSolved) {
                output.setText(String.format(getString(R.string.solution), output.getText()));
            } else {
                output.setText(R.string.unsolvable);
            }
            super.onPostExecute(isSolved);
        }
    }
}
