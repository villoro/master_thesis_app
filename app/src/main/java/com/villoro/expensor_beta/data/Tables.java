package com.villoro.expensor_beta.data;

/**
 * Created by Arnau on 19/01/2015.
 */

import android.provider.BaseColumns;
import android.util.Log;

public class Tables {

    public String tableName;
    public String[] columns;
    public String[] origin;
    public String acl;
    public int lastPrivateColumn;

    private String[] types;
    private boolean[] unique;


    //main columns
    public static final String ID = BaseColumns._ID;
    public static final String DATE = "date";
    public static final String AMOUNT = "amount";
    public static final String COMMENTS = "comments";
    public static final String FROM = "fromX";
    public static final String TO = "toX";
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String TYPE = "type";
    public static final String EMAIL = "email";
    public static final String PAID = "paid";
    public static final String SPENT = "spend";

    //query columns
    public static final String SUM_AMOUNT = "sum_amount";
    public static final String SUM_AMOUNT2 = "sum_of_sum_amount";
    public static final String RECEIVED = "received";
    public static final String GIVEN = "given";
    public static final String BALANCE = "balance";

    //foreign keys columns
    public static final String CATEGORY_ID = "categoryID";
    public static final String GROUP_ID = "groupID";
    public static final String TRANSACTION_ID = "transactionID";
    public static final String PEOPLE_ID = "peopleID";
    public static final String POINTS = "pointsTo";

    //Parse sync
    public static final String USER_ID = "userID";
    public static final String LAST_UPDATE = "updated";
    public static final String PARSE_ID_NAME = "parseID";
    public static final String DELETED = "deleted";
    public static final String NEEDS_ACL = "needACL";

    //Values for parse
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final String ACL_INDIVIDUAL = "individual";
    public static final String ACL_ONE_PERSON = "one_person";
    public static final String ACL_GROUP = "group";
    public static final String ACL_PUBLIC = "public";

    //main tables
    public static final String TABLENAME_TRANSACTION_SIMPLE = "transSimple";
    public static final String TABLENAME_CATEGORIES = "categories";
    public static final String TABLENAME_PEOPLE = "people";
    public static final String TABLENAME_PEOPLE_IN_GROUP = "peopleInGroup";
    public static final String TABLENAME_GROUPS = "groups";
    public static final String TABLENAME_TRANSACTIONS_GROUP = "transGroups";
    public static final String TABLENAME_TRANSACTIONS_PEOPLE = "transPeople";
    public static final String TABLENAME_WHO_PAID_SPENT = "whoPaidSpent";
    public static final String TABLENAME_HOW_TO_SETTLE = "howToSettle";

    //types in SQLite
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_INT = "integer";
    public static final String TYPE_DATE = "text";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_LONG = "long";

    //types used in expensor
    public static final String TYPE_EXPENSE = "expense";
    public static final String TYPE_INCOME = "income";

    public static final String TYPE_TRANSACTION = "transaction";
    public static final String TYPE_GIVE = "gives";


    public static final String[] TABLES = {
            TABLENAME_TRANSACTION_SIMPLE,
            TABLENAME_CATEGORIES,
            TABLENAME_PEOPLE,
            TABLENAME_GROUPS,
            TABLENAME_PEOPLE_IN_GROUP,
            TABLENAME_TRANSACTIONS_GROUP,
            TABLENAME_TRANSACTIONS_PEOPLE,
            TABLENAME_WHO_PAID_SPENT,
            TABLENAME_HOW_TO_SETTLE};


    public Tables(String tableName)
    {
        this.tableName = tableName;

        switch(tableName){
            case TABLENAME_TRANSACTION_SIMPLE:
                columns = new String[]{DATE, CATEGORY_ID, AMOUNT, COMMENTS, TYPE};
                origin = new String[]{null, TABLENAME_CATEGORIES, null, null, null};
                types = new String[]{TYPE_DATE, TYPE_INT, TYPE_DOUBLE, TYPE_TEXT, TYPE_TEXT};
                unique = new boolean[]{false, false, false, false, false};
                lastPrivateColumn = 5;
                acl = ACL_INDIVIDUAL;
                break;

            case TABLENAME_CATEGORIES:
                columns = new String[]{NAME, TYPE, COLOR};
                origin = new String[]{null, null, null};
                types = new String[]{TYPE_TEXT, TYPE_TEXT, TYPE_INT};
                unique = new boolean[]{false, false, false};
                lastPrivateColumn = 3;
                acl = ACL_INDIVIDUAL;
                break;

            case TABLENAME_PEOPLE:
                columns = new String[]{NAME, POINTS, // 1 - 2 private
                        EMAIL, USER_ID}; //  3 - 4 public
                origin = new String[]{null, null,
                        null, null};
                types = new String[]{TYPE_TEXT, TYPE_INT,
                        TYPE_TEXT, TYPE_TEXT};
                unique = new boolean[]{false, false,
                        true, true};
                lastPrivateColumn = 2;
                acl = ACL_PUBLIC;
                break;

            case TABLENAME_PEOPLE_IN_GROUP:
                columns = new String[]{PEOPLE_ID, GROUP_ID};
                origin = new String[]{TABLENAME_PEOPLE, TABLENAME_GROUPS};
                types = new String[]{TYPE_INT, TYPE_INT};
                unique = new boolean[]{false, false};
                lastPrivateColumn = -1;
                acl = ACL_GROUP;
                break;

            case TABLENAME_GROUPS:
                columns = new String[]{NAME};
                origin = new String[]{null};
                types = new String[]{TYPE_TEXT};
                unique = new boolean[]{true};
                lastPrivateColumn = -1;
                acl = ACL_GROUP;
                break;

            case TABLENAME_TRANSACTIONS_GROUP:
                columns = new String[]{DATE, GROUP_ID, AMOUNT, COMMENTS, TYPE};
                origin = new String[]{null, TABLENAME_GROUPS, null, null, null};
                types = new String[]{TYPE_DATE, TYPE_INT, TYPE_DOUBLE, TYPE_TEXT, TYPE_TEXT};
                unique = new boolean[]{false, false, false, false, false};
                lastPrivateColumn = -1;
                acl = ACL_GROUP;
                break;

            case TABLENAME_TRANSACTIONS_PEOPLE:
                columns = new String[]{DATE, AMOUNT, COMMENTS, PEOPLE_ID};
                origin = new String[]{null, null, null, TABLENAME_PEOPLE};
                types = new String[]{TYPE_DATE, TYPE_DOUBLE, TYPE_TEXT, TYPE_INT};
                unique = new boolean[]{false, false, false, false};
                lastPrivateColumn = -1;
                acl = ACL_ONE_PERSON;
                break;

            case TABLENAME_WHO_PAID_SPENT:
                columns = new String[]{TRANSACTION_ID, PEOPLE_ID, PAID, SPENT};
                origin = new String[]{TABLENAME_TRANSACTIONS_GROUP, TABLENAME_PEOPLE, null, null};
                types = new String[]{TYPE_INT, TYPE_INT, TYPE_DOUBLE, TYPE_DOUBLE};
                unique = new boolean[]{false, false, false, false};
                lastPrivateColumn = -1;
                acl = ACL_GROUP;
                break;

            case TABLENAME_HOW_TO_SETTLE:
                columns = new String[]{GROUP_ID, FROM, TO, AMOUNT};
                origin = new String[]{TABLENAME_GROUPS, TABLENAME_PEOPLE, TABLENAME_PEOPLE, null};
                types = new String[]{TYPE_INT, TYPE_INT, TYPE_INT, TYPE_DOUBLE};
                unique = new boolean[]{false, false, false, false};
                lastPrivateColumn = -1;
                acl = ACL_GROUP;
                break;

            default:
                columns = null; types = null; origin = null; unique = null;
        }
    }


    public boolean isAllPrivate(){
        return lastPrivateColumn >= columns.length;
    }

    public boolean isAllShared(){
        return lastPrivateColumn < 0;
    }

    public boolean isPrivateAndShared(){
        return !isAllPrivate() && !isAllShared();
    }

    public String createTable()
    {
        int length = columns.length;
        if(length != origin.length || length != types.length || length != unique.length){
            Log.e("Tables", "error creating table= " + tableName);
            return null;
        }
        return createGenericTable(tableName, columns, types, unique);
    }

    public String[] getColumnsWithID()
    {
        String[] aux = new String[columns.length + 1];
        aux[0] = ID;
        for (int i = 0 ; i < columns.length ; i++)
        {
            aux[i+1] = columns[i];
        }
        return aux;
    }

    public String[] getTypes()
    {
        return types;
    }

    public String dropTable()
    {
        return DROP_TEXT + tableName;
    }


	/* Internal utilities
	 * --------------------------------------------------------------------------------
	*/

    private static final String DROP_TEXT = "DROP TABLE IF EXISTS ";
    private static final String UNIQUE_TEXT = "NOT NULL UNIQUE";

    private static String createGenericTable(String table, String[] columns, String[] types,
                                             boolean[] unique)
    {
        final StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(table).append(" (");

        sb.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");

        if (columns != null)
        {
            for (int i=0 ; i < columns.length ; i++)
            {
                sb.append(", " + columns[i] + " " + types[i]);
                if(unique[i])
                {
                    sb.append(UNIQUE_TEXT);
                }
            }
        }

        //Add parse columns
        sb.append(", " + LAST_UPDATE + " " + TYPE_LONG);
        sb.append(", " + PARSE_ID_NAME + " " + TYPE_TEXT);
        sb.append(", " + DELETED + " " + TYPE_INT);

        sb.append(");");

        return sb.toString();
    }
}
