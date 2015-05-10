package com.villoro.expensor_beta.data;

import com.villoro.expensor_beta.Utility;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Arnau on 10/05/2015.
 */
public class ExpensorQueries {

    private static final String SELECT = "SELECT ";
    private static final String SELECT_ALL_FROM = "SELECT * FROM ";
    private static final String FROM = " FROM ";
    private static final String GROUP_BY = " GROUP BY ";
    private static final String JOIN = " JOIN ";
    private static final String LEFT_JOIN = " LEFT OUTER JOIN ";
    private static final String ON = " ON ";
    private static final String EQUAL = " = ";
    private static final String WHERE = " WHERE ";
    private static final String WHERE_CLAUSE_NAME = Tables.NAME + " = ";
    private static final String GREATER_THAN = " > ";
    private static final String GREATER_EQUAL_THAN = " >= ";
    private static final String LESS_EQUAL_THAN = " <= ";
    private static final String APOSTROPHE = "'";
    private static final String COMA = ", ";
    private static final String SECOND_WHERE = " =? and ";
    private static final String FIRST_WHERE = " =?";
    private static final String CLOSE = ";";
    private static final String PARENTHESIS_OPEN = "(";
    private static final String PARENTHESIS_CLOSE = ")";
    private static final String SUM = "SUM";
    private static final String AS = " AS ";
    private static final String AND = " AND ";
    private static final String AUX = "aux";
    private static final String AUX0 = "aux0";
    private static final String IS_NULL = " IS NULL ";
    private static final String DATETIME = "Datetime('";
    private static final String CLOSE_DATE = "')";

    public static final String queryGraphExpense(int year, int month){
        return queryGraph(Tables.TYPE_EXPENSE, year, month);
    }

    public static final String queryGraphIncome(int year, int month){
        return queryGraph(Tables.TYPE_EXPENSE, year, month);
    }

    private static final String queryGraph(String type, int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(sumAmount());
        sb.append(FROM).append(Tables.TABLENAME_TRANSACTION_SIMPLE);
        sb.append(WHERE).append(Tables.TYPE).append(EQUAL).append(APOSTROPHE).append(type).append(APOSTROPHE);
        sb.append(AND).append(whereDate(year, month)).append(CLOSE);

        return sb.toString();
    }

    private static final String whereDate(int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(Tables.DATE).append(GREATER_EQUAL_THAN).append(DATETIME);
        sb.append(Utility.getFirstDay(year, month)).append(CLOSE_DATE).append(AND);
        sb.append(Tables.DATE).append(LESS_EQUAL_THAN).append(DATETIME);
        sb.append(Utility.getLastDay(year, month)).append(CLOSE_DATE);

        return sb.toString();
    }

    public static final String queryGraphExpenseAll(int year, int month){
        return queryGraphAll(Tables.TYPE_EXPENSE, year, month);
    }

    public static final String queryGraphIncomeAll(int year, int month){
        return queryGraphAll(Tables.TYPE_EXPENSE, year, month);
    }

    private static final String queryGraphAll(String type, int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(Tables.NAME).append(COMA).append(Tables.SUM_AMOUNT).append(FROM);
        sb.append(PARENTHESIS_OPEN).append(SELECT);
        sb.append(Tables.CATEGORY_ID).append(COMA).append(sumAmount());
        sb.append(FROM).append(Tables.TABLENAME_TRANSACTION_SIMPLE);
        sb.append(WHERE).append(Tables.TYPE).append(EQUAL).append(APOSTROPHE).append(type).append(APOSTROPHE);
        sb.append(AND).append(whereDate(year, month)).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(AUX).append(JOIN).append(Tables.TABLENAME_CATEGORIES);
        sb.append(ON).append(AUX).append(".").append(Tables.CATEGORY_ID).append(EQUAL);
        sb.append(Tables.TABLENAME_CATEGORIES).append(".").append(Tables.ID).append(CLOSE);

        return sb.toString();
    }

    private static final String sumAmount(){
        StringBuilder sb = new StringBuilder();

        sb.append(SUM).append(PARENTHESIS_OPEN).append(Tables.AMOUNT).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(Tables.SUM_AMOUNT);

        return sb.toString();
    }

    public static final String queryIncomeMonth(int year, int month){
        return queryTransactionSimpleMonth(Tables.TYPE_INCOME, year, month);
    }

    public static final String queryExpenseMonth(int year, int month){
        return queryTransactionSimpleMonth(Tables.TYPE_EXPENSE, year, month);
    }

    private static final String queryTransactionSimpleMonth(String type, int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT_ALL_FROM).append(Tables.TABLENAME_TRANSACTION_SIMPLE);
        sb.append(WHERE).append(Tables.TYPE).append(EQUAL).append(APOSTROPHE).append(type).append(APOSTROPHE);
        sb.append(AND).append(whereDate(year, month)).append(CLOSE);

        return sb.toString();
    }


}