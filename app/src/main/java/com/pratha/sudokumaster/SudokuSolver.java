package com.pratha.sudokumaster;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * sudoku solver
 * <p>
 * Created by Pratha on 3/10/2018.
 */

public class SudokuSolver {

    private String TAG = "SudokuSolver";
    private ProgressListener progressListener;
    private boolean shouldStop = false;


    public boolean solveSudoku(int[][] problem, @NonNull ProgressListener progressListener)
            throws InvalidSudoKuException, InterruptedException {

        this.progressListener = progressListener;

        //validate the size of problem
        if (problem.length == 9) {
            for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
                if (problem[rowIndex].length != 9) {
                    return false;
                }
            }
        } else {
            return false;
        }

        //solve 9x9 sudoku starting first position
        return solveSudoku(problem, 0, -1);// start before first cell
    }


    private boolean solveSudoku(int[][] problem, int row, int col) throws InvalidSudoKuException, InterruptedException {
        if (shouldStop) {
            shouldStop = false;
            throw new InterruptedException("Interrupted");
        }
        Cell nextCell = getNextCell(row, col);

        if (nextCell == null) {
            //reached at the end problem solved
            Log.i(TAG, "SOLUTION::: \n" + problemToString(problem));
            return true;
        } else if (problem[nextCell.row][nextCell.col] != 0) {
            //validate if pre filled values forms a valid sudoku
            if (isValidValue(problem, nextCell.row, nextCell.col, problem[nextCell.row][nextCell.col])) {
                return solveSudoku(problem, nextCell.row, nextCell.col);
            } else {
                Log.e(TAG, "invalid sudoku (prefilled validation) " + nextCell.row + ","
                        + nextCell.col + ":::" + problem[nextCell.row][nextCell.col]);
                throw new InvalidSudoKuException("Invalid problem");
            }
        } else {
            //find a number for empty cell
            for (int value = 1; value <= 9; value++) {
                if (isValidValue(problem, nextCell.row, nextCell.col, value)) {
                    //assign this value to empty cell
                    problem[nextCell.row][nextCell.col] = value;
                    //update progress of solution
                    progressListener.onUpdateProgress(problem);

                    //try solving for next cell
                    if (solveSudoku(problem, nextCell.row, nextCell.col)) {
                        return true;
                    } else {
                        Log.d(TAG, "Backtrack: " + nextCell.row + "," + nextCell.col + "::0");
                        problem[nextCell.row][nextCell.col] = 0;//reset value
                    }
                }
            }
        }
        return false;
    }


    private boolean isValidValue(int[][] problem, int row, int col, int value) {

        //check in row
        for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
            if (col != columnIndex && problem[row][columnIndex] == value) {
                return false;
            }
        }

        //check in column
        for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
            if (row != rowIndex && problem[rowIndex][col] == value) {
                return false;
            }
        }

        //check in 3X3
        int start3x3RowIndex = (row / 3) * 3;// 0 for 0-2, 3 for 3-5, 6 for 6-8
        int start3x3ColIndex = (col / 3) * 3;

        for (int rowIndex = start3x3RowIndex; rowIndex < 3 + start3x3RowIndex; rowIndex++) {
            for (int columnIndex = start3x3ColIndex; columnIndex < 3 + start3x3ColIndex; columnIndex++) {
                if (!(row == rowIndex && col == columnIndex) && problem[rowIndex][columnIndex] == value) {
                    Log.d(TAG, "isValidValue: box check for  " + row + rowIndex + "," + col + columnIndex + ":::" + value);
                    return false;
                }
            }
        }
        Log.d(TAG, "isValidValue: valid number" + value + " for " + row + "," + col);

        return true;
    }

    private Cell getNextCell(int row, int col) {
        col++; //increment column
        if (col > 8) {//max column
            col = 0;//start at 0
            row++; //increment row
            if (row > 8) {//max row
                return null;//end of sudoku
            }
        }
        return new Cell(row, col);
    }


    /**
     * convert problem array to readable string format
     *
     * @param problem 9x9 int array
     * @return
     */
    public String problemToString(int[][] problem) {
        StringBuilder str = new StringBuilder();
        for (int rowIndex = 0; rowIndex < problem.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < problem[rowIndex].length; columnIndex++) {
                str.append(problem[rowIndex][columnIndex]).append("  ");
                if (columnIndex < 6 && columnIndex % 3 == 2) {
                    str.append("|  ");
                }
            }
            if (rowIndex < 6 && rowIndex % 3 == 2) {
                str.append("\n——————————————\n");
            } else {
                str.append("\n");
            }
        }
        return str.toString();
    }

    /**
     * stop solving current problem
     */
    public void stop() {
        shouldStop = true;
    }

    /**
     * progress listener
     */
    interface ProgressListener {
        /**
         * updated solution
         *
         * @param solution
         */
        void onUpdateProgress(int[][] solution);
    }
}
