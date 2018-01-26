package com.yazhi1992.moon.sql;

import java.util.List;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public interface IDaoOperationListener {

    interface IInsertListener {
        void onInsertComplete(boolean isSuc);
    }

    interface IQuerySingleListener<T> {
        void onQuerySingle(T result);
    }

    interface IQueryAllListener<T> {
        void onQueryAll(List<T> result);
    }

    interface IOperationistener {
        void onCompleteListener(boolean isSuc);
    }
}
