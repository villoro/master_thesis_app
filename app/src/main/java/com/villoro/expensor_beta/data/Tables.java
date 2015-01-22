package com.villoro.expensor_beta.data;

/**
 * Created by Arnau on 19/01/2015.
 */

import android.provider.BaseColumns;

public class Tables {

    private String tableName;
    private String[] columns;
    private String[] types;
    private boolean[] unique;

    //main columns
    public static final String ID = BaseColumns._ID;
    public static final String DATE = "date";
    public static final String AMOUNT = "amount";
    public static final String COMMENTS = "comments";
    //public static final String TAGS = "tags";
    public static final String FROM = "from_group";
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String TYPE = "type";
    public static final String EMAIL = "email";
    public static final String PAID = "paid";
    public static final String SPEND = "spend";
    public static final String LETTER = "letter";

    //foreign keys columns
    public static final String CATEGORY_ID = "categoryID";
    public static final String GROUP_ID = "groupID";
    public static final String TRANSACTION_ID = "transactionID";
    public static final String PEOPLE_ID = "peopleID";

    //Parse sync
    public static final String LAST_UPDATE = "updated";
    public static final String PARSE_ID_NAME = "parse_id";

    //main tables
    public static final String TABLENAME_EXPENSE = "expense";
    public static final String TABLENAME_INCOME = "income";
    public static final String TABLENAME_CATEGORIES = "categories";
    public static final String TABLENAME_PEOPLE = "people";
    public static final String TABLENAME_PEOPLE_IN_GROUP = "peopleInGroup";
    public static final String TABLENAME_GROUPS = "groups";
    public static final String TABLENAME_TRANSACTIONS_GROUP = "transactionsGroups";
    public static final String TABLENAME_TRANSACTIONS_PEOPLE = "transactionsPeople";
    public static final String TABLENAME_WHO_PAID = "whoPaid";
    public static final String TABLENAME_WHO_SPENT = "whoSpent";

    //types in SQLite
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_INT = "integer";
    public static final String TYPE_DATE = "text";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_BOOLEAN = "boolean";

    //types used in expensor
    public static final String TYPE_EXPENSE = "expense";
    public static final String TYPE_INCOME = "income";

    public static final String TYPE_TRANSACTION = "transaction";
    public static final String TYPE_GIVE = "gives";


    public static final String[] TABLES = {
            TABLENAME_EXPENSE,
            TABLENAME_INCOME,
            TABLENAME_CATEGORIES,
            TABLENAME_PEOPLE,
            TABLENAME_GROUPS,
            TABLENAME_PEOPLE_IN_GROUP,
            TABLENAME_TRANSACTIONS_GROUP,
            TABLENAME_TRANSACTIONS_PEOPLE,
            TABLENAME_WHO_PAID,
            TABLENAME_WHO_SPENT};


    public Tables(String tableName)
    {
        this.tableName = tableName;

        switch(tableName){
            case TABLENAME_EXPENSE:
                columns = new String[]{DATE, CATEGORY_ID, AMOUNT, COMMENTS, FROM};
                types = new String[]{TYPE_DATE, TYPE_INT, TYPE_DOUBLE, TYPE_TEXT,
                        TYPE_TEXT};
                unique = new boolean[]{false, false, false, false, false, false};
                break;

            case TABLENAME_INCOME:
                columns = new String[]{DATE, CATEGORY_ID, AMOUNT, COMMENTS};
                types = new String[]{TYPE_DATE, TYPE_INT, TYPE_DOUBLE, TYPE_TEXT};
                unique = new boolean[]{false, false, false, false, false};
                break;

            case TABLENAME_CATEGORIES:
                columns = new String[]{LETTER, NAME, TYPE, COLOR};
                types = new String[]{TYPE_TEXT, TYPE_TEXT, TYPE_TEXT, TYPE_INT};
                unique = new boolean[]{false, true, false, false};
                break;

            case TABLENAME_PEOPLE:
                columns = new String[]{NAME, EMAIL};
                types = new String[]{TYPE_TEXT, TYPE_TEXT};
                unique = new boolean[]{true, false};
                break;

            case TABLENAME_PEOPLE_IN_GROUP:
                columns = new String[]{PEOPLE_ID, GROUP_ID};
                types = new String[]{TYPE_INT, TYPE_INT};
                unique = new boolean[]{false, false};
                break;

            case TABLENAME_GROUPS:
                columns = new String[]{NAME};
                types = new String[]{TYPE_TEXT};
                unique = new boolean[]{true};
                break;

            case TABLENAME_TRANSACTIONS_GROUP:
                columns = new String[]{DATE, GROUP_ID, CATEGORY_ID, AMOUNT, COMMENTS};
                types = new String[]{TYPE_DATE, TYPE_INT, TYPE_INT, TYPE_DOUBLE, TYPE_TEXT};
                unique = new boolean[]{false, false, false, false, false};
                break;

            case TABLENAME_TRANSACTIONS_PEOPLE:
                columns = new String[]{DATE, AMOUNT, COMMENTS, PEOPLE_ID};
                types = new String[]{TYPE_DATE, TYPE_DOUBLE, TYPE_TEXT, TYPE_INT};
                unique = new boolean[]{false, false, false, false};
                break;

            case TABLENAME_WHO_PAID:
                columns = new String[]{TRANSACTION_ID, PEOPLE_ID, PAID};
                types = new String[]{TYPE_INT, TYPE_INT, TYPE_DOUBLE};
                unique = new boolean[]{false, false, false};
                break;

            case TABLENAME_WHO_SPENT:
                columns = new String[]{TRANSACTION_ID, PEOPLE_ID, SPEND};
                types = new String[]{TYPE_INT, TYPE_INT, TYPE_DOUBLE};
                unique = new boolean[]{false, false, false};
                break;

            default:
                columns = null; types = null; unique = null;
        }
    }


    public String createTable()
    {
        return createGenericTable(tableName, columns, types, unique);
    }

    public String getTableName()
    {
        return tableName;
    }

    public String[] getColumns()
    {
        return columns;
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
        //Add last_update
        sb.append(", " + LAST_UPDATE + " " + TYPE_DATE);

        sb.append(");");

        return sb.toString();
    }
}
