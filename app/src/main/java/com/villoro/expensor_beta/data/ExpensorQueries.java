package com.villoro.expensor_beta.data;

import com.parse.ParseUser;
import com.villoro.expensor_beta.Utilities.UtilitiesDates;

/**
 * Created by Arnau on 10/05/2015.
 */
public class ExpensorQueries {

    private static final String SELECT = "SELECT ";
    private static final String SELECT_ALL_FROM = "SELECT * FROM ";
    private static final String FROM = " FROM ";
    private static final String GROUP_BY = " GROUP BY ";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String ASC = " ASC ";
    private static final String DESC = " DESC ";
    private static final String JOIN = " JOIN ";
    private static final String LEFT_JOIN = " LEFT JOIN ";
    private static final String ON = " ON ";
    private static final String EQUAL = " = ";
    private static final String NOT_EQUAL = " != ";
    private static final String WHERE = " WHERE ";
    private static final String WHERE_CLAUSE_NAME = Tables.NAME + " = ";
    private static final String GREATER_THAN = " > ";
    private static final String GREATER_EQUAL_THAN = " >= ";
    private static final String LESS_EQUAL_THAN = " <= ";
    private static final String LESS_THAN = " < ";
    private static final String LIKE_OPEN = " LIKE '%";
    private static final String LIKE_CLOSE = "%' ";
    private static final String APOSTROPHE = "'";
    private static final String COMA = ", ";
    private static final String SECOND_WHERE = " =? and ";
    private static final String FIRST_WHERE = " =?";
    private static final String CLOSE = ";";
    private static final String PARENTHESIS_OPEN = "(";
    private static final String PARENTHESIS_CLOSE = ")";
    private static final String SUM = "SUM";
    private static final String ABSOLUTE = " ABS";
    private static final String AS = " AS ";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String AUX = "aux";
    private static final String AUX_ID = "auxID";
    private static final String AUX0 = "aux0";
    private static final String IS_NULL = " IS NULL ";
    private static final String DATETIME = "Datetime('";
    private static final String CLOSE_DATE = "')";

    public static final String queryGraph(String type, int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(Tables.TYPE).append(COMA).append(sumAmount());
        sb.append(FROM).append(Tables.TABLENAME_TRANSACTION_SIMPLE);
        sb.append(WHERE).append(whereDateInterval(year, month)).append(AND).append(whereNoDeleted());
        sb.append(GROUP_BY).append(Tables.TYPE);

        return sb.toString();
    }

    public static final String queryGraphPeople(int caseBalance, int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(sumAmount2());
        sb.append(FROM).append(Tables.TABLENAME_PEOPLE);
        sb.append(LEFT_JOIN);
        sb.append(PARENTHESIS_OPEN).append(SELECT).append(Tables.PEOPLE_ID).append(COMA).append(sumAmount());
        sb.append(FROM).append(Tables.TABLENAME_TRANSACTIONS_PEOPLE);
        sb.append(WHERE).append(whereNoDeleted()).append(AND).append(whereDateOnlyFinalDate(year, month));
        sb.append(GROUP_BY).append(Tables.PEOPLE_ID).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(AUX);
        sb.append(ON).append(Tables.TABLENAME_PEOPLE).append(".").append(Tables.ID).append(EQUAL);
        sb.append(AUX).append(".").append(Tables.PEOPLE_ID);
        sb.append(WHERE).append(balanceFromCase(caseBalance));
        sb.append(AND).append(notMe()).append(AND);
        sb.append(whereNoDeleted()).append(CLOSE);

        return sb.toString();
    }

    private static final String whereDateInterval(int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(Tables.DATE).append(GREATER_EQUAL_THAN).append(DATETIME);
        sb.append(UtilitiesDates.getFirstDay(year, month)).append(CLOSE_DATE).append(AND);
        sb.append(Tables.DATE).append(LESS_EQUAL_THAN).append(DATETIME);
        sb.append(UtilitiesDates.getLastDay(year, month)).append(CLOSE_DATE);

        return sb.toString();
    }

    private static final String whereDateOnlyFinalDate(int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(Tables.DATE).append(LESS_EQUAL_THAN).append(DATETIME);
        sb.append(UtilitiesDates.getLastDay(year, month)).append(CLOSE_DATE);

        return sb.toString();
    }

    private static final String whereNoDeleted(){
        StringBuilder sb = new StringBuilder();

        sb.append(Tables.DELETED).append(EQUAL).append("'");
        sb.append(Tables.FALSE).append("'");

        return sb.toString();
    }

    public static final String queryGraphAll(String type, int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(Tables.ID).append(COMA).append(Tables.NAME).append(COMA);
        sb.append(Tables.COLOR).append(COMA).append(Tables.SUM_AMOUNT);
        sb.append(FROM);
        sb.append(PARENTHESIS_OPEN).append(SELECT);
        sb.append(Tables.CATEGORY_ID).append(COMA).append(sumAmount());
        sb.append(FROM).append(Tables.TABLENAME_TRANSACTION_SIMPLE);
        sb.append(WHERE).append(Tables.TYPE).append(EQUAL).append(APOSTROPHE).append(type).append(APOSTROPHE);
        sb.append(AND).append(whereDateInterval(year, month));
        sb.append(AND).append(whereNoDeleted());
        sb.append(GROUP_BY).append(Tables.CATEGORY_ID).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(AUX).append(JOIN).append(Tables.TABLENAME_CATEGORIES);
        sb.append(ON).append(AUX).append(".").append(Tables.CATEGORY_ID).append(EQUAL);
        sb.append(Tables.TABLENAME_CATEGORIES).append(".").append(Tables.ID);
        sb.append(WHERE).append(whereNoDeleted());
        sb.append(ORDER_BY).append(Tables.SUM_AMOUNT).append(DESC).append(CLOSE);

        return sb.toString();
    }

    private static final String sumAmount(){
        StringBuilder sb = new StringBuilder();

        sb.append(SUM).append(PARENTHESIS_OPEN).append(Tables.AMOUNT).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(Tables.SUM_AMOUNT);

        return sb.toString();
    }

    private static final String sumAmount2(){
        StringBuilder sb = new StringBuilder();

        sb.append(SUM).append(PARENTHESIS_OPEN).append(Tables.SUM_AMOUNT).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(Tables.SUM_AMOUNT2);

        return sb.toString();
    }

    public static final String queryTransactionSimpleMonth(String type, int year, int month){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT_ALL_FROM);
        sb.append(PARENTHESIS_OPEN).append(SELECT);
        sb.append(Tables.ID).append(AS).append(AUX_ID).append(COMA).append(Tables.COLOR).append(COMA).append(Tables.NAME);
        sb.append(FROM).append(Tables.TABLENAME_CATEGORIES);
        sb.append(WHERE).append(whereType(null, type)).append(AND).append(whereNoDeleted()).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(AUX).append(JOIN).append(Tables.TABLENAME_TRANSACTION_SIMPLE);
        sb.append(ON).append(AUX).append(".").append(AUX_ID).append(EQUAL).append(Tables.CATEGORY_ID);
        sb.append(WHERE).append(whereType(null, type)).append(AND).append(whereDateInterval(year, month));
        sb.append(AND).append(whereNoDeleted());
        sb.append(ORDER_BY).append(Tables.DATE).append(ASC).append(CLOSE);

        return sb.toString();
    }

    public static final String queryTransactionPersonal(long id){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT_ALL_FROM);
        sb.append(PARENTHESIS_OPEN).append(SELECT);
        sb.append(Tables.ID).append(COMA).append(Tables.NAME);
        sb.append(FROM).append(Tables.TABLENAME_PEOPLE);
        sb.append(WHERE).append(whereNoDeleted()).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(AUX).append(JOIN).append(Tables.TABLENAME_TRANSACTIONS_PEOPLE);
        sb.append(ON).append(AUX).append(".").append(Tables.ID).append(EQUAL).append(Tables.PEOPLE_ID);
        sb.append(WHERE).append(whereNoDeleted());
        sb.append(AND).append(Tables.PEOPLE_ID).append(EQUAL).append(APOSTROPHE).append(id).append(APOSTROPHE);
        sb.append(ORDER_BY).append(Tables.DATE).append(ASC).append(CLOSE);

        return sb.toString();
    }

    public static final String whereType(String where, String type){
        StringBuilder sb = new StringBuilder();

        if(where != null){
            sb.append(where).append(AND);
        }
        sb.append(Tables.TYPE).append(EQUAL).append(APOSTROPHE).append(type).append(APOSTROPHE);

        return sb.toString();
    }

    public static final String queryPeopleWithNameLike(String like){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(Tables.ID).append(COMA).append(Tables.NAME);
        sb.append(FROM).append(Tables.TABLENAME_PEOPLE);
        sb.append(WHERE).append(whereNoDeleted());
        sb.append(AND).append(notMe());
        sb.append(AND).append(Tables.NAME).append(LIKE_OPEN).append(like).append(LIKE_CLOSE);
        sb.append(CLOSE);

        return sb.toString();
    }

    public static final String notMe(){
        StringBuilder sb = new StringBuilder();

        sb.append(Tables.EMAIL).append(NOT_EQUAL).append(APOSTROPHE);
        sb.append(ParseUser.getCurrentUser().getEmail()).append(APOSTROPHE);

        return sb.toString();
    }

    public static final String queryPeopleFromBalanceCase(int caseBalance){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(Tables.NAME).append(COMA).append(Tables.TABLENAME_PEOPLE).append(".").append(Tables.ID);
        sb.append(COMA).append(Tables.SUM_AMOUNT);
        sb.append(FROM).append(Tables.TABLENAME_PEOPLE);
        sb.append(LEFT_JOIN);
        sb.append(PARENTHESIS_OPEN).append(SELECT).append(Tables.PEOPLE_ID).append(COMA).append(sumAmount());
        sb.append(FROM).append(Tables.TABLENAME_TRANSACTIONS_PEOPLE);
        sb.append(WHERE).append(whereNoDeleted());
        sb.append(GROUP_BY).append(Tables.PEOPLE_ID).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(AUX);
        sb.append(ON).append(Tables.TABLENAME_PEOPLE).append(".").append(Tables.ID).append(EQUAL);
        sb.append(AUX).append(".").append(Tables.PEOPLE_ID);
        sb.append(WHERE).append(balanceFromCase(caseBalance));
        sb.append(AND).append(notMe()).append(AND);
        sb.append(whereNoDeleted()).append(CLOSE);

        return sb.toString();
    }

    private static final String balanceFromCase(int caseBalance){
        StringBuilder sb = new StringBuilder();

        if(caseBalance == ExpensorContract.PeopleEntry.CASE_BALANCE_POSITIVE){
            sb.append(Tables.SUM_AMOUNT).append(GREATER_THAN).append("0.00001");
        } else if (caseBalance == ExpensorContract.PeopleEntry.CASE_BALANCE_NEGATIVE){
            sb.append(Tables.SUM_AMOUNT).append(LESS_THAN).append("-0.00001");
        } else {
            sb.append(PARENTHESIS_OPEN);
            sb.append(ABSOLUTE).append(PARENTHESIS_OPEN).append(Tables.SUM_AMOUNT).append(PARENTHESIS_CLOSE);
            sb.append(LESS_EQUAL_THAN).append("0.00001");
            sb.append(OR).append(AUX).append(".").append(Tables.PEOPLE_ID).append(IS_NULL);
            sb.append(PARENTHESIS_CLOSE);
        }

        return sb.toString();
    }

    public static final String queryPersonalGroupSummary(long groupId){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(Tables.TABLENAME_PEOPLE).append(".").append(Tables.ID);
        sb.append(COMA).append(Tables.TABLENAME_PEOPLE).append(".").append(Tables.NAME);
        sb.append(innerBalanceSumAmountAs("t1", Tables.PAID, Tables.PAID));
        sb.append(innerBalanceSumAmountAs("t1", Tables.SPENT, Tables.SPENT));
        sb.append(innerBalanceSumAmountAs("t2", Tables.PAID, Tables.RECEIVED));
        sb.append(innerBalanceSumAmountAs("t2", Tables.SPENT, Tables.GIVEN));
        sb.append(FROM).append(Tables.TABLENAME_PEOPLE_IN_GROUP);
        sb.append(LEFT_JOIN).append(innerBalancesGroup(groupId, Tables.TYPE_TRANSACTION, "t1"));
        sb.append(LEFT_JOIN).append(innerBalancesGroup(groupId, Tables.TYPE_GIVE, "t2"));
        sb.append(LEFT_JOIN).append(Tables.TABLENAME_PEOPLE);
        sb.append(ON).append(Tables.TABLENAME_PEOPLE).append(".").append(Tables.ID).append(EQUAL);
        sb.append(Tables.TABLENAME_PEOPLE_IN_GROUP).append(".").append(Tables.PEOPLE_ID);
        sb.append(WHERE).append(Tables.TABLENAME_PEOPLE_IN_GROUP).append(".").append(whereNoDeleted());

        sb.append(CLOSE);

        return sb.toString();
    }

    private static final String innerBalancesGroup(long groupId, String type, String as){
        StringBuilder sb = new StringBuilder();

        sb.append(PARENTHESIS_OPEN).append(SELECT).append(AUX).append(".").append(Tables.PEOPLE_ID);
        sb.append(COMA).append(SUM).append(PARENTHESIS_OPEN);
        sb.append(AUX).append(".").append(Tables.PAID).append(PARENTHESIS_CLOSE).append(AS).append(Tables.PAID);
        sb.append(COMA).append(SUM).append(PARENTHESIS_OPEN);
        sb.append(AUX).append(".").append(Tables.SPENT).append(PARENTHESIS_CLOSE).append(AS).append(Tables.SPENT);

        sb.append(FROM).append(PARENTHESIS_OPEN).append(SELECT_ALL_FROM).append(Tables.TABLENAME_WHO_PAID_SPENT);
        sb.append(JOIN).append(Tables.TABLENAME_TRANSACTIONS_GROUP);
        sb.append(ON).append(Tables.TABLENAME_WHO_PAID_SPENT).append(".").append(Tables.TRANSACTION_ID);
        sb.append(EQUAL).append(Tables.TABLENAME_TRANSACTIONS_GROUP).append(".").append(Tables.ID);
        sb.append(WHERE).append(Tables.TABLENAME_TRANSACTIONS_GROUP).append(".").append(Tables.GROUP_ID);
        sb.append(EQUAL).append(APOSTROPHE).append(groupId).append(APOSTROPHE);
        sb.append(AND).append(Tables.TABLENAME_TRANSACTIONS_GROUP).append(".").append(Tables.TYPE).append(EQUAL);
        sb.append(APOSTROPHE).append(type).append(APOSTROPHE).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(AUX);


        sb.append(WHERE).append(AUX).append(".").append(whereNoDeleted());
        sb.append(GROUP_BY).append(AUX).append(".").append(Tables.PEOPLE_ID).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(as);
        sb.append(ON).append(as).append(".").append(Tables.PEOPLE_ID).append(EQUAL);
        sb.append(Tables.TABLENAME_PEOPLE_IN_GROUP).append(".").append(Tables.PEOPLE_ID);

        return sb.toString();
    }

    private static final String innerBalanceSumAmountAs(String nameAuxTable, String from, String as){
        StringBuilder sb = new StringBuilder();

        sb.append(COMA).append(nameAuxTable).append(".").append(from);
        sb.append(AS).append(as);

        return sb.toString();
    }

    public static final String queryPeopleInGroup(long groupId){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(Tables.NAME);
        sb.append(COMA).append(AUX).append(".").append(Tables.ID).append(AS).append(Tables.ID);
        sb.append(FROM).append(PARENTHESIS_OPEN).append(SELECT).append(Tables.NAME).append(COMA).append(Tables.ID);
        sb.append(FROM).append(Tables.TABLENAME_PEOPLE);
        sb.append(WHERE).append(whereNoDeleted()).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(AUX).append(JOIN).append(Tables.TABLENAME_PEOPLE_IN_GROUP);
        sb.append(ON).append(AUX).append(".").append(Tables.ID).append(EQUAL);
        sb.append(Tables.TABLENAME_PEOPLE_IN_GROUP).append(".").append(Tables.PEOPLE_ID);
        sb.append(WHERE).append(Tables.GROUP_ID).append(EQUAL).append(APOSTROPHE);
        sb.append(groupId).append(APOSTROPHE).append(CLOSE);

        return sb.toString();
    }

    public static final String peopleWithOnlyBalances(long groupId){
        StringBuilder sb = new StringBuilder();

        sb.append(SELECT).append(Tables.TABLENAME_WHO_PAID_SPENT).append(".").append(Tables.PEOPLE_ID);
        sb.append(COMA).append(SUM).append(PARENTHESIS_OPEN).append(Tables.PAID).append(PARENTHESIS_CLOSE);
        sb.append(" - ").append(SUM).append(PARENTHESIS_OPEN).append(Tables.SPENT).append(PARENTHESIS_CLOSE);
        sb.append(AS).append(Tables.BALANCE);
        sb.append(FROM).append(Tables.TABLENAME_WHO_PAID_SPENT);
        sb.append(JOIN).append(Tables.TABLENAME_PEOPLE_IN_GROUP);
        sb.append(ON).append(Tables.TABLENAME_WHO_PAID_SPENT).append(".").append(Tables.PEOPLE_ID);
        sb.append(EQUAL).append(Tables.TABLENAME_PEOPLE_IN_GROUP).append(".").append(Tables.PEOPLE_ID);
        sb.append(WHERE).append(Tables.TABLENAME_PEOPLE_IN_GROUP).append(".").append(Tables.GROUP_ID);
        sb.append(EQUAL).append(APOSTROPHE).append(groupId).append(APOSTROPHE);
        sb.append(AND).append(Tables.TABLENAME_WHO_PAID_SPENT).append(".").append(whereNoDeleted());
        sb.append(GROUP_BY).append(Tables.TABLENAME_WHO_PAID_SPENT).append(".").append(Tables.PEOPLE_ID);

        return sb.toString();
    }


}
