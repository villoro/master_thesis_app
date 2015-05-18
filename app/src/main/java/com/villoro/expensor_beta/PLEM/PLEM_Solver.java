package com.villoro.expensor_beta.PLEM;

import android.util.Log;
import com.villoro.expensor_beta.Utilities.UtilitiesNumbers;
import java.util.ArrayList;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

/**
 * Created by Arnau on 11/04/2015.
 */

/* INFO:
    General API:    http://lpsolve.sourceforge.net/5.5/
    Java API:       http://lpsolve.sourceforge.net/5.5/Java/docs/api/index.html
    Java examples:  http://lpsolve.sourceforge.net/5.5/Java/README.html#usage
                    http://lpsolve.sourceforge.net/5.5/formulate.htm#Java
 */
public class PLEM_Solver {

    private final static double[] LAMBDA = {1000, 50, 100};
    //private final static double[] LAMBDA = {4000, 1000, 50, 100};
    private final static double EPSILON = 0.0001;
    private final static long TIME_MAX = 10;

    int n_people, N;
    double M;
    long[] ids;
    double[] balances;
    int[] rows;
    int n_positive, n_negative;

    BalancesWithId balances_positives;
    BalancesWithId balances_negatives;

    LpSolve lp;

    CommPLEM_Solver comm;

    //TODO: delete aux
    String aux_ids, aux_balances;

    //Constructor and initializations
    public PLEM_Solver(long[] identities, double[] valueBalances){
        n_people = valueBalances.length;
        ids = new long[n_people];
        balances = new double[n_people];

        this.ids = identities;
        this.balances = valueBalances;

        n_negative = 0;
        n_positive = 0;
        M = 0;

        aux_ids = ""; aux_balances = "";

        for (double value : valueBalances){
            if(value > 0){
                n_positive++;
            } else {
                n_negative++;
            }
            M += Math.abs(value);
        }
    }

    public String solve(){
        createSeparatedBalances();
        String output = "";

        Solution solution_greedily = solveGreedily();

        try {
            Solution solution_exact = solvePLEM();
            output = "" +solution_exact.time_spent;
            solution_exact.saveSolution();
            Log.d("", "solution exact feasible?= " + solution_exact.feasible);
            if (solution_exact.feasible) {
                if(solution_exact.z < solution_greedily.z){
                    Log.e("", "SAVE EXACT since exact= " + solution_exact.z + " < greedily= " + solution_greedily.z);
                    solution_exact.saveSolution();
                } else {
                    Log.e("", "SAVE GREEDILY since exact= " + solution_exact.z + " > greedily= " + solution_greedily.z);
                    solution_greedily.saveSolution();
                }
            }
        } catch (LpSolveException e) {
            e.printStackTrace();
            Log.e("", "SAVE GREEDILY because there was an error in exact solution");
            solution_greedily.saveSolution();
        }
        return output;
    }

    //inner class to represent people with his id and balance
    private class BalancesWithId{
        long[] ids;
        int[] internalId;
        double[] balances;
        int i;

        public BalancesWithId(int N){
            ids = new long[N];
            balances = new double[N];
            internalId = new int[N];
            i = 0;
        }

        public void add(long id, double balance){
            ids[i] = id;
            balances[i] = balance;
            internalId[i] = i;
            i++;
        }

        public void orderByBalance(){
            for (int i = 0; i < balances.length; i++) {
                for (int j = 0; j < balances.length - 1 - i; j++) {
                    if (balances[j] < balances[j + 1]) {
                        double swapBalance = balances[j];
                        balances[j] = balances[j + 1];
                        balances[j + 1] = swapBalance;
                        long swapId = ids[j];
                        ids[j] = ids[j + 1];
                        ids[j + 1] = swapId;
                        int swapInternalId = internalId[j];
                        internalId[j] = internalId[j + 1];
                        internalId[j + 1] = swapInternalId;
                    }
                }
            }
        }
    }

    private class Solution {
        ArrayList<Long> from;
        ArrayList<Long> to;
        ArrayList<Double> money;

        double time_spent, z;
        boolean feasible;

        public Solution(){
            from = new ArrayList<>();
            to = new ArrayList<>();
            money = new ArrayList<>();
        }

        public void addTransfer(long from, long to, double money){
            this.from.add(from);
            this.to.add(to);
            this.money.add(money);
        }

        public void saveSolution(){
            for (int i = 0; i < money.size(); i++) {
                Log.d("", money.get(i) + ", from= " + from.get(i) + ", to= " + to.get(i));
            }
            comm.saveSolution(from, to, money);
        }
    }

    //separate all people to people with positive balance and with negative balance
    private void createSeparatedBalances(){
        balances_positives = new BalancesWithId(n_positive);
        balances_negatives = new BalancesWithId(n_negative);

        for (int i = 0; i < balances.length; i++){
            if(balances[i] > 0){
                balances_positives.add(ids[i], balances[i]);
            } else {
                balances_negatives.add(ids[i], -balances[i]);
            }

            aux_ids += ids[i] + "  ";
            aux_balances += balances[i] + "  ";
        }
    }

    private void printBalanceWithId(BalancesWithId balancesWithId){
        String aux = "balances= ";
        for(double val : balancesWithId.balances){
            aux += val + " ";
        } Log.d("", aux);
        aux = "id= ";
        for(long val : balancesWithId.ids){
            aux += val + " ";
        } Log.d("", aux);
    }

    private Solution solveGreedily(){
        Solution solution = new Solution();
        balances_positives.orderByBalance();
        balances_negatives.orderByBalance();

        printBalanceWithId(balances_positives);
        printBalanceWithId(balances_negatives);

        boolean finish = false;
        int[] t_count = new int[n_positive];
        int[] q_count = new int[n_negative];
        int a = 0;
        int b = 0;
        int p = 0;
        double t = 0;
        double q = 0;

        while(!finish){
            double moneyTransferred = Math.min(balances_positives.balances[0],
                    balances_negatives.balances[0]);

            balances_positives.balances[0] -= moneyTransferred;
            balances_negatives.balances[0] -= moneyTransferred;

            solution.addTransfer(balances_negatives.ids[0], balances_positives.ids[0], moneyTransferred);

            t_count[ balances_positives.internalId[0] ]++;
            q_count[ balances_negatives.internalId[0] ]++;
            p++;

            balances_positives.orderByBalance();
            balances_negatives.orderByBalance();

            if(Math.abs(balances_positives.balances[0]) < EPSILON ||
                    Math.abs(balances_negatives.balances[0]) < EPSILON) {
                finish = true;
                t = Math.abs(balances_positives.balances[0]);
                q = Math.abs(balances_negatives.balances[0]);
            }
            printBalanceWithId(balances_negatives);
            printBalanceWithId(balances_positives);
        }

        for (int every_t : t_count){
            if (every_t > a){
                a = every_t;
            }
        }
        for (int every_q : q_count){
            if (every_q > b){
                b = every_q;
            }
        }

        solution.z = p + LAMBDA[0]*(t+q) + LAMBDA[1]*a + LAMBDA[2]*b;
        Log.d("", "p= " + p + ", t= " + t + ", q= " + q + ", a= " + a + ", b= " + b);
        Log.d("", "z= " + solution.z);
        createSeparatedBalances();
        return solution;
    }

    private Solution solvePLEM() throws LpSolveException {

        Log.e("", "starting to solve, mode= ALWAYS");
        N = 2 * n_negative * n_positive + n_negative + n_positive + 2;

        //make that stupid array
        rows = new int[N];
        for (int i = 0; i < N ; i++){
            rows[i] = i + 1;
        }

        //Log.d("", "ids= " + aux_ids);
        //Log.d("", "balances= " + aux_balances);
        Log.d("", "N= " + N + " ,n_pos= " + n_positive + " ,n_neg= " + n_negative);

        //set names of variables
        lp = LpSolve.makeLp(0, N);
        for (int i = 0; i < n_negative ; i++){
            for (int j = 0; j < n_positive ; j++){
                lp.setColName(getXIndex(i,j), "x" + i + j);

                //Log.d("", "i= " + i + ", j= " + j + ", name= x" + i + j + " , index= " + getXIndex(i,j) + " , i_bin= " + getPIndex(i,j));
                lp.setColName(getPIndex(i, j), "p" + i + j);
                lp.setBinary(getPIndex(i,j), true);
            }
        }
        for (int i = 0; i < n_negative; i++) {
            lp.setColName(getTIndex(i), "t" + i);
        }
        for (int i = 0; i < n_positive; i++) {
            lp.setColName(getQIndex(i), "q" + i);
        }
        lp.setColName(N-1, "a"); lp.setInt(N-1, true);
        lp.setColName(N, "b"); lp.setInt(N, true);

        lp.setAddRowmode(true);
        lp.setTimeout(TIME_MAX);


        //Add constraints
        double[] values1;
        double[] values2;

        for (int i = 0; i < n_negative ; i++) {
            values1 = new double[N];
            values2 = new double[N];
            for (int j = 0; j < n_positive; j++) {
                values1[getXIndex(i,j) - 1] = 1;
                values2[getPIndex(i, j) - 1] = 1;
                values1[getTIndex(i) - 1] = 1;
            }
            values2[N-2] = -1;
            printConstraint(values1, balances_negatives.balances[i]);
            printConstraint(values2, 0);
            lp.addConstraintex(N, values1, rows, lp.EQ, balances_negatives.balances[i]);
            lp.addConstraintex(N, values2, rows, lp.LE, 0);
        }

        for (int j = 0; j < n_positive ; j++) {
            values1 = new double[N];
            values2 = new double[N];
            for (int i = 0; i < n_negative; i++) {
                values1[getXIndex(i,j) - 1] = 1;
                values2[getPIndex(i,j) - 1] = 1;
                values1[getQIndex(j) - 1] = 1;
            }
            values2[N-1] = -1;
            printConstraint(values1, balances_positives.balances[j]);
            printConstraint(values2, 0);
            lp.addConstraintex(N, values1, rows, lp.EQ, balances_positives.balances[j]);
            lp.addConstraintex(N, values2, rows, lp.LE, 0);
        }

        for (int i = 0; i < n_negative * n_positive ; i++){
            values1 = new double[N];
            values1[i] = 1;
            values1[getPIndex(i) - 1] = -M;
            printConstraint(values1, 0);
            lp.addConstraintex(N, values1, rows, lp.LE, 0);
        }

        lp.setAddRowmode(false); // rowmode should be turned off again when done building the model

        //add objective function
        double[] aux = new double[N];
        for (int i = 0; i < n_negative * n_positive; i++) {
            aux[getPIndex(i) - 1] = 1;
        }
        for (int i = 0; i < n_negative; i++) {
            aux[getTIndex(i) - 1] = LAMBDA[0];
        }
        for (int i = 0; i < n_positive; i++) {
            aux[getQIndex(i) - 1] = LAMBDA[0];
        }
        aux[N-1] = LAMBDA[1];
        aux[N-2] = LAMBDA[2];
        printConstraint(aux, 0);
        lp.setObjFnex(N, aux, rows);
        lp.setMinim();
        lp.setEpslevel(3); //set precision to minimum (solution would be faster)

        //solve the MILP
        lp.setVerbose(LpSolve.IMPORTANT);
        lp.solve();

        //Write in var Solution
        Solution solution = new Solution();
        lp.getVariables(aux);
        for(int i = 0; i < n_positive * n_negative ; i++){
            if(Math.abs(aux[i]) > EPSILON) {
                Log.d("", "i= " + getIFromPosition(i) + ", j= " + getJFromPosition(i) + ", val= " + aux[i]);
                solution.addTransfer(balances_negatives.ids[getIFromPosition(i)],
                        balances_positives.ids[getJFromPosition(i)], aux[i]);
            }
        }
        for(int i = 0; i < N; i++)
            Log.d("", lp.getColName(i + 1) + ": " + UtilitiesNumbers.round(aux[i], 2) + "     (" + aux[i]+")");
        solution.time_spent = lp.timeElapsed();
        solution.z = lp.getObjective();

        solution.feasible = aux[N-1] + aux[N-2] > EPSILON;
        Log.d("", "Objective value: " + lp.getObjective());
        Log.d("", "time= " + lp.timeElapsed());
        Log.d("", "isFeasible= " + solution.feasible + "   a= " + aux[N-1] + ", b= " + aux[N-2]);

        lp.deleteLp();
        return solution;

    }

    private int getXIndex(int i, int j){
        return i*n_positive + j + 1;
    }

    private int getPIndex(int i, int j){
        return i*n_positive + j + 1 + n_positive*n_negative;
    }

    private int getPIndex(int pos){
        return pos + n_positive*n_negative + 1;
    }

    private int getTIndex(int pos){
        return pos + 2*n_positive*n_negative + 1;
    }

    private int getQIndex(int pos){
        return pos + 2*n_positive*n_negative + n_negative + 1;
    }

    private int getJFromPosition(int pos){
        return pos % n_positive;
    }

    private int getIFromPosition(int pos){
        return (pos - getJFromPosition(pos))/n_positive;
    }

    private void printConstraint(double[] values, double rh){
        /*String aux = "";
        try {
            for (int i = 0; i < values.length; i++) {
                if (values[i] == 1) {
                    aux += "+" + lp.getColName(i + 1) + " ";
                } else if (values[i] != 0 && values[i] != 1){
                    aux += values[i] + "*" + lp.getColName(i + 1) + " ";
                }

            }
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
        Log.d("", "constr1= " + aux + " | " + rh);*/
    }

    public void setCommunicator(CommPLEM_Solver comm){
        this.comm = comm;
    }

    public interface CommPLEM_Solver{
        public void saveSolution(ArrayList<Long> from, ArrayList<Long> to, ArrayList<Double> money);
    }
}
