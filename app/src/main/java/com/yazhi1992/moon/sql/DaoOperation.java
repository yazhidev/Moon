package com.yazhi1992.moon.sql;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class DaoOperation {

    protected IDaoOperationListener.IInsertListener mInsertListener;
    protected IDaoOperationListener.IQuerySingleListener mQuerySingleListener;
    protected IDaoOperationListener.IQueryAllListener mQueryAllListener;

    public IDaoOperationListener.IInsertListener getInsertListener() {
        return mInsertListener;
    }

    public void setInsertListener(IDaoOperationListener.IInsertListener insertListener) {
        mInsertListener = insertListener;
    }

    public IDaoOperationListener.IQuerySingleListener getQuerySingleListener() {
        return mQuerySingleListener;
    }

    public void setQuerySingleListener(IDaoOperationListener.IQuerySingleListener querySingleListener) {
        mQuerySingleListener = querySingleListener;
    }

    public IDaoOperationListener.IQueryAllListener getQueryAllListener() {
        return mQueryAllListener;
    }

    public void setQueryAllListener(IDaoOperationListener.IQueryAllListener queryAllListener) {
        mQueryAllListener = queryAllListener;
    }
}
