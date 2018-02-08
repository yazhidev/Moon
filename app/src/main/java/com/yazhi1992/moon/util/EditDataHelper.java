package com.yazhi1992.moon.util;

import com.yazhi1992.moon.viewmodel.IHistoryBean;

/**
 * Created by zengyazhi on 2018/2/8.
 *
 * 首页历史列表、查看详情、编辑共用数据
 */

public class EditDataHelper {

    private EditDataHelper() {
    }

    private static class EditDataHelperHolder {
        private static EditDataHelper INSTANCE = new EditDataHelper();
    }

    public static EditDataHelper getInstance() {
        return EditDataHelperHolder.INSTANCE;
    }

    private IHistoryBean mIHistoryBean;

    public void saveEditData(IHistoryBean historyBean) {
        mIHistoryBean = historyBean;
    }

    public IHistoryBean getIHistoryBean() {
        return mIHistoryBean;
    }
}
