package com.yazhi1992.moon.widget.calendarview;

import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.ui.mc.McDataFromApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zengyazhi on 2018/4/5.
 */

public class CalendarHelper {

    private CalendarHelper() {
    }

    private static class CalendarHelperHolder {
        private static CalendarHelper INSTANCE = new CalendarHelper();
    }

    public static CalendarHelper getInstance() {
        return CalendarHelperHolder.INSTANCE;
    }


    // TODO: 2018/4/7 如果本月第一个数据时去，则再获取一下上一个数据，用来决定是否要显示中间。该数据不加入缓存，只是用于临时判断，以免弄复杂

    public void buildMonthData(int year, int month, DataCallback<List<DateBean>> callback) {
        BuildMonthDataHelper buildMonthDataHelper = new BuildMonthDataHelper();
        buildMonthDataHelper.setYear(year);
        buildMonthDataHelper.setMonth(month);
        Observable.just(buildMonthDataHelper)
                .observeOn(Schedulers.computation())
                .doOnNext(new Consumer<BuildMonthDataHelper>() {
                    @Override
                    public void accept(BuildMonthDataHelper buildMonthDataHelper) throws Exception {
                        buildMonthDataHelper.setMonthDatas(getNoInfoDatas(buildMonthDataHelper));
                    }
                })
                .concatMap(new Function<BuildMonthDataHelper, ObservableSource<BuildMonthDataHelper>>() {
                    @Override
                    public ObservableSource<BuildMonthDataHelper> apply(BuildMonthDataHelper buildMonthDataHelper) throws Exception {
                        return getMonthDataFromApi(buildMonthDataHelper);

                    }
                })
                .concatMap(new Function<BuildMonthDataHelper, ObservableSource<BuildMonthDataHelper>>() {
                    @Override
                    public ObservableSource<BuildMonthDataHelper> apply(BuildMonthDataHelper helper) throws Exception {
                        return getFinalDate(helper);

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BuildMonthDataHelper>() {
                    @Override
                    public void accept(BuildMonthDataHelper helper) throws Exception {
                        callback.onSuccess(helper.getMonthDatas());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.onFailed(-1, throwable.toString());
                    }
                });
    }

    //获取不带mc信息的日程数据
    public List<DateBean> getNoInfoDatas(BuildMonthDataHelper buildMonthDataHelper) {
        int year = buildMonthDataHelper.getYear();
        int month = buildMonthDataHelper.getMonth();
        List<DateBean> datas = new ArrayList<>();
        int week = getFirstWeekOfMonth(year, month - 1);
        buildMonthDataHelper.setFirstDayPosition(week);

        int lastYear;
        int lastMonth;
        if (month == 1) {
            lastMonth = 12;
            lastYear = year - 1;
        } else {
            lastMonth = month - 1;
            lastYear = year;
        }
        int lastMonthDays = getMonthDays(lastYear, lastMonth);//上个月总天数

        int currentMonthDays = getMonthDays(year, month);//当前月总天数

        int nextYear;
        int nextMonth;
        if (month == 12) {
            nextMonth = 1;
            nextYear = year + 1;
        } else {
            nextMonth = month + 1;
            nextYear = year;
        }

        int index = 0;//周一开始，1周日开始

        for (int i = 0; i < week; i++) {
            datas.add(initDateBean(lastYear, lastMonth, lastMonthDays - week + 1 + i, TypeConstant.CALENDAR_LAST_MONTH));
        }

        for (int i = 0; i < currentMonthDays; i++) {
            datas.add(initDateBean(year, month, i + 1, TypeConstant.CALENDAR_THIS_MONTH));
        }

//        for (int i = 0; i < 7 * getMonthRows(year, month) - currentMonthDays - week; i++) {
//            datas.add(initDateBean(nextYear, nextMonth, i + 1, TypeConstant.CALENDAR_NEXT_MONTH));
//        }
        for (int i = 0; i < 7 * 6 - currentMonthDays - week; i++) {
            datas.add(initDateBean(nextYear, nextMonth, i + 1, TypeConstant.CALENDAR_NEXT_MONTH));
        }

        return datas;
    }

    public Observable<BuildMonthDataHelper> getMonthDataFromApi(BuildMonthDataHelper buildMonthDataHelper) {
        int year = buildMonthDataHelper.getYear();
        int month = buildMonthDataHelper.getMonth();
        return Observable.create(new ObservableOnSubscribe<BuildMonthDataHelper>() {
            @Override
            public void subscribe(ObservableEmitter<BuildMonthDataHelper> e) throws Exception {
                if(CalendarInfoCache.getInstance().getMcDataFromApis() == null) {
                    //未初始化
                    e.onNext(buildMonthDataHelper);
                    e.onComplete();
                    return;
                }
                CalendarInfoCache.getInstance().getData(year, month, new DataCallback<List<McDataFromApi>>() {
                    @Override
                    public void onSuccess(List<McDataFromApi> data) {
                        buildMonthDataHelper.setApiDatas(data);
                        e.onNext(buildMonthDataHelper);
                        e.onComplete();
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        e.onError(new Throwable(msg + code));
                    }
                });
            }
        });
    }

    public Observable<BuildMonthDataHelper> getFinalDate(BuildMonthDataHelper helper) {
        List<McDataFromApi> apiDatas = helper.getApiDatas();
        if (apiDatas == null || apiDatas.isEmpty()) {
            return Observable.just(helper);
        } else {
            return Observable.create(new ObservableOnSubscribe<BuildMonthDataHelper>() {
                @Override
                public void subscribe(ObservableEmitter<BuildMonthDataHelper> e) throws Exception {
                    Observable.fromIterable(apiDatas)
                            .concatMap(new Function<McDataFromApi, ObservableSource<McDataFromApi>>() {
                                @Override
                                public ObservableSource<McDataFromApi> apply(McDataFromApi mcDataFromApi) throws Exception {
                                    return buildMonthDateByApiDate(mcDataFromApi, helper);
                                }
                            })
                            .doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {
                                    e.onNext(helper);
                                    e.onComplete();
                                }
                            })
                            .subscribe(new Consumer<McDataFromApi>() {
                                @Override
                                public void accept(McDataFromApi mcDataFromApi) throws Exception {
                                }
                            });

                }
            });
        }
    }

    private Observable<McDataFromApi> buildMonthDateByApiDate(McDataFromApi mcDataFromApi, BuildMonthDataHelper helper) {
        return Observable.create(new ObservableOnSubscribe<McDataFromApi>() {
            @Override
            public void subscribe(ObservableEmitter<McDataFromApi> e) throws Exception {
                int nowBuildYear = helper.getYear();
                int nowBuildMonth = helper.getMonth();
                int day = mcDataFromApi.getDay();
                List<DateBean> monthDatas = helper.getMonthDatas();

                long thisYearMinTime = CalendarUtil.getTime(nowBuildYear, nowBuildMonth, 1);
//                long nextMonthTime;
//                if (nowBuildMonth == 12) {
//                    nextMonthTime = CalendarUtil.getTime(nowBuildYear + 1, 1, 1);
//                } else {
//                    nextMonthTime = CalendarUtil.getTime(nowBuildYear, nowBuildMonth + 1, 1);
//                }

                if(mcDataFromApi.getMonth() == nowBuildMonth && mcDataFromApi.getYear() == nowBuildYear) {
                    int week = helper.getFirstDayPosition();
                    monthDatas.get(day - 1 + week).setMcType(mcDataFromApi.getAction());
                } else if(mcDataFromApi.getTime() < thisYearMinTime){
                    //上个月的数据
                    if(mcDataFromApi.getAction() == TypeConstant.MC_COME) {
                        helper.setLastComeDay(0);
                    }
                    e.onNext(mcDataFromApi);
                    e.onComplete();
                    return;
                } else {
                    //下个月数据
                    day = Integer.MAX_VALUE;
                }
                if(mcDataFromApi.getAction() == TypeConstant.MC_COME) {
                    helper.setLastComeDay(mcDataFromApi.getDay());
                    e.onNext(mcDataFromApi);
                    e.onComplete();
                } else if(mcDataFromApi.getAction() == TypeConstant.MC_GO) {
                    //如果是去，需要往前设置所有上一次来之前的日期中间状态
                    int finalDay = day;
                    Observable.fromIterable(monthDatas)
                            .filter(new Predicate<DateBean>() {
                                @Override
                                public boolean test(DateBean dateBean) throws Exception {
                                    return dateBean.getType() == TypeConstant.CALENDAR_THIS_MONTH
                                            && helper.getLastComeDay() >= 0
                                            && dateBean.getDate()[2] < finalDay
                                            && dateBean.getDate()[2] > helper.getLastComeDay();
                                }
                            })
                            .doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {
                                    //设置完中间日期，还原
                                    helper.setLastComeDay(-1);
                                    e.onNext(mcDataFromApi);
                                    e.onComplete();
                                }
                            })
                            .subscribe(new Consumer<DateBean>() {
                                @Override
                                public void accept(DateBean dateBean) throws Exception {
                                    dateBean.setMcType(TypeConstant.MC_MIDDLE);
                                }
                            });
                } else {
                    e.onNext(mcDataFromApi);
                    e.onComplete();
                }
            }
        });
    }

    /**
     * 计算当月1号是周几
     *
     * @param year
     * @param month
     * @return
     */
    public int getFirstWeekOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
//        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        if (i == 1) {
            return 6;
        } else {
            return i - 2;
        }
    }

    private DateBean initDateBean(int year, int month, int day, @TypeConstant.MONTH_TYPE int type) {
        DateBean dateBean = new DateBean();
        dateBean.setDate(year, month, day);
        dateBean.setType(type);
        return dateBean;
    }

    /**
     * 计算指定月份的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 计算当前月需要显示几行
     *
     * @param year
     * @param month
     * @return
     */
    public int getMonthRows(int year, int month) {
        int items = getFirstWeekOfMonth(year, month - 1) + getMonthDays(year, month);
        int rows = items % 7 == 0 ? items / 7 : (items / 7) + 1;
//        if (rows == 4) {
//            rows = 5;
//        }
        return rows;
    }
}
