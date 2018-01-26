package com.yazhi1992.moon.sql;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class BaseDao<T>{

    protected DaoManager mDaoManager;
    protected DaoSession mDaoSession;

    public BaseDao() {
        mDaoManager = DaoManager.getInstance();
        mDaoSession = mDaoManager.getDaoSession();
    }

    protected void insert(T obj, IDaoOperationListener.IOperationistener listener) {
        AsyncSession asyncSession = mDaoSession.startAsyncSession();
        setOperation(asyncSession, listener);
        asyncSession.insert(obj);
    }

    private void setOperation(AsyncSession asyncSession, IDaoOperationListener.IOperationistener listener) {
        asyncSession.setListenerMainThread(operation -> {
            if(listener != null) {
                if(operation.isCompletedSucessfully()) {
                    listener.onCompleteListener(true);
                } else {
                    listener.onCompleteListener(false);
                }
            }
        });
    }

    protected void updateSingle(T entity) {
        mDaoSession.update(entity);
    }

    protected List<T> queryAll(Class obj, WhereCondition whereCondition) {
        AbstractDao dao = mDaoSession.getDao(obj);
        if(whereCondition == null) {
            return dao.loadAll();
        } else {
            return dao.queryBuilder().where(whereCondition).list();
        }
    }

    protected void queryAllAsyn(Class obj, Query<T> query, IDaoOperationListener.IQueryAllListener<T> listener) {
        AsyncSession asyncSession = mDaoSession.startAsyncSession();
        asyncSession.setListenerMainThread(operation -> {
            if(listener != null) {
                List<T> result = (List<T>) operation.getResult();
                listener.onQueryAll(result);
            }
        });
        if(query == null) {
            asyncSession.loadAll(obj);
        } else {
            asyncSession.queryList(query);
        }
    }

    protected void deleteAll(Class obj, IDaoOperationListener.IOperationistener listener) {
        AsyncSession asyncSession = mDaoSession.startAsyncSession();
        setOperation(asyncSession, listener);
        asyncSession.runInTx(() -> {
            asyncSession.deleteAll(obj);
        });
    }
}
